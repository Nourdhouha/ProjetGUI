package com.voyageaffaires.controllers;

import com.voyageaffaires.models.Vol;
import com.voyageaffaires.services.ReservationService;
import com.voyageaffaires.utils.AlertUtil;
import com.voyageaffaires.utils.NavigationUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class SearchFlightController {
    
    @FXML private TextField origineField;
    @FXML private TextField destinationField;
    @FXML private TextField maxPrixField;
    @FXML private TableView<Vol> flightsTable;
    @FXML private TableColumn<Vol, String> colCompagnie;
    @FXML private TableColumn<Vol, String> colOrigine;
    @FXML private TableColumn<Vol, String> colDestination;
    @FXML private TableColumn<Vol, String> colDepart;
    @FXML private TableColumn<Vol, String> colArrivee;
    @FXML private TableColumn<Vol, Double> colPrix;
    @FXML private TableColumn<Vol, Void> colActions;
    
    private ReservationService reservationService;
    
    @FXML
    public void initialize() {
        reservationService = new ReservationService();
        
        colCompagnie.setCellValueFactory(new PropertyValueFactory<>("compagnie"));
        colOrigine.setCellValueFactory(new PropertyValueFactory<>("origine"));
        colDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        colDepart.setCellValueFactory(new PropertyValueFactory<>("dateDepart"));
        colArrivee.setCellValueFactory(new PropertyValueFactory<>("dateArrivee"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        
        handleSearch(); // Load all flights initially
    }
    
    @FXML
    private void handleSearch() {
        String origine = origineField.getText();
        String destination = destinationField.getText();
        Double maxPrix = null;
        
        try {
            if (!maxPrixField.getText().isEmpty()) {
                maxPrix = Double.parseDouble(maxPrixField.getText());
            }
        } catch (NumberFormatException e) {
            AlertUtil.showError("Erreur", "Prix invalide", "Veuillez entrer un nombre valide.");
            return;
        }
        
        flightsTable.setItems(FXCollections.observableArrayList(
            reservationService.searchFlights(origine, destination, maxPrix)
        ));
    }
    
    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) flightsTable.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/fxml/DashboardView.fxml", "Dashboard", 1280, 800);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
