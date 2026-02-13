package com.voyageaffaires.services;

import com.voyageaffaires.dao.UtilisateurDAO;
import com.voyageaffaires.models.Utilisateur;
import com.voyageaffaires.utils.PasswordUtil;
import com.voyageaffaires.utils.SessionManager;

/**
 * Service class for authentication operations.
 */
public class AuthService {
    
    private UtilisateurDAO utilisateurDAO;
    private SessionManager sessionManager;
    
    public AuthService() {
        this.utilisateurDAO = new UtilisateurDAO();
        this.sessionManager = SessionManager.getInstance();
    }
    
    /**
     * Authenticates a user with email and password.
     * 
     * @param email User email
     * @param password Plain text password
     * @return true if authentication successful, false otherwise
     */
    public boolean login(String email, String password) {
        // Validate inputs
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return false;
        }
        
        // Find user by email
        Utilisateur user = utilisateurDAO.findByEmail(email.trim());
        
        if (user == null) {
            System.out.println("User not found: " + email);
            return false;
        }
        
        // Verify password
        if (!PasswordUtil.verifyPassword(password, user.getMotDePasse())) {
            System.out.println("Invalid password for user: " + email);
            return false;
        }
        
        // Set current user in session
        sessionManager.setCurrentUser(user);
        System.out.println("Login successful for: " + user.getFullName());
        
        return true;
    }
    
    /**
     * Logs out the current user.
     */
    public void logout() {
        Utilisateur currentUser = sessionManager.getCurrentUser();
        if (currentUser != null) {
            System.out.println("Logout: " + currentUser.getFullName());
        }
        sessionManager.clearSession();
    }
    
    /**
     * Gets the currently logged-in user.
     * 
     * @return Current user, or null if not logged in
     */
    public Utilisateur getCurrentUser() {
        return sessionManager.getCurrentUser();
    }
    
    /**
     * Checks if a user is currently logged in.
     * 
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }
    
    /**
     * Changes the password for the current user.
     * 
     * @param oldPassword Current password
     * @param newPassword New password
     * @return true if password changed successfully, false otherwise
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        Utilisateur currentUser = sessionManager.getCurrentUser();
        
        if (currentUser == null) {
            return false;
        }
        
        // Verify old password
        if (!PasswordUtil.verifyPassword(oldPassword, currentUser.getMotDePasse())) {
            return false;
        }
        
        // Validate new password
        if (!PasswordUtil.isPasswordStrong(newPassword)) {
            return false;
        }
        
        // Hash new password
        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        
        // Update password in database
        boolean success = utilisateurDAO.updatePassword(currentUser.getIdUtilisateur(), hashedPassword);
        
        if (success) {
            // Update current user object
            currentUser.setMotDePasse(hashedPassword);
        }
        
        return success;
    }
}
