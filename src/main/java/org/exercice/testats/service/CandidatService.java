package org.exercice.testats.service;

import org.exercice.testats.dto.CandidatRequestDto;
import org.exercice.testats.dto.CandidatResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CandidatService {
    CandidatResponseDto createCandidat(CandidatRequestDto requestDto, MultipartFile cvFile) throws IOException;
    CandidatResponseDto getCandidatById(Long id);
    byte[] downloadCV(Long id) throws IOException;
    List<CandidatResponseDto> getAllCandidats();
}
