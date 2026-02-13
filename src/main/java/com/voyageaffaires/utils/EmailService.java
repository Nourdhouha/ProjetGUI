package com.voyageaffaires.utils;

import com.voyageaffaires.models.Reservation;
import com.voyageaffaires.models.Utilisateur;
import com.voyageaffaires.models.Vol;
import com.voyageaffaires.models.Hotel;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Properties;

/**
 * Service class for sending email notifications.
 * Handles all email-related operations including reservation confirmations.
 */
public class EmailService {
    
    private Properties emailProperties;
    private boolean emailEnabled;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(Locale.FRANCE);
    
    /**
     * Constructor that loads email configuration.
     */
    public EmailService() {
        loadEmailConfiguration();
    }
    
    /**
     * Loads email configuration from email.properties file.
     */
    private void loadEmailConfiguration() {
        emailProperties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("email.properties")) {
            if (input == null) {
                System.err.println("Unable to find email.properties file");
                emailEnabled = false;
                return;
            }
            emailProperties.load(input);
            emailEnabled = Boolean.parseBoolean(emailProperties.getProperty("email.notifications.enabled", "false"));
            
            if (!emailEnabled) {
                System.out.println("Email notifications are disabled in configuration");
            }
        } catch (IOException e) {
            System.err.println("Error loading email configuration: " + e.getMessage());
            emailEnabled = false;
        }
    }
    
    /**
     * Sends a reservation confirmation email to the user.
     * 
     * @param user User who made the reservation
     * @param reservation Reservation details
     * @param vol Flight details (optional)
     * @param hotel Hotel details (optional)
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendReservationConfirmation(Utilisateur user, Reservation reservation, Vol vol, Hotel hotel) {
        if (!emailEnabled) {
            System.out.println("Email notifications are disabled. Skipping email send.");
            return false;
        }
        
        if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
            System.err.println("Cannot send email: User or user email is null");
            return false;
        }
        
        try {
            // Create email session
            Session session = createEmailSession();
            
            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(
                emailProperties.getProperty("email.sender.address"),
                emailProperties.getProperty("email.sender.name")
            ));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
            
            String subject = emailProperties.getProperty("email.subject.prefix", "[Voyage & Affaires]") 
                           + " Confirmation de Réservation - " + reservation.getIdReservation();
            message.setSubject(subject);
            
            // Build email content
            String emailContent = buildReservationEmailContent(user, reservation, vol, hotel);
            message.setContent(emailContent, "text/html; charset=utf-8");
            
            // Send email
            Transport.send(message);
            System.out.println("Reservation confirmation email sent successfully to: " + user.getEmail());
            return true;
            
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Creates an email session with authentication.
     * 
     * @return Email session
     */
    private Session createEmailSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", emailProperties.getProperty("email.smtp.auth"));
        props.put("mail.smtp.starttls.enable", emailProperties.getProperty("email.smtp.starttls.enable"));
        props.put("mail.smtp.host", emailProperties.getProperty("email.smtp.host"));
        props.put("mail.smtp.port", emailProperties.getProperty("email.smtp.port"));
        props.put("mail.smtp.ssl.trust", emailProperties.getProperty("email.smtp.host"));
        
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    emailProperties.getProperty("email.sender.address"),
                    emailProperties.getProperty("email.sender.password")
                );
            }
        });
    }
    
    /**
     * Builds the HTML email content for reservation confirmation.
     * 
     * @param user User details
     * @param reservation Reservation details
     * @param vol Flight details (optional)
     * @param hotel Hotel details (optional)
     * @return HTML email content
     */
    private String buildReservationEmailContent(Utilisateur user, Reservation reservation, Vol vol, Hotel hotel) {
        StringBuilder content = new StringBuilder();
        
        content.append("<!DOCTYPE html>");
        content.append("<html>");
        content.append("<head>");
        content.append("<style>");
        content.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        content.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        content.append(".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }");
        content.append(".content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }");
        content.append(".section { background: white; padding: 20px; margin: 20px 0; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
        content.append(".label { font-weight: bold; color: #667eea; }");
        content.append(".total { font-size: 24px; font-weight: bold; color: #667eea; text-align: center; padding: 20px; background: #e8eaf6; border-radius: 8px; margin-top: 20px; }");
        content.append(".footer { text-align: center; color: #999; padding: 20px; font-size: 12px; }");
        content.append("table { width: 100%; border-collapse: collapse; }");
        content.append("td { padding: 8px 0; }");
        content.append("</style>");
        content.append("</head>");
        content.append("<body>");
        content.append("<div class='container'>");
        
        // Header
        content.append("<div class='header'>");
        content.append("<h1>✈️ Confirmation de Réservation</h1>");
        content.append("<p>Votre voyage est confirmé !</p>");
        content.append("</div>");
        
        content.append("<div class='content'>");
        
        // Greeting
        content.append("<p>Bonjour ").append(user.getPrenom()).append(" ").append(user.getNom()).append(",</p>");
        content.append("<p>Merci pour votre réservation. Voici le récapitulatif de votre voyage d'affaires :</p>");
        
        // Reservation Details
        content.append("<div class='section'>");
        content.append("<h2>📋 Détails de la Réservation</h2>");
        content.append("<table>");
        content.append("<tr><td class='label'>Numéro de réservation :</td><td>").append(reservation.getIdReservation()).append("</td></tr>");
        content.append("<tr><td class='label'>Date de création :</td><td>").append(reservation.getDateCreation().format(DATE_FORMATTER)).append("</td></tr>");
        content.append("<tr><td class='label'>Statut :</td><td>").append(formatStatus(reservation.getStatut())).append("</td></tr>");
        
        if (reservation.getMotifVoyage() != null && !reservation.getMotifVoyage().isEmpty()) {
            content.append("<tr><td class='label'>Motif du voyage :</td><td>").append(reservation.getMotifVoyage()).append("</td></tr>");
        }
        content.append("</table>");
        content.append("</div>");
        
        // Flight Details
        if (vol != null) {
            content.append("<div class='section'>");
            content.append("<h2>✈️ Détails du Vol</h2>");
            content.append("<table>");
            content.append("<tr><td class='label'>Compagnie :</td><td>").append(vol.getCompagnie()).append("</td></tr>");
            content.append("<tr><td class='label'>Numéro de vol :</td><td>").append(vol.getIdVol()).append("</td></tr>");
            content.append("<tr><td class='label'>Origine :</td><td>").append(vol.getOrigine()).append("</td></tr>");
            content.append("<tr><td class='label'>Destination :</td><td>").append(vol.getDestination()).append("</td></tr>");
            content.append("<tr><td class='label'>Date de départ :</td><td>").append(vol.getDateDepart().format(DATETIME_FORMATTER)).append("</td></tr>");
            content.append("<tr><td class='label'>Date d'arrivée :</td><td>").append(vol.getDateArrivee().format(DATETIME_FORMATTER)).append("</td></tr>");
            content.append("<tr><td class='label'>Classe :</td><td>").append(vol.getClasse()).append("</td></tr>");
            content.append("<tr><td class='label'>Prix :</td><td>").append(CURRENCY_FORMATTER.format(vol.getPrix())).append("</td></tr>");
            content.append("</table>");
            content.append("</div>");
        }
        
        // Hotel Details
        if (hotel != null) {
            content.append("<div class='section'>");
            content.append("<h2>🏨 Détails de l'Hôtel</h2>");
            content.append("<table>");
            content.append("<tr><td class='label'>Nom :</td><td>").append(hotel.getNom()).append("</td></tr>");
            content.append("<tr><td class='label'>Ville :</td><td>").append(hotel.getVille()).append("</td></tr>");
            content.append("<tr><td class='label'>Adresse :</td><td>").append(hotel.getAdresse()).append("</td></tr>");
            content.append("<tr><td class='label'>Étoiles :</td><td>").append("⭐".repeat(hotel.getEtoiles())).append(" (").append(hotel.getEtoiles()).append(" étoiles)</td></tr>");
            
            if (reservation.getDateCheckin() != null) {
                content.append("<tr><td class='label'>Check-in :</td><td>").append(reservation.getDateCheckin().format(DATE_FORMATTER)).append("</td></tr>");
            }
            if (reservation.getDateCheckout() != null) {
                content.append("<tr><td class='label'>Check-out :</td><td>").append(reservation.getDateCheckout().format(DATE_FORMATTER)).append("</td></tr>");
            }
            content.append("<tr><td class='label'>Nombre de chambres :</td><td>").append(reservation.getNombreChambres()).append("</td></tr>");
            content.append("<tr><td class='label'>Prix par nuit :</td><td>").append(CURRENCY_FORMATTER.format(hotel.getPrixParNuit())).append("</td></tr>");
            content.append("</table>");
            content.append("</div>");
        }
        
        // Total Amount
        content.append("<div class='total'>");
        content.append("💰 Montant Total : ").append(CURRENCY_FORMATTER.format(reservation.getMontantTotal()));
        content.append("</div>");
        
        // Additional Information
        if (reservation.getCommentaire() != null && !reservation.getCommentaire().isEmpty()) {
            content.append("<div class='section'>");
            content.append("<h2>📝 Commentaire</h2>");
            content.append("<p>").append(reservation.getCommentaire()).append("</p>");
            content.append("</div>");
        }
        
        // Closing
        content.append("<p style='margin-top: 30px;'>Nous vous souhaitons un excellent voyage !</p>");
        content.append("<p>Pour toute question, n'hésitez pas à nous contacter.</p>");
        content.append("<p>Cordialement,<br><strong>L'équipe Voyage & Affaires</strong></p>");
        
        content.append("</div>");
        
        // Footer
        content.append("<div class='footer'>");
        content.append("<p>Cet email a été envoyé automatiquement. Merci de ne pas y répondre.</p>");
        content.append("<p>&copy; 2026 Voyage & Affaires - Tous droits réservés</p>");
        content.append("</div>");
        
        content.append("</div>");
        content.append("</body>");
        content.append("</html>");
        
        return content.toString();
    }
    
    /**
     * Formats reservation status for display.
     * 
     * @param status Status code
     * @return Formatted status
     */
    private String formatStatus(String status) {
        switch (status) {
            case "EN_ATTENTE":
                return "En attente d'approbation";
            case "APPROUVEE":
                return "Approuvée";
            case "REJETEE":
                return "Rejetée";
            case "ANNULEE":
                return "Annulée";
            case "COMPLETEE":
                return "Complétée";
            default:
                return status;
        }
    }
    
    /**
     * Checks if email notifications are enabled.
     * 
     * @return true if enabled, false otherwise
     */
    public boolean isEmailEnabled() {
        return emailEnabled;
    }
    
    /**
     * Sends a cancellation notification email.
     * 
     * @param user User who cancelled the reservation
     * @param reservation Cancelled reservation details
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendCancellationNotification(Utilisateur user, Reservation reservation) {
        if (!emailEnabled) {
            System.out.println("Email notifications are disabled. Skipping email send.");
            return false;
        }
        
        if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
            System.err.println("Cannot send email: User or user email is null");
            return false;
        }
        
        try {
            Session session = createEmailSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(
                emailProperties.getProperty("email.sender.address"),
                emailProperties.getProperty("email.sender.name")
            ));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
            
            String subject = emailProperties.getProperty("email.subject.prefix", "[Voyage & Affaires]") 
                           + " Annulation de Réservation - " + reservation.getIdReservation();
            message.setSubject(subject);
            
            String content = buildCancellationEmailContent(user, reservation);
            message.setContent(content, "text/html; charset=utf-8");
            
            Transport.send(message);
            System.out.println("Cancellation notification email sent successfully to: " + user.getEmail());
            return true;
            
        } catch (Exception e) {
            System.err.println("Error sending cancellation email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Builds the HTML email content for cancellation notification.
     * 
     * @param user User details
     * @param reservation Cancelled reservation details
     * @return HTML email content
     */
    private String buildCancellationEmailContent(Utilisateur user, Reservation reservation) {
        StringBuilder content = new StringBuilder();
        
        content.append("<!DOCTYPE html>");
        content.append("<html><head><style>");
        content.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        content.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        content.append(".header { background: #f44336; color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }");
        content.append(".content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }");
        content.append("</style></head><body>");
        content.append("<div class='container'>");
        content.append("<div class='header'><h1>❌ Annulation de Réservation</h1></div>");
        content.append("<div class='content'>");
        content.append("<p>Bonjour ").append(user.getPrenom()).append(" ").append(user.getNom()).append(",</p>");
        content.append("<p>Votre réservation <strong>").append(reservation.getIdReservation()).append("</strong> a été annulée avec succès.</p>");
        content.append("<p>Si vous avez des questions, n'hésitez pas à nous contacter.</p>");
        content.append("<p>Cordialement,<br><strong>L'équipe Voyage & Affaires</strong></p>");
        content.append("</div></div></body></html>");
        
        return content.toString();
    }
}
