package org.exercice.testats.service;

import lombok.RequiredArgsConstructor;
import org.exercice.testats.entity.Candidat;
import org.exercice.testats.entity.Document;
import org.exercice.testats.exception.FileStorageException;
import org.exercice.testats.exception.ResourceNotException;
import org.exercice.testats.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;

    // Chemin configurable via application.properties (valeur par défaut "uploads/")
    @Value("${app.storage.dir:uploads/}")
    private String storageDir;

    public Document saveDocument(Candidat candidat, MultipartFile file, String fileType) throws IOException {
        // 1. Vérifier le type MIME
        if (!"application/pdf".equals(file.getContentType())) {
            throw new FileStorageException("Seul le format PDF est accepté");
        }

        // 2. Créer le répertoire de stockage (absolu pour éviter les problèmes Tomcat)
        Path uploadPath = Paths.get(storageDir).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 3. Générer un nom unique
        String originalName = file.getOriginalFilename();
        String uniqueName = UUID.randomUUID() + "_" + originalName;
        Path filePath = uploadPath.resolve(uniqueName);

        // 4. Transférer le fichier
        file.transferTo(filePath.toFile());

        // 5. Construire l'entité Document
        Document doc = new Document();
        doc.setFileName(originalName);
        doc.setFileType(fileType);
        doc.setMimeType(file.getContentType());
        doc.setStoragePath(filePath.toString());   // chemin absolu stocké
        doc.setFileSize(file.getSize());
        doc.setCandidat(candidat);

        return documentRepository.save(doc);
    }

    public byte[] downloadDocument(Long docId) throws IOException {
        Document doc = documentRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotException("Document non trouvé avec l'id : " + docId));
        Path path = Paths.get(doc.getStoragePath());
        if (!Files.exists(path)) {
            throw new FileStorageException("Le fichier n'existe plus sur le disque : " + doc.getStoragePath());
        }
        return Files.readAllBytes(path);
    }
}