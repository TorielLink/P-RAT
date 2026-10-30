package cpar.backend.service;

import cpar.backend.model.entities.Prestataire;
import cpar.backend.repository.PrestataireRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PrestataireService implements IPrestataireService {

    private final PrestataireRepository prestataireRepository;

    public PrestataireService(PrestataireRepository prestataireRepository) {
        this.prestataireRepository = prestataireRepository;
    }

    @Override
    public List<Prestataire> obtenirTousPrestataires() {
        return prestataireRepository.findAll(Sort.by("nom", "id"));
    }
}