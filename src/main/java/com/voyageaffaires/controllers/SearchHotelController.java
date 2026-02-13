package com.voyageaffaires.controllers;

import com.voyageaffaires.models.Hotel;
import com.voyageaffaires.services.ReservationService;
import com.voyageaffaires.utils.AlertUtil;
import com.voyageaffaires.utils.NavigationUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class SearchHotelController {
    
    @FXML private TextField villeField;
    @FXML private TextField minEtoilesField;
    @FXML private TextField maxPrixField;
    @FXML private TableView<Hotel> hotelsTable;
    @FXML private TableColumn<Hotel, String> colNom;
    @FXML private TableColumn<Hotel, String> colVille;
    @FXML private TableColumn<Hotel, String> colAdresse;
    @FXML private TableColumn<Hotel, Integer> colEtoiles;
    @FXML private TableColumn<Hotel, Double> colPrix;
    @FXML private TableColumn<Hotel, Void> colActions;
    
    private ReservationService reservationService;
    
    @FXML
    public void initialize() {
        reservationService = new ReservationService();
        
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colVille.setCellValueFactory(new PropertyValueFactory<>("ville"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colEtoiles.setCellValueFactory(new PropertyValueFactory<>("etoiles"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixParNuit"));
        
        // Custom cell factory for stars - display golden ★ icons instead of numbers
        colEtoiles.setCellFactory(column -> new TableCell<Hotel, Integer>() {
            @Override
            protected void updateItem(Integer etoiles, boolean empty) {
                super.updateItem(etoiles, empty);
                if (empty || etoiles == null) {
                    setText(null);
                    setStyle("");
                } else {
                    // Create filled stars (★) for rated stars and empty stars (☆) for remaining
                    StringBuilder stars = new StringBuilder();
                    for (int i = 0; i < etoiles; i++) {
                        stars.append("★"); // Filled star
                    }
                    // Add empty stars to make it 5 stars total
                    for (int i = etoiles; i < 5; i++) {
                        stars.append("☆"); // Empty star
                    }
                    setText(stars.toString());
                    setStyle("-fx-font-size: 18px; -fx-alignment: center; -fx-text-fill: #FFD700; -fx-font-weight: bold;");
                }
            }
        });
        
        handleSearch(); // Load all hotels initially
    }
    
    @FXML
    private void handleSearch() {
        String ville = villeField.getText();
        Integer minEtoiles = null;
        Double maxPrix = null;
        
        try {
            if (!minEtoilesField.getText().isEmpty()) {
                minEtoiles = Integer.parseInt(minEtoilesField.getText());
            }
            if (!maxPrixField.getText().isEmpty()) {
                maxPrix = Double.parseDouble(maxPrixField.getText());
            }
        } catch (NumberFormatException e) {
            AlertUtil.showError("Erreur", "Valeur invalide", "Veuillez entrer des nombres valides.");
            return;
        }
        
        hotelsTable.setItems(FXCollections.observableArrayList(
            reservationService.searchHotels(ville, minEtoiles, maxPrix)
        ));
    }
    
    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) hotelsTable.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/fxml/DashboardView.fxml", "Dashboard", 1280, 800);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
