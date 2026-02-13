package com.voyageaffaires.controllers;

import com.voyageaffaires.dao.VolDAO;
import com.voyageaffaires.dao.HotelDAO;
import com.voyageaffaires.dao.UtilisateurDAO;
import com.voyageaffaires.models.Reservation;
import com.voyageaffaires.models.Vol;
import com.voyageaffaires.models.Hotel;
import com.voyageaffaires.models.Utilisateur;
import com.voyageaffaires.services.ReservationService;
import com.voyageaffaires.utils.AlertUtil;
import com.voyageaffaires.utils.NavigationUtil;
import com.voyageaffaires.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class ReservationListController {
    
    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, String> colId;
    @FXML private TableColumn<Reservation, String> colDate;
    @FXML private TableColumn<Reservation, String> colUtilisateur;
    @FXML private TableColumn<Reservation, String> colStatut;
    @FXML private TableColumn<Reservation, Double> colMontant;
    @FXML private TableColumn<Reservation, String> colVol;
    @FXML private TableColumn<Reservation, String> colHotel;
    @FXML private TableColumn<Reservation, Void> colActions;
    
    @FXML private Label statsTotal;
    @FXML private Label statsEnAttente;
    @FXML private Label statsConfirmee;
    @FXML private Label statsMontantTotal;
    
    @FXML private Button btnFilterAll;
    @FXML private Button btnFilterEnAttente;
    @FXML private Button btnFilterConfirmee;
    @FXML private Button btnFilterAnnulee;
    
    @FXML private TextField searchField;
    @FXML private Button btnNewReservation;
    
    private ReservationService reservationService;
    private VolDAO volDAO;
    private HotelDAO hotelDAO;
    private UtilisateurDAO utilisateurDAO;
    private ObservableList<Reservation> allReservations;
    private String currentFilter = "ALL";
    private boolean isAdmin = false;
    
    @FXML
    public void initialize() {
        reservationService = new ReservationService();
        volDAO = new VolDAO();
        hotelDAO = new HotelDAO();
        utilisateurDAO = new UtilisateurDAO();
        
        // Check if current user is admin
        isAdmin = SessionManager.getInstance().isAdmin();
        
        // Configure UI based on role
        configureUIForRole();
        
        // Configure table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("idReservation"));
        
        // Custom cell factory for ID - shorten and gray out if cancelled
        colId.setCellFactory(column -> new TableCell<Reservation, String>() {
            @Override
            protected void updateItem(String idReservation, boolean empty) {
                super.updateItem(idReservation, empty);
                if (empty || idReservation == null) {
                    setText(null);
                    setStyle("");
                    setTooltip(null);
                } else {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    // Show shortened ID
                    if (idReservation.length() > 20) {
                        setText(idReservation.substring(0, 20) + "...");
                        setTooltip(new Tooltip(idReservation));
                    } else {
                        setText(idReservation);
                    }
                    // Gray out cancelled reservations
                    if ("ANNULEE".equals(reservation.getStatut())) {
                        setStyle("-fx-text-fill: #94a3b8;");
                    } else {
                        setStyle("-fx-text-fill: #475569; -fx-font-family: monospace;");
                    }
                }
            }
        });
        
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        
        // Utilisateur column
        colUtilisateur.setCellValueFactory(new PropertyValueFactory<>("utilisateurNom"));
        
        // Custom cell factory for Utilisateur - fetch and display user name
        colUtilisateur.setCellFactory(column -> new TableCell<Reservation, String>() {
            @Override
            protected void updateItem(String utilisateurNom, boolean empty) {
                super.updateItem(utilisateurNom, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    
                    // Fetch user if not already loaded
                    if (utilisateurNom == null || utilisateurNom.isEmpty()) {
                        Utilisateur user = utilisateurDAO.findById(reservation.getIdUtilisateur());
                        if (user != null) {
                            utilisateurNom = user.getPrenom() + " " + user.getNom();
                            reservation.setUtilisateurNom(utilisateurNom);
                        } else {
                            utilisateurNom = "Utilisateur #" + reservation.getIdUtilisateur();
                        }
                    }
                    
                    setText(utilisateurNom);
                    
                    // Gray out if cancelled
                    if ("ANNULEE".equals(reservation.getStatut())) {
                        setStyle("-fx-text-fill: #94a3b8;");
                    } else {
                        setStyle("-fx-text-fill: #475569; -fx-font-weight: 500;");
                    }
                }
            }
        });
        
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        
        // Custom cell factory for Montant - format as currency
        colMontant.setCellFactory(column -> new TableCell<Reservation, Double>() {
            @Override
            protected void updateItem(Double montant, boolean empty) {
                super.updateItem(montant, empty);
                if (empty || montant == null) {
                    setText(null);
                } else {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    setText(String.format("%.0f €", montant));
                    // Strikethrough for cancelled reservations
                    if ("ANNULEE".equals(reservation.getStatut())) {
                        setStyle("-fx-text-fill: #94a3b8; -fx-strikethrough: true;");
                    } else {
                        setStyle("-fx-text-fill: #1e293b; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        colVol.setCellValueFactory(new PropertyValueFactory<>("idVol"));
        colHotel.setCellValueFactory(new PropertyValueFactory<>("idHotel"));
        
        // Custom cell factory for Vol - display name instead of ID
        colVol.setCellFactory(column -> new TableCell<Reservation, String>() {
            @Override
            protected void updateItem(String idVol, boolean empty) {
                super.updateItem(idVol, empty);
                if (empty || idVol == null) {
                    setText(null);
                } else {
                    Vol vol = volDAO.findById(idVol);
                    if (vol != null) {
                        setText(vol.getOrigine() + " → " + vol.getDestination());
                    } else {
                        setText(idVol);
                    }
                }
            }
        });
        
        // Custom cell factory for Hotel - display name instead of ID
        colHotel.setCellFactory(column -> new TableCell<Reservation, String>() {
            @Override
            protected void updateItem(String idHotel, boolean empty) {
                super.updateItem(idHotel, empty);
                if (empty || idHotel == null) {
                    setText("-");
                } else {
                    Hotel hotel = hotelDAO.findById(idHotel);
                    if (hotel != null) {
                        setText(hotel.getNom() + " (" + hotel.getVille() + ")");
                    } else {
                        setText(idHotel);
                    }
                }
            }
        });
        
        // Custom cell factory for status with colored badges
        colStatut.setCellFactory(column -> new TableCell<Reservation, String>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    Label badge = new Label(statut);
                    badge.setStyle(getStatusStyle(statut));
                    badge.setPadding(new Insets(4, 12, 4, 12));
                    setGraphic(badge);
                    setText(null);
                }
            }
        });
        
        // Add action buttons
        addActionButtons();
        
        // Load reservations
        loadReservations();
        
        // Update statistics
        updateStatistics();
    }
    
    private String getStatusStyle(String statut) {
        switch (statut) {
            case "EN_ATTENTE":
                return "-fx-background-color: #fef3c7; -fx-text-fill: #d97706; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;";
            case "CONFIRMEE":
                return "-fx-background-color: #d1fae5; -fx-text-fill: #059669; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;";
            case "ANNULEE":
                return "-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;";
            default:
                return "-fx-background-color: #e2e8f0; -fx-text-fill: #475569; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11px;";
        }
    }
    
    private void addActionButtons() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnDetails = new Button("Détails");
            private final Button btnConfirm = new Button("Confirmer");
            private final Button btnCancel = new Button("Annuler");
            
            {
                btnDetails.setStyle("-fx-background-color: #4F8FF0; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand; -fx-background-radius: 6px;");
                btnConfirm.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand; -fx-background-radius: 6px;");
                btnCancel.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand; -fx-background-radius: 6px;");
                
                btnDetails.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleViewDetails(reservation);
                });
                
                btnConfirm.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleConfirmReservation(reservation);
                });
                
                btnCancel.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleCancelReservation(reservation);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    
                    if ("ANNULEE".equals(reservation.getStatut())) {
                        // Only show Details for cancelled reservations
                        setGraphic(new HBox(btnDetails));
                    } else if (isAdmin) {
                        // Admin can view details and confirm/cancel reservations
                        if ("EN_ATTENTE".equals(reservation.getStatut())) {
                            setGraphic(new HBox(8, btnDetails, btnConfirm, btnCancel));
                        } else if ("CONFIRMEE".equals(reservation.getStatut())) {
                            setGraphic(new HBox(8, btnDetails, btnCancel));
                        } else {
                            setGraphic(new HBox(btnDetails));
                        }
                    } else {
                        // Regular users can only view details and cancel
                        setGraphic(new HBox(8, btnDetails, btnCancel));
                    }
                }
            }
        });
    }
    
    /**
     * Configures UI elements based on user role.
     */
    private void configureUIForRole() {
        if (isAdmin) {
            // Hide "Nouvelle Réservation" button for admin
            if (btnNewReservation != null) {
                btnNewReservation.setVisible(false);
                btnNewReservation.setManaged(false);
            }
        } else {
            // Show "Nouvelle Réservation" button for employees
            if (btnNewReservation != null) {
                btnNewReservation.setVisible(true);
                btnNewReservation.setManaged(true);
            }
        }
    }
    
    private void loadReservations() {
        if (isAdmin) {
            // Admin sees ALL reservations
            allReservations = FXCollections.observableArrayList(
                reservationService.getAllReservations()
            );
        } else {
            // Regular users see only their own reservations
            int userId = SessionManager.getInstance().getCurrentUser().getIdUtilisateur();
            allReservations = FXCollections.observableArrayList(
                reservationService.getUserReservations(userId)
            );
        }
        applyFilter();
    }
    
    private void applyFilter() {
        List<Reservation> filtered;
        
        switch (currentFilter) {
            case "EN_ATTENTE":
                filtered = allReservations.stream()
                    .filter(r -> "EN_ATTENTE".equals(r.getStatut()))
                    .collect(Collectors.toList());
                break;
            case "CONFIRMEE":
                filtered = allReservations.stream()
                    .filter(r -> "CONFIRMEE".equals(r.getStatut()))
                    .collect(Collectors.toList());
                break;
            case "ANNULEE":
                filtered = allReservations.stream()
                    .filter(r -> "ANNULEE".equals(r.getStatut()))
                    .collect(Collectors.toList());
                break;
            default:
                filtered = allReservations;
        }
        
        reservationsTable.setItems(FXCollections.observableArrayList(filtered));
    }
    
    private void updateStatistics() {
        if (allReservations == null) return;
        
        statsTotal.setText(String.valueOf(allReservations.size()));
        
        long enAttente = allReservations.stream()
            .filter(r -> "EN_ATTENTE".equals(r.getStatut())).count();
        statsEnAttente.setText(String.valueOf(enAttente));
        
        long confirmee = allReservations.stream()
            .filter(r -> "CONFIRMEE".equals(r.getStatut())).count();
        statsConfirmee.setText(String.valueOf(confirmee));
        
        // Calculate total EXCLUDING cancelled reservations
        double montantTotal = allReservations.stream()
            .filter(r -> !"ANNULEE".equals(r.getStatut()))
            .mapToDouble(Reservation::getMontantTotal)
            .sum();
        statsMontantTotal.setText(String.format("%.0f €", montantTotal));
    }
    
    @FXML
    private void filterAll() {
        currentFilter = "ALL";
        setActiveFilterButton(btnFilterAll);
        applyFilter();
    }
    
    @FXML
    private void filterEnAttente() {
        currentFilter = "EN_ATTENTE";
        setActiveFilterButton(btnFilterEnAttente);
        applyFilter();
    }
    
    @FXML
    private void filterConfirmee() {
        currentFilter = "CONFIRMEE";
        setActiveFilterButton(btnFilterConfirmee);
        applyFilter();
    }
    
    @FXML
    private void filterAnnulee() {
        currentFilter = "ANNULEE";
        setActiveFilterButton(btnFilterAnnulee);
        applyFilter();
    }
    
    private void setActiveFilterButton(Button activeButton) {
        btnFilterAll.getStyleClass().remove("filter-button-active");
        btnFilterEnAttente.getStyleClass().remove("filter-button-active");
        btnFilterConfirmee.getStyleClass().remove("filter-button-active");
        btnFilterAnnulee.getStyleClass().remove("filter-button-active");
        
        btnFilterAll.getStyleClass().add("filter-button");
        btnFilterEnAttente.getStyleClass().add("filter-button");
        btnFilterConfirmee.getStyleClass().add("filter-button");
        btnFilterAnnulee.getStyleClass().add("filter-button");
        
        activeButton.getStyleClass().remove("filter-button");
        activeButton.getStyleClass().add("filter-button-active");
    }
    
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            applyFilter();
        } else {
            List<Reservation> filtered = allReservations.stream()
                .filter(r -> {
                    // Ensure user name is loaded
                    if (r.getUtilisateurNom() == null || r.getUtilisateurNom().isEmpty()) {
                        Utilisateur user = utilisateurDAO.findById(r.getIdUtilisateur());
                        if (user != null) {
                            r.setUtilisateurNom(user.getPrenom() + " " + user.getNom());
                        }
                    }
                    
                    return r.getIdReservation().toLowerCase().contains(keyword.toLowerCase()) ||
                           (r.getIdVol() != null && r.getIdVol().toLowerCase().contains(keyword.toLowerCase())) ||
                           (r.getIdHotel() != null && r.getIdHotel().toLowerCase().contains(keyword.toLowerCase())) ||
                           (r.getUtilisateurNom() != null && r.getUtilisateurNom().toLowerCase().contains(keyword.toLowerCase()));
                })
                .collect(Collectors.toList());
            reservationsTable.setItems(FXCollections.observableArrayList(filtered));
        }
    }
    
    @FXML
    private void handleReset() {
        searchField.clear();
        currentFilter = "ALL";
        setActiveFilterButton(btnFilterAll);
        applyFilter();
    }
    
    @FXML
    private void handleNewReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreateReservationView.fxml"));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nouvelle Réservation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(reservationsTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(true);
            
            dialogStage.showAndWait();
            
            // Reload after dialog closes
            loadReservations();
            updateStatistics();
            
        } catch (Exception e) {
            System.err.println("Error opening reservation form: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleViewDetails(Reservation reservation) {
        StringBuilder details = new StringBuilder();
        
        // Get user information
        String userName = reservation.getUtilisateurNom();
        if (userName == null || userName.isEmpty()) {
            Utilisateur user = utilisateurDAO.findById(reservation.getIdUtilisateur());
            if (user != null) {
                userName = user.getPrenom() + " " + user.getNom();
            } else {
                userName = "Utilisateur #" + reservation.getIdUtilisateur();
            }
        }
        
        details.append("Réservé par: ").append(userName).append("\n");
        details.append("Numéro: ").append(reservation.getIdReservation()).append("\n");
        details.append("Date: ").append(reservation.getDateCreation()).append("\n");
        details.append("Statut: ").append(reservation.getStatut()).append("\n");
        details.append("Montant: ").append(String.format("%.2f €", reservation.getMontantTotal())).append("\n\n");
        
        if (reservation.getIdVol() != null) {
            details.append("Vol: ").append(reservation.getIdVol()).append("\n");
        }
        
        if (reservation.getIdHotel() != null) {
            details.append("Hôtel: ").append(reservation.getIdHotel()).append("\n");
            details.append("Check-in: ").append(reservation.getDateCheckin()).append("\n");
            details.append("Check-out: ").append(reservation.getDateCheckout()).append("\n");
            details.append("Chambres: ").append(reservation.getNombreChambres()).append("\n");
        }
        
        if (reservation.getMotifVoyage() != null && !reservation.getMotifVoyage().isEmpty()) {
            details.append("\nMotif: ").append(reservation.getMotifVoyage()).append("\n");
        }
        
        if (reservation.getCommentaire() != null && !reservation.getCommentaire().isEmpty()) {
            details.append("Commentaire: ").append(reservation.getCommentaire()).append("\n");
        }
        
        AlertUtil.showInfo("Détails de la Réservation", 
                          "Réservation " + reservation.getIdReservation().substring(0, 8) + "...", 
                          details.toString());
    }
    
    private void handleConfirmReservation(Reservation reservation) {
        if ("CONFIRMEE".equals(reservation.getStatut())) {
            AlertUtil.showWarning("Attention", "Réservation déjà confirmée", 
                                 "Cette réservation est déjà confirmée.");
            return;
        }
        
        if ("ANNULEE".equals(reservation.getStatut())) {
            AlertUtil.showWarning("Attention", "Réservation annulée", 
                                 "Impossible de confirmer une réservation annulée.");
            return;
        }
        
        boolean confirmed = AlertUtil.showConfirmation(
            "Confirmation",
            "Confirmer la réservation?",
            "Êtes-vous sûr de vouloir confirmer cette réservation?\nNuméro: " + 
            reservation.getIdReservation().substring(0, 8) + "..."
        );
        
        if (confirmed) {
            // Update reservation status to CONFIRMEE
            reservation.setStatut("CONFIRMEE");
            boolean success = reservationService.updateReservation(reservation);
            if (success) {
                AlertUtil.showSuccess("Succès", "La réservation a été confirmée avec succès.");
                loadReservations();
                updateStatistics();
            } else {
                AlertUtil.showError("Erreur", "Confirmation échouée", 
                                  "Impossible de confirmer la réservation.");
            }
        }
    }
    
    private void handleCancelReservation(Reservation reservation) {
        if ("ANNULEE".equals(reservation.getStatut())) {
            AlertUtil.showWarning("Attention", "Réservation déjà annulée", 
                                 "Cette réservation est déjà annulée.");
            return;
        }
        
        boolean confirmed = AlertUtil.showConfirmation(
            "Confirmation",
            "Annuler la réservation?",
            "Êtes-vous sûr de vouloir annuler cette réservation?\nNuméro: " + 
            reservation.getIdReservation().substring(0, 8) + "..."
        );
        
        if (confirmed) {
            boolean success = reservationService.cancelReservation(reservation.getIdReservation());
            if (success) {
                AlertUtil.showSuccess("Succès", "La réservation a été annulée avec succès.");
                loadReservations();
                updateStatistics();
            } else {
                AlertUtil.showError("Erreur", "Annulation échouée", 
                                  "Impossible d'annuler la réservation.");
            }
        }
    }
    
    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) reservationsTable.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/fxml/DashboardView.fxml", "Dashboard", 1280, 800);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
