package org.exercice.testats.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CandidatResponseDto {
    private Long id;

    @NotBlank(message = "le nom est obligatoire")
    private String nom;

    @Email(message = "Email invalide")
    @NotBlank(message = "email est obligatoire")
    private String email;

    @NotBlank(message = "la competences est obligatoire")
    private String competences;

    @PositiveOrZero(message = "l'experience est zero ou positive")
    private Integer experience;

    private String cvFileName;
    private String cvUrl;


}
