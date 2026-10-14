package cpar.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import cpar.backend.logs.GeneralLogger;
import org.springframework.stereotype.Service;

import cpar.backend.model.Equipement;
import cpar.backend.repository.EquipementRepository;

@Service
public class EquipementService implements IEquipementService {

    private static final String FICHIER_LOGS = "backend/src/main/java/cpar/backend/logs/files/EquipementLogs.txt";

    private final EquipementRepository equipementRepository;
    private final GeneralLogger generalLogger;

    public EquipementService(EquipementRepository equipementRepository,GeneralLogger generalLogger) {
        this.equipementRepository = equipementRepository;
        this.generalLogger = generalLogger;
    }

    @Override
    public List<Equipement> getAll() {
        return equipementRepository.findAll();
    }

    @Override
    public Equipement addEquipement(Equipement equipement) {
        Equipement saved = equipementRepository.save(equipement);
        generalLogger.ecrire(FICHIER_LOGS, LocalDateTime.now() + " | Ajout équipement id=" + saved.getId());
        return saved;
    }
}