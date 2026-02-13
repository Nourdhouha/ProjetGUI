package com.voyageaffaires.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.util.Optional;

/**
 * Utility class for displaying alerts and notifications.
 */
public class AlertUtil {
    
    /**
     * Shows an information alert.
     * 
     * @param title Alert title
     * @param header Alert header
     * @param content Alert content/message
     */
    public static void showInfo(String title, String header, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Shows an error alert.
     * 
     * @param title Alert title
     * @param header Alert header
     * @param content Alert content/message
     */
    public static void showError(String title, String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Shows a warning alert.
     * 
     * @param title Alert title
     * @param header Alert header
     * @param content Alert content/message
     */
    public static void showWarning(String title, String header, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Shows a success alert (using information type with custom styling).
     * 
     * @param title Alert title
     * @param message Success message
     */
    public static void showSuccess(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Shows a confirmation dialog.
     * 
     * @param title Dialog title
     * @param header Dialog header
     * @param content Dialog content/message
     * @return true if user clicked OK, false if user clicked Cancel
     */
    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    /**
     * Shows a database connection error alert.
     */
    public static void showDatabaseError() {
        showError(
            "Erreur de Base de Données",
            "Impossible de se connecter à la base de données",
            "Veuillez vérifier que MySQL est démarré et que la configuration est correcte."
        );
    }
    
    /**
     * Shows a validation error alert.
     * 
     * @param message Validation error message
     */
    public static void showValidationError(String message) {
        showError(
            "Erreur de Validation",
            "Données invalides",
            message
        );
    }
}
