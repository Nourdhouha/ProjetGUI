package com.voyageaffaires.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.voyageaffaires.dao.HotelDAO;
import com.voyageaffaires.dao.UtilisateurDAO;
import com.voyageaffaires.dao.VolDAO;
import com.voyageaffaires.models.Hotel;
import com.voyageaffaires.models.Reservation;
import com.voyageaffaires.models.Utilisateur;
import com.voyageaffaires.models.Vol;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for exporting reservations to PDF format.
 */
public class PdfExportUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
    private static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.DARK_GRAY);
    
    /**
     * Exports reservations to PDF file.
     * 
     * @param reservations List of reservations to export
     * @param filePath Path where to save the PDF file
     * @param includeUserColumn Whether to include user column (for admin)
     * @return true if export successful, false otherwise
     */
    public static boolean exportToPdf(List<Reservation> reservations, String filePath, boolean includeUserColumn) {
        Document document = new Document(PageSize.A4.rotate()); // Landscape for more columns
        
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Create DAOs
            VolDAO volDAO = new VolDAO();
            HotelDAO hotelDAO = new HotelDAO();
            UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
            
            // Add title
            Paragraph title = new Paragraph("Historique des Voyages d'Affaires", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);
            
            // Add generation date
            Paragraph date = new Paragraph("Généré le " + LocalDate.now().format(DATE_FORMATTER), SMALL_FONT);
            date.setAlignment(Element.ALIGN_CENTER);
            date.setSpacingAfter(20);
            document.add(date);
            
            // Add statistics
            addStatistics(document, reservations);
            
            // Create table
            int numColumns = includeUserColumn ? 9 : 8;
            PdfPTable table = new PdfPTable(numColumns);
            table.setWidthPercentage(100);
            table.setSpacingBefore(15);
            table.setSpacingAfter(15);
            
            // Set column widths
            if (includeUserColumn) {
                table.setWidths(new float[]{2f, 1.5f, 2f, 1.5f, 3f, 2.5f, 1.5f, 2f, 1.5f});
            } else {
                table.setWidths(new float[]{2f, 1.5f, 1.5f, 3f, 2.5f, 1.5f, 2f, 1.5f});
            }
            
            // Add headers
            addTableHeader(table, includeUserColumn);
            
            // Add data rows
            for (Reservation reservation : reservations) {
                // Reservation ID (shortened)
                String shortId = reservation.getIdReservation();
                if (shortId.length() > 16) {
                    shortId = shortId.substring(0, 16) + "...";
                }
                addCell(table, shortId, NORMAL_FONT, Element.ALIGN_LEFT);
                
                // Date
                addCell(table, reservation.getDateCreation().format(DATE_FORMATTER), NORMAL_FONT, Element.ALIGN_CENTER);
                
                // User (if admin)
                if (includeUserColumn) {
                    String userName = reservation.getUtilisateurNom();
                    if (userName == null || userName.isEmpty()) {
                        Utilisateur user = utilisateurDAO.findById(reservation.getIdUtilisateur());
                        if (user != null) {
                            userName = user.getPrenom() + " " + user.getNom();
                        } else {
                            userName = "User #" + reservation.getIdUtilisateur();
                        }
                    }
                    addCell(table, userName, NORMAL_FONT, Element.ALIGN_LEFT);
                }
                
                // Status
                BaseColor statusColor = getStatusColor(reservation.getStatut());
                Font statusFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, statusColor);
                addCell(table, reservation.getStatut(), statusFont, Element.ALIGN_CENTER);
                
                // Destination (from flight)
                if (reservation.getIdVol() != null) {
                    Vol vol = volDAO.findById(reservation.getIdVol());
                    if (vol != null) {
                        addCell(table, vol.getOrigine() + " → " + vol.getDestination(), NORMAL_FONT, Element.ALIGN_LEFT);
                    } else {
                        addCell(table, "-", NORMAL_FONT, Element.ALIGN_CENTER);
                    }
                } else {
                    addCell(table, "-", NORMAL_FONT, Element.ALIGN_CENTER);
                }
                
                // Hotel
                if (reservation.getIdHotel() != null) {
                    Hotel hotel = hotelDAO.findById(reservation.getIdHotel());
                    if (hotel != null) {
                        addCell(table, hotel.getNom() + " (" + hotel.getVille() + ")", NORMAL_FONT, Element.ALIGN_LEFT);
                    } else {
                        addCell(table, "-", NORMAL_FONT, Element.ALIGN_CENTER);
                    }
                } else {
                    addCell(table, "-", NORMAL_FONT, Element.ALIGN_CENTER);
                }
                
                // Check-in
                if (reservation.getDateCheckin() != null) {
                    addCell(table, reservation.getDateCheckin().format(DATE_FORMATTER), NORMAL_FONT, Element.ALIGN_CENTER);
                } else {
                    addCell(table, "-", NORMAL_FONT, Element.ALIGN_CENTER);
                }
                
                // Motif (truncated)
                String motif = reservation.getMotifVoyage() != null ? reservation.getMotifVoyage() : "-";
                if (motif.length() > 25) {
                    motif = motif.substring(0, 25) + "...";
                }
                addCell(table, motif, SMALL_FONT, Element.ALIGN_LEFT);
                
                // Amount
                addCell(table, String.format("%.0f €", reservation.getMontantTotal()), NORMAL_FONT, Element.ALIGN_RIGHT);
            }
            
            document.add(table);
            
            // Add footer
            Paragraph footer = new Paragraph("Total: " + reservations.size() + " réservation(s)", SMALL_FONT);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);
            
            document.close();
            return true;
            
        } catch (Exception e) {
            System.err.println("Error exporting to PDF: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static void addStatistics(Document document, List<Reservation> reservations) throws DocumentException {
        PdfPTable statsTable = new PdfPTable(4);
        statsTable.setWidthPercentage(80);
        statsTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        statsTable.setSpacingAfter(15);
        
        long total = reservations.size();
        long confirmees = reservations.stream().filter(r -> "CONFIRMEE".equals(r.getStatut())).count();
        long annulees = reservations.stream().filter(r -> "ANNULEE".equals(r.getStatut())).count();
        double montantTotal = reservations.stream()
            .filter(r -> !"ANNULEE".equals(r.getStatut()))
            .mapToDouble(Reservation::getMontantTotal)
            .sum();
        
        addStatCell(statsTable, "Total Voyages", String.valueOf(total), new BaseColor(79, 143, 240));
        addStatCell(statsTable, "Confirmés", String.valueOf(confirmees), new BaseColor(16, 185, 129));
        addStatCell(statsTable, "Annulés", String.valueOf(annulees), new BaseColor(239, 68, 68));
        addStatCell(statsTable, "Montant Total", String.format("%.0f €", montantTotal), new BaseColor(79, 143, 240));
        
        document.add(statsTable);
    }
    
    private static void addStatCell(PdfPTable table, String label, String value, BaseColor color) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(10);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        
        Paragraph labelPara = new Paragraph(label, new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.DARK_GRAY));
        labelPara.setAlignment(Element.ALIGN_CENTER);
        
        Paragraph valuePara = new Paragraph(value, new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, color));
        valuePara.setAlignment(Element.ALIGN_CENTER);
        valuePara.setSpacingBefore(5);
        
        cell.addElement(labelPara);
        cell.addElement(valuePara);
        
        table.addCell(cell);
    }
    
    private static void addTableHeader(PdfPTable table, boolean includeUserColumn) {
        addHeaderCell(table, "Numéro");
        addHeaderCell(table, "Date");
        
        if (includeUserColumn) {
            addHeaderCell(table, "Utilisateur");
        }
        
        addHeaderCell(table, "Statut");
        addHeaderCell(table, "Destination");
        addHeaderCell(table, "Hôtel");
        addHeaderCell(table, "Check-in");
        addHeaderCell(table, "Motif");
        addHeaderCell(table, "Montant");
    }
    
    private static void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setBackgroundColor(new BaseColor(79, 143, 240));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8);
        table.addCell(cell);
    }
    
    private static void addCell(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        table.addCell(cell);
    }
    
    private static BaseColor getStatusColor(String statut) {
        switch (statut) {
            case "CONFIRMEE":
                return new BaseColor(16, 185, 129); // Green
            case "ANNULEE":
                return new BaseColor(239, 68, 68); // Red
            case "EN_ATTENTE":
                return new BaseColor(245, 158, 11); // Orange
            default:
                return BaseColor.DARK_GRAY;
        }
    }
}
