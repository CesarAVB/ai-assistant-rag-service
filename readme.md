# 🤖 RAG Sales Assistant — Backend

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.1.x-brightgreen.svg)](https://spring.io/projects/spring-ai)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue.svg)](https://www.postgresql.org/)
[![PGVector](https://img.shields.io/badge/PGVector-latest-blueviolet.svg)](https://github.com/pgvector/pgvector)
[![OpenAI](https://img.shields.io/badge/OpenAI-GPT--4o--mini-412991.svg)](https://openai.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> Microservico de assistente de vendas com IA generativa, RAG e memoria de conversas para suporte ao cliente via chat.

---

## 📌 Topics

`java` `spring-boot` `spring-ai` `rag` `retrieval-augmented-generation` `openai` `pgvector` `postgresql` `vector-database` `embeddings` `chatbot` `ai` `generative-ai` `apache-tika` `document-processing` `rest-api` `portfolio`

---

## 🧱 Tecnologias

| Tecnologia | Versão | Função |
|---|---|---|
| Java | 21 | Linguagem |
| Spring Boot | 3.5.x | Framework principal |
| Spring AI | 1.1.x | Integração com IA e RAG |
| OpenAI | GPT-4o-mini | Modelo de linguagem |
| PGVector | — | Banco vetorial para embeddings |
| PostgreSQL | 17 | Banco de dados principal |
| Apache Tika | — | Leitura de documentos (PDF, DOCX, XLSX, TXT) |
| Lombok | — | Reducao de boilerplate |

---

## 🗂️ Estrutura do Projeto

```
src/main/java/br/com/cesaravb/rag_service/
├── auth/
│   ├── AuthController.java                 # Endpoint de login
│   ├── AuthFilter.java                     # Intercepta e valida o token em cada requisicao
│   ├── AuthRequest.java                    # DTO de requisicao de login
│   └── AuthResponse.java                   # DTO de resposta do login
├── chat/
│   ├── ChatController.java                 # Endpoint REST do chat
│   ├── ChatService.java                    # Logica RAG + memoria de conversa por sessao
│   ├── ChatRequest.java                    # DTO de requisicao do chat
│   └── ChatResponse.java                   # DTO de resposta do chat
└── ingestion/
    ├── controller/
    │   ├── DocumentController.java         # Lista e exclui documentos
    │   └── DocumentImportController.java   # Upload de novos documentos
    ├── model/
    │   └── ArquivoImportado.java           # Entidade JPA (tabela ingested_files)
    ├── repository/
    │   └── ArquivoImportadoRepository.java # Repositorio JPA
    └── service/
        └── DocumentImportService.java      # Importa documentos para o PGVector
```

---

## ⚙️ Configuracao

### Pre-requisitos

- Java 21+
- PostgreSQL com extensao **pgvector** instalada
- Chave de API da OpenAI

### `application.properties`

```properties
# Server
server.port=8080

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/rag_assistant_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# OpenAI
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-4o-mini
spring.ai.openai.embedding.options.model=text-embedding-3-small

# PGVector
spring.ai.vectorstore.pgvector.initialize-schema=true
spring.ai.vectorstore.pgvector.dimensions=1536
spring.ai.vectorstore.pgvector.distance-type=COSINE_DISTANCE

# Auth
app.auth.password=${AUTH_PASSWORD}
app.auth.token=${AUTH_TOKEN}

# CORS
app.cors.allowed-origins=${CORS_ALLOWED_ORIGINS}
```

---

## 🚀 Como executar

```bash
# Clone o repositorio
git clone https://github.com/cesaravb/rag-sales-assistant.git
cd rag-sales-assistant

# Configure as variaveis de ambiente
export OPENAI_API_KEY=sk-...
export AUTH_PASSWORD=suaSenha
export AUTH_TOKEN=seuToken
export CORS_ALLOWED_ORIGINS=http://localhost:4200

# Execute a aplicacao
./mvnw spring-boot:run
```

---

## 🔐 Autenticacao

A API utiliza autenticacao simples por token estatico configurado via variavel de ambiente.

**Fluxo:**
```
Frontend envia a senha → POST /api/auth/login
  → Backend valida contra AUTH_PASSWORD
  → Retorna o token se correto
  → Frontend envia o token no header Authorization: Bearer <token>
  → AuthFilter valida o token em cada requisicao
```

A rota `/api/auth/login` e publica. Todas as demais exigem o token no header.

---

## 📡 Endpoints

### Autenticacao

| Metodo | Endpoint | Descricao |
|---|---|---|
| POST | `/api/auth/login` | Valida a senha e retorna o token |

### Chat

| Metodo | Endpoint | Descricao |
|---|---|---|
| POST | `/api/chat` | Envia mensagem e recebe resposta do assistente |

**Exemplo de requisicao:**
```json
{
  "message": "Quais planos de internet voces oferecem?",
  "conversationId": "uuid-da-sessao"
}
```

**Exemplo de resposta:**
```json
{
  "conversationId": "uuid-da-sessao",
  "response": "Oferecemos os seguintes planos..."
}
```

### Documentos

| Metodo | Endpoint | Descricao |
|---|---|---|
| GET | `/api/documents` | Lista todos os documentos importados |
| POST | `/api/documents/upload` | Faz upload de um novo documento |
| DELETE | `/api/documents/{id}` | Exclui documento da base de conhecimento |

---

## 🔄 Fluxo RAG

```
Usuario faz uma pergunta
  → QuestionAnswerAdvisor busca chunks relevantes no PGVector
  → MessageChatMemoryAdvisor injeta historico da sessao (em memoria RAM)
  → OpenAI gera a resposta com base nos documentos
  → Resposta retornada ao usuario
```

---

## 📂 Importacao automatica de documentos

Coloque arquivos PDF, DOCX, XLSX ou TXT na pasta `src/main/resources/docs/`.
Ao iniciar a aplicacao, o `DocumentImportService` processa automaticamente os arquivos ainda nao importados.
Cada chunk recebe o metadado `filename` para permitir exclusao precisa no PGVector.

---

## 🚀 Melhorias Futuras

- **Memoria persistente com Redis** — substituir o `MessageWindowChatMemory` em RAM por uma implementacao com Redis, permitindo que o historico de conversas sobreviva a restarts e funcione com multiplas instancias
- **Autenticacao com JWT** — evoluir a autenticacao simples por senha para um sistema completo com usuarios, roles e tokens JWT com expiracao
- **Rate limiting** — adicionar limitacao de requisicoes por IP ou usuario com Bucket4j para proteger a API contra abusos e controlar o consumo de tokens
- **Pagina de monitoramento de sessoes** — interface para visualizar as conversas ativas, historico de mensagens e consumo de tokens por sessao
- **Suporte a imagens** — expandir o RAG para processar imagens e diagramas dentro dos documentos usando modelos multimodais
- **Webhook de notificacao** — notificar via webhook ou e-mail quando um novo documento for importado ou quando houver falha no processamento
- **Versionamento de documentos** — permitir multiplas versoes do mesmo documento e controlar qual versao esta ativa na base de conhecimento
- **Metricas e observabilidade** — integrar com Micrometer e Grafana para monitorar latencia das respostas, consumo de tokens e taxa de erro
- **Testes automatizados** — adicionar testes de integracao para os endpoints e testes unitarios para o servico de importacao
- **Flyway para migrations** — substituir o `ddl-auto=update` por migrations versionadas com Flyway para maior controle do schema em producao

---

## 🌐 Deploy

- **URL da API:** https://rag-api.cesaravb.com.br
- **Plataforma:** Coolify
- **Projeto:** REDELOGNET - RAG ASSISTANT

---

## 👤 Autor

**Cesar Augusto Vieira Bezerra**
[portfolio.cesaraugusto.dev.br](https://portfolio.cesaraugusto.dev.br/)