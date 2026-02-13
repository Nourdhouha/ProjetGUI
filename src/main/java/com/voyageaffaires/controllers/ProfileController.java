package com.voyageaffaires.controllers;

import com.voyageaffaires.models.Utilisateur;
import com.voyageaffaires.services.UserService;
import com.voyageaffaires.utils.AlertUtil;
import com.voyageaffaires.utils.NavigationUtil;
import com.voyageaffaires.utils.SessionManager;
import com.voyageaffaires.utils.ValidationUtil;
import com.voyageaffaires.utils.PasswordUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller for the Profile screen.
 * Allows employees to view and edit their own information.
 */
public class ProfileController {
    
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelephone;
    @FXML private TextField txtDepartement;
    @FXML private PasswordField txtCurrentPassword;
    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Label lblMessage;
    @FXML private Label lblRole;
    @FXML private Label lblStatut;
    @FXML private Label lblUserId;
    
    private UserService userService;
    private Utilisateur currentUser;
    
    @FXML
    public void initialize() {
        userService = new UserService();
        currentUser = SessionManager.getInstance().getCurrentUser();
        
        if (currentUser != null) {
            loadUserData();
        } else {
            AlertUtil.showError("Erreur", "Session expirée", 
                              "Votre session a expiré. Veuillez vous reconnecter.");
            goBack();
        }
    }
    
    /**
     * Loads current user data into form fields.
     */
    private void loadUserData() {
        txtNom.setText(currentUser.getNom());
        txtPrenom.setText(currentUser.getPrenom());
        txtEmail.setText(currentUser.getEmail());
        txtTelephone.setText(currentUser.getTelephone());
        txtDepartement.setText(currentUser.getDepartement());
        
        lblRole.setText(currentUser.getRole());
        lblStatut.setText(currentUser.isActif() ? "Actif" : "Inactif");
        lblUserId.setText(String.valueOf(currentUser.getIdUtilisateur()));
        
        // Set status color
        if (currentUser.isActif()) {
            lblStatut.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
        } else {
            lblStatut.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        }
    }
    
    /**
     * Handles save button click.
     */
    @FXML
    private void handleSave() {
        hideMessage();
        
        // Get form values
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String email = txtEmail.getText().trim();
        String telephone = txtTelephone.getText().trim();
        String departement = txtDepartement.getText().trim();
        String currentPassword = txtCurrentPassword.getText();
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();
        
        // Validate required fields
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty()) {
            showErrorMessage("Tous les champs marqués avec * sont obligatoires.");
            return;
        }
        
        // Validate email
        if (!ValidationUtil.isValidEmail(email)) {
            showErrorMessage("Format d'email invalide.");
            return;
        }
        
        // Validate phone
        if (!ValidationUtil.isValidPhone(telephone)) {
            showErrorMessage("Format de téléphone invalide. Utilisez 8 chiffres ou +216 suivi de 8 chiffres.");
            return;
        }
        
        // Check if password is being changed
        boolean changingPassword = !currentPassword.isEmpty() || !newPassword.isEmpty() || !confirmPassword.isEmpty();
        
        if (changingPassword) {
            // Validate password change
            if (currentPassword.isEmpty()) {
                showErrorMessage("Veuillez saisir votre mot de passe actuel.");
                return;
            }
            
            // Verify current password
            if (!PasswordUtil.verifyPassword(currentPassword, currentUser.getMotDePasse())) {
                showErrorMessage("Le mot de passe actuel est incorrect.");
                return;
            }
            
            if (newPassword.isEmpty()) {
                showErrorMessage("Veuillez saisir un nouveau mot de passe.");
                return;
            }
            
            if (!PasswordUtil.isPasswordStrong(newPassword)) {
                showErrorMessage("Le mot de passe doit contenir au moins 6 caractères.");
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                showErrorMessage("Les mots de passe ne correspondent pas.");
                return;
            }
        }
        
        // Update user object
        currentUser.setNom(nom);
        currentUser.setPrenom(prenom);
        currentUser.setEmail(email);
        currentUser.setTelephone(telephone);
        currentUser.setDepartement(departement);
        
        // Update password if changed
        if (changingPassword) {
            String hashedPassword = PasswordUtil.hashPassword(newPassword);
            currentUser.setMotDePasse(hashedPassword);
        }
        
        // Save to database
        boolean success = userService.updateUser(currentUser);
        
        if (success) {
            // Update session
            SessionManager.getInstance().setCurrentUser(currentUser);
            
            AlertUtil.showSuccess("Succès", "Votre profil a été mis à jour avec succès.");
            
            // Clear password fields
            txtCurrentPassword.clear();
            txtNewPassword.clear();
            txtConfirmPassword.clear();
            
            // Reload data
            loadUserData();
        } else {
            String error = userService.getLastValidationError(currentUser);
            if (error != null && !error.isEmpty()) {
                showErrorMessage(error);
            } else {
                showErrorMessage("Erreur lors de la mise à jour du profil. Veuillez réessayer.");
            }
        }
    }
    
    /**
     * Handles cancel button click.
     */
    @FXML
    private void handleCancel() {
        // Reload original data
        loadUserData();
        
        // Clear password fields
        txtCurrentPassword.clear();
        txtNewPassword.clear();
        txtConfirmPassword.clear();
        
        hideMessage();
        
        AlertUtil.showInfo("Annulé", "Modifications annulées", 
                          "Les modifications ont été annulées.");
    }
    
    /**
     * Shows error message.
     */
    private void showErrorMessage(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-padding: 12; -fx-background-radius: 8;");
        lblMessage.setVisible(true);
        lblMessage.setManaged(true);
    }
    
    /**
     * Shows success message.
     */
    private void showSuccessMessage(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-background-color: #d1fae5; -fx-text-fill: #059669; -fx-padding: 12; -fx-background-radius: 8;");
        lblMessage.setVisible(true);
        lblMessage.setManaged(true);
    }
    
    /**
     * Hides message.
     */
    private void hideMessage() {
        lblMessage.setVisible(false);
        lblMessage.setManaged(false);
    }
    
    /**
     * Returns to dashboard.
     */
    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) txtNom.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/fxml/DashboardView.fxml", "Dashboard", 1280, 800);
        } catch (Exception e) {
            System.err.println("Error navigating back: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
