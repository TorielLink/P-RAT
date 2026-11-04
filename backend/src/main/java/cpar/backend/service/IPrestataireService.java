package cpar.backend.service;

import cpar.backend.dto.PrestataireFicheDto;
import cpar.backend.dto.PrestataireResumeDto;

import java.util.List;

public interface IPrestataireService {

    List<PrestataireResumeDto> obtenirPrestataires(String login);

    PrestataireFicheDto obtenirFiche(Long id, String login);
}