package cpar.backend.controller;

import cpar.backend.config.SecurityConfig;
import cpar.backend.dto.UtilisateurConnecteDto;
import cpar.backend.service.UtilisateurDetailsService;
import cpar.backend.service.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    private static final String LOGIN = "agent";
    private static final String MOT_DE_PASSE = "demo123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder encodeurDeMotDePasse;

    @ MockitoBean
    private UtilisateurDetailsService utilisateurDetailsService;

    @MockitoBean
    private UtilisateurService utilisateurService;

    @BeforeEach
    void preparerUnUtilisateurExistant() {
        when(utilisateurDetailsService.loadUserByUsername(LOGIN)).thenReturn(
                User.withUsername(LOGIN)
                        .password(encodeurDeMotDePasse.encode(MOT_DE_PASSE))
                        .roles("AGENT")
                        .build());
    }

    @Test
    void uneConnexionAvecDesIdentifiantsValidesReussit() throws Exception {
        mockMvc.perform(formLogin("/api/auth/login")
                        .userParameter("login").passwordParam("motDePasse")
                        .user(LOGIN).password(MOT_DE_PASSE))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(LOGIN));
    }

    @Test
    void uneConnexionAvecUnMauvaisMotDePasseRenvoie401() throws Exception {
        mockMvc.perform(formLogin("/api/auth/login")
                        .userParameter("login").passwordParam("motDePasse")
                        .user(LOGIN).password("mauvais"))
                .andExpect(status().isUnauthorized())
                .andExpect(unauthenticated());
    }

    @Test
    void unAppelApiSansSessionRenvoie401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = LOGIN, roles = "AGENT")
    void unUtilisateurConnecteRecupereSonPrenomEtSonProfil() throws Exception {
        when(utilisateurService.trouverUtilisateurConnecte(LOGIN))
                .thenReturn(new UtilisateurConnecteDto("Inès", "AGENT", "Agent"));

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prenom").value("Inès"))
                .andExpect(jsonPath("$.profilLibelle").value("Agent"));
    }

    @Test
    @WithMockUser(username = LOGIN)
    void laDeconnexionRenvoie204() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent());
    }
}
