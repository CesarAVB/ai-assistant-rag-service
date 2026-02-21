package br.com.cesaravb.rag_service.service;

import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

/**
 * DocumentImportService é responsável por importar documentos (PDF, DOCX, XLSX,
 * TXT, etc.) para o banco vetorial (PGVector), que será usado como base de
 * conhecimento do assistente de IA.
 *
 * Implementa CommandLineRunner para executar automaticamente ao iniciar a
 * aplicação.
 */
@Slf4j
@Service
public class DocumentImportService implements CommandLineRunner {

	// VectorStore é a interface do Spring AI que representa o banco vetorial
	// (PGVector neste caso).
	// É aqui que os documentos processados serão armazenados como
	// vetores/embeddings.
	private final VectorStore vectorStore;

	// JdbcClient é usado para consultar/registrar quais arquivos já foram
	// importados,
	// evitando que o mesmo documento seja processado mais de uma vez.
	private final JdbcClient jdbcClient;

	public DocumentImportService(VectorStore vectorStore, JdbcClient jdbcClient) {
		this.vectorStore = vectorStore;
		this.jdbcClient = jdbcClient;
	}

	// ==========================================================================================
	// Método executado automaticamente ao iniciar a aplicação (CommandLineRunner).
	// Busca todos os arquivos suportados na pasta resources/docs e os importa se
	// ainda não existirem.
	// ==========================================================================================
	@Override
	public void run(String... args) {
		try {
			// PathMatchingResourcePatternResolver permite buscar arquivos usando padrões
			// (wildcards)
			// dentro do classpath (pasta resources do projeto)
			var resolver = new PathMatchingResourcePatternResolver();

			// Busca todos os arquivos suportados dentro de resources/docs/
			// O padrão ** significa qualquer arquivo, independente do formato
			Resource[] documents = resolver.getResources("classpath:/docs/*.*");

			if (documents.length == 0) {
				log.warn("Nenhum documento encontrado na pasta /docs.");
				return;
			}

			// Processa cada arquivo encontrado
			for (Resource document : documents) {
				importIfNotExists(document);
			}

		} catch (Exception e) {
			log.error("Erro ao carregar documentos da pasta /docs", e);
		}
	}

	// ==========================================================================================
	// Importa um arquivo enviado via endpoint REST (upload manual pelo Angular).
	// Converte o MultipartFile em Resource e reutiliza o fluxo já existente.
	// ==========================================================================================
	public void importFile(MultipartFile file) throws Exception {
		// Converte o MultipartFile em Resource para ser lido pelo TikaDocumentReader
		Resource resource = file.getResource();
		importIfNotExists(resource);
	}

	// ==========================================================================================
	// Verifica se o arquivo já foi importado anteriormente.
	// Caso não tenha sido, realiza todo o processo de importação para o PGVector.
	// ==========================================================================================
	private void importIfNotExists(Resource resource) {
		try {
			final String filename = resource.getFilename();

			// Consulta na tabela de controle se esse arquivo já foi importado
			Integer count = jdbcClient.sql("SELECT COUNT(*) FROM ingested_files WHERE filename = ?").params(filename).query(Integer.class).single();

			if (count == 0) {
				log.info("Importando arquivo '{}'...", filename);

				// TikaDocumentReader é um leitor universal do Spring AI.
				// Ele usa o Apache Tika internamente e suporta automaticamente:
				// PDF, DOCX, DOC, XLSX, XLS, TXT, HTML, entre outros.
				// Não é necessário configurar nada além de passar o Resource.
				var reader = new TikaDocumentReader(resource);

				// TokenTextSplitter divide o conteúdo do documento em pedaços menores (chunks).
				// Isso é necessário porque modelos de IA têm limite de tokens por requisição,
				// e chunks menores permitem buscas mais precisas no vector store.
				var splitter = new TokenTextSplitter();

				// Fluxo completo:
				// 1. reader.get() → lê o arquivo e retorna List<Document>
				// 2. splitter.apply() → divide em chunks menores
				// 3. vectorStore.add() → gera os embeddings e salva no PGVector
				vectorStore.add(splitter.apply(reader.get()));

				// Registra na tabela de controle que esse arquivo já foi importado
				jdbcClient.sql("INSERT INTO ingested_files (filename) VALUES (?)").params(filename).update();

				log.info("✅ Arquivo '{}' importado com sucesso!", filename);

			} else {
				log.info("⏭️ Arquivo '{}' já foi importado anteriormente. Pulando...", filename);
			}

		} catch (Exception e) {
			log.error("❌ Erro ao importar arquivo '{}'", resource.getFilename(), e);
		}
	}
}