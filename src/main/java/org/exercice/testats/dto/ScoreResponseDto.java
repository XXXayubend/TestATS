package org.exercice.testats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreResponseDto {
    private Integer score;
    private String justification;
    private String candidatNom;
    private String offreTitre;
}
