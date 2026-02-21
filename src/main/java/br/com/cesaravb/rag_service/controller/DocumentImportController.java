package br.com.cesaravb.rag_service.controller;

import java.util.List;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.cesaravb.rag_service.model.DocumentImport;
import br.com.cesaravb.rag_service.repository.DocumentImportRepository;
import br.com.cesaravb.rag_service.service.DocumentImportService;
import lombok.extern.slf4j.Slf4j;

/**
 * DocumentImportController expõe um endpoint REST para importar novos
 * documentos para a base de conhecimento do assistente sem precisar reiniciar a
 * aplicação.
 *
 * O Angular pode usar este endpoint para permitir que administradores façam
 * upload de novos planos, preços ou FAQs a qualquer momento.
 */
@Slf4j
@RestController
@RequestMapping("/api/documents")
public class DocumentImportController {

	private final DocumentImportService documentImportService;
	private final DocumentImportRepository repository;
	private final VectorStore vectorStore;

	public DocumentImportController(DocumentImportService documentImportService, DocumentImportRepository repository, VectorStore vectorStore) {
		this.documentImportService = documentImportService;
		this.repository = repository;
		this.vectorStore = vectorStore;
	}

	
	// ========================================================================================================================
	// O Angular pode usar este endpoint para permitir que administradores façam upload de novos
	// Aceita qualquer formato suportado pelo Tika (PDF, DOCX, XLSX, TXT, etc.)
	// ========================================================================================================================
	@PostMapping("/upload")
	public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body("Nenhum arquivo enviado.");
		}

		try {
			// Delega o processamento para o DocumentImportService
			documentImportService.importFile(file);
			return ResponseEntity.ok("Arquivo '" + file.getOriginalFilename() + "' importado com sucesso!");

		} catch (Exception e) {
			log.error("Erro ao importar arquivo '{}'", file.getOriginalFilename(), e);
			return ResponseEntity.internalServerError().body("Erro ao importar arquivo: " + e.getMessage());
		}
	}

	
	// ========================================================================================================================
	// list - Retorna todos os documentos importados na base de conhecimento.
	// ========================================================================================================================
	@GetMapping
	public ResponseEntity<List<DocumentImport>> list() {
		return ResponseEntity.ok(repository.findAll());
	}

	
	// ========================================================================================================================
	// delete - Exclui o documento da tabela, ingested_files E remove todos os chunks do PGVector vinculados ao arquivo.
	// ========================================================================================================================
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		return repository.findById(id).map(arquivo -> {
			try {
				// Remove os chunks do PGVector filtrando pelo metadado filename
				var filter = new FilterExpressionBuilder();
				vectorStore.delete(filter.eq("filename", arquivo.getFilename()).build());

				// Remove o registro da tabela de controle
				repository.delete(arquivo);

				log.info("✅ Documento '{}' excluído com sucesso.", arquivo.getFilename());
				return ResponseEntity.ok("Documento '" + arquivo.getFilename() + "' excluído com sucesso.");

			} catch (Exception e) {
				log.error("❌ Erro ao excluir documento '{}'", arquivo.getFilename(), e);
				return ResponseEntity.internalServerError().body("Erro ao excluir o documento: " + e.getMessage());
			}
		}).orElse(ResponseEntity.notFound().build());
	}
}