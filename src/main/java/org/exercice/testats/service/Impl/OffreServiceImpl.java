package org.exercice.testats.service.Impl;

import lombok.RequiredArgsConstructor;
import org.exercice.testats.dto.OffreDto;
import org.exercice.testats.entity.Offre;
import org.exercice.testats.exception.DuplicationResourceException;
import org.exercice.testats.mapper.OffreMapper;
import org.exercice.testats.repository.OffreRepository;
import org.exercice.testats.service.OffreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OffreServiceImpl implements OffreService {

    private final OffreRepository offreRepository;
    private final OffreMapper offreMapper;

    @Override
    public OffreDto createOffre(OffreDto offreDto) {
        if (offreRepository.existsByTitre(offreDto.getTitre())) {
            throw new DuplicationResourceException("Une offre avec le titre '" + offreDto.getTitre() + "' existe déjà.");
        }
        Offre offre = offreMapper.toEntity(offreDto);
        Offre savedOffre = offreRepository.save(offre);
        return offreMapper.toDto(savedOffre);
    }

    @Override
    public OffreDto getOffreById(Long id) {
        Offre offre = offreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée avec l'id : " + id));
        return offreMapper.toDto(offre);
    }

    @Override
    public List<OffreDto> getAllOffres() {
        return offreRepository.findAll().stream()
                .map(offreMapper::toDto)
                .collect(Collectors.toList());
    }
}