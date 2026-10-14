package cpar.backend.service;

import java.util.List;

import cpar.backend.model.Equipement;

public interface IEquipementService {
    List<Equipement> getAll();
    Equipement addEquipement(Equipement equipement);
}