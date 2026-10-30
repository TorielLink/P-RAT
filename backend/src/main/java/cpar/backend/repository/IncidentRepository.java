package cpar.backend.repository;

import cpar.backend.model.entities.Incident;
import cpar.backend.model.enums.StatutIncident;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    // Charge l'équipement (et sa station), le prestataire et le déclarant dans la même requête
    @EntityGraph(attributePaths = {"equipement", "equipement.station", "prestataire", "utilisateur"})
    Optional<Incident> findWithDetailsById(Long id);

    @EntityGraph(attributePaths = {"equipement", "equipement.station", "prestataire", "utilisateur"})
    List<Incident> findAllByOrderByDateOuvertureDescIdDesc();

    @EntityGraph(attributePaths = {"equipement", "equipement.station", "prestataire", "utilisateur"})
    List<Incident> findByEquipementIdOrderByDateOuvertureDescIdDesc(Long equipementId);

    @EntityGraph(attributePaths = {"equipement", "equipement.station", "prestataire", "utilisateur"})
    List<Incident> findByStatutOrderByDateOuvertureDescIdDesc(StatutIncident statut);

    @EntityGraph(attributePaths = {"equipement", "equipement.station", "prestataire", "utilisateur"})
    List<Incident> findByPrestataireIdOrderByDateOuvertureDescIdDesc(Long prestataireId);

    @EntityGraph(attributePaths = {"equipement", "equipement.station", "prestataire", "utilisateur"})
    List<Incident> findByEquipementIdAndPrestataireIdOrderByDateOuvertureDescIdDesc(Long equipementId, Long prestataireId);

    @EntityGraph(attributePaths = {"equipement", "equipement.station", "prestataire", "utilisateur"})
    List<Incident> findByStatutAndPrestataireIdOrderByDateOuvertureDescIdDesc(StatutIncident statut, Long prestataireId);
}