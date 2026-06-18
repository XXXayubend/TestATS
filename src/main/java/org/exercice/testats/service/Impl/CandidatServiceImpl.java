package org.exercice.testats.service.Impl;

import lombok.RequiredArgsConstructor;
import org.exercice.testats.exception.DuplicationResourceException;
import org.exercice.testats.exception.ResourceNotException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.exercice.testats.dto.CandidatRequestDto;
import org.exercice.testats.dto.CandidatResponseDto;
import org.exercice.testats.entity.Candidat;
import org.exercice.testats.entity.Document;
import org.exercice.testats.mapper.CandidatMapper;
import org.exercice.testats.repository.CandidatRepository;
import org.exercice.testats.repository.DocumentRepository;
import org.exercice.testats.service.CandidatService;
import org.exercice.testats.service.DocumentService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidatServiceImpl implements CandidatService {

    private final CandidatRepository candidatRepository;
    private final DocumentRepository documentRepository;
    private final DocumentService documentService;
    private final CandidatMapper mapper;

    @Override
    @Transactional
    public CandidatResponseDto createCandidat(CandidatRequestDto dto, MultipartFile cvFile) throws IOException {
        if (candidatRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicationResourceException("Un candidat avec cet email existe déjà");
        }

        Candidat candidat = mapper.toEntity(dto);
        candidat = candidatRepository.save(candidat);

        if (cvFile != null && !cvFile.isEmpty()) {
            documentService.saveDocument(candidat, cvFile, "CV");
        }

        return buildResponseDto(candidat);
    }

    @Override
    @Transactional(readOnly = true)
    public CandidatResponseDto getCandidatById(Long id) {
        Candidat candidat = candidatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotException("Candidat non trouvé"));
        return buildResponseDto(candidat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CandidatResponseDto> getAllCandidats() {
        return candidatRepository.findAll().stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadCV(Long candidatId) throws IOException {
        Document cv = documentRepository.findByCandidatIdAndFileType(candidatId, "CV")
                .orElseThrow(() -> new ResourceNotException("Aucun CV pour ce candidat"));
        return documentService.downloadDocument(cv.getId());
    }

    private CandidatResponseDto buildResponseDto(Candidat candidat) {
        CandidatResponseDto dto = mapper.toResponseDto(candidat);

        documentRepository.findByCandidatIdAndFileType(candidat.getId(), "CV")
                .ifPresent(cv -> {
                    dto.setCvFileName(cv.getFileName());
                    dto.setCvUrl("/api/candidats/" + candidat.getId() + "/cv");
                });

        return dto;
    }
}