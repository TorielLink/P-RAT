package cpar.backend.model.entitys;

import cpar.backend.model.enums.EtatsEquipement;
import cpar.backend.model.enums.TypeEquipement;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "equipement")
public class Equipement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 8)
    @Column(name = "reference", length = 8)
    private String reference;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "type", nullable = false, length = 30)
    private TypeEquipement type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "etat", nullable = false, length = 30)
    private EtatsEquipement etat = EtatsEquipement.EN_SERVICE;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "stationId")
    private Station station;

    @NotNull
    @Column(name = "dateCreation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeEquipement getType() {
        return type;
    }

    public void setType(TypeEquipement type) {
        this.type = type;
    }

    public EtatsEquipement getEtat() { return etat;}

    public void setEtat(EtatsEquipement etat) { this.etat = etat;}

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

}