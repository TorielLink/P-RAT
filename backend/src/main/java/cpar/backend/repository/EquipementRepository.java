package cpar.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cpar.backend.model.Equipement;

public interface EquipementRepository extends JpaRepository<Equipement, Long> {
}