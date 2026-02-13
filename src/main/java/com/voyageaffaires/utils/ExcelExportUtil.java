package com.voyageaffaires.utils;

import com.voyageaffaires.dao.HotelDAO;
import com.voyageaffaires.dao.UtilisateurDAO;
import com.voyageaffaires.dao.VolDAO;
import com.voyageaffaires.models.Hotel;
import com.voyageaffaires.models.Reservation;
import com.voyageaffaires.models.Utilisateur;
import com.voyageaffaires.models.Vol;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for exporting reservations to Excel format.
 */
public class ExcelExportUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Exports reservations to Excel file.
     * 
     * @param reservations List of reservations to export
     * @param filePath Path where to save the Excel file
     * @param includeUserColumn Whether to include user column (for admin)
     * @return true if export successful, false otherwise
     */
    public static boolean exportToExcel(List<Reservation> reservations, String filePath, boolean includeUserColumn) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Historique des Voyages");
        
        try {
            // Create DAOs
            VolDAO volDAO = new VolDAO();
            HotelDAO hotelDAO = new HotelDAO();
            UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
            
            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            CellStyle statusStyle = createStatusStyle(workbook);
            
            // Create header row
            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);
            int colNum = 0;
            
            createCell(headerRow, colNum++, "Numéro Réservation", headerStyle);
            createCell(headerRow, colNum++, "Date", headerStyle);
            
            if (includeUserColumn) {
                createCell(headerRow, colNum++, "Utilisateur", headerStyle);
            }
            
            createCell(headerRow, colNum++, "Statut", headerStyle);
            createCell(headerRow, colNum++, "Origine", headerStyle);
            createCell(headerRow, colNum++, "Destination", headerStyle);
            createCell(headerRow, colNum++, "Compagnie Aérienne", headerStyle);
            createCell(headerRow, colNum++, "Hôtel", headerStyle);
            createCell(headerRow, colNum++, "Ville Hôtel", headerStyle);
            createCell(headerRow, colNum++, "Check-in", headerStyle);
            createCell(headerRow, colNum++, "Check-out", headerStyle);
            createCell(headerRow, colNum++, "Chambres", headerStyle);
            createCell(headerRow, colNum++, "Montant (€)", headerStyle);
            createCell(headerRow, colNum++, "Motif", headerStyle);
            createCell(headerRow, colNum++, "Commentaire", headerStyle);
            
            // Add data rows
            for (Reservation reservation : reservations) {
                Row row = sheet.createRow(rowNum++);
                colNum = 0;
                
                // Reservation ID
                createCell(row, colNum++, reservation.getIdReservation(), null);
                
                // Date
                Cell dateCell = row.createCell(colNum++);
                dateCell.setCellValue(reservation.getDateCreation().format(DATE_FORMATTER));
                dateCell.setCellStyle(dateStyle);
                
                // User (if admin)
                if (includeUserColumn) {
                    String userName = reservation.getUtilisateurNom();
                    if (userName == null || userName.isEmpty()) {
                        Utilisateur user = utilisateurDAO.findById(reservation.getIdUtilisateur());
                        if (user != null) {
                            userName = user.getPrenom() + " " + user.getNom();
                        } else {
                            userName = "Utilisateur #" + reservation.getIdUtilisateur();
                        }
                    }
                    createCell(row, colNum++, userName, null);
                }
                
                // Status
                Cell statusCell = row.createCell(colNum++);
                statusCell.setCellValue(reservation.getStatut());
                statusCell.setCellStyle(statusStyle);
                
                // Flight information
                if (reservation.getIdVol() != null) {
                    Vol vol = volDAO.findById(reservation.getIdVol());
                    if (vol != null) {
                        createCell(row, colNum++, vol.getOrigine(), null);
                        createCell(row, colNum++, vol.getDestination(), null);
                        createCell(row, colNum++, vol.getCompagnie(), null);
                    } else {
                        createCell(row, colNum++, "-", null);
                        createCell(row, colNum++, "-", null);
                        createCell(row, colNum++, "-", null);
                    }
                } else {
                    createCell(row, colNum++, "-", null);
                    createCell(row, colNum++, "-", null);
                    createCell(row, colNum++, "-", null);
                }
                
                // Hotel information
                if (reservation.getIdHotel() != null) {
                    Hotel hotel = hotelDAO.findById(reservation.getIdHotel());
                    if (hotel != null) {
                        createCell(row, colNum++, hotel.getNom(), null);
                        createCell(row, colNum++, hotel.getVille(), null);
                    } else {
                        createCell(row, colNum++, "-", null);
                        createCell(row, colNum++, "-", null);
                    }
                    
                    if (reservation.getDateCheckin() != null) {
                        Cell checkinCell = row.createCell(colNum++);
                        checkinCell.setCellValue(reservation.getDateCheckin().format(DATE_FORMATTER));
                        checkinCell.setCellStyle(dateStyle);
                    } else {
                        createCell(row, colNum++, "-", null);
                    }
                    
                    if (reservation.getDateCheckout() != null) {
                        Cell checkoutCell = row.createCell(colNum++);
                        checkoutCell.setCellValue(reservation.getDateCheckout().format(DATE_FORMATTER));
                        checkoutCell.setCellStyle(dateStyle);
                    } else {
                        createCell(row, colNum++, "-", null);
                    }
                    
                    createCell(row, colNum++, String.valueOf(reservation.getNombreChambres()), null);
                } else {
                    createCell(row, colNum++, "-", null);
                    createCell(row, colNum++, "-", null);
                    createCell(row, colNum++, "-", null);
                    createCell(row, colNum++, "-", null);
                    createCell(row, colNum++, "-", null);
                }
                
                // Amount
                Cell amountCell = row.createCell(colNum++);
                amountCell.setCellValue(reservation.getMontantTotal());
                amountCell.setCellStyle(currencyStyle);
                
                // Motif
                createCell(row, colNum++, reservation.getMotifVoyage() != null ? reservation.getMotifVoyage() : "-", null);
                
                // Commentaire
                createCell(row, colNum++, reservation.getCommentaire() != null ? reservation.getCommentaire() : "-", null);
            }
            
            // Auto-size columns
            for (int i = 0; i < (includeUserColumn ? 15 : 14); i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to file
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
            
            workbook.close();
            return true;
            
        } catch (IOException e) {
            System.err.println("Error exporting to Excel: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value : "");
        if (style != null) {
            cell.setCellStyle(style);
        }
    }
    
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private static CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private static CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00 €"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }
    
    private static CellStyle createStatusStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
