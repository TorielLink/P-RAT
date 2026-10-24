package cpar.backend.service;

import cpar.backend.dto.UtilisateurConnecteDto;
import cpar.backend.exception.UtilisateurIntrouvableException;
import cpar.backend.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public UtilisateurConnecteDto trouverUtilisateurConnecte(String login) {
        return utilisateurRepository.findByLogin(login)
                .map(UtilisateurConnecteDto::depuis)
                .orElseThrow(() -> new UtilisateurIntrouvableException(login));
    }
}
