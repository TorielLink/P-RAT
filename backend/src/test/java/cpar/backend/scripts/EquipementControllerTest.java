package cpar.backend.scripts;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cpar.backend.controller.EquipementController;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cpar.backend.model.Equipement;
import cpar.backend.service.IEquipementService;

class EquipementControllerTest {

    private final IEquipementService service = mock(IEquipementService.class);
    private final MockMvc mockMvc =
            MockMvcBuilders.standaloneSetup(new EquipementController(service)).build();

    @Test
    void post_retourne201EtAppelleLeService() throws Exception {
        when(service.addEquipement(any(Equipement.class))).thenReturn(new Equipement());

        mockMvc.perform(post("/api/equipements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reference":"REF-001","libelle":"Test",
                                 "station":"Station A","etat":"OK","type":"X"}
                                """))
                .andExpect(status().isCreated());

        verify(service).addEquipement(any(Equipement.class));
    }
}
