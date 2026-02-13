package com.voyageaffaires;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main application class for Voyage & Affaires.
 * Entry point for the JavaFX application.
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            
            // Create the scene
            Scene scene = new Scene(root, 900, 600);
            
            // Configure the stage
            primaryStage.setTitle("Voyage & Affaires - Connexion");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            
            // Optional: Set application icon
            // primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
            
            // Show the stage
            primaryStage.show();
            
            System.out.println("Application started successfully");
            
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void stop() {
        System.out.println("Application closing...");
        // Cleanup resources if needed
    }
    
    /**
     * Main method - entry point of the application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Launching Voyage & Affaires application...");
        launch(args);
    }
}
