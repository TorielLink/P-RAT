package cpar.backend.model.entities;

import cpar.backend.model.enums.PrioriteIncident;
import cpar.backend.model.enums.StatutIncident;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "incident")
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "equipementId", nullable = false)
    private Equipement equipement;

    @Size(max = 2000)
    @Column(name = "description", length = 2000)
    private String description;

    @NotNull
    @Column(name = "dateOuverture", nullable = false)
    private LocalDateTime dateOuverture;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutIncident statut = StatutIncident.OUVERT;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "priorite", length = 20)
    private PrioriteIncident priorite = PrioriteIncident.MOYENNE;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "prestataireId")
    private Prestataire prestataire;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "utilisateurId")
    private Utilisateur utilisateur;

    @Column(name = "dateResolution")
    private LocalDateTime dateResolution;

    /** La durée s'arrête à la résolution : un incident résolu a une durée figée */
    public Duration dureeOuverture(LocalDateTime maintenant) {
        LocalDateTime fin = dateResolution != null ? dateResolution : maintenant;
        return Duration.between(dateOuverture, fin);
    }

    /** Admin corrige tout incident non résolu ; Agent, ses propres incidents encore non affectés */
    public boolean estModifiablePar(Utilisateur demandeur) {
        if (statut == StatutIncident.RESOLU) {
            return false;
        }
        return switch (demandeur.getProfil()) {
            case ADMIN -> true;
            case AGENT -> statut == StatutIncident.OUVERT && estDeclarePar(demandeur);
            case PRESTATAIRE -> false;
        };
    }

    private boolean estDeclarePar(Utilisateur demandeur) {
        return utilisateur != null
                && utilisateur.getId() != null
                && Objects.equals(utilisateur.getId(), demandeur.getId());
    }

    /** l'incident devient affecté + réaffecter au même prestataire ne fait rien */
    public void affecterA(Prestataire nouveauPrestataire) {
        if (estDejaAffecteA(nouveauPrestataire)) {
            return;
        }
        prestataire = nouveauPrestataire;
        statut = StatutIncident.EN_COURS;
    }

    private boolean estDejaAffecteA(Prestataire candidat) {
        return prestataire != null
                && prestataire.getId() != null
                && Objects.equals(prestataire.getId(), candidat.getId());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Equipement getEquipement() {
        return equipement;
    }

    public void setEquipement(Equipement equipement) {
        this.equipement = equipement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(LocalDateTime dateOuverture) {
        this.dateOuverture = dateOuverture;
    }

    public StatutIncident getStatut() {
        return statut;
    }

    public void setStatut(StatutIncident statut) {
        this.statut = statut;
    }

    public PrioriteIncident getPriorite() {
        return priorite;
    }

    public void setPriorite(PrioriteIncident priorite) {
        this.priorite = priorite;
    }

    public Prestataire getPrestataire() {
        return prestataire;
    }

    public void setPrestataire(Prestataire prestataire) {
        this.prestataire = prestataire;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public LocalDateTime getDateResolution() {
        return dateResolution;
    }

    public void setDateResolution(LocalDateTime dateResolution) {
        this.dateResolution = dateResolution;
    }

}