package br.com.cesaravb.rag_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.cesaravb.rag_service.model.DocumentImport;

// ========================================
// ArquivoImportadoRepository - Repositório JPA
// para operações na tabela ingested_files.
// ========================================
@Repository
public interface DocumentImportRepository extends JpaRepository<DocumentImport, Long> {

    // ========================================
    // findByFilename - Busca um documento pelo
    // nome do arquivo. Usado para verificar
    // duplicatas antes de importar.
    // ========================================
    Optional<DocumentImport> findByFilename(String filename);

    // ========================================
    // existsByFilename - Verifica se um arquivo
    // já foi importado anteriormente.
    // ========================================
    boolean existsByFilename(String filename);
}