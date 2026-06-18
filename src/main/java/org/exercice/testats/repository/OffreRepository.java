package org.exercice.testats.repository;

import org.exercice.testats.entity.Offre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OffreRepository extends JpaRepository<Offre, Long> {
    boolean existsByTitre(String titre);
}
