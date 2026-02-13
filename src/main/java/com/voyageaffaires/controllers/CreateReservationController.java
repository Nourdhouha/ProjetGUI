package com.voyageaffaires.controllers;

import com.voyageaffaires.dao.VolDAO;
import com.voyageaffaires.dao.HotelDAO;
import com.voyageaffaires.models.Vol;
import com.voyageaffaires.models.Hotel;
import com.voyageaffaires.models.Reservation;
import com.voyageaffaires.services.ReservationService;
import com.voyageaffaires.utils.AlertUtil;
import com.voyageaffaires.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Controller for creating new reservations.
 */
public class CreateReservationController {
    
    @FXML
    private TextField volSearchField;
    
    @FXML
    private TableView<Vol> volTable;
    
    @FXML
    private TableColumn<Vol, String> colVolId;
    
    @FXML
    private TableColumn<Vol, String> colVolCompagnie;
    
    @FXML
    private TableColumn<Vol, String> colVolOrigine;
    
    @FXML
    private TableColumn<Vol, String> colVolDestination;
    
    @FXML
    private TableColumn<Vol, String> colVolDepart;
    
    @FXML
    private TableColumn<Vol, Double> colVolPrix;
    
    @FXML
    private TableColumn<Vol, Void> colVolActions;
    
    @FXML
    private Label selectedVolLabel;
    
    @FXML
    private TextField hotelSearchField;
    
    @FXML
    private TableView<Hotel> hotelTable;
    
    @FXML
    private TableColumn<Hotel, String> colHotelId;
    
    @FXML
    private TableColumn<Hotel, String> colHotelNom;
    
    @FXML
    private TableColumn<Hotel, String> colHotelVille;
    
    @FXML
    private TableColumn<Hotel, Integer> colHotelEtoiles;
    
    @FXML
    private TableColumn<Hotel, Double> colHotelPrix;
    
    @FXML
    private TableColumn<Hotel, Void> colHotelActions;
    
    @FXML
    private Label selectedHotelLabel;
    
    @FXML
    private DatePicker checkinDate;
    
    @FXML
    private DatePicker checkoutDate;
    
    @FXML
    private Spinner<Integer> chambresSpinner;
    
    @FXML
    private TextField motifField;
    
    @FXML
    private TextArea commentaireArea;
    
    @FXML
    private Label summaryLabel;
    
    @FXML
    private Label totalLabel;
    
    @FXML
    private Label errorLabel;
    
    private VolDAO volDAO;
    private HotelDAO hotelDAO;
    private ReservationService reservationService;
    
    private Vol selectedVol;
    private Hotel selectedHotel;
    
    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        volDAO = new VolDAO();
        hotelDAO = new HotelDAO();
        reservationService = new ReservationService();
        
        // Configure vol table
        colVolId.setCellValueFactory(new PropertyValueFactory<>("idVol"));
        colVolCompagnie.setCellValueFactory(new PropertyValueFactory<>("compagnie"));
        colVolOrigine.setCellValueFactory(new PropertyValueFactory<>("origine"));
        colVolDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        colVolDepart.setCellValueFactory(new PropertyValueFactory<>("dateDepart"));
        colVolPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        
        // Configure hotel table
        colHotelId.setCellValueFactory(new PropertyValueFactory<>("idHotel"));
        colHotelNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colHotelVille.setCellValueFactory(new PropertyValueFactory<>("ville"));
        colHotelEtoiles.setCellValueFactory(new PropertyValueFactory<>("etoiles"));
        colHotelPrix.setCellValueFactory(new PropertyValueFactory<>("prixParNuit"));
        
        // Custom cell factory for stars - display golden ★ icons instead of numbers
        colHotelEtoiles.setCellFactory(column -> new TableCell<Hotel, Integer>() {
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
        
        // Add action buttons
        addVolActionButtons();
        addHotelActionButtons();
        
        // Load initial data
        loadVols();
        loadHotels();
        
        // Setup spinner
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        chambresSpinner.setValueFactory(valueFactory);
        
        // Add listeners for auto-update summary
        checkinDate.valueProperty().addListener((obs, old, newVal) -> updateSummary());
        checkoutDate.valueProperty().addListener((obs, old, newVal) -> updateSummary());
        chambresSpinner.valueProperty().addListener((obs, old, newVal) -> updateSummary());
    }
    
    private void loadVols() {
        try {
            ObservableList<Vol> vols = FXCollections.observableArrayList(volDAO.findAllAvailable());
            volTable.setItems(vols);
        } catch (Exception e) {
            System.err.println("Error loading vols: " + e.getMessage());
        }
    }
    
    private void loadHotels() {
        try {
            ObservableList<Hotel> hotels = FXCollections.observableArrayList(hotelDAO.findAllAvailable());
            hotelTable.setItems(hotels);
        } catch (Exception e) {
            System.err.println("Error loading hotels: " + e.getMessage());
        }
    }
    
    private void addVolActionButtons() {
        colVolActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnSelect = new Button("Sélectionner");
            
            {
                btnSelect.getStyleClass().add("primary-button");
                btnSelect.setOnAction(event -> {
                    Vol vol = getTableView().getItems().get(getIndex());
                    selectVol(vol);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnSelect);
            }
        });
    }
    
