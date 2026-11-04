package cpar.backend.dto;

import cpar.backend.model.entities.Prestataire;

public record PrestataireResumeDto(
        Long id,
        String nom,
        String email,
        String telephone,
        Double noteMoyenne,
        long nombreEvaluations) {

    /** statistiques vaut null si le prestataire n'a encore jamais été évalué */
    public static PrestataireResumeDto depuis(Prestataire prestataire, StatistiquesPrestataire statistiques) {
        return new PrestataireResumeDto(
                prestataire.getId(),
                prestataire.getNom(),
                prestataire.getEmail(),
                prestataire.getTelephone(),
                statistiques != null ? statistiques.noteMoyenne() : null,
                statistiques != null ? statistiques.nombreEvaluations() : 0);
    }
}