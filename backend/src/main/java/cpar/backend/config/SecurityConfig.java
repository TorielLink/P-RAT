package cpar.backend.config;

import cpar.backend.service.UtilisateurDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filtreDeSecurite(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(autorisations -> autorisations
                        .requestMatchers("/api/auth/login").permitAll()
                        // Documentation OpenAPI réservée aux utilisateurs connectés
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").authenticated()
                        // Admin et prestataire (sur ces incidents) peuvent le resoudre
                        .requestMatchers(HttpMethod.PATCH, "/api/incidents/*/resolution").hasAnyRole("ADMIN", "PRESTATAIRE")
                        // Acces tableau de bord
                        .requestMatchers(HttpMethod.GET, "/api/incidents/actifs").hasAnyRole("ADMIN", "PRESTATAIRE")
                        // Déclaration = Admin et Agent
                        .requestMatchers(HttpMethod.POST, "/api/equipements/*/incidents").hasAnyRole("ADMIN", "AGENT")
                        // Lecture des stations pour tt le monde
                        .requestMatchers(HttpMethod.GET, "/api/stations/**").authenticated()
                        // Restraint les autres droits de station
                        .requestMatchers("/api/stations/**").hasRole("ADMIN")
                        // Lecture des equipemts pour tt le monde
                        .requestMatchers(HttpMethod.GET, "/api/equipements/**").authenticated()
                        // Modification de l'etat d'un equipement par tt le monde
                        .requestMatchers(HttpMethod.PATCH, "/api/equipements/*/etat").hasAnyRole("ADMIN", "AGENT", "PRESTATAIRE")
                        // Restraint les autres droits d'equipement
                        .requestMatchers("/api/equipements/**").hasAnyRole("ADMIN", "AGENT")
                        // Lecture pour tous (le service filtre pour le prestataire)
                        .requestMatchers(HttpMethod.GET, "/api/incidents/**").authenticated()
                        // Modification = admin, et agent sur ses incidents
                        .requestMatchers(HttpMethod.PATCH, "/api/incidents/*").hasAnyRole("ADMIN", "AGENT")
                        // Restraint les autres droits d'incident
                        .requestMatchers("/api/incidents/**").hasRole("ADMIN")
                        // Seul Admin peut acceder aux prestataires
                        .requestMatchers("/api/prestataires/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(formulaire -> formulaire
                        .loginProcessingUrl("/api/auth/login")
                        .usernameParameter("login")
                        .passwordParameter("motDePasse")
                        .successHandler((requete, reponse, authentification) ->
                                reponse.setStatus(HttpStatus.OK.value()))
                        .failureHandler((requete, reponse, exception) ->
                                reponse.setStatus(HttpStatus.UNAUTHORIZED.value())))
                .logout(deconnexion -> deconnexion
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((requete, reponse, exception) ->
                                reponse.setStatus(HttpStatus.FORBIDDEN.value())));

        return http.build();
    }

    @Bean
    public PasswordEncoder encodeurDeMotDePasse() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider fournisseurAuthentification(
            UtilisateurDetailsService utilisateurDetailsService,
            PasswordEncoder encodeurDeMotDePasse) {
        DaoAuthenticationProvider fournisseur = new DaoAuthenticationProvider(utilisateurDetailsService);
        fournisseur.setPasswordEncoder(encodeurDeMotDePasse);
        return fournisseur;
    }
}
