package cpar.backend.service;

import cpar.backend.dto.EquipementFormulaire;
import cpar.backend.exception.EquipementIntrouvableException;
import cpar.backend.exception.ReferenceDejaUtiliseeException;
import cpar.backend.logs.GeneralLogger;
import cpar.backend.model.entities.Equipement;
import cpar.backend.model.enums.EtatsEquipement;
import cpar.backend.model.enums.TypeEquipement;
import cpar.backend.repository.EquipementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EquipementServiceTest {

    @Mock
    private EquipementRepository equipementRepository;

    @Mock
    private IStationService stationService;

    @Mock
    private GeneralLogger generalLogger;

    @InjectMocks
    private EquipementService equipementService;

    private static Equipement camera(Long id) {
        Equipement equipement = new Equipement();
        equipement.setId(id);
        equipement.setReference("CAM00001");
        equipement.setType(TypeEquipement.CAMERA);
        equipement.setEtat(EtatsEquipement.EN_SERVICE);
        return equipement;
    }

    @Test
    void ajouterUneReferenceDejaUtiliseeEstRefuse() {
        when(equipementRepository.existsByReference("CAM00001")).thenReturn(true);
        assertThatThrownBy(() -> equipementService.ajouterEquipement(
                new EquipementFormulaire("CAM00001", null, TypeEquipement.CAMERA, null, null)))
                .isInstanceOf(ReferenceDejaUtiliseeException.class);
        verify(equipementRepository, never()).save(any());
    }

    @Test
    void modifierEquipementChangeSesChamps() {
        when(equipementRepository.findWithStationById(1L)).thenReturn(Optional.of(camera(1L)));
        when(equipementRepository.existsByReferenceAndIdNot("ASC00001", 1L)).thenReturn(false);
        Equipement resultat = equipementService.modifierEquipement(1L,
                new EquipementFormulaire("ASC00001", "Ascenseur", TypeEquipement.ASCENSEUR, EtatsEquipement.EN_PANNE, null));
        assertThat(resultat.getReference()).isEqualTo("ASC00001");
        assertThat(resultat.getType()).isEqualTo(TypeEquipement.ASCENSEUR);
        assertThat(resultat.getEtat()).isEqualTo(EtatsEquipement.EN_PANNE);
    }

    @Test
    void changerEtatEquipementNeModifieQueLEtat() {
        when(equipementRepository.findWithStationById(1L)).thenReturn(Optional.of(camera(1L)));
        Equipement resultat = equipementService.changerEtatEquipement(1L, EtatsEquipement.EN_MAINTENANCE);
        assertThat(resultat.getEtat()).isEqualTo(EtatsEquipement.EN_MAINTENANCE);
        assertThat(resultat.getReference()).isEqualTo("CAM00001");
    }

    @Test
    void supprimerEquipementSupprimeLEquipementTrouve() {
        Equipement existant = camera(1L);
        when(equipementRepository.findWithStationById(1L)).thenReturn(Optional.of(existant));
        equipementService.supprimerEquipement(1L);
        verify(equipementRepository).delete(existant);
    }

    @Test
    void obtenirUnEquipementInexistantLeveUneException() {
        when(equipementRepository.findWithStationById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> equipementService.obtenirEquipementParId(99L)).isInstanceOf(EquipementIntrouvableException.class);
    }
}