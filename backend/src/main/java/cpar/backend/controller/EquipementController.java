package cpar.backend.controller;

import cpar.backend.dto.EquipementDto;
import cpar.backend.dto.EquipementFormulaire;
import cpar.backend.dto.EtatEquipementFormulaire;
import cpar.backend.model.entities.Equipement;
import cpar.backend.service.IEquipementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/equipements")
public class EquipementController {

    private final IEquipementService equipementService;

    public EquipementController(IEquipementService equipementService) {
        this.equipementService = equipementService;
    }

    // GET /api/equipements = tous les equipements
    // GET /api/equipements?stationId=3 = equipements d'une station
    @GetMapping
    public List<EquipementDto> obtenirEquipements(@RequestParam(required = false) Long stationId) {
        List<Equipement> equipements = stationId == null
                ? equipementService.obtenirTousEquipements()
                : equipementService.obtenirEquipementsParStation(stationId);
        return equipements.stream().map(EquipementDto::depuis).toList();
    }

    @GetMapping("/{id}")
    public EquipementDto obtenirEquipementParId(@PathVariable Long id) {
        return EquipementDto.depuis(equipementService.obtenirEquipementParId(id));
    }

    @GetMapping("/reference/{reference}")
    public EquipementDto obtenirEquipementParReference(@PathVariable String reference) {
        return EquipementDto.depuis(equipementService.obtenirEquipementParReference(reference));
    }

    @PostMapping
    public ResponseEntity<EquipementDto> ajouterEquipement(@Valid @RequestBody EquipementFormulaire formulaire) {
        EquipementDto equipement = EquipementDto.depuis(equipementService.ajouterEquipement(formulaire));
        return ResponseEntity.created(URI.create("/api/equipements/" + equipement.id())).body(equipement);
    }

    @PutMapping("/{id}")
    public EquipementDto modifierEquipement(@PathVariable Long id,
                                            @Valid @RequestBody EquipementFormulaire formulaire) {
        return EquipementDto.depuis(equipementService.modifierEquipement(id, formulaire));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerEquipement(@PathVariable Long id) {
        equipementService.supprimerEquipement(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/etat")
    public EquipementDto changerEtatEquipement(@PathVariable Long id,
                                               @Valid @RequestBody EtatEquipementFormulaire formulaire) {
        return EquipementDto.depuis(equipementService.changerEtatEquipement(id, formulaire.etat()));
    }
}