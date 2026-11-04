package cpar.backend.repository;

import cpar.backend.dto.StatistiquesPrestataire;
import cpar.backend.model.entities.Evaluation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    Optional<Evaluation> findByIncidentId(Long incidentId);

    boolean existsByIncidentId(Long incidentId);

    List<Evaluation> findByIncidentIdIn(Collection<Long> incidentIds);

    @EntityGraph(attributePaths = {"incident", "incident.equipement", "incident.equipement.station"})
    List<Evaluation> findByIncidentPrestataireIdOrderByDateEvaluationDescIdDesc(Long prestataireId);

    // Moyenne et nombre d'évaluations par prestataire, recalculés à chaque appel (jamais stockés)
    @Query("""
            SELECT new cpar.backend.dto.StatistiquesPrestataire(
                       e.incident.prestataire.id, AVG(e.note), COUNT(e))
            FROM Evaluation e
            WHERE e.incident.prestataire IS NOT NULL
            GROUP BY e.incident.prestataire.id
            """)
    List<StatistiquesPrestataire> calculerStatistiquesParPrestataire();
}