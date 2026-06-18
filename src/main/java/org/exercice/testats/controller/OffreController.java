package org.exercice.testats.controller;

import jakarta.validation.Valid;
import org.exercice.testats.dto.OffreDto;
import org.exercice.testats.service.OffreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offres")
public class OffreController {

    @Autowired
    private OffreService offreService;

    @PostMapping
    public ResponseEntity<OffreDto> createOffre(@Valid @RequestBody OffreDto offreDto) {
        OffreDto createdOffre = offreService.createOffre(offreDto);
        return new ResponseEntity<>(createdOffre, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OffreDto> getOffreById(@PathVariable Long id) {
        OffreDto offreDto = offreService.getOffreById(id);
        return ResponseEntity.ok(offreDto);
    }

    @GetMapping
    public ResponseEntity<List<OffreDto>> getAllOffres() {
        List<OffreDto> offres = offreService.getAllOffres();
        return ResponseEntity.ok(offres);
    }
}