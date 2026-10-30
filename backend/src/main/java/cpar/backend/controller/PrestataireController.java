package cpar.backend.controller;

import cpar.backend.model.entities.Prestataire;
import cpar.backend.service.IPrestataireService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/prestataires")
public class PrestataireController {

    private final IPrestataireService prestataireService;

    public PrestataireController(IPrestataireService prestataireService) {
        this.prestataireService = prestataireService;
    }

    @GetMapping
    public List<Prestataire> obtenirTousPrestataires() {
        return prestataireService.obtenirTousPrestataires();
    }
}