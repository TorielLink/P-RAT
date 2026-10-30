package cpar.backend.repository;

import cpar.backend.model.entities.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    Optional<Evaluation> findByIncidentId(Long incidentId);
}