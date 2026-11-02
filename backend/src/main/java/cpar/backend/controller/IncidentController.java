package cpar.backend.controller;

import cpar.backend.dto.*;
import cpar.backend.model.entities.Incident;
import cpar.backend.model.enums.StatutIncident;
import cpar.backend.service.IIncidentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class IncidentController {

    private final IIncidentService incidentService;

    public IncidentController(IIncidentService incidentService) {
        this.incidentService = incidentService;
    }

    // GET /api/incidents                  → tous (un prestataire ne voit que les siens)
    // GET /api/incidents?equipementId=3   → historique d'un équipement
    // GET /api/incidents?statut=OUVERT    → par statut
    @GetMapping("/incidents")
    public List<IncidentDto> obtenirIncidents(@RequestParam(required = false) Long equipementId,
                                              @RequestParam(required = false) StatutIncident statut,
                                              Authentication authentification) {
        String login = authentification.getName();
        List<Incident> incidents;
        if (equipementId != null) {
            incidents = incidentService.obtenirIncidentsParEquipement(equipementId, login);
        } else if (statut != null) {
            incidents = incidentService.obtenirIncidentsParStatut(statut, login);
        } else {
            incidents = incidentService.obtenirTousIncidents(login);
        }
        return incidents.stream().map(IncidentDto::depuis).toList();
    }

    @GetMapping("/incidents/{id}")
    public IncidentDetailDto obtenirIncidentParId(@PathVariable Long id, Authentication authentification) {
        return incidentService.obtenirDetailIncident(id, authentification.getName());
    }
    @PostMapping("/equipements/{equipementId}/incidents")
    public ResponseEntity<IncidentDto> declarerIncident(@PathVariable Long equipementId,
                                                        @Valid @RequestBody DeclarationIncidentRequest demande,
                                                        Authentication authentification) {
        IncidentDto incident = IncidentDto.depuis(
                incidentService.declarerIncident(equipementId, demande, authentification.getName()));
        return ResponseEntity.created(URI.create("/api/incidents/" + incident.id())).body(incident);
    }

    @PatchMapping("/incidents/{id}")
    public IncidentDto modifierIncident(@PathVariable Long id,
                                        @Valid @RequestBody ModificationIncidentRequest demande,
                                        Authentication authentification) {
        return IncidentDto.depuis(incidentService.modifierIncident(id, demande, authentification.getName()));
    }

    @PatchMapping("/incidents/{id}/prestataire")
    public IncidentDto affecterPrestataire(@PathVariable Long id,
                                           @Valid @RequestBody AffectationPrestataireFormulaire formulaire) {
        return IncidentDto.depuis(incidentService.affecterPrestataire(id, formulaire.prestataireId()));
    }

    @DeleteMapping("/incidents/{id}")
    public ResponseEntity<Void> supprimerIncident(@PathVariable Long id) {
        incidentService.supprimerIncident(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/incidents/{id}/resolution")
    public IncidentDto resoudreIncident(@PathVariable Long id, Authentication authentification) {
        return IncidentDto.depuis(incidentService.resoudreIncident(id, authentification.getName()));
    }

    @GetMapping("/incidents/historique")
    public PageDto<HistoriqueIncidentDto> obtenirHistorique(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "20") int taille,
                                                            Authentication authentification) {
        return incidentService.obtenirHistorique(authentification.getName(), page, taille);
    }
}