package cpar.backend.service;

import cpar.backend.dto.EquipementFormulaire;
import cpar.backend.model.entities.Equipement;
import cpar.backend.model.enums.EtatsEquipement;

import java.util.List;

public interface IEquipementService {

    Equipement obtenirEquipementParId(Long id);

    Equipement obtenirEquipementParReference(String reference);

    List<Equipement> obtenirTousEquipements();

    List<Equipement> obtenirEquipementsParStation(Long stationId);

    Equipement ajouterEquipement(EquipementFormulaire formulaire);

    Equipement modifierEquipement(Long id, EquipementFormulaire formulaire);

    void supprimerEquipement(Long id);

    Equipement changerEtatEquipement(Long id, EtatsEquipement etat);
}