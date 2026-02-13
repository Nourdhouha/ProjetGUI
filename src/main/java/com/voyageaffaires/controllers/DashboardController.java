package com.voyageaffaires.controllers;

import com.voyageaffaires.models.Utilisateur;
import com.voyageaffaires.services.AuthService;
import com.voyageaffaires.services.UserService;
import com.voyageaffaires.services.ReservationService;
import com.voyageaffaires.utils.AlertUtil;
import com.voyageaffaires.utils.NavigationUtil;
import com.voyageaffaires.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.chart.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the Dashboard screen.
 * Main navigation hub of the application.
 */
public class DashboardController {
    
    @FXML
    private Label welcomeLabel;
    
    @FXML
    private Label roleLabel;
    
    @FXML
    private Label statsReservations;
    
    @FXML
    private Label statsUsers;
    
    @FXML
    private Label statsPending;
    
    @FXML
    private Button btnDashboard;
    
    @FXML
    private Button btnUsers;
    
    @FXML
    private Button btnProfile;
    
    @FXML
    private Button btnReservations;
    
    @FXML
    private Button btnCreateReservation;
    
    @FXML
    private Button btnSearchFlights;
    
    @FXML
    private Button btnSearchHotels;
    
    @FXML
    private Button btnReports;
    
    @FXML
    private Button btnHistory;
    
    @FXML
    private Button logoutButton;
    
    @FXML
    private StackPane contentArea;
    
    @FXML
    private LineChart<String, Number> reservationsChart;
    
    @FXML
    private BarChart<String, Number> destinationsChart;
    
    private AuthService authService;
    private UserService userService;
    private ReservationService reservationService;
    private Utilisateur currentUser;
    
    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        authService = new AuthService();
        userService = new UserService();
        reservationService = new ReservationService();
        
        // Get current user
        currentUser = SessionManager.getInstance().getCurrentUser();
        
