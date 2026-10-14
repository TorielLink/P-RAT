package cpar.backend.controller;

import cpar.backend.model.Equipement;
import cpar.backend.service.EquipementService;
import cpar.backend.service.IEquipementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipements")
public class EquipementController {

    private final IEquipementService equipementService;

    public EquipementController(IEquipementService equipementService) {
        this.equipementService = equipementService;
    }

    @GetMapping
    public List<Equipement> getAll() {
        return equipementService.getAll();
    }

    @PostMapping
    public ResponseEntity<Equipement> add(@RequestBody Equipement equipement) {
        Equipement saved = equipementService.addEquipement(equipement);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
