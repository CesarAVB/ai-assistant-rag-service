package br.com.cesaravb.rag_service.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

/**
 * ChatService é responsável por toda a lógica do assistente de vendas.
 *
 * Ele combina dois recursos principais:
 * - RAG (Retrieval-Augmented Generation): busca documentos relevantes no PGVector
 *   antes de responder, enriquecendo a resposta com o conhecimento da empresa.
 * - Memória de conversa: mantém o histórico por sessão, permitindo que o assistente
 *   lembre o que foi dito anteriormente na mesma conversa.
 */
@Service
public class ChatService {

    private final ChatClient chatClient;

    // ChatMemory armazena o histórico de mensagens por sessão (conversationId).
    // InMemoryChatMemory guarda em memória RAM — simples e suficiente para começar.
    // Importante: os dados são perdidos ao reiniciar a aplicação.
    // Futuramente pode ser substituído por uma implementação com banco de dados.
    private final ChatMemory chatMemory = MessageWindowChatMemory.builder().build();

    // Prompt de sistema: define a personalidade e comportamento do assistente.
    // Este texto é enviado ao modelo a cada requisição como instrução base.
    private static final String SYSTEM_PROMPT = """
    		    Você é um consultor de vendas humano e experiente da empresa de internet.

				Sua comunicação deve parecer totalmente natural, como a de um atendente real conversando pelo WhatsApp ou chat online. 
				Evite qualquer linguagem robótica, formal demais ou técnica em excesso.
				
				Seu objetivo é entender a necessidade do cliente e ajudá-lo a escolher o melhor plano disponível.
				
				COMPORTAMENTO:
				
				- Seja educado, simpático e profissional.
				- Use frases naturais e fluidas.
				- Demonstre interesse genuíno na necessidade do cliente.
				- Explique de forma clara e simples.
				- Sempre que fizer sentido, faça perguntas para entender melhor o perfil do cliente (ex: uso residencial, streaming, jogos, empresa, etc).
				- Finalize oferecendo ajuda adicional.
				
				REGRAS IMPORTANTES:
				
				- Utilize EXCLUSIVAMENTE as informações presentes nos documentos fornecidos como base de conhecimento.
				- Nunca invente planos, valores, velocidades ou condições.
				- Sempre que mencionar preço, velocidade ou benefícios, cite o nome do documento de onde a informação foi extraída.
				- Se a informação não estiver disponível nos documentos, diga de forma natural que irá verificar internamente e peça para o cliente aguardar.
				- Nunca diga que está consultando documentos ou base de dados — apenas responda naturalmente.
				
				ESTILO DE RESPOSTA:
				
				- Não use listas técnicas a menos que o cliente peça.
				- Não use linguagem de IA.
				- Não diga "conforme os documentos".
				- Responda como um atendente humano real.
				
				Seu foco é converter o cliente com clareza, confiança e naturalidade.
            """;

    // ============================================================================================
    // O construtor do ChatService recebe um ChatClient.Builder e um VectorStore.
    // ============================================================================================
    public ChatService(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        // QuestionAnswerAdvisor é o coração do RAG.
                        // Antes de cada resposta, ele busca automaticamente no PGVector
                        // os chunks de documentos mais relevantes para a pergunta do usuário
                        // e os injeta no contexto enviado ao modelo de IA.
                        QuestionAnswerAdvisor.builder(vectorStore).build(),

                        // MessageChatMemoryAdvisor adiciona memória de conversa.
                        // Ele injeta automaticamente o histórico da sessão em cada requisição,
                        // permitindo que o modelo entenda o contexto das mensagens anteriores.
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }


    // ============================================================================================
    // Método que processa a mensagem do usuário e retorna a resposta do assistente.
    // ============================================================================================
    public String chat(String message, String conversationId) {
        return chatClient.prompt()
                .user(message)																	// Define a mensagem do usuário
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId)) // Passa o conversationId para os advisors, garantindo que eles usem a memória correta
                .call()																			// Executa a chamada ao modelo de IA, que processará a mensagem, buscará documentos relevantes e retornará a resposta
                .content();																		// Extrai o conteúdo da resposta gerada pelo modelo
    }
}