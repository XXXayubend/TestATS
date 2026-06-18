package org.exercice.testats.service;

import org.exercice.testats.dto.OffreDto;
import java.util.List;

public interface OffreService {
    OffreDto createOffre(OffreDto offreDto);
    OffreDto getOffreById(Long id);
    List<OffreDto> getAllOffres();
}