package cpar.backend.service;

import cpar.backend.dto.StationCarteDto;
import cpar.backend.dto.StationFormulaire;
import cpar.backend.exception.PositionOccupeeException;
import cpar.backend.exception.StationIntrouvableException;
import cpar.backend.model.entities.Incident;
import cpar.backend.model.entities.Station;
import cpar.backend.model.enums.NiveauAttentionIncident;
import cpar.backend.model.enums.StatutIncident;
import cpar.backend.repository.EquipementRepository;
import cpar.backend.repository.IncidentRepository;
import cpar.backend.repository.StationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class StationService implements IStationService {

    // Du plus grave au moins grave : la situation d'une station est le premier niveau présent
    private static final List<NiveauAttentionIncident> ORDRE_GRAVITE = List.of(
            NiveauAttentionIncident.ROUGE, NiveauAttentionIncident.ORANGE,
            NiveauAttentionIncident.VERT, NiveauAttentionIncident.PRIS_EN_CHARGE);

    private final StationRepository stationRepository;
    private final EquipementRepository equipementRepository;
    private final IncidentRepository incidentRepository;
    private final Clock horloge;

    public StationService(StationRepository stationRepository,
                          EquipementRepository equipementRepository,
                          IncidentRepository incidentRepository,
                          Clock horloge) {
        this.stationRepository = stationRepository;
        this.equipementRepository = equipementRepository;
        this.incidentRepository = incidentRepository;
        this.horloge = horloge;
    }

    // ---------- Lecture ----------

    @Override
    public List<Station> obtenirStationParNom(String nom) {
        List<Station> stations = stationRepository.findByNomContainingIgnoreCaseOrderByNomAsc(nom.trim());
        if (stations.isEmpty()) {
            throw new StationIntrouvableException(nom);
        }
        return stations;
    }

    @Override
    public Station obtenirStationParId(Long id) {
        return stationRepository.findById(id).orElseThrow(() -> new StationIntrouvableException(id));
    }

    @Override
    public List<Station> obtenirToutesStations() {
        return stationRepository.findAll(Sort.by("nom"));
    }

    /** Carte du réseau : niveaux calculés avec l'heure courante (RG4), jamais stockés. */
    @Override
    public List<StationCarteDto> obtenirCarte() {
        Map<Long, Long> equipementsParStation = new HashMap<>();
        for (Object[] ligne : equipementRepository.compterParStation()) {
            equipementsParStation.put((Long) ligne[0], (Long) ligne[1]);
        }

        // Niveaux des incidents actifs, regroupés par station
        LocalDateTime maintenant = LocalDateTime.now(horloge);
        Map<Long, List<NiveauAttentionIncident>> niveauxParStation = new HashMap<>();
        for (Incident incident : incidentRepository.findByStatutNotOrderByDateOuvertureAscIdAsc(StatutIncident.RESOLU)) {
            Station station = incident.getEquipement().getStation();
            if (station != null) {
                niveauxParStation.computeIfAbsent(station.getId(), id -> new ArrayList<>())
                        .add(NiveauAttentionIncident.de(incident, maintenant));
            }
        }

        return stationRepository.findAll(Sort.by("nom", "id")).stream()
                .map(station -> {
                    List<NiveauAttentionIncident> niveaux = niveauxParStation.getOrDefault(station.getId(), List.of());
                    String situation = ORDRE_GRAVITE.stream()
                            .filter(niveaux::contains)
                            .findFirst()
                            .map(Enum::name)
                            .orElse("AUCUN");
                    return new StationCarteDto(
                            station.getId(), station.getNom(), station.getPositionX(), station.getPositionY(),
                            equipementsParStation.getOrDefault(station.getId(), 0L),
                            situation,
                            compter(niveaux, NiveauAttentionIncident.ROUGE),
                            compter(niveaux, NiveauAttentionIncident.ORANGE),
                            compter(niveaux, NiveauAttentionIncident.VERT),
                            compter(niveaux, NiveauAttentionIncident.PRIS_EN_CHARGE));
                })
                .toList();
    }

    // ---------- Écriture ----------

    @Override
    @Transactional
    public Station ajouterStation(StationFormulaire formulaire) {
        verifierPositionLibre(formulaire.positionX(), formulaire.positionY(), null);
        Station station = new Station();
        remplir(station, formulaire);
        return stationRepository.save(station);
    }

    @Override
    @Transactional
    public Station modifierStation(Long id, StationFormulaire formulaire) {
        Station station = obtenirStationParId(id);
        verifierPositionLibre(formulaire.positionX(), formulaire.positionY(), id);
        remplir(station, formulaire);
        return station;
    }

    // Attention : supprime en cascade les équipements, les incidents et leurs évaluations
    @Override
    @Transactional
    public void supprimerStation(Long id) {
        stationRepository.delete(obtenirStationParId(id));
    }

    // ---------- Outils ----------

    private static void remplir(Station station, StationFormulaire formulaire) {
        station.setNom(formulaire.nom().trim());
        station.setPositionX(formulaire.positionX());
        station.setPositionY(formulaire.positionY());
    }

    // Une case par station ; la station modifiée peut garder sa propre case
    private void verifierPositionLibre(Integer positionX, Integer positionY, Long idStation) {
        if (positionX == null || positionY == null) {
            return;
        }
        stationRepository.findByPositionXAndPositionY(positionX, positionY)
                .filter(occupante -> !occupante.getId().equals(idStation))
                .ifPresent(occupante -> {
                    throw new PositionOccupeeException(positionX, positionY, occupante.getNom());
                });
    }

    private static long compter(List<NiveauAttentionIncident> niveaux, NiveauAttentionIncident niveau) {
        return niveaux.stream().filter(niveau::equals).count();
    }
}