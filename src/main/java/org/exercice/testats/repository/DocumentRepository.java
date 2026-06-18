package org.exercice.testats.repository;

import org.exercice.testats.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByCandidatId(Long candidatId);
    Optional<Document> findByCandidatIdAndFileType(Long candidatId, String fileType);
}
