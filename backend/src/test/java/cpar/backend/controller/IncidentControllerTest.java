package cpar.backend.controller;

import cpar.backend.config.SecurityConfig;
import cpar.backend.dto.IncidentDetailDto;
import cpar.backend.exception.EquipementIntrouvableException;
import cpar.backend.exception.IncidentIntrouvableException;
import cpar.backend.model.entities.Equipement;
import cpar.backend.model.entities.Incident;
import cpar.backend.model.enums.StatutIncident;
import cpar.backend.service.IIncidentService;
import cpar.backend.service.UtilisateurDetailsService;
import cpar.backend.dto.ModificationIncidentRequest;
import cpar.backend.exception.IncidentDejaResoluException;
import cpar.backend.exception.OperationInterditeException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IncidentController.class)
@Import(SecurityConfig.class)
class IncidentControllerTest {

    private static final String URL_DECLARATION = "/api/equipements/1/incidents";
    private static final String DEMANDE_VALIDE = """
            { "description": "La caméra ne répond plus depuis ce matin", "priorite": "HAUTE" }""";

    @Autowired private MockMvc mockMvc;
    @MockitoBean private IIncidentService incidentService;
    @MockitoBean private UtilisateurDetailsService utilisateurDetailsService;

    @Test
    @WithMockUser(roles = "AGENT")
    void uneDeclarationValideRenvoie201AvecLocation() throws Exception {
        Incident cree = new Incident();
        cree.setId(42L);
        cree.setEquipement(new Equipement());
        when(incidentService.declarerIncident(eq(1L), any(), anyString())).thenReturn(cree);

        mockMvc.perform(post(URL_DECLARATION).contentType(MediaType.APPLICATION_JSON).content(DEMANDE_VALIDE))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/incidents/42"));
    }

