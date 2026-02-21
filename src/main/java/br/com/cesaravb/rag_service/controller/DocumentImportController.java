package br.com.cesaravb.rag_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.cesaravb.rag_service.service.DocumentImportService;
import lombok.extern.slf4j.Slf4j;

/**
 * DocumentImportController expõe um endpoint REST para importar novos documentos
 * para a base de conhecimento do assistente sem precisar reiniciar a aplicação.
 *
 * O Angular pode usar este endpoint para permitir que administradores
 * façam upload de novos planos, preços ou FAQs a qualquer momento.
 */
@Slf4j
@RestController
@RequestMapping("/api/documents")
public class DocumentImportController {

    private final DocumentImportService documentImportService;

    public DocumentImportController(DocumentImportService documentImportService) {
        this.documentImportService = documentImportService;
    }

    
    // ======================================================================================
    // O Angular pode usar este endpoint para permitir que administradores façam upload de novos
    // Aceita qualquer formato suportado pelo Tika (PDF, DOCX, XLSX, TXT, etc.)
    // ======================================================================================
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
}