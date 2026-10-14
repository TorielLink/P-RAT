package cpar.backend.controller;

import cpar.backend.model.Equipement;
import cpar.backend.repository.EquipementRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/equipements")
public class EquipementController {
    private final EquipementRepository equipementRepository;

    public EquipementController(EquipementRepository equipementRepository) {
        this.equipementRepository = equipementRepository;
    }

    @GetMapping
    public List<Equipement> findAll() {
        return equipementRepository.findAll();
    }
}
