package org.exercice.testats.repository;

import org.exercice.testats.entity.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidatRepository extends JpaRepository<Candidat, Long> {
    Optional<Candidat> findByEmail(String email);
}
