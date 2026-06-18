package org.exercice.testats.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OffreDto {
    private Long id;
    @NotBlank(message = "Le titre ne peut pas etre vide")
    private String titre;

    @NotBlank(message = "Les competences ne peut pas etre vide")
    private String competences;
}
