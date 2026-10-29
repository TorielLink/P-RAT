package cpar.backend.service;

import cpar.backend.dto.EquipementFormulaire;
import cpar.backend.model.entities.Equipement;
import cpar.backend.model.enums.EtatsEquipement;

import java.util.List;

public interface IEquipementService {

    Equipement ObtenirEquipementParId(Long id);

    Equipement ObtenirEquipementParReference(String reference);

    List<Equipement> ObtenirTousEquipements();

    List<Equipement> ObtenirEquipementsParStation(Long stationId);

    Equipement AjouterEquipement(EquipementFormulaire formulaire);

    Equipement ModifierEquipement(Long id, EquipementFormulaire formulaire);

    void SupprimerEquipement(Long id);

    Equipement ChangerEtatEquipement(Long id, EtatsEquipement etat);
}