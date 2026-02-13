package com.voyageaffaires.models;

import java.time.LocalDateTime;

/**
 * Represents a user in the system.
 * Base class for Employe and Manager.
 */
public class Utilisateur {
    
    private int idUtilisateur;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String departement;
    private String motDePasse;
    private String role;  // EMPLOYE, MANAGER, ADMIN
    private LocalDateTime dateCreation;
    private boolean actif;
    
    // Constructors
    public Utilisateur() {
        this.actif = true;
        this.dateCreation = LocalDateTime.now();
    }
    
    public Utilisateur(String nom, String prenom, String email, String telephone, 
                       String departement, String motDePasse, String role) {
        this();
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.departement = departement;
        this.motDePasse = motDePasse;
        this.role = role;
    }
    
    // Getters and Setters
    public int getIdUtilisateur() {
        return idUtilisateur;
    }
    
    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    public String getDepartement() {
        return departement;
    }
    
    public void setDepartement(String departement) {
        this.departement = departement;
    }
    
    public String getMotDePasse() {
        return motDePasse;
    }
    
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public boolean isActif() {
        return actif;
    }
    
    public void setActif(boolean actif) {
        this.actif = actif;
    }
    
    /**
     * Returns the full name of the user.
     * 
     * @return Full name (prenom + nom)
     */
    public String getFullName() {
        return prenom + " " + nom;
    }
    
    /**
     * Checks if user has admin privileges.
     * 
     * @return true if user is admin, false otherwise
     */
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
    
    /**
     * Checks if user has manager privileges.
     * 
     * @return true if user is manager or admin, false otherwise
     */
    public boolean isManager() {
        return "MANAGER".equalsIgnoreCase(role) || isAdmin();
    }
    
    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + idUtilisateur +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