    @Test
    @WithMockUser(roles = "AGENT")
    void uneDescriptionTropCourteRenvoie400AvecUnMessage() throws Exception {
        mockMvc.perform(post(URL_DECLARATION).contentType(MediaType.APPLICATION_JSON).content("""
                        { "description": "court", "priorite": "HAUTE" }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Certaines données saisies sont invalides."))
                .andExpect(jsonPath("$.erreurs.description")
                        .value("La description doit contenir entre 10 et 500 caractères."));
        verifyNoInteractions(incidentService);
    }

    @Test
    @WithMockUser(roles = "AGENT")
    void uneDescriptionVideRenvoie400EtRienNEstEnregistre() throws Exception {
        mockMvc.perform(post(URL_DECLARATION).contentType(MediaType.APPLICATION_JSON).content("""
                        { "description": "   ", "priorite": "HAUTE" }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreurs.description").isNotEmpty());
        verifyNoInteractions(incidentService);
    }

    @Test
    @WithMockUser(roles = "AGENT")
    void unEquipementInexistantRenvoie404() throws Exception {
        when(incidentService.declarerIncident(eq(999L), any(), anyString()))
                .thenThrow(new EquipementIntrouvableException(999L));

        mockMvc.perform(post("/api/equipements/999/incidents")
                        .contentType(MediaType.APPLICATION_JSON).content(DEMANDE_VALIDE))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "PRESTATAIRE")
    void unPrestataireRecoit403EtRienNEstEnregistre() throws Exception {
        mockMvc.perform(post(URL_DECLARATION).contentType(MediaType.APPLICATION_JSON).content(DEMANDE_VALIDE))
                .andExpect(status().isForbidden());
        verifyNoInteractions(incidentService);
    }

    @Test
    void unUtilisateurNonConnecteRecoit401() throws Exception {
        mockMvc.perform(post(URL_DECLARATION).contentType(MediaType.APPLICATION_JSON).content(DEMANDE_VALIDE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "AGENT")
    void leDetailEstAplatiAvecDureeEtNiveauDAttention() throws Exception {
        LocalDateTime maintenant = LocalDateTime.of(2026, 10, 30, 10, 0);
        Incident incident = new Incident();
        incident.setId(1L);
        incident.setEquipement(new Equipement());
        incident.setDescription("Image figée sur le moniteur");
        incident.setStatut(StatutIncident.OUVERT);
        incident.setDateOuverture(maintenant.minusHours(2));
        when(incidentService.obtenirDetailIncident(eq(1L), anyString()))
                .thenReturn(IncidentDetailDto.depuis(incident, null, maintenant, true));

        mockMvc.perform(get("/api/incidents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Image figée sur le moniteur"))
                .andExpect(jsonPath("$.dureeOuvertureMinutes").value(120))
                .andExpect(jsonPath("$.niveauAttention").value("VERT"))
                .andExpect(jsonPath("$.modifiable").value(true));
    }

    @Test
    @WithMockUser(roles = "AGENT")
    void unIncidentInexistantRenvoie404AvecUnMessage() throws Exception {
        when(incidentService.obtenirDetailIncident(eq(999L), anyString()))
                .thenThrow(new IncidentIntrouvableException(999L));

        mockMvc.perform(get("/api/incidents/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Aucun incident ne correspond à l'identifiant 999."));
    }

    @Test
    @WithMockUser(roles = "PRESTATAIRE")
    void unPrestataireSurLIncidentDUnAutreRecoit404() throws Exception {
        when(incidentService.obtenirDetailIncident(eq(5L), anyString()))
                .thenThrow(new IncidentIntrouvableException(5L));

        mockMvc.perform(get("/api/incidents/5"))
                .andExpect(status().isNotFound());
    }

    // -------- MODIFICATION --------

    private static final String MODIFICATION_VALIDE = """
        { "description": "Image figée sur le moniteur du poste", "priorite": "HAUTE" }""";

    private static Incident incidentRenvoye() {
        Incident incident = new Incident();
        incident.setId(1L);
        incident.setEquipement(new Equipement());
        return incident;
    }

    @Test
    @WithMockUser(roles = "AGENT")
    void uneModificationValideRenvoie200() throws Exception {
        when(incidentService.modifierIncident(eq(1L), any(), anyString())).thenReturn(incidentRenvoye());

        mockMvc.perform(patch("/api/incidents/1").contentType(MediaType.APPLICATION_JSON).content(MODIFICATION_VALIDE))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "AGENT")
    void lesChampsNonModifiablesEnvoyesSontIgnores() throws Exception {
        when(incidentService.modifierIncident(eq(1L), any(), anyString())).thenReturn(incidentRenvoye());

        mockMvc.perform(patch("/api/incidents/1").contentType(MediaType.APPLICATION_JSON).content("""
                    { "description": "Image figée sur le moniteur du poste",
                      "statut": "RESOLU", "prestataireId": 3, "dateOuverture": "2020-01-01T00:00:00" }"""))
                .andExpect(status().isOk());

        ArgumentCaptor<ModificationIncidentRequest> demande = ArgumentCaptor.forClass(ModificationIncidentRequest.class);
        verify(incidentService).modifierIncident(eq(1L), demande.capture(), anyString());
        assertEquals(new ModificationIncidentRequest("Image figée sur le moniteur du poste", null), demande.getValue());
    }

    @Test
    @WithMockUser(roles = "AGENT")
    void uneDescriptionTropCourteEnModificationRenvoie400() throws Exception {
        mockMvc.perform(patch("/api/incidents/1").contentType(MediaType.APPLICATION_JSON).content("""
                    { "description": "court" }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreurs.description")
                        .value("La description doit contenir entre 10 et 500 caractères."));
        verifyNoInteractions(incidentService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void modifierUnIncidentResoluRenvoie409() throws Exception {
        when(incidentService.modifierIncident(eq(1L), any(), anyString())).thenThrow(new IncidentDejaResoluException(1L));

        mockMvc.perform(patch("/api/incidents/1").contentType(MediaType.APPLICATION_JSON).content(MODIFICATION_VALIDE))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("L'incident 1 est résolu : il ne peut plus être modifié."));
    }

    @Test
    @WithMockUser(roles = "AGENT")
    void unAgentSurLIncidentDUnAutreRecoit403AvecMessage() throws Exception {
        when(incidentService.modifierIncident(eq(1L), any(), anyString())).thenThrow(new OperationInterditeException(
                "Vous ne pouvez modifier que les incidents non affectés que vous avez déclarés."));

        mockMvc.perform(patch("/api/incidents/1").contentType(MediaType.APPLICATION_JSON).content(MODIFICATION_VALIDE))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail")
                        .value("Vous ne pouvez modifier que les incidents non affectés que vous avez déclarés."));
    }

    @Test
    @WithMockUser(roles = "PRESTATAIRE")
    void unPrestataireNePeutPasModifier() throws Exception {
        mockMvc.perform(patch("/api/incidents/1").contentType(MediaType.APPLICATION_JSON).content(MODIFICATION_VALIDE))
                .andExpect(status().isForbidden());
        verifyNoInteractions(incidentService);
    }

    @Test
    @WithMockUser(roles = "AGENT")
    void modifierUnIncidentInexistantRenvoie404() throws Exception {
        when(incidentService.modifierIncident(eq(999L), any(), anyString()))
                .thenThrow(new IncidentIntrouvableException(999L));

        mockMvc.perform(patch("/api/incidents/999").contentType(MediaType.APPLICATION_JSON).content(MODIFICATION_VALIDE))
                .andExpect(status().isNotFound());
    }
}