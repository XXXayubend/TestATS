package org.exercice.testats.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.exercice.testats.dto.ScoreRequestDto;
import org.exercice.testats.dto.ScoreResponseDto;
import org.exercice.testats.service.AiScoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scoring")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ScoringController {

    private final AiScoringService aiScoringService;

    @PostMapping
    public ResponseEntity<ScoreResponseDto> scoreCandidate(@Valid @RequestBody ScoreRequestDto request) throws Exception {
        return ResponseEntity.ok(aiScoringService.scoreCandidatForOffre(request.getCandidatId(), request.getOffreId()));
    }
}