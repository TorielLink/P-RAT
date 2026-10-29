package cpar.backend.model.enums;

public enum ProfilUtilisateur {
    ADMIN("Administrateur"),
    PRESTATAIRE("Prestataire"),
    AGENT("Agent");

    private final String libelle;

    ProfilUtilisateur(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
