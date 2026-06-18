package org.exercice.testats.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "document")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String fileType;
    private String mimeType;
    private String storagePath;
    private Long fileSize;

    @ManyToOne
    @JoinColumn(name = "candidat_id")
    private Candidat candidat;
}
