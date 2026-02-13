package com.voyageaffaires.models;

import java.time.LocalDateTime;

/**
 * Represents a flight in the system.
 */
public class Vol {
    
    private String idVol;
    private String compagnie;
    private String origine;
    private String destination;
    private LocalDateTime dateDepart;
    private LocalDateTime dateArrivee;
    private double prix;
    private int nombrePlaces;
    private String classe;  // ECONOMIE, BUSINESS, PREMIERE
    private boolean disponible;
    
    // Constructors
    public Vol() {
        this.disponible = true;
        this.classe = "ECONOMIE";
    }
    
    public Vol(String idVol, String compagnie, String origine, String destination,
               LocalDateTime dateDepart, LocalDateTime dateArrivee, double prix, int nombrePlaces) {
        this();
        this.idVol = idVol;
        this.compagnie = compagnie;
        this.origine = origine;
        this.destination = destination;
        this.dateDepart = dateDepart;
        this.dateArrivee = dateArrivee;
        this.prix = prix;
        this.nombrePlaces = nombrePlaces;
    }
    
    // Getters and Setters
    public String getIdVol() {
        return idVol;
    }
    
    public void setIdVol(String idVol) {
        this.idVol = idVol;
    }
    
    public String getCompagnie() {
        return compagnie;
    }
    
    public void setCompagnie(String compagnie) {
        this.compagnie = compagnie;
    }
    
    public String getOrigine() {
        return origine;
    }
    
    public void setOrigine(String origine) {
        this.origine = origine;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public LocalDateTime getDateDepart() {
        return dateDepart;
    }
    
    public void setDateDepart(LocalDateTime dateDepart) {
        this.dateDepart = dateDepart;
    }
    
    public LocalDateTime getDateArrivee() {
        return dateArrivee;
    }
    
    public void setDateArrivee(LocalDateTime dateArrivee) {
        this.dateArrivee = dateArrivee;
    }
    
    public double getPrix() {
        return prix;
    }
    
    public void setPrix(double prix) {
        this.prix = prix;
    }
    
    public int getNombrePlaces() {
        return nombrePlaces;
    }
    
    public void setNombrePlaces(int nombrePlaces) {
        this.nombrePlaces = nombrePlaces;
    }
    
    public String getClasse() {
        return classe;
    }
    
    public void setClasse(String classe) {
        this.classe = classe;
    }
    
    public boolean isDisponible() {
        return disponible;
    }
    
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
    /**
     * Returns a formatted string of the flight route.
     * 
     * @return Flight route (e.g., "Paris CDG → London Heathrow")
     */
    public String getRoute() {
        return origine + " → " + destination;
    }
    
    @Override
    public String toString() {
        return "Vol{" +
                "id='" + idVol + '\'' +
                ", compagnie='" + compagnie + '\'' +
                ", route='" + getRoute() + '\'' +
                ", prix=" + prix +
                '}';
    }
}
