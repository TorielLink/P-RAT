package cpar.backend.repository;

import cpar.backend.model.entities.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {

    List<Station> findByNomContainingIgnoreCaseOrderByNomAsc(String nom);
}