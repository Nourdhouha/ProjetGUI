package com.voyageaffaires.dao;

import com.voyageaffaires.models.Utilisateur;
import com.voyageaffaires.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Utilisateur entity.
 * Handles all database operations for users.
 */
public class UtilisateurDAO {
    
    private Connection connection;
    
    public UtilisateurDAO() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            System.err.println("Error establishing database connection: " + e.getMessage());
        }
    }
    
    /**
     * Finds a user by email.
     * 
     * @param email User email
     * @return Utilisateur object if found, null otherwise
     */
    public Utilisateur findByEmail(String email) {
        String query = "SELECT * FROM utilisateur WHERE email = ? AND actif = TRUE";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUtilisateur(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by email: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Finds a user by ID.
     * 
     * @param id User ID
     * @return Utilisateur object if found, null otherwise
     */
    public Utilisateur findById(int id) {
        String query = "SELECT * FROM utilisateur WHERE id_utilisateur = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUtilisateur(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Retrieves all users from the database.
     * 
     * @return List of all users
     */
    public List<Utilisateur> findAll() {
        List<Utilisateur> users = new ArrayList<>();
        String query = "SELECT * FROM utilisateur WHERE actif = TRUE ORDER BY nom, prenom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUtilisateur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all users: " + e.getMessage());
        }
        return users;
    }
    
    /**
     * Saves a new user to the database.
     * 
     * @param user User to save
     * @return true if successful, false otherwise
     */
    public boolean save(Utilisateur user) {
        String query = "INSERT INTO utilisateur (nom, prenom, email, telephone, departement, mot_de_passe, role) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getTelephone());
            stmt.setString(5, user.getDepartement());
            stmt.setString(6, user.getMotDePasse());
            stmt.setString(7, user.getRole());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setIdUtilisateur(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Updates an existing user in the database.
     * 
     * @param user User to update
     * @return true if successful, false otherwise
     */
    public boolean update(Utilisateur user) {
        String query = "UPDATE utilisateur SET nom = ?, prenom = ?, email = ?, telephone = ?, " +
                      "departement = ?, role = ? WHERE id_utilisateur = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getTelephone());
            stmt.setString(5, user.getDepartement());
            stmt.setString(6, user.getRole());
            stmt.setInt(7, user.getIdUtilisateur());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Updates user password.
     * 
     * @param userId User ID
     * @param newPassword New hashed password
     * @return true if successful, false otherwise
     */
    public boolean updatePassword(int userId, String newPassword) {
        String query = "UPDATE utilisateur SET mot_de_passe = ? WHERE id_utilisateur = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Soft deletes a user (sets actif to false).
     * 
     * @param userId User ID to delete
     * @return true if successful, false otherwise
     */
    public boolean delete(int userId) {
        String query = "UPDATE utilisateur SET actif = FALSE WHERE id_utilisateur = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Searches users by name or email.
     * 
     * @param searchTerm Search term
     * @return List of matching users
     */
    public List<Utilisateur> search(String searchTerm) {
        List<Utilisateur> users = new ArrayList<>();
        String query = "SELECT * FROM utilisateur WHERE actif = TRUE AND " +
                      "(nom LIKE ? OR prenom LIKE ? OR email LIKE ?) ORDER BY nom, prenom";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUtilisateur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching users: " + e.getMessage());
        }
        return users;
    }
    
    /**
     * Checks if an email already exists in the database.
     * 
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Maps a ResultSet row to an Utilisateur object.
     * 
     * @param rs ResultSet
     * @return Utilisateur object
     * @throws SQLException if error occurs
     */
    private Utilisateur mapResultSetToUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur user = new Utilisateur();
        user.setIdUtilisateur(rs.getInt("id_utilisateur"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setEmail(rs.getString("email"));
        user.setTelephone(rs.getString("telephone"));
        user.setDepartement(rs.getString("departement"));
        user.setMotDePasse(rs.getString("mot_de_passe"));
        user.setRole(rs.getString("role"));
        
        Timestamp timestamp = rs.getTimestamp("date_creation");
        if (timestamp != null) {
            user.setDateCreation(timestamp.toLocalDateTime());
        }
        
        user.setActif(rs.getBoolean("actif"));
        return user;
    }
}
