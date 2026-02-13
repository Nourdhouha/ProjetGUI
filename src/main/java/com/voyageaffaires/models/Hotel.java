package com.voyageaffaires.models;

/**
 * Represents a hotel in the system.
 */
public class Hotel {
    
    private String idHotel;
    private String nom;
    private String adresse;
    private String ville;
    private String pays;
    private int etoiles;  // 1-5 stars
    private double prixParNuit;
    private boolean disponible;
    
    // Constructors
    public Hotel() {
        this.disponible = true;
        this.pays = "France";
    }
    
    public Hotel(String idHotel, String nom, String adresse, String ville, 
                 int etoiles, double prixParNuit) {
        this();
        this.idHotel = idHotel;
        this.nom = nom;
        this.adresse = adresse;
        this.ville = ville;
        this.etoiles = etoiles;
        this.prixParNuit = prixParNuit;
    }
    
    // Getters and Setters
    public String getIdHotel() {
        return idHotel;
    }
    
    public void setIdHotel(String idHotel) {
        this.idHotel = idHotel;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    
    public String getVille() {
        return ville;
    }
    
    public void setVille(String ville) {
        this.ville = ville;
    }
    
    public String getPays() {
        return pays;
    }
    
    public void setPays(String pays) {
        this.pays = pays;
    }
    
    public int getEtoiles() {
        return etoiles;
    }
    
    public void setEtoiles(int etoiles) {
        this.etoiles = etoiles;
    }
    
    public double getPrixParNuit() {
        return prixParNuit;
    }
    
    public void setPrixParNuit(double prixParNuit) {
        this.prixParNuit = prixParNuit;
    }
    
    public boolean isDisponible() {
        return disponible;
    }
    
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
    /**
     * Returns a star rating string (e.g., "⭐⭐⭐⭐⭐").
     * 
     * @return Star rating as string
     */
    public String getStarRating() {
        return "⭐".repeat(Math.max(0, etoiles));
    }
    
    /**
     * Returns the full location of the hotel.
     * 
     * @return Location (e.g., "Paris, France")
     */
    public String getLocation() {
        return ville + ", " + pays;
    }
    
    @Override
    public String toString() {
        return "Hotel{" +
                "id='" + idHotel + '\'' +
                ", nom='" + nom + '\'' +
                ", ville='" + ville + '\'' +
                ", etoiles=" + etoiles +
                ", prix=" + prixParNuit +
                '}';
    }
}
