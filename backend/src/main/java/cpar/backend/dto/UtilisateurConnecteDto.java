package cpar.backend.dto;

import cpar.backend.model.entities.Utilisateur;

public record UtilisateurConnecteDto(String prenom, String profil, String profilLibelle) {

    public static UtilisateurConnecteDto depuis(Utilisateur utilisateur) {
        return new UtilisateurConnecteDto(
                utilisateur.getPrenom(),
                utilisateur.getProfil().name(),
                utilisateur.getProfil().getLibelle());
    }
}
