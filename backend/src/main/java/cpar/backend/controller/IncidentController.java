package cpar.backend.controller;

import cpar.backend.dto.AffectationPrestataireFormulaire;
import cpar.backend.dto.DeclarationIncidentFormulaire;
import cpar.backend.dto.IncidentDto;
import cpar.backend.dto.ModificationIncidentFormulaire;
import cpar.backend.model.entities.Incident;
import cpar.backend.model.enums.StatutIncident;
import cpar.backend.service.IIncidentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IIncidentService incidentService;

    public IncidentController(IIncidentService incidentService) {
        this.incidentService = incidentService;
    }

    // GET /api/incidents                  → tous (un prestataire ne voit que les siens)
    // GET /api/incidents?equipementId=3   → historique d'un équipement
    // GET /api/incidents?statut=OUVERT    → par statut
    @GetMapping
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

    @GetMapping("/{id}")
    public IncidentDto obtenirIncidentParId(@PathVariable Long id, Authentication authentification) {
        return IncidentDto.depuis(incidentService.obtenirIncidentParId(id, authentification.getName()));
    }

    @PostMapping
    public ResponseEntity<IncidentDto> declarerIncident(@Valid @RequestBody DeclarationIncidentFormulaire formulaire,
                                                        Authentication authentification) {
        IncidentDto incident = IncidentDto.depuis(
                incidentService.declarerIncident(formulaire, authentification.getName()));
        return ResponseEntity.created(URI.create("/api/incidents/" + incident.id())).body(incident);
    }

    @PutMapping("/{id}")
    public IncidentDto modifierIncident(@PathVariable Long id,
                                        @Valid @RequestBody ModificationIncidentFormulaire formulaire,
                                        Authentication authentification) {
        return IncidentDto.depuis(incidentService.modifierIncident(id, formulaire, authentification.getName()));
    }

    @PatchMapping("/{id}/prestataire")
    public IncidentDto affecterPrestataire(@PathVariable Long id,
                                           @Valid @RequestBody AffectationPrestataireFormulaire formulaire) {
        return IncidentDto.depuis(incidentService.affecterPrestataire(id, formulaire.prestataireId()));
    }

    @PatchMapping("/{id}/resolution")
    public IncidentDto resoudreIncident(@PathVariable Long id) {
        return IncidentDto.depuis(incidentService.resoudreIncident(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerIncident(@PathVariable Long id) {
        incidentService.supprimerIncident(id);
        return ResponseEntity.noContent().build();
    }
}