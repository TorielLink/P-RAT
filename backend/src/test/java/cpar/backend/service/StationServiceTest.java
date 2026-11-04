package cpar.backend.service;

import cpar.backend.dto.StationFormulaire;
import cpar.backend.exception.PositionOccupeeException;
import cpar.backend.exception.StationIntrouvableException;
import cpar.backend.model.entities.Station;
import cpar.backend.repository.EquipementRepository;
import cpar.backend.repository.IncidentRepository;
import cpar.backend.repository.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationRepository stationRepository;
    @Mock
    private EquipementRepository equipementRepository;

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private Clock horloge;

    @InjectMocks
    private StationService stationService;

    private static Station station(Long id, String nom) {
        Station station = new Station();
        station.setId(id);
        station.setNom(nom);
        return station;
    }

    @Test
    void ajouterStationEnregistreLeNom() {
        when(stationRepository.save(any(Station.class))).thenAnswer(appel -> appel.getArgument(0));

        Station resultat = stationService.ajouterStation(new StationFormulaire("  La Defense  ", null, null));

        assertThat(resultat.getNom()).isEqualTo("La Defense");
    }

    @Test
    void modifierStationChangeLeNomEtLaPosition() {
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station(1L, "Ancien nom")));

        Station resultat = stationService.modifierStation(1L, new StationFormulaire("Nouveau nom", 3, -2));

        assertThat(resultat.getNom()).isEqualTo("Nouveau nom");
        assertThat(resultat.getPositionX()).isEqualTo(3);
    }

    @Test
    void uneCaseDejaOccupeeEstRefusee() {
        when(stationRepository.findByPositionXAndPositionY(4, -4)).thenReturn(Optional.of(station(2L, "Gare de Lyon")));

        assertThatThrownBy(() -> stationService.ajouterStation(new StationFormulaire("Bercy", 4, -4)))
                .isInstanceOf(PositionOccupeeException.class)
                .hasMessageContaining("Gare de Lyon");
    }

    @Test
    void supprimerStationSupprimeLaStationTrouvee() {
        Station existante = station(1L, "La Defense");
        when(stationRepository.findById(1L)).thenReturn(Optional.of(existante));
        stationService.supprimerStation(1L);
        verify(stationRepository).delete(existante);
    }

    @Test
    void obtenirUneStationInexistanteLeveUneException() {
        when(stationRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> stationService.obtenirStationParId(99L)).isInstanceOf(StationIntrouvableException.class);
    }

    @Test
    void obtenirToutesStationsRenvoieLaListe() {
        when(stationRepository.findAll(any(Sort.class)))
                .thenReturn(List.of(station(1L, "Chatelet"), station(2L, "La Defense")));
        assertThat(stationService.obtenirToutesStations()).hasSize(2);
    }
}