package com.voyageaffaires.controllers;

import com.voyageaffaires.models.Utilisateur;
import com.voyageaffaires.services.UserService;
import com.voyageaffaires.utils.AlertUtil;
import com.voyageaffaires.utils.PasswordUtil;
import com.voyageaffaires.utils.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller for User Add/Edit form.
 */
public class UserFormController {
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private TextField nomField;
    
    @FXML
    private TextField prenomField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField telephoneField;
    
    @FXML
    private TextField departementField;
    
    @FXML
    private Label passwordLabel;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private ComboBox<String> roleComboBox;
    
    @FXML
    private CheckBox actifCheckBox;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button saveButton;
    
    private UserService userService;
    private Utilisateur userToEdit;
    private boolean isEditMode = false;
    
    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        userService = new UserService();
        
        // Populate role combobox
        roleComboBox.getItems().addAll("EMPLOYE", "MANAGER", "ADMIN");
        roleComboBox.setValue("EMPLOYE");
    }
    
    /**
     * Sets the user to edit (for edit mode).
     * 
     * @param user User to edit
     */
    public void setUser(Utilisateur user) {
        this.userToEdit = user;
        this.isEditMode = true;
        
        // Update title
        titleLabel.setText("Modifier l'Utilisateur");
        
        // Hide password field in edit mode
        passwordLabel.setVisible(false);
        passwordField.setVisible(false);
        passwordField.setManaged(false);
        
        // Populate fields
        nomField.setText(user.getNom());
        prenomField.setText(user.getPrenom());
        emailField.setText(user.getEmail());
        telephoneField.setText(user.getTelephone());
        departementField.setText(user.getDepartement());
        roleComboBox.setValue(user.getRole());
        actifCheckBox.setSelected(user.isActif());
    }
    
    /**
     * Handles save button click.
     */
    @FXML
    private void handleSave() {
        // Clear previous errors
        errorLabel.setVisible(false);
        
        // Validate inputs
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String departement = departementField.getText().trim();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();
        
        // Validation
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
            showError("Les champs Nom, Prénom et Email sont obligatoires.");
            return;
        }
        
        if (!ValidationUtil.isValidEmail(email)) {
            showError("L'adresse email n'est pas valide.");
            return;
        }
        
        if (!isEditMode && (password.isEmpty() || !PasswordUtil.isPasswordStrong(password))) {
            showError("Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }
        
        if (role == null || role.isEmpty()) {
            showError("Veuillez sélectionner un rôle.");
            return;
        }
        
        // Validate phone if provided
        if (!telephone.isEmpty() && !ValidationUtil.isValidPhone(telephone)) {
            showError("Numéro de téléphone invalide.\nFormat attendu: 8 chiffres (ex: 20123456) ou +216xxxxxxxx");
            return;
        }
        
        // Check if email already exists (for new user or if email changed)
        if (!isEditMode || !email.equals(userToEdit.getEmail())) {
            if (userService.emailExists(email)) {
                showError("Un utilisateur avec cet email existe déjà.");
                return;
            }
        }
        
        try {
            if (isEditMode) {
                // Update existing user
                userToEdit.setNom(nom);
                userToEdit.setPrenom(prenom);
                userToEdit.setEmail(email);
                userToEdit.setTelephone(telephone.isEmpty() ? null : telephone);
                userToEdit.setDepartement(departement.isEmpty() ? null : departement);
                userToEdit.setRole(role);
                userToEdit.setActif(actifCheckBox.isSelected());
                
                // Validate before saving
                String validationError = userService.getLastValidationError(userToEdit);
                if (validationError != null) {
                    showError(validationError);
                    return;
                }
                
                boolean success = userService.updateUser(userToEdit);
                
                if (success) {
                    AlertUtil.showSuccess("Succès", "L'utilisateur a été modifié avec succès.");
                    closeWindow();
                } else {
                    showError("Erreur lors de la modification de l'utilisateur.");
                }
                
            } else {
                // Create new user
                Utilisateur newUser = new Utilisateur();
                newUser.setNom(nom);
                newUser.setPrenom(prenom);
                newUser.setEmail(email);
                newUser.setTelephone(telephone.isEmpty() ? null : telephone);
                newUser.setDepartement(departement.isEmpty() ? null : departement);
                newUser.setMotDePasse(PasswordUtil.hashPassword(password));
                newUser.setRole(role);
                newUser.setActif(actifCheckBox.isSelected());
                
                // Validate before saving
                String validationError = userService.getLastValidationError(newUser);
                if (validationError != null) {
                    showError(validationError);
                    return;
                }
                
                boolean success = userService.createUser(newUser);
                
                if (success) {
                    AlertUtil.showSuccess("Succès", "L'utilisateur a été créé avec succès.");
                    closeWindow();
                } else {
                    showError("Erreur lors de la création de l'utilisateur.");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
            showError("Une erreur est survenue: " + e.getMessage());
        }
    }
    
    /**
     * Handles cancel button click.
     */
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    /**
     * Shows error message.
     * 
     * @param message Error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    /**
     * Closes the window.
     */
    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
