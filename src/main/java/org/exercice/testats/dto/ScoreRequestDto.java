package org.exercice.testats.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreRequestDto {
    @NotNull
    private Long candidatId;

    @NotNull
    private Long offreId;
}
