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
            Você é um assistente de vendas especializado da empresa de internet.
            Seu objetivo é ajudar clientes a escolherem o melhor plano de internet.
            
            Regras que você deve seguir:
            - Responda APENAS com base nos documentos fornecidos.
            - Se não souber a resposta, diga que vai verificar e peça para o cliente aguardar.
            - Seja cordial, objetivo e sempre ofereça ajuda adicional ao final.
            - Cite a fonte (nome do documento) quando fornecer informações sobre planos ou preços.
            - Não invente informações que não estejam nos documentos.
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