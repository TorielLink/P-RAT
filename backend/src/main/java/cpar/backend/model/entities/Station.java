package cpar.backend.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "station")
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Min(value = -20, message = "La position X doit être comprise entre -20 et 20.")
    @Max(value = 20, message = "La position X doit être comprise entre -20 et 20.")
    @Column(name = "positionX")
    private Integer positionX;

    @Min(value = -20, message = "La position Y doit être comprise entre -20 et 20.")
    @Max(value = 20, message = "La position Y doit être comprise entre -20 et 20.")
    @Column(name = "positionY")
    private Integer positionY;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }


    public void setPositionX(@Min(value = -20, message = "La position X doit être comprise entre -20 et 20.") @Max(value = 20, message = "La position X doit être comprise entre -20 et 20.") Integer integer) {
        this.positionX = integer;
    }

    public Integer getPositionX(){return positionX ;}

    public void setPositionY(@Min(value = -20, message = "La position Y doit être comprise entre -20 et 20.") @Max(value = 20, message = "La position Y doit être comprise entre -20 et 20.") Integer integer) {
        this.positionY = integer;
    }

    public Integer getPositionY(){return positionY ;}
}