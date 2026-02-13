package com.voyageaffaires.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Utility class for handling navigation between screens.
 */
public class NavigationUtil {
    
    /**
     * Loads and displays a new screen.
     * 
     * @param currentStage The current stage
     * @param fxmlPath Path to the FXML file
     * @param title Window title
     * @throws IOException if FXML file cannot be loaded
     */
    public static void navigateTo(Stage currentStage, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(fxmlPath));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.setTitle(title);
        currentStage.centerOnScreen();
    }
    
    /**
     * Loads and displays a new screen with custom dimensions.
     * 
     * @param currentStage The current stage
     * @param fxmlPath Path to the FXML file
     * @param title Window title
     * @param width Window width
     * @param height Window height
     * @throws IOException if FXML file cannot be loaded
     */
    public static void navigateTo(Stage currentStage, String fxmlPath, String title, 
                                   double width, double height) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(fxmlPath));
        Parent root = loader.load();
        
        Scene scene = new Scene(root, width, height);
        currentStage.setScene(scene);
        currentStage.setTitle(title);
        currentStage.centerOnScreen();
    }
    
    /**
     * Loads and displays a new screen, returns the controller.
     * 
     * @param currentStage The current stage
     * @param fxmlPath Path to the FXML file
     * @param title Window title
     * @param <T> Controller type
     * @return The controller instance
     * @throws IOException if FXML file cannot be loaded
     */
    public static <T> T navigateToWithController(Stage currentStage, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(fxmlPath));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.setTitle(title);
        currentStage.centerOnScreen();
        
        return loader.getController();
    }
    
    /**
     * Opens a new window.
     * 
     * @param fxmlPath Path to the FXML file
     * @param title Window title
     * @return The new stage
     * @throws IOException if FXML file cannot be loaded
     */
    public static Stage openNewWindow(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(fxmlPath));
        Parent root = loader.load();
        
        Stage newStage = new Stage();
        Scene scene = new Scene(root);
        newStage.setScene(scene);
        newStage.setTitle(title);
        newStage.centerOnScreen();
        newStage.show();
        
        return newStage;
    }
}
