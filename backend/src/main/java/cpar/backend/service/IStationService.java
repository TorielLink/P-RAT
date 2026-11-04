package cpar.backend.service;

import cpar.backend.dto.StationCarteDto;
import cpar.backend.dto.StationFormulaire;
import cpar.backend.model.entities.Station;

import java.util.List;

public interface IStationService {

    List<Station> obtenirStationParNom(String nom);

    Station obtenirStationParId(Long id);

    List<Station> obtenirToutesStations();

    void supprimerStation(Long id);

    Station ajouterStation(StationFormulaire formulaire);

    Station modifierStation(Long id, StationFormulaire formulaire);

    List<StationCarteDto> obtenirCarte();
}