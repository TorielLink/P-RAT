package cpar.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "equipement")
public class Equipement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotNull
    @Column(name = "reference", nullable = false, length = 50)
    private String reference;

    @Size(max = 100)
    @NotNull
    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;

    @Lob
    @Column(name = "description")
    private String description;

    @Size(max = 50)
    @NotNull
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Size(max = 100)
    @NotNull
    @Column(name = "station", nullable = false, length = 100)
    private String station;

    @NotNull
    @ColumnDefault("'ACTIF'")
    @Lob
    @Column(name = "etat", nullable = false)
    private String etat;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Equipement() { }

    public Equipement(Long id, String reference, String libelle, String description, String type, String station, String etat, LocalDateTime dateCreation) {
        this.id = id;
        this.reference = reference;
        this.libelle = libelle;
        this.description = description;
        this.type = type;
        this.station = station;
        this.etat = etat;
        this.dateCreation = dateCreation;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    @PrePersist
    void avantCreation() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
    }
}