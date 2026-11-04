package cpar.backend.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import cpar.backend.model.entities.Evaluation;
import cpar.backend.model.entities.Incident;
import cpar.backend.model.enums.NiveauAttentionIncident;

import java.time.LocalDateTime;

public record IncidentDetailDto(
        @JsonUnwrapped IncidentDto incident,
        long dureeOuvertureMinutes,
        NiveauAttentionIncident niveauAttention,
        EvaluationDto evaluation,
        boolean modifiable) {

    public static IncidentDetailDto depuis(Incident incident, Evaluation evaluation, LocalDateTime maintenant,
                                           boolean modifiable) {
        return new IncidentDetailDto(
                IncidentDto.depuis(incident),
                incident.dureeOuverture(maintenant).toMinutes(),
                NiveauAttentionIncident.de(incident, maintenant),
                evaluation != null ? EvaluationDto.depuis(evaluation) : null,
                modifiable);
    }
}
