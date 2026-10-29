package cpar.backend.controller;

import cpar.backend.dto.StationFormulaire;
import cpar.backend.model.entities.Station;
import cpar.backend.service.IStationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/stations")
public class StationController {

    private final IStationService stationService;

    public StationController(IStationService stationService) {
        this.stationService = stationService;
    }

    // GET /api/stations = toutes les stations
    // GET /api/stations?nom=def = recherche par nom
    @GetMapping
    public List<Station> obtenirStations(@RequestParam(required = false) String nom) {
        if (nom == null || nom.isBlank()) {
            return stationService.ObtenirToutesStations();
        }
        return stationService.ObtenirStationParNom(nom);
    }

    @GetMapping("/{id}")
    public Station obtenirStationParId(@PathVariable Long id) {
        return stationService.ObtenirStationParId(id);
    }

    @PostMapping
    public ResponseEntity<Station> ajouterStation(@Valid @RequestBody StationFormulaire formulaire) {
        Station station = stationService.AjouterStation(formulaire.nom());
        return ResponseEntity.created(URI.create("/api/stations/" + station.getId())).body(station);
    }

    @PutMapping("/{id}")
    public Station modifierStation(@PathVariable Long id, @Valid @RequestBody StationFormulaire formulaire) {
        return stationService.ModifierStation(id, formulaire.nom());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerStation(@PathVariable Long id) {
        stationService.SupprimerStation(id);
        return ResponseEntity.noContent().build();
    }
}