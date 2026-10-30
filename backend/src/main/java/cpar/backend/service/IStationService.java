package cpar.backend.service;

import cpar.backend.model.entities.Station;

import java.util.List;

public interface IStationService {

    List<Station> obtenirStationParNom(String nom);

    Station obtenirStationParId(Long id);

    List<Station> obtenirToutesStations();

    Station ajouterStation(String nom);

    void supprimerStation(Long id);

    Station modifierStation(Long id, String nom);
}