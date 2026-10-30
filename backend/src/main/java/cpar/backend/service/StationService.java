package cpar.backend.service;

import cpar.backend.exception.StationIntrouvableException;
import cpar.backend.model.entities.Station;
import cpar.backend.repository.StationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StationService implements IStationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

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

    @Override
    @Transactional
    public Station ajouterStation(String nom) {
        Station station = new Station();
        station.setNom(nom.trim());
        return stationRepository.save(station);
    }

    // Attention suprimme en cascade les equipements, incidents et leurs evaluations
    @Override
    @Transactional
    public void supprimerStation(Long id) {
        stationRepository.delete(obtenirStationParId(id));
    }

    @Override
    @Transactional
    public Station modifierStation(Long id, String nom) {
        Station station = obtenirStationParId(id);
        station.setNom(nom.trim());
        return station;
    }
}