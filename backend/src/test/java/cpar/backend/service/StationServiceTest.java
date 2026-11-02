package cpar.backend.service;

import cpar.backend.exception.StationIntrouvableException;
import cpar.backend.model.entities.Station;
import cpar.backend.repository.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

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
        Station resultat = stationService.ajouterStation("  La Defense  ");
        assertThat(resultat.getNom()).isEqualTo("La Defense");
    }

    @Test
    void modifierStationChangeLeNom() {
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station(1L, "Ancien nom")));
        Station resultat = stationService.modifierStation(1L, "Nouveau nom");
        assertThat(resultat.getNom()).isEqualTo("Nouveau nom");
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