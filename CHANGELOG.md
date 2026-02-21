## [1.6.0](https://github.com/CesarAVB/ai-assistant-rag-service/compare/v1.5.0...v1.6.0) (2026-02-21)

### Features

* adiciona logging para origens permitidas no CORS ([5a08ec5](https://github.com/CesarAVB/ai-assistant-rag-service/commit/5a08ec51cb24e016ff5497ee8efaffd2f33a35cc))

## [1.5.0](https://github.com/CesarAVB/ai-assistant-rag-service/compare/v1.4.0...v1.5.0) (2026-02-21)

### Features

* ajusta a ordem de execução dos filtros de autenticação e CORS ([9522182](https://github.com/CesarAVB/ai-assistant-rag-service/commit/952218286821a01bdfe6dd0dc02e4b5217e2a522))

## [1.4.0](https://github.com/CesarAVB/ai-assistant-rag-service/compare/v1.3.0...v1.4.0) (2026-02-21)

### Features

* Refatora configuração CORS para usar CorsFilter ao invés de ([d6b052c](https://github.com/CesarAVB/ai-assistant-rag-service/commit/d6b052c5c1e239639d5ac4357ef20e4e7a955103))

## [1.3.0](https://github.com/CesarAVB/ai-assistant-rag-service/compare/v1.2.0...v1.3.0) (2026-02-21)

### Features

* adiciona configurações para modelos GPT-4o-mini e ([51145a8](https://github.com/CesarAVB/ai-assistant-rag-service/commit/51145a8b0641fb96fb2e466e791f8110a95d0384))

## [1.2.0](https://github.com/CesarAVB/ai-assistant-rag-service/compare/v1.1.0...v1.2.0) (2026-02-21)

### Features

* adiciona configurações para PGVECTOR e autenticação no ([20b56af](https://github.com/CesarAVB/ai-assistant-rag-service/commit/20b56af7759dabd231b911e3753f8461fbfb0517))

## [1.1.0](https://github.com/CesarAVB/ai-assistant-rag-service/compare/v1.0.0...v1.1.0) (2026-02-21)

### Features

* adiciona AuthController para gerenciamento de autenticação com ([c61cb5e](https://github.com/CesarAVB/ai-assistant-rag-service/commit/c61cb5e9657d171c6caec61d468df248b474361d))
* adiciona AuthFilter para interceptar requisições e validar token ([d4daf3e](https://github.com/CesarAVB/ai-assistant-rag-service/commit/d4daf3e838eed6818b78214d9d2d0e68b7137b87))
* adiciona endpoints para listar e excluir documentos importados no ([d1718e7](https://github.com/CesarAVB/ai-assistant-rag-service/commit/d1718e792d2509c2373a1545eadc47182cbc3e7d))
* adiciona entidade JPA DocumentImport para gerenciamento de ([926044b](https://github.com/CesarAVB/ai-assistant-rag-service/commit/926044b55d7a4b19e1d20a9d9c4ef885c3e6ef34))
* atualiza comportamento do ChatService para simular atendimento ([643a2c0](https://github.com/CesarAVB/ai-assistant-rag-service/commit/643a2c0620f8ed4e2887739f1779a4b080cb5663))
* cria DTO AuthRequest para representar o corpo da requisição de ([02caa86](https://github.com/CesarAVB/ai-assistant-rag-service/commit/02caa860ea3b669dd0647e7c33a89aa52fdd5934))
* cria DTO AuthResponse para representar a resposta do login com o ([9dd0a25](https://github.com/CesarAVB/ai-assistant-rag-service/commit/9dd0a255adf50998e8f5f21d4fe92d696a1ab35c))
* cria repositório JPA DocumentImportRepository para gerenciar ([e83e0d1](https://github.com/CesarAVB/ai-assistant-rag-service/commit/e83e0d1a76eff7c2bf3d16a0d47349914990a034))

## 1.0.0 (2026-02-21)

### Features

* adiciona controladores REST para chat e importação de documentos ([716fdbe](https://github.com/CesarAVB/ai-assistant-rag-service/commit/716fdbeb9d0b1edd25698f91747b728628e72d13))
* adiciona dependências para leitura de documentos e integração com ([6a8f4ad](https://github.com/CesarAVB/ai-assistant-rag-service/commit/6a8f4ad99ff66016b1597a1e48b3e382196d2f5c))
* adiciona documentação no README.md com informações sobre o ([4e6f686](https://github.com/CesarAVB/ai-assistant-rag-service/commit/4e6f6860014eccd41d91b9c4f45252ece8c3302e))
* adiciona DTOs para requisições e respostas de chat ([a6bd770](https://github.com/CesarAVB/ai-assistant-rag-service/commit/a6bd770197221ea6c3944402f1c674ee9e954c56))
* adiciona serviços ChatService e DocumentImportService para ([3cf4cd0](https://github.com/CesarAVB/ai-assistant-rag-service/commit/3cf4cd023b673f398944619fcd05473301845106))
