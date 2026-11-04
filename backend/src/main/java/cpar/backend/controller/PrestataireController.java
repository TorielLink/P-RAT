package cpar.backend.controller;

import cpar.backend.dto.PrestataireFicheDto;
import cpar.backend.dto.PrestataireResumeDto;
import cpar.backend.service.IPrestataireService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public List<PrestataireResumeDto> obtenirPrestataires(Authentication authentification) {
        return prestataireService.obtenirPrestataires(authentification.getName());
    }

    @GetMapping("/{id}")
    public PrestataireFicheDto obtenirFiche(@PathVariable Long id, Authentication authentification) {
        return prestataireService.obtenirFiche(id, authentification.getName());
    }
}