package com.voyageaffaires.services;

import com.voyageaffaires.dao.UtilisateurDAO;
import com.voyageaffaires.models.Utilisateur;
import com.voyageaffaires.utils.PasswordUtil;
import com.voyageaffaires.utils.ValidationUtil;
import java.util.List;

/**
 * Service class for user management operations.
 */
public class UserService {
    
    private UtilisateurDAO utilisateurDAO;
    
    public UserService() {
        this.utilisateurDAO = new UtilisateurDAO();
    }
    
    /**
     * Retrieves all users.
     * 
     * @return List of all users
     */
    public List<Utilisateur> getAllUsers() {
        return utilisateurDAO.findAll();
    }
    
    /**
     * Finds a user by ID.
     * 
     * @param id User ID
     * @return User if found, null otherwise
     */
    public Utilisateur getUserById(int id) {
        return utilisateurDAO.findById(id);
    }
    
    /**
     * Creates a new user (password already hashed).
     * 
     * @param user User to create (with hashed password)
     * @return true if successful, false otherwise
     */
    public boolean createUser(Utilisateur user) {
        // Validate user data
        String validationError = validateUser(user);
        if (validationError != null) {
            System.err.println("Validation error: " + validationError);
            return false;
        }
        
        // Check if email already exists
        if (utilisateurDAO.emailExists(user.getEmail())) {
            System.err.println("Email already exists: " + user.getEmail());
            return false;
        }
        
        // Save user
        return utilisateurDAO.save(user);
    }
    
    /**
     * Creates a new user with plain text password.
     * 
     * @param user User to create
     * @param plainPassword Plain text password
     * @return true if successful, false otherwise
     */
    public boolean createUser(Utilisateur user, String plainPassword) {
        // Validate password
        if (!PasswordUtil.isPasswordStrong(plainPassword)) {
            System.err.println("Password is not strong enough");
            return false;
        }
        
        // Hash password
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        user.setMotDePasse(hashedPassword);
        
        return createUser(user);
    }
    
    /**
     * Checks if an email already exists.
     * 
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        return utilisateurDAO.emailExists(email);
    }
    
    /**
     * Updates an existing user.
     * 
     * @param user User to update
     * @return true if successful, false otherwise
     */
    public boolean updateUser(Utilisateur user) {
        // Validate user data
        String validationError = validateUser(user);
        if (validationError != null) {
            System.err.println("Validation error: " + validationError);
            return false;
        }
        
        return utilisateurDAO.update(user);
    }
    
    /**
     * Deletes a user.
     * 
     * @param userId User ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(int userId) {
        return utilisateurDAO.delete(userId);
    }
    
    /**
     * Searches users by keyword.
     * 
     * @param keyword Search keyword
     * @return List of matching users
     */
    public List<Utilisateur> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllUsers();
        }
        return utilisateurDAO.search(keyword.trim());
    }
    
    /**
     * Validates user data.
     * 
     * @param user User to validate
     * @return Error message if validation fails, null otherwise
     */
    private String validateUser(Utilisateur user) {
        if (!ValidationUtil.isNotEmpty(user.getNom())) {
            return "Le nom est obligatoire";
        }
        
        if (!ValidationUtil.isNotEmpty(user.getPrenom())) {
            return "Le prénom est obligatoire";
        }
        
        if (!ValidationUtil.isValidEmail(user.getEmail())) {
            return "Email invalide";
        }
        
        // Phone is optional, but if provided must be valid
        if (user.getTelephone() != null && !user.getTelephone().trim().isEmpty()) {
            if (!ValidationUtil.isValidPhone(user.getTelephone())) {
                return "Numéro de téléphone invalide (format: 8 chiffres ou +216xxxxxxxx)";
            }
        }
        
        if (!ValidationUtil.isNotEmpty(user.getRole())) {
            return "Le rôle est obligatoire";
        }
        
        return null;
    }
    
    /**
     * Gets the last validation error message.
     * 
     * @return Last validation error or null
     */
    public String getLastValidationError(Utilisateur user) {
        return validateUser(user);
    }
}
