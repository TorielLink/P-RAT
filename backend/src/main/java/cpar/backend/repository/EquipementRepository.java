package cpar.backend.repository;

import cpar.backend.model.entities.Equipement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EquipementRepository extends JpaRepository<Equipement, Long> {

    @EntityGraph(attributePaths = "station")
    Optional<Equipement> findWithStationById(Long id);

    @EntityGraph(attributePaths = "station")
    Optional<Equipement> findByReference(String reference);

    @EntityGraph(attributePaths = "station")
    List<Equipement> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = "station")
    List<Equipement> findByStationIdOrderByIdAsc(Long stationId);

    @EntityGraph(attributePaths = "station")
    List<Equipement> findAllByOrderByTypeAscReferenceAscIdAsc();

    @EntityGraph(attributePaths = "station")
    List<Equipement> findByStationIdOrderByTypeAscReferenceAscIdAsc(Long stationId);

    boolean existsByReference(String reference);

    boolean existsByReferenceAndIdNot(String reference, Long id);

    // [stationId, nombre d'équipements] pour toutes les stations qui en ont
    @Query("SELECT e.station.id, COUNT(e) FROM Equipement e WHERE e.station IS NOT NULL GROUP BY e.station.id")
    List<Object[]> compterParStation();
}