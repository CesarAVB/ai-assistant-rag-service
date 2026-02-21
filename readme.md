# 🤖 RAG Sales Assistant — Backend

Assistente de vendas com IA generativa utilizando **Spring Boot**, **Spring AI** e **PGVector**.
Implementa o padrão **RAG (Retrieval-Augmented Generation)** para responder perguntas com base nos documentos da empresa, mantendo memória de conversa por sessão.

---

## 🧱 Tecnologias

| Tecnologia | Versão | Função |
|---|---|---|
| Java | 21 | Linguagem |
| Spring Boot | 3.x | Framework principal |
| Spring AI | 1.1.x | Integração com IA e RAG |
| OpenAI | GPT-4o-mini | Modelo de linguagem |
| PGVector | — | Banco vetorial para embeddings |
| PostgreSQL | — | Banco de dados principal |
| Apache Tika | — | Leitura de documentos (PDF, DOCX, XLSX, TXT) |
| Lombok | — | Redução de boilerplate |

---

## 🗂️ Estrutura do Projeto

```
src/main/java/br/com/cesaravb/rag_service/
├── chat/
│   ├── ChatController.java         # Endpoint REST do chat
│   ├── ChatService.java            # Lógica RAG + memória de conversa
│   ├── ChatRequest.java            # DTO de requisição
│   └── ChatResponse.java          # DTO de resposta
├── ingestion/
│   ├── DocumentImportService.java  # Importação de documentos para o PGVector
│   └── DocumentImportController.java # Endpoint de upload de documentos
└── resources/
    ├── docs/                       # Pasta para documentos automáticos (lidos ao iniciar)
    └── application.properties      # Configurações da aplicação
```

---

## ⚙️ Configuração

### Pré-requisitos

- Java 21+
- PostgreSQL com extensão **pgvector** instalada
- Chave de API da OpenAI

### `application.properties`

```properties
# Server
server.port=8080

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/ragdb
spring.datasource.username=postgres
spring.datasource.password=postgres

# OpenAI
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-4o-mini
spring.ai.openai.embedding.options.model=text-embedding-3-small

# PGVector
spring.ai.vectorstore.pgvector.initialize-schema=true
spring.ai.vectorstore.pgvector.dimensions=1536
spring.ai.vectorstore.pgvector.distance-type=COSINE_DISTANCE
```

### Banco de dados

Execute o script abaixo para criar a tabela de controle de arquivos importados:

```sql
CREATE TABLE IF NOT EXISTS ingested_files (
    id          SERIAL PRIMARY KEY,
    filename    VARCHAR(255) UNIQUE NOT NULL,
    ingested_at TIMESTAMP DEFAULT NOW()
);
```

---

## 🚀 Como executar

```bash
# Clone o repositório
git clone https://github.com/cesaravb/rag-sales-assistant.git
cd rag-sales-assistant

# Configure a variável de ambiente com sua chave OpenAI
export OPENAI_API_KEY=sk-...

# Execute a aplicação
./mvnw spring-boot:run
```

---

## 📡 Endpoints

### Chat

```
POST /api/chat
Content-Type: application/json

{
  "message": "Quais planos de internet vocês oferecem?",
  "conversationId": "uuid-da-sessao"
}
```

**Resposta:**
```json
{
  "conversationId": "uuid-da-sessao",
  "response": "Oferecemos os seguintes planos..."
}
```

---

### Upload de documentos

```
POST /api/documents/upload
Content-Type: multipart/form-data

file: [arquivo]
```

**Resposta:**
```
Arquivo 'planos.pdf' importado com sucesso!
```

---

## 🔄 Fluxo RAG

```
Usuário faz uma pergunta
  → QuestionAnswerAdvisor busca chunks relevantes no PGVector
  → MessageChatMemoryAdvisor injeta histórico da sessão
  → OpenAI gera a resposta com base nos documentos
  → Resposta retornada ao usuário
```

---

## 📂 Importação automática de documentos

Coloque arquivos PDF, DOCX, XLSX ou TXT na pasta `src/main/resources/docs/`.
Ao iniciar a aplicação, o `DocumentImportService` processa automaticamente os arquivos ainda não importados.

---

## 🌐 CORS

Configure as origens permitidas no `application.properties`:

```properties
app.cors.allowed-origins=http://localhost:4200
```

---

## 👤 Autor

**César Augusto Vieira Bezerra**
[portfolio.cesaraugusto.dev.br](https://portfolio.cesaraugusto.dev.br/)