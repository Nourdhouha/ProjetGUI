package com.voyageaffaires.utils;

import com.voyageaffaires.models.Utilisateur;

/**
 * Manages user session information throughout the application.
 * Stores the currently logged-in user information.
 */
public class SessionManager {
    
    private static SessionManager instance;
    private Utilisateur currentUser;
    
    /**
     * Private constructor to prevent instantiation.
     */
    private SessionManager() {
    }
    
    /**
     * Gets the singleton instance of SessionManager.
     * 
     * @return SessionManager instance
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Sets the current logged-in user.
     * 
     * @param user The user who just logged in
     */
    public void setCurrentUser(Utilisateur user) {
        this.currentUser = user;
    }
    
    /**
     * Gets the current logged-in user.
     * 
     * @return Current user, or null if no user is logged in
     */
    public Utilisateur getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Checks if a user is currently logged in.
     * 
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Clears the current session (logout).
     */
    public void clearSession() {
        this.currentUser = null;
    }
    
    /**
     * Checks if current user has a specific role.
     * 
     * @param role The role to check
     * @return true if user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        return currentUser != null && currentUser.getRole().equalsIgnoreCase(role);
    }
    
    /**
     * Checks if current user is an admin.
     * 
     * @return true if user is admin, false otherwise
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
    
    /**
     * Checks if current user is a manager.
     * 
     * @return true if user is manager or admin, false otherwise
     */
    public boolean isManager() {
        return hasRole("MANAGER") || hasRole("ADMIN");
    }
}
