package cpar.backend.repository;

import cpar.backend.model.entities.Prestataire;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestataireRepository extends JpaRepository<Prestataire, Long> {
}