package com.voyageaffaires.controllers;

import com.voyageaffaires.services.ReservationService;
import com.voyageaffaires.utils.AlertUtil;
import com.voyageaffaires.utils.NavigationUtil;
import com.voyageaffaires.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

public class ReportingController {
    
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label statsTotal;
    @FXML private Label statsCost;
    
    private ReservationService reservationService;
    
    @FXML
    public void initialize() {
        reservationService = new ReservationService();
        
        // Set default dates (last 30 days)
        endDatePicker.setValue(LocalDate.now());
        startDatePicker.setValue(LocalDate.now().minusDays(30));
        
        loadStatistics();
    }
    
    private void loadStatistics() {
        try {
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();
            
            if (start != null && end != null) {
                var reservations = reservationService.getReservationsByDateRange(start, end);
                
                statsTotal.setText(String.valueOf(reservations.size()));
                
                double totalCost = reservations.stream()
                    .mapToDouble(r -> r.getMontantTotal())
                    .sum();
                statsCost.setText(String.format("%.2f €", totalCost));
            }
        } catch (Exception e) {
            System.err.println("Error loading statistics: " + e.getMessage());
        }
    }
    
    @FXML
    private void exportPDF() {
        AlertUtil.showInfo("Export PDF", "Fonctionnalité en développement", 
                          "L'export PDF sera disponible prochainement.");
    }
    
    @FXML
    private void exportExcel() {
        AlertUtil.showInfo("Export Excel", "Fonctionnalité en développement", 
                          "L'export Excel sera disponible prochainement.");
    }
    
    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) startDatePicker.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/fxml/DashboardView.fxml", "Dashboard", 1280, 800);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
