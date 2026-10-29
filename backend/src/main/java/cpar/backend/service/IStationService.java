package cpar.backend.service;

import cpar.backend.model.entities.Station;

import java.util.List;

public interface IStationService {

    List<Station> ObtenirStationParNom(String nom);

    Station ObtenirStationParId(Long id);

    List<Station> ObtenirToutesStations();

    Station AjouterStation(String nom);

    void SupprimerStation(Long id);

    Station ModifierStation(Long id, String nom);
}