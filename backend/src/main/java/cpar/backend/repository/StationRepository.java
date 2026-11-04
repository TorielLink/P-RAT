package cpar.backend.repository;

import cpar.backend.model.entities.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long> {

    List<Station> findByNomContainingIgnoreCaseOrderByNomAsc(String nom);

    Optional<Station> findByPositionXAndPositionY(Integer positionX, Integer positionY);
}