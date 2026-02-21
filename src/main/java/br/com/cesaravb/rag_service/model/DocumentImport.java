package br.com.cesaravb.rag_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ========================================
// ArquivoImportado - Entidade JPA que representa
// um documento importado para a base de conhecimento.
// Mapeada para a tabela ingested_files no PostgreSQL.
// ========================================
@Entity
@Table(name = "ingested_files")
public class DocumentImport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome do arquivo — usado para evitar reimportação
    // e para identificar os chunks no PGVector
    @Column(nullable = false, unique = true)
    private String filename;

    @Column(name = "ingested_at")
    private LocalDateTime ingestedAt;

    @PrePersist
    public void prePersist() {
        this.ingestedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public LocalDateTime getIngestedAt() { return ingestedAt; }
}