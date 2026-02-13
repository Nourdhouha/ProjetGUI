package com.voyageaffaires.controllers;

import com.voyageaffaires.services.AuthService;
import com.voyageaffaires.utils.AlertUtil;
import com.voyageaffaires.utils.DatabaseConnection;
import com.voyageaffaires.utils.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller for the Login screen.
 */
public class LoginController {
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button loginButton;
    
    private AuthService authService;
    
    /**
     * Initializes the controller.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        authService = new AuthService();
        
        // Test database connection on startup
        testDatabaseConnection();
        
        // Add listeners for clearing error message
        emailField.textProperty().addListener((obs, oldVal, newVal) -> clearError());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> clearError());
    }
    
    /**
     * Handles the login button click.
     */
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        // Validate inputs
        if (email.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }
        
        // Attempt login
        boolean success = authService.login(email, password);
        
        if (success) {
            System.out.println("Login successful for: " + email);
            navigateToDashboard();
        } else {
            showError("Email ou mot de passe incorrect.");
        }
    }
    
    /**
     * Navigates to the dashboard screen.
     */
    private void navigateToDashboard() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/fxml/DashboardView.fxml", 
                                     "Voyage & Affaires - Dashboard", 1280, 800);
        } catch (Exception e) {
            System.err.println("Error navigating to dashboard: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Erreur", "Erreur de Navigation", 
                              "Impossible d'ouvrir le tableau de bord.");
        }
    }
    
    /**
     * Shows an error message.
     * 
     * @param message Error message to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    /**
     * Clears the error message.
     */
    private void clearError() {
        errorLabel.setVisible(false);
    }
    
    /**
     * Tests database connection on startup.
     */
    private void testDatabaseConnection() {
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        if (!dbConnection.testConnection()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("Connexion à la base de données");
            alert.setContentText("Impossible de se connecter à la base de données.\n\n" +
                               "Veuillez vérifier que:\n" +
                               "1. MySQL est démarré\n" +
                               "2. La base de données 'voyage_affaires' existe\n" +
                               "3. Les identifiants dans database.properties sont corrects");
            alert.showAndWait();
        }
    }
}
