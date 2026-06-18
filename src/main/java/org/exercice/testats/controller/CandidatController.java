package org.exercice.testats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.exercice.testats.dto.CandidatRequestDto;
import org.exercice.testats.dto.CandidatResponseDto;
import org.exercice.testats.service.CandidatService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/candidats")
@RequiredArgsConstructor
public class CandidatController {
    private final CandidatService service;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<CandidatResponseDto> create(
            @Valid
            @RequestPart("candidat") String candidatJson,
            @RequestPart("cv") MultipartFile cvFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CandidatRequestDto dto = mapper.readValue(candidatJson, CandidatRequestDto.class);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createCandidat(dto, cvFile));
    }

    @GetMapping
    public ResponseEntity<List<CandidatResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAllCandidats());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidatResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCandidatById(id));
    }

    @GetMapping("/{id}/cv")
    public ResponseEntity<byte[]> downloadCV(@PathVariable Long id) throws IOException {
        byte[] data = service.downloadCV(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cv.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }
}