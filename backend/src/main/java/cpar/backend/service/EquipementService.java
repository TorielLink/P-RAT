package cpar.backend.service;

import cpar.backend.dto.EquipementFormulaire;
import cpar.backend.exception.EquipementIntrouvableException;
import cpar.backend.exception.ReferenceDejaUtiliseeException;
import cpar.backend.logs.GeneralLogger;
import cpar.backend.model.entities.Equipement;
import cpar.backend.model.entities.Station;
import cpar.backend.model.enums.EtatsEquipement;
import cpar.backend.repository.EquipementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EquipementService implements IEquipementService {

    private static final String FICHIER_LOG = "backend/src/main/java/cpar/backend/logs/files/EquipementLogs.txt";

    private final EquipementRepository equipementRepository;
    private final IStationService stationService;
    private final GeneralLogger generalLogger;

    public EquipementService(EquipementRepository equipementRepository,
                             IStationService stationService,
                             GeneralLogger generalLogger) {
        this.equipementRepository = equipementRepository;
        this.stationService = stationService;
        this.generalLogger = generalLogger;
    }

    @Override
    public Equipement ObtenirEquipementParId(Long id) {
        return equipementRepository.findWithStationById(id)
                .orElseThrow(() -> new EquipementIntrouvableException(id));
    }

    @Override
    public Equipement ObtenirEquipementParReference(String reference) {
        return equipementRepository.findByReference(reference.trim())
                .orElseThrow(() -> new EquipementIntrouvableException(reference));
    }

    @Override
    public List<Equipement> ObtenirTousEquipements() {
        return equipementRepository.findAllByOrderByIdAsc();
    }

    @Override
    public List<Equipement> ObtenirEquipementsParStation(Long stationId) {
        stationService.ObtenirStationParId(stationId);
        return equipementRepository.findByStationIdOrderByIdAsc(stationId);
    }

    @Override
    @Transactional
    public Equipement AjouterEquipement(EquipementFormulaire formulaire) {
        String reference = texteOuNull(formulaire.reference());
        if (reference != null && equipementRepository.existsByReference(reference)) {
            throw new ReferenceDejaUtiliseeException(reference);
        }

        Equipement equipement = new Equipement();
        remplir(equipement, formulaire, reference);
        Equipement enregistre = equipementRepository.save(equipement);

        generalLogger.ecrire(FICHIER_LOG, LocalDateTime.now() + " | Ajout équipement id=" + enregistre.getId());
        return enregistre;
    }

    @Override
    @Transactional
    public Equipement ModifierEquipement(Long id, EquipementFormulaire formulaire) {
        Equipement equipement = ObtenirEquipementParId(id);
        String reference = texteOuNull(formulaire.reference());
        if (reference != null && equipementRepository.existsByReferenceAndIdNot(reference, id)) {
            throw new ReferenceDejaUtiliseeException(reference);
        }

        remplir(equipement, formulaire, reference);
        return equipement;
    }

    // Attention supprime en cascade les incidents et leurs evaluations
    @Override
    @Transactional
    public void SupprimerEquipement(Long id) {
        equipementRepository.delete(ObtenirEquipementParId(id));
    }

    private void remplir(Equipement equipement, EquipementFormulaire formulaire, String reference) {
        equipement.setReference(reference);
        equipement.setDescription(texteOuNull(formulaire.description()));
        equipement.setType(formulaire.type());
        equipement.setEtat(formulaire.etat() != null ? formulaire.etat() : EtatsEquipement.EN_SERVICE);
        equipement.setStation(stationOuNull(formulaire.stationId()));
    }

    //Pour Prestataire
    @Override
    @Transactional
    public Equipement ChangerEtatEquipement(Long id, EtatsEquipement etat) {
        Equipement equipement = ObtenirEquipementParId(id);
        equipement.setEtat(etat);
        return equipement;
    }

    private Station stationOuNull(Long stationId) {
        return stationId == null ? null : stationService.ObtenirStationParId(stationId);
    }

    // "" devient null : la contrainte UNIQUE accepte plusieurs NULL, mais pas plusieurs ""
    private static String texteOuNull(String texte) {
        return texte == null || texte.isBlank() ? null : texte.trim();
    }
}