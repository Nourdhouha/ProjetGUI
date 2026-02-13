package com.voyageaffaires.dao;

import com.voyageaffaires.models.Reservation;
import com.voyageaffaires.models.Vol;
import com.voyageaffaires.models.Hotel;
import com.voyageaffaires.utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Reservation entity.
 */
public class ReservationDAO {
    
    private Connection connection;
    private VolDAO volDAO;
    private HotelDAO hotelDAO;
    
    public ReservationDAO() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            this.volDAO = new VolDAO();
            this.hotelDAO = new HotelDAO();
        } catch (SQLException e) {
            System.err.println("Error establishing database connection: " + e.getMessage());
        }
    }
    
    /**
     * Finds a reservation by ID.
     * 
     * @param idReservation Reservation ID
     * @return Reservation object if found, null otherwise
     */
    public Reservation findById(String idReservation) {
        String query = "SELECT r.*, u.nom AS user_nom, u.prenom AS user_prenom " +
                      "FROM reservation r " +
                      "INNER JOIN utilisateur u ON r.id_utilisateur = u.id_utilisateur " +
                      "WHERE r.id_reservation = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, idReservation);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToReservation(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding reservation by ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Retrieves all reservations.
     * 
     * @return List of all reservations
     */
    public List<Reservation> findAll() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, u.nom AS user_nom, u.prenom AS user_prenom " +
                      "FROM reservation r " +
                      "INNER JOIN utilisateur u ON r.id_utilisateur = u.id_utilisateur " +
                      "ORDER BY r.date_creation DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all reservations: " + e.getMessage());
        }
        return reservations;
    }
    
    /**
     * Retrieves reservations for a specific user.
     * 
     * @param userId User ID
     * @return List of user's reservations
     */
    public List<Reservation> findByUserId(int userId) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, u.nom AS user_nom, u.prenom AS user_prenom " +
                      "FROM reservation r " +
                      "INNER JOIN utilisateur u ON r.id_utilisateur = u.id_utilisateur " +
                      "WHERE r.id_utilisateur = ? ORDER BY r.date_creation DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user reservations: " + e.getMessage());
        }
        return reservations;
    }
    
    /**
     * Saves a new reservation to the database.
     * 
     * @param reservation Reservation to save
     * @return true if successful, false otherwise
     */
    public boolean save(Reservation reservation) {
        String query = "INSERT INTO reservation (id_reservation, date_creation, statut, montant_total, " +
                      "id_utilisateur, id_vol, id_hotel, date_checkin, date_checkout, nombre_chambres, " +
                      "motif_voyage, commentaire) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, reservation.getIdReservation());
            stmt.setDate(2, Date.valueOf(reservation.getDateCreation()));
            stmt.setString(3, reservation.getStatut());
            stmt.setDouble(4, reservation.getMontantTotal());
            stmt.setInt(5, reservation.getIdUtilisateur());
            stmt.setString(6, reservation.getIdVol());
            stmt.setString(7, reservation.getIdHotel());
            
            if (reservation.getDateCheckin() != null) {
                stmt.setDate(8, Date.valueOf(reservation.getDateCheckin()));
            } else {
                stmt.setNull(8, Types.DATE);
            }
            
            if (reservation.getDateCheckout() != null) {
                stmt.setDate(9, Date.valueOf(reservation.getDateCheckout()));
            } else {
                stmt.setNull(9, Types.DATE);
            }
            
            stmt.setInt(10, reservation.getNombreChambres());
            stmt.setString(11, reservation.getMotifVoyage());
            stmt.setString(12, reservation.getCommentaire());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving reservation: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Updates an existing reservation.
     * 
     * @param reservation Reservation to update
     * @return true if successful, false otherwise
     */
    public boolean update(Reservation reservation) {
        String query = "UPDATE reservation SET statut = ?, montant_total = ?, id_vol = ?, id_hotel = ?, " +
                      "date_checkin = ?, date_checkout = ?, nombre_chambres = ?, motif_voyage = ?, " +
                      "commentaire = ? WHERE id_reservation = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, reservation.getStatut());
            stmt.setDouble(2, reservation.getMontantTotal());
            stmt.setString(3, reservation.getIdVol());
            stmt.setString(4, reservation.getIdHotel());
            
            if (reservation.getDateCheckin() != null) {
                stmt.setDate(5, Date.valueOf(reservation.getDateCheckin()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            if (reservation.getDateCheckout() != null) {
                stmt.setDate(6, Date.valueOf(reservation.getDateCheckout()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            
            stmt.setInt(7, reservation.getNombreChambres());
            stmt.setString(8, reservation.getMotifVoyage());
            stmt.setString(9, reservation.getCommentaire());
            stmt.setString(10, reservation.getIdReservation());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating reservation: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Updates reservation status.
     * 
     * @param idReservation Reservation ID
     * @param statut New status
     * @return true if successful, false otherwise
     */
    public boolean updateStatus(String idReservation, String statut) {
        String query = "UPDATE reservation SET statut = ? WHERE id_reservation = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, statut);
            stmt.setString(2, idReservation);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating reservation status: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Deletes a reservation.
     * 
     * @param idReservation Reservation ID to delete
     * @return true if successful, false otherwise
     */
    public boolean delete(String idReservation) {
        String query = "DELETE FROM reservation WHERE id_reservation = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, idReservation);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Gets reservation statistics for reporting.
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return List of reservations in the date range
     */
    public List<Reservation> getReservationsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, u.nom AS user_nom, u.prenom AS user_prenom " +
                      "FROM reservation r " +
                      "INNER JOIN utilisateur u ON r.id_utilisateur = u.id_utilisateur " +
                      "WHERE r.date_creation BETWEEN ? AND ? ORDER BY r.date_creation DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving reservations by date range: " + e.getMessage());
        }
        return reservations;
    }
    
    /**
     * Maps a ResultSet row to a Reservation object.
     * 
     * @param rs ResultSet
     * @return Reservation object
     * @throws SQLException if error occurs
     */
    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setIdReservation(rs.getString("id_reservation"));
        
        Date dateCreation = rs.getDate("date_creation");
        if (dateCreation != null) {
            reservation.setDateCreation(dateCreation.toLocalDate());
        }
        
        reservation.setStatut(rs.getString("statut"));
        reservation.setMontantTotal(rs.getDouble("montant_total"));
        reservation.setIdUtilisateur(rs.getInt("id_utilisateur"));
        reservation.setIdVol(rs.getString("id_vol"));
        reservation.setIdHotel(rs.getString("id_hotel"));
        
        Date dateCheckin = rs.getDate("date_checkin");
        if (dateCheckin != null) {
            reservation.setDateCheckin(dateCheckin.toLocalDate());
        }
        
        Date dateCheckout = rs.getDate("date_checkout");
        if (dateCheckout != null) {
            reservation.setDateCheckout(dateCheckout.toLocalDate());
        }
        
        reservation.setNombreChambres(rs.getInt("nombre_chambres"));
        reservation.setMotifVoyage(rs.getString("motif_voyage"));
        reservation.setCommentaire(rs.getString("commentaire"));
        
        // Set user name
        String userPrenom = rs.getString("user_prenom");
        String userNom = rs.getString("user_nom");
        if (userPrenom != null && userNom != null) {
            reservation.setUtilisateurNom(userPrenom + " " + userNom);
        }
        
        // Load related entities
        if (reservation.getIdVol() != null) {
            reservation.setVol(volDAO.findById(reservation.getIdVol()));
        }
        if (reservation.getIdHotel() != null) {
            reservation.setHotel(hotelDAO.findById(reservation.getIdHotel()));
        }
        
        return reservation;
    }
}
