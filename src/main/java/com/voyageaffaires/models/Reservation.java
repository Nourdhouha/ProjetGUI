package com.voyageaffaires.models;

import java.time.LocalDate;

/**
 * Represents a travel reservation in the system.
 */
public class Reservation {
    
    private String idReservation;
    private LocalDate dateCreation;
    private String statut;  // EN_ATTENTE, APPROUVEE, REJETEE, ANNULEE, COMPLETEE
    private double montantTotal;
    private int idUtilisateur;
    private String idVol;
    private String idHotel;
    private LocalDate dateCheckin;
    private LocalDate dateCheckout;
    private int nombreChambres;
    private String motifVoyage;
    private String commentaire;
    
    // Additional fields for display (not in database)
    private String utilisateurNom;
    private Vol vol;
    private Hotel hotel;
    
    // Constructors
    public Reservation() {
        this.dateCreation = LocalDate.now();
        this.statut = "EN_ATTENTE";
        this.nombreChambres = 1;
    }
    
    public Reservation(String idReservation, int idUtilisateur, double montantTotal) {
        this();
        this.idReservation = idReservation;
        this.idUtilisateur = idUtilisateur;
        this.montantTotal = montantTotal;
    }
    
    // Getters and Setters
    public String getIdReservation() {
        return idReservation;
    }
    
    public void setIdReservation(String idReservation) {
        this.idReservation = idReservation;
    }
    
    public LocalDate getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public double getMontantTotal() {
        return montantTotal;
    }
    
    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }
    
    public int getIdUtilisateur() {
        return idUtilisateur;
    }
    
    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
    
    public String getIdVol() {
        return idVol;
    }
    
    public void setIdVol(String idVol) {
        this.idVol = idVol;
    }
    
    public String getIdHotel() {
        return idHotel;
    }
    
    public void setIdHotel(String idHotel) {
        this.idHotel = idHotel;
    }
    
    public LocalDate getDateCheckin() {
        return dateCheckin;
    }
    
    public void setDateCheckin(LocalDate dateCheckin) {
        this.dateCheckin = dateCheckin;
    }
    
    public LocalDate getDateCheckout() {
        return dateCheckout;
    }
    
    public void setDateCheckout(LocalDate dateCheckout) {
        this.dateCheckout = dateCheckout;
    }
    
    public int getNombreChambres() {
        return nombreChambres;
    }
    
    public void setNombreChambres(int nombreChambres) {
        this.nombreChambres = nombreChambres;
    }
    
    public String getMotifVoyage() {
        return motifVoyage;
    }
    
    public void setMotifVoyage(String motifVoyage) {
        this.motifVoyage = motifVoyage;
    }
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    
    public String getUtilisateurNom() {
        return utilisateurNom;
    }
    
    public void setUtilisateurNom(String utilisateurNom) {
        this.utilisateurNom = utilisateurNom;
    }
    
    public Vol getVol() {
        return vol;
    }
    
    public void setVol(Vol vol) {
        this.vol = vol;
    }
    
    public Hotel getHotel() {
        return hotel;
    }
    
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }
    
    /**
     * Generates a unique reservation ID.
     * 
     * @return Unique reservation ID
     */
    public static String generateId() {
        return "RES-" + System.currentTimeMillis();
    }
    
    @Override
    public String toString() {
        return "Reservation{" +
                "id='" + idReservation + '\'' +
                ", dateCreation=" + dateCreation +
                ", statut='" + statut + '\'' +
                ", montantTotal=" + montantTotal +
                '}';
    }
}