        if (currentUser != null) {
            welcomeLabel.setText("Bienvenue, " + currentUser.getFullName());
            roleLabel.setText("Rôle: " + currentUser.getRole());
            
            // Configure UI based on role
            configureUIForRole();
            
            // Load statistics
            loadStatistics();
            
            // Initialize charts
            initializeCharts();
            
            // Setup icon hover effects
            setupIconHoverEffects();
            
            // Set Accueil as active by default
            setActiveButton(btnDashboard);
        } else {
            // No user logged in, redirect to login
            returnToLogin();
        }
    }
    
    /**
     * Configures UI elements based on user role.
     */
    private void configureUIForRole() {
        if (currentUser.isAdmin()) {
            // Admin can manage users but cannot create reservations
            btnUsers.setVisible(true);
            btnUsers.setManaged(true);
            
            // Hide profile for admin
            btnProfile.setVisible(false);
            btnProfile.setManaged(false);
            
            // Hide "Nouvelle Réservation" button for admin
            if (btnCreateReservation != null) {
                btnCreateReservation.setVisible(false);
                btnCreateReservation.setManaged(false);
            }
            
        } else {
            // Regular employees can create reservations but not manage users
            btnUsers.setVisible(false);
            btnUsers.setManaged(false);
            
            // Show profile for employees
            btnProfile.setVisible(true);
            btnProfile.setManaged(true);
            
            // Show "Nouvelle Réservation" button for employees
            if (btnCreateReservation != null) {
                btnCreateReservation.setVisible(true);
                btnCreateReservation.setManaged(true);
            }
        }
    }
    
    /**
     * Loads statistics for the dashboard.
     */
    private void loadStatistics() {
        try {
            // Load reservation count
            int reservationCount = 0;
            if (currentUser.isAdmin() || currentUser.isManager()) {
                reservationCount = reservationService.getAllReservations().size();
            } else {
                reservationCount = reservationService.getUserReservations(currentUser.getIdUtilisateur()).size();
            }
            statsReservations.setText(String.valueOf(reservationCount));
            
            // Load user count (admin only)
            if (currentUser.isAdmin()) {
                int userCount = userService.getAllUsers().size();
                statsUsers.setText(String.valueOf(userCount));
            } else {
                statsUsers.setText("-");
            }
            
            // Count pending reservations
            long pendingCount = reservationService.getUserReservations(currentUser.getIdUtilisateur())
                .stream()
                .filter(r -> "EN_ATTENTE".equals(r.getStatut()))
                .count();
            statsPending.setText(String.valueOf(pendingCount));
            
        } catch (Exception e) {
            System.err.println("Error loading statistics: " + e.getMessage());
        }
    }
    
    /**
     * Initializes charts with data.
     */
    private void initializeCharts() {
        if (reservationsChart != null && destinationsChart != null) {
            populateReservationsChart();
            populateDestinationsChart();
            applyChartStyling();
        }
    }
    
    /**
     * Populates the reservations line chart with last 7 days data.
     */
    private void populateReservationsChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        
        // Generate last 7 days
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateLabel = date.format(formatter);
            
            // Simulate data - in production, query database by date
            int count = (int) (Math.random() * 10) + 1;
            series.getData().add(new XYChart.Data<>(dateLabel, count));
        }
        
        reservationsChart.getData().add(series);
    }
    
    /**
     * Populates the destinations bar chart with popular destinations.
     */
    private void populateDestinationsChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        // Popular destinations - in production, query from database
        series.getData().add(new XYChart.Data<>("Paris", 8));
        series.getData().add(new XYChart.Data<>("London", 6));
        series.getData().add(new XYChart.Data<>("New York", 5));
        series.getData().add(new XYChart.Data<>("Berlin", 4));
        series.getData().add(new XYChart.Data<>("Tokyo", 3));
        
        destinationsChart.getData().add(series);
    }
    
    /**
     * Applies custom styling to charts matching the reference design.
     */
    private void applyChartStyling() {
        // Style line chart
        if (reservationsChart != null) {
            reservationsChart.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-padding: 0;"
            );
            reservationsChart.lookup(".chart-plot-background").setStyle("-fx-background-color: white;");
        }
        
        // Style bar chart  
        if (destinationsChart != null) {
            destinationsChart.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-padding: 0;"
            );
            destinationsChart.lookup(".chart-plot-background").setStyle("-fx-background-color: white;");
        }
    }
    
    /**
     * Shows the dashboard home view.
     */
    @FXML
    private void showDashboard() {
        // Already on dashboard - just reload statistics
        loadStatistics();
        setActiveButton(btnDashboard);
    }
    
    /**
     * Opens the create reservation dialog.
     */
    @FXML
    private void handleCreateReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreateReservationView.fxml"));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nouvelle Réservation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(contentArea.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(true);
            
            dialogStage.showAndWait();
            
            // Reload statistics after dialog closes
            loadStatistics();
            
        } catch (Exception e) {
            System.err.println("Error opening reservation form: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Erreur", "Erreur d'ouverture", 
                              "Impossible d'ouvrir le formulaire de réservation.");
        }
    }
    
    /**
     * Shows the user management screen (Admin only).
     */
    @FXML
    private void showUserManagement() {
        if (!currentUser.isAdmin()) {
            AlertUtil.showError("Accès refusé", "Permission insuffisante", 
                              "Seuls les administrateurs peuvent accéder à cette fonctionnalité.");
            return;
        }
        
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/fxml/UserManagementView.fxml", 
                                     "Gestion des Utilisateurs", 1280, 800);
        } catch (Exception e) {
            System.err.println("Error navigating to user management: " + e.getMessage());
            AlertUtil.showError("Erreur", "Navigation", 
                              "Impossible d'ouvrir la gestion des utilisateurs.");
        }
    }
    
    /**
     * Shows the user profile screen (Employee only).
     */
    @FXML
    private void showProfile() {
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/fxml/ProfileView.fxml", 
                                     "Mon Profil", 1280, 800);
        } catch (Exception e) {
            System.err.println("Error navigating to profile: " + e.getMessage());
            AlertUtil.showError("Erreur", "Navigation", 
                              "Impossible d'ouvrir votre profil.");
        }
    }
    
    /**
     * Shows the reservations screen.
     */
    @FXML
    private void showReservations() {
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/fxml/ReservationListView.fxml", 
                                     "Mes Réservations", 1280, 800);
        } catch (Exception e) {
            System.err.println("Error navigating to reservations: " + e.getMessage());
            AlertUtil.showError("Erreur", "Navigation", 
                              "Impossible d'ouvrir les réservations.");
        }
    }
    
    /**
     * Shows the flight search screen.
     */
    @FXML
    private void showSearchFlights() {
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/fxml/SearchFlightView.fxml", 
                                     "Rechercher un Vol", 1280, 800);
        } catch (Exception e) {
            System.err.println("Error navigating to flight search: " + e.getMessage());
            AlertUtil.showError("Erreur", "Navigation", 
                              "Impossible d'ouvrir la recherche de vols.");
        }
    }
    
    /**
     * Shows the hotel search screen.
     */
    @FXML
    private void showSearchHotels() {
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/fxml/SearchHotelView.fxml", 
                                     "Rechercher un Hôtel", 1280, 800);
        } catch (Exception e) {
            System.err.println("Error navigating to hotel search: " + e.getMessage());
            AlertUtil.showError("Erreur", "Navigation", 
                              "Impossible d'ouvrir la recherche d'hôtels.");
        }
    }
    
    /**
     * Shows the reports screen.
     */
    @FXML
    private void showReports() {
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/fxml/ReportingView.fxml", 
                                     "Rapports", 1280, 800);
        } catch (Exception e) {
            System.err.println("Error navigating to reports: " + e.getMessage());
            AlertUtil.showError("Erreur", "Navigation", 
                              "Impossible d'ouvrir les rapports.");
        }
    }
    
    /**
     * Shows the history screen.
     */
    @FXML
    private void showHistory() {
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/fxml/HistoryView.fxml", 
                                     "Historique", 1280, 800);
        } catch (Exception e) {
            System.err.println("Error navigating to history: " + e.getMessage());
            AlertUtil.showError("Erreur", "Navigation", 
                              "Impossible d'ouvrir l'historique.");
        }
    }
    
    /**
     * Handles logout.
     */
    @FXML
    private void handleLogout() {
        boolean confirmed = AlertUtil.showConfirmation(
            "Déconnexion",
            "Êtes-vous sûr de vouloir vous déconnecter?",
            "Vous devrez vous reconnecter pour accéder à l'application."
        );
        
        if (confirmed) {
            authService.logout();
            returnToLogin();
        }
    }
    
    /**
     * Returns to the login screen.
     */
    private void returnToLogin() {
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/fxml/LoginView.fxml", 
                                     "Voyage & Affaires - Connexion", 900, 600);
        } catch (Exception e) {
            System.err.println("Error returning to login: " + e.getMessage());
        }
    }
    
    /**
     * Setup hover effects for sidebar icons.
     */
    private void setupIconHoverEffects() {
        Button[] buttons = {btnDashboard, btnUsers, btnProfile, btnReservations, 
                           btnSearchFlights, btnSearchHotels, btnReports, btnHistory};
        
        for (Button button : buttons) {
            if (button != null && button.isVisible() && button.getGraphic() instanceof FontIcon) {
                FontIcon icon = (FontIcon) button.getGraphic();
                
                button.setOnMouseEntered(e -> {
                    if (!button.getStyleClass().contains("sidebar-button-active")) {
                        icon.setIconColor(Paint.valueOf("#4F8FF0"));
                    }
                });
                
                button.setOnMouseExited(e -> {
                    if (!button.getStyleClass().contains("sidebar-button-active")) {
                        icon.setIconColor(Paint.valueOf("#475569"));
                    }
                });
            }
        }
    }
    
    /**
     * Sets the active button style.
     * 
     * @param activeButton The button to mark as active
     */
    private void setActiveButton(Button activeButton) {
        Button[] buttons = {btnDashboard, btnUsers, btnProfile, btnReservations, 
                           btnSearchFlights, btnSearchHotels, btnReports, btnHistory};
        
        // Reset all buttons and icons
        for (Button button : buttons) {
            if (button != null && button.isVisible()) {
                button.getStyleClass().remove("sidebar-button-active");
                if (button.getGraphic() instanceof FontIcon) {
                    ((FontIcon) button.getGraphic()).setIconColor(Paint.valueOf("#475569"));
                }
            }
        }
        
        // Set active button and icon color
        if (activeButton != null) {
            if (!activeButton.getStyleClass().contains("sidebar-button-active")) {
                activeButton.getStyleClass().add("sidebar-button-active");
            }
            if (activeButton.getGraphic() instanceof FontIcon) {
                ((FontIcon) activeButton.getGraphic()).setIconColor(Paint.valueOf("#FFFFFF"));
            }
        }
    }
}
