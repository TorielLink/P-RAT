package cpar.backend.dto;

/** result requête d'agrégation : une ligne par prestataire évalué au moins une fois */
public record StatistiquesPrestataire(Long prestataireId, Double noteMoyenne, Long nombreEvaluations) {
}