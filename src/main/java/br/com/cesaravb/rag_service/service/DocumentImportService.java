package br.com.cesaravb.rag_service.service;

import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.cesaravb.rag_service.model.DocumentImport;
import br.com.cesaravb.rag_service.repository.DocumentImportRepository;
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

	
	private final VectorStore vectorStore;	// VectorStore é a interface do Spring AI que representa o banco vetorial. Os documentos processados serão armazenados como vetores/embeddings.
	private final DocumentImportRepository repository;	// Repositório JPA para registrar os arquivos importados e evitar duplicatas

	
	// Construtor para injeção de dependências do VectorStore e do DocumentImportRepository
	public DocumentImportService(VectorStore vectorStore, DocumentImportRepository repository) {
		this.vectorStore = vectorStore;
		this.repository = repository;
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

	        // Ignora arquivos de controle como .gitkeep
	        if (filename == null || filename.startsWith(".")) {
	            return;
	        }

	        if (!repository.existsByFilename(filename)) {
	            log.info("Importando arquivo '{}'...", filename);

	            var reader = new TikaDocumentReader(resource);
	            var splitter = new TokenTextSplitter();
	            var docs = splitter.apply(reader.get());

	            // Salva o filename como metadado em cada chunk
	            // Permite localizar e excluir os chunks por arquivo depois
	            docs.forEach(doc -> doc.getMetadata().put("filename", filename));

	            vectorStore.add(docs);

	            // Registra via JPA no lugar do JdbcClient
	            var arquivoImportado = new DocumentImport();
	            arquivoImportado.setFilename(filename);
	            repository.save(arquivoImportado);

	            log.info("✅ Arquivo '{}' importado com sucesso!", filename);

	        } else {
	            log.info("⏭️ Arquivo '{}' já foi importado anteriormente. Pulando...", filename);
	        }

	    } catch (Exception e) {
	        log.error("❌ Erro ao importar arquivo '{}'", resource.getFilename(), e);
	    }
	}
}