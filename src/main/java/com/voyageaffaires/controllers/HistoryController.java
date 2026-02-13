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
import com.voyageaffaires.utils.ExcelExportUtil;
import com.voyageaffaires.utils.PdfExportUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the History screen.
 * Displays historical travel reservations with filtering capabilities.
 */
public class HistoryController {
    
    @FXML private DatePicker filterStartDate;
    @FXML private DatePicker filterEndDate;
    @FXML private ComboBox<String> filterStatut;
    @FXML private TableView<Reservation> historyTable;
    @FXML private TableColumn<Reservation, String> colId;
    @FXML private TableColumn<Reservation, String> colDate;
    @FXML private TableColumn<Reservation, String> colUtilisateur;
    @FXML private TableColumn<Reservation, String> colStatut;
    @FXML private TableColumn<Reservation, String> colDestination;
    @FXML private TableColumn<Reservation, String> colHotel;
    @FXML private TableColumn<Reservation, Double> colMontant;
    @FXML private TableColumn<Reservation, String> colMotif;
    @FXML private TableColumn<Reservation, Void> colActions;
    
    @FXML private Label statsTotal;
    @FXML private Label statsCompleted;
    @FXML private Label statsCancelled;
    @FXML private Label statsTotalAmount;
    @FXML private Label lblResultCount;
    
    private ReservationService reservationService;
    private VolDAO volDAO;
    private HotelDAO hotelDAO;
    private UtilisateurDAO utilisateurDAO;
    private ObservableList<Reservation> allReservations;
    private boolean isAdmin = false;
    
    @FXML
    public void initialize() {
        reservationService = new ReservationService();
        volDAO = new VolDAO();
        hotelDAO = new HotelDAO();
        utilisateurDAO = new UtilisateurDAO();
        
        // Check if current user is admin
        isAdmin = SessionManager.getInstance().isAdmin();
        
        // Initialize status filter
        filterStatut.setItems(FXCollections.observableArrayList(
            "Tous les statuts", "EN_ATTENTE", "CONFIRMEE", "ANNULEE"
        ));
        filterStatut.setValue("Tous les statuts");
        
        // Set default date range (last 6 months)
        filterEndDate.setValue(LocalDate.now());
        filterStartDate.setValue(LocalDate.now().minusMonths(6));
        
        // Configure table columns
        setupTableColumns();
        
        // Load reservations
        loadHistory();
        
        // Update statistics
        updateStatistics();
    }
    
    /**
     * Configures table columns with custom cell factories.
     */
    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idReservation"));
        
        // Custom cell factory for ID - shorten
        colId.setCellFactory(column -> new TableCell<Reservation, String>() {
            @Override
            protected void updateItem(String idReservation, boolean empty) {
                super.updateItem(idReservation, empty);
                if (empty || idReservation == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    if (idReservation.length() > 18) {
                        setText(idReservation.substring(0, 18) + "...");
                        setTooltip(new Tooltip(idReservation));
                    } else {
                        setText(idReservation);
                    }
                    setStyle("-fx-text-fill: #475569; -fx-font-family: monospace; -fx-font-size: 11px;");
                }
            }
        });
        
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        
        // Utilisateur column (show for admin, hide for employees)
        colUtilisateur.setCellValueFactory(new PropertyValueFactory<>("utilisateurNom"));
        
        if (isAdmin) {
            colUtilisateur.setVisible(true);
            colUtilisateur.setCellFactory(column -> new TableCell<Reservation, String>() {
                @Override
                protected void updateItem(String utilisateurNom, boolean empty) {
                    super.updateItem(utilisateurNom, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        Reservation reservation = getTableView().getItems().get(getIndex());
                        
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
                        setStyle("-fx-text-fill: #475569; -fx-font-weight: 500;");
                    }
                }
            });
        } else {
            // Hide user column for employees (they only see their own data)
            colUtilisateur.setVisible(false);
        }
        
        // Status with colored badges
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colStatut.setCellFactory(column -> new TableCell<Reservation, String>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(statut);
                    badge.setStyle(getStatusStyle(statut));
                    badge.setPadding(new Insets(4, 12, 4, 12));
                    setGraphic(badge);
                    setText(null);
                }
            }
        });
        
        // Destination - fetch from Vol
        colDestination.setCellValueFactory(new PropertyValueFactory<>("idVol"));
        colDestination.setCellFactory(column -> new TableCell<Reservation, String>() {
            @Override
            protected void updateItem(String idVol, boolean empty) {
                super.updateItem(idVol, empty);
                if (empty || idVol == null) {
                    setText("-");
                } else {
                    Vol vol = volDAO.findById(idVol);
                    if (vol != null) {
                        setText(vol.getOrigine() + " → " + vol.getDestination());
                    } else {
                        setText("-");
                    }
                }
            }
        });
        
        // Hotel - fetch from Hotel
        colHotel.setCellValueFactory(new PropertyValueFactory<>("idHotel"));
        colHotel.setCellFactory(column -> new TableCell<Reservation, String>() {
            @Override
            protected void updateItem(String idHotel, boolean empty) {
                super.updateItem(idHotel, empty);
                if (empty || idHotel == null) {
                    setText("-");
                } else {
                    Hotel hotel = hotelDAO.findById(idHotel);
                    if (hotel != null) {
                        setText(hotel.getNom());
                    } else {
                        setText("-");
                    }
                }
            }
        });
        
        // Montant with currency formatting
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        colMontant.setCellFactory(column -> new TableCell<Reservation, Double>() {
            @Override
            protected void updateItem(Double montant, boolean empty) {
                super.updateItem(montant, empty);
                if (empty || montant == null) {
                    setText(null);
                } else {
                    setText(String.format("%.0f €", montant));
                    setStyle("-fx-text-fill: #1e293b; -fx-font-weight: bold;");
                }
            }
        });
        
        colMotif.setCellValueFactory(new PropertyValueFactory<>("motifVoyage"));
        colMotif.setCellFactory(column -> new TableCell<Reservation, String>() {
            @Override
            protected void updateItem(String motif, boolean empty) {
                super.updateItem(motif, empty);
                if (empty || motif == null || motif.isEmpty()) {
                    setText("-");
                } else {
                    if (motif.length() > 25) {
                        setText(motif.substring(0, 25) + "...");
                        setTooltip(new Tooltip(motif));
                    } else {
                        setText(motif);
                    }
                    setStyle("-fx-text-fill: #64748b;");
                }
            }
        });
        
        // Actions column
        addActionButtons();
    }
    
    /**
     * Returns CSS style for status badge.
     */
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
    
    /**
     * Adds action buttons to table.
     */
    private void addActionButtons() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnDetails = new Button("Détails");
            
            {
                btnDetails.setStyle("-fx-background-color: #4F8FF0; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand; -fx-background-radius: 6px;");
                
                btnDetails.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleViewDetails(reservation);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDetails);
                }
            }
        });
    }
    
    /**
     * Loads reservation history.
     */
    private void loadHistory() {
        if (isAdmin) {
            // Admin sees all reservations
            allReservations = FXCollections.observableArrayList(
                reservationService.getAllReservations()
            );
        } else {
            // Regular users see only their own
            int userId = SessionManager.getInstance().getCurrentUser().getIdUtilisateur();
            allReservations = FXCollections.observableArrayList(
                reservationService.getUserReservations(userId)
            );
        }
        
        applyFilters();
    }
    
    /**
     * Applies current filters to reservation list.
     */
    private void applyFilters() {
        LocalDate startDate = filterStartDate.getValue();
        LocalDate endDate = filterEndDate.getValue();
        String statut = filterStatut.getValue();
        
        List<Reservation> filtered = allReservations.stream()
            .filter(r -> {
                // Date filter
                if (startDate != null && r.getDateCreation().isBefore(startDate)) {
                    return false;
                }
                if (endDate != null && r.getDateCreation().isAfter(endDate)) {
                    return false;
                }
                
                // Status filter
                if (statut != null && !statut.equals("Tous les statuts") && !statut.equals(r.getStatut())) {
                    return false;
                }
                
                return true;
            })
            .collect(Collectors.toList());
        
        historyTable.setItems(FXCollections.observableArrayList(filtered));
        lblResultCount.setText(filtered.size() + " résultat" + (filtered.size() > 1 ? "s" : ""));
    }
    
    /**
     * Updates statistics cards.
     */
    private void updateStatistics() {
        if (allReservations == null) return;
        
        statsTotal.setText(String.valueOf(allReservations.size()));
        
        long completed = allReservations.stream()
            .filter(r -> "CONFIRMEE".equals(r.getStatut())).count();
        statsCompleted.setText(String.valueOf(completed));
        
        long cancelled = allReservations.stream()
            .filter(r -> "ANNULEE".equals(r.getStatut())).count();
        statsCancelled.setText(String.valueOf(cancelled));
        
        double totalAmount = allReservations.stream()
            .filter(r -> !"ANNULEE".equals(r.getStatut()))
            .mapToDouble(Reservation::getMontantTotal)
            .sum();
        statsTotalAmount.setText(String.format("%.0f €", totalAmount));
    }
    
    /**
     * Handles filter button click.
     */
    @FXML
    private void handleFilter() {
        applyFilters();
    }
    
    /**
     * Handles reset button click.
     */
    @FXML
    private void handleReset() {
        filterStartDate.setValue(LocalDate.now().minusMonths(6));
        filterEndDate.setValue(LocalDate.now());
        filterStatut.setValue("Tous les statuts");
        applyFilters();
    }
    
    /**
     * Handles export button click.
     */
    @FXML
    private void handleExport() {
        // Get current filtered data
        List<Reservation> dataToExport = historyTable.getItems().stream().collect(Collectors.toList());
        
        if (dataToExport.isEmpty()) {
            AlertUtil.showWarning("Aucune donnée", "Pas de données à exporter", 
                                "Il n'y a aucune réservation à exporter.");
            return;
        }
        
        // Create file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter l'historique");
        fileChooser.setInitialFileName("historique_voyages_" + LocalDate.now().toString());
        
        // Add file extensions
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"),
            new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf")
        );
        
        // Show save dialog
        Stage stage = (Stage) historyTable.getScene().getWindow();
        java.io.File file = fileChooser.showSaveDialog(stage);
        
        if (file != null) {
            String filePath = file.getAbsolutePath();
            boolean success = false;
            String format = "";
            
            // Determine format from file extension
            if (filePath.toLowerCase().endsWith(".xlsx")) {
                format = "Excel";
                success = ExcelExportUtil.exportToExcel(dataToExport, filePath, isAdmin);
            } else if (filePath.toLowerCase().endsWith(".pdf")) {
                format = "PDF";
                success = PdfExportUtil.exportToPdf(dataToExport, filePath, isAdmin);
            } else {
                AlertUtil.showError("Format invalide", "Extension non supportée", 
                                  "Veuillez choisir .xlsx ou .pdf");
                return;
            }
            
            if (success) {
                AlertUtil.showSuccess("Export réussi", 
                                    "L'historique a été exporté avec succès en format " + format + ".\n\n" +
                                    dataToExport.size() + " réservation(s) exportée(s).\n" +
                                    "Fichier: " + file.getName());
            } else {
                AlertUtil.showError("Erreur d'export", "L'export a échoué", 
                                  "Une erreur s'est produite lors de l'export. Veuillez réessayer.");
            }
        }
    }
    
    /**
     * Shows details of a reservation.
     */
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
            Vol vol = volDAO.findById(reservation.getIdVol());
            if (vol != null) {
                details.append("Vol: ").append(vol.getOrigine()).append(" → ").append(vol.getDestination()).append("\n");
                details.append("Compagnie: ").append(vol.getCompagnie()).append("\n");
            }
        }
        
        if (reservation.getIdHotel() != null) {
            Hotel hotel = hotelDAO.findById(reservation.getIdHotel());
            if (hotel != null) {
                details.append("\nHôtel: ").append(hotel.getNom()).append("\n");
                details.append("Ville: ").append(hotel.getVille()).append("\n");
            }
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
    
    /**
     * Returns to dashboard.
     */
    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) historyTable.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/fxml/DashboardView.fxml", "Dashboard", 1280, 800);
        } catch (Exception e) {
            System.err.println("Error navigating back: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