    private void addHotelActionButtons() {
        colHotelActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnSelect = new Button("Sélectionner");
            
            {
                btnSelect.getStyleClass().add("primary-button");
                btnSelect.setOnAction(event -> {
                    Hotel hotel = getTableView().getItems().get(getIndex());
                    selectHotel(hotel);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnSelect);
            }
        });
    }
    
    private void selectVol(Vol vol) {
        selectedVol = vol;
        selectedVolLabel.setText(vol.getOrigine() + " → " + vol.getDestination() + 
                                " (" + vol.getCompagnie() + ")");
        updateSummary();
    }
    
    private void selectHotel(Hotel hotel) {
        selectedHotel = hotel;
        selectedHotelLabel.setText(hotel.getNom() + " - " + hotel.getVille());
        updateSummary();
    }
    
    @FXML
    private void handleSearchVol() {
        String keyword = volSearchField.getText().trim();
        if (keyword.isEmpty()) {
            loadVols();
        } else {
            try {
                ObservableList<Vol> vols = FXCollections.observableArrayList(
                    volDAO.searchFlightsAdvanced(keyword, keyword, Double.MAX_VALUE)
                );
                volTable.setItems(vols);
            } catch (Exception e) {
                System.err.println("Error searching vols: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleSearchHotel() {
        String ville = hotelSearchField.getText().trim();
        if (ville.isEmpty()) {
            loadHotels();
        } else {
            try {
                ObservableList<Hotel> hotels = FXCollections.observableArrayList(
                    hotelDAO.searchHotelsAdvanced(ville, 0, Double.MAX_VALUE)
                );
                hotelTable.setItems(hotels);
            } catch (Exception e) {
                System.err.println("Error searching hotels: " + e.getMessage());
            }
        }
    }
    
    private void updateSummary() {
        StringBuilder summary = new StringBuilder();
        double total = 0.0;
        
        if (selectedVol != null) {
            summary.append("Vol: ").append(selectedVol.getOrigine())
                   .append(" → ").append(selectedVol.getDestination())
                   .append(" (").append(selectedVol.getPrix()).append(" €)\n");
            total += selectedVol.getPrix();
        }
        
        if (selectedHotel != null && checkinDate.getValue() != null && 
            checkoutDate.getValue() != null) {
            
            long nights = ChronoUnit.DAYS.between(checkinDate.getValue(), 
                                                   checkoutDate.getValue());
            if (nights > 0) {
                int chambres = chambresSpinner.getValue();
                double hotelCost = selectedHotel.getPrixParNuit() * nights * chambres;
                
                summary.append("Hôtel: ").append(selectedHotel.getNom())
                       .append(" (").append(nights).append(" nuit(s), ")
                       .append(chambres).append(" chambre(s) = ")
                       .append(hotelCost).append(" €)\n");
                total += hotelCost;
            }
        }
        
        summaryLabel.setText(summary.toString());
        totalLabel.setText(String.format("Total: %.2f €", total));
    }
    
    @FXML
    private void handleCreateReservation() {
        errorLabel.setVisible(false);
        
        // Validation
        if (selectedVol == null) {
            showError("Veuillez sélectionner un vol.");
            return;
        }
        
        if (selectedHotel != null) {
            if (checkinDate.getValue() == null || checkoutDate.getValue() == null) {
                showError("Veuillez sélectionner les dates de check-in et check-out.");
                return;
            }
            
            if (checkoutDate.getValue().isBefore(checkinDate.getValue())) {
                showError("La date de check-out doit être après la date de check-in.");
                return;
            }
        }
        
        // Create reservation
        try {
            Reservation reservation = new Reservation();
            reservation.setIdReservation(UUID.randomUUID().toString());
            reservation.setDateCreation(LocalDate.now());
            reservation.setStatut("EN_ATTENTE");
            reservation.setIdUtilisateur(SessionManager.getInstance().getCurrentUser().getIdUtilisateur());
            reservation.setIdVol(selectedVol.getIdVol());
            
            double total = selectedVol.getPrix();
            
            if (selectedHotel != null) {
                reservation.setIdHotel(selectedHotel.getIdHotel());
                reservation.setDateCheckin(checkinDate.getValue());
                reservation.setDateCheckout(checkoutDate.getValue());
                reservation.setNombreChambres(chambresSpinner.getValue());
                
                long nights = ChronoUnit.DAYS.between(checkinDate.getValue(), 
                                                       checkoutDate.getValue());
                total += selectedHotel.getPrixParNuit() * nights * chambresSpinner.getValue();
            }
            
            reservation.setMontantTotal(total);
            reservation.setMotifVoyage(motifField.getText());
            reservation.setCommentaire(commentaireArea.getText());
            
            boolean success = reservationService.createReservation(reservation);
            
            if (success) {
                AlertUtil.showSuccess("Succès", 
                                    "Votre réservation a été créée avec succès.\nNuméro: " + 
                                    reservation.getIdReservation());
                closeWindow();
            } else {
                showError("Erreur lors de la création de la réservation.");
            }
            
        } catch (Exception e) {
            System.err.println("Error creating reservation: " + e.getMessage());
            e.printStackTrace();
            showError("Une erreur est survenue: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private void closeWindow() {
        Stage stage = (Stage) volTable.getScene().getWindow();
        stage.close();
    }
}
