package cpar.backend.controller;

import cpar.backend.dto.UtilisateurConnecteDto;
import cpar.backend.service.UtilisateurService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UtilisateurService utilisateurService;

    public AuthController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/me")
    public UtilisateurConnecteDto utilisateurConnecte(Authentication authentification) {
        return utilisateurService.trouverUtilisateurConnecte(authentification.getName());
    }
}
