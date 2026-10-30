package cpar.backend.service;

import cpar.backend.model.entities.Prestataire;

import java.util.List;

public interface IPrestataireService {

    List<Prestataire> obtenirTousPrestataires();
}