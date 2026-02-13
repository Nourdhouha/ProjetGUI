package com.voyageaffaires.services;

import com.voyageaffaires.dao.ReservationDAO;
import com.voyageaffaires.dao.VolDAO;
import com.voyageaffaires.dao.HotelDAO;
import com.voyageaffaires.models.Reservation;
import com.voyageaffaires.models.Vol;
import com.voyageaffaires.models.Hotel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service class for reservation management operations.
 */
public class ReservationService {
    
    private ReservationDAO reservationDAO;
    private VolDAO volDAO;
    private HotelDAO hotelDAO;
    
    public ReservationService() {
        this.reservationDAO = new ReservationDAO();
        this.volDAO = new VolDAO();
        this.hotelDAO = new HotelDAO();
    }
    
    /**
     * Retrieves all reservations.
     * 
     * @return List of all reservations
     */
    public List<Reservation> getAllReservations() {
        return reservationDAO.findAll();
    }
    
    /**
     * Retrieves reservations for a specific user.
     * 
     * @param userId User ID
     * @return List of user's reservations
     */
    public List<Reservation> getUserReservations(int userId) {
        return reservationDAO.findByUserId(userId);
    }
    
    /**
     * Finds a reservation by ID.
     * 
     * @param id Reservation ID
     * @return Reservation if found, null otherwise
     */
    public Reservation getReservationById(String id) {
        return reservationDAO.findById(id);
    }
    
    /**
     * Creates a new reservation.
     * 
     * @param reservation Reservation to create
     * @return true if successful, false otherwise
     */
    public boolean createReservation(Reservation reservation) {
        // Validate reservation
        if (!validateReservation(reservation)) {
            return false;
        }
        
        // Calculate total amount
        double totalAmount = calculateTotalAmount(reservation);
        reservation.setMontantTotal(totalAmount);
        
        // Save reservation
        return reservationDAO.save(reservation);
    }
    
    /**
     * Updates an existing reservation.
     * 
     * @param reservation Reservation to update
     * @return true if successful, false otherwise
     */
    public boolean updateReservation(Reservation reservation) {
        if (!validateReservation(reservation)) {
            return false;
        }
        
        // Recalculate total amount
        double totalAmount = calculateTotalAmount(reservation);
        reservation.setMontantTotal(totalAmount);
        
        return reservationDAO.update(reservation);
    }
    
    /**
     * Cancels a reservation.
     * 
     * @param idReservation Reservation ID
     * @return true if successful, false otherwise
     */
    public boolean cancelReservation(String idReservation) {
        return reservationDAO.updateStatus(idReservation, "ANNULEE");
    }
    
    /**
     * Deletes a reservation.
     * 
     * @param idReservation Reservation ID
     * @return true if successful, false otherwise
     */
    public boolean deleteReservation(String idReservation) {
        return reservationDAO.delete(idReservation);
    }
    
    /**
     * Searches flights.
     * 
     * @param origine Origin (optional)
     * @param destination Destination (optional)
     * @param maxPrix Maximum price (optional)
     * @return List of matching flights
     */
    public List<Vol> searchFlights(String origine, String destination, Double maxPrix) {
        return volDAO.searchFlightsAdvanced(origine, destination, maxPrix);
    }
    
    /**
     * Searches hotels.
     * 
     * @param ville City (optional)
     * @param minEtoiles Minimum stars (optional)
     * @param maxPrix Maximum price (optional)
     * @return List of matching hotels
     */
    public List<Hotel> searchHotels(String ville, Integer minEtoiles, Double maxPrix) {
        return hotelDAO.searchHotelsAdvanced(ville, minEtoiles, maxPrix);
    }
    
    /**
     * Gets reservations for a date range (for reporting).
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return List of reservations in the date range
     */
    public List<Reservation> getReservationsByDateRange(LocalDate startDate, LocalDate endDate) {
        return reservationDAO.getReservationsByDateRange(startDate, endDate);
    }
    
    /**
     * Calculates the total amount for a reservation.
     * 
     * @param reservation Reservation
     * @return Total amount
     */
    private double calculateTotalAmount(Reservation reservation) {
        double total = 0.0;
        
        // Add flight cost
        if (reservation.getIdVol() != null) {
            Vol vol = volDAO.findById(reservation.getIdVol());
            if (vol != null) {
                total += vol.getPrix();
            }
        }
        
        // Add hotel cost
        if (reservation.getIdHotel() != null && reservation.getDateCheckin() != null && 
            reservation.getDateCheckout() != null) {
            Hotel hotel = hotelDAO.findById(reservation.getIdHotel());
            if (hotel != null) {
                long nights = ChronoUnit.DAYS.between(reservation.getDateCheckin(), 
                                                       reservation.getDateCheckout());
                total += hotel.getPrixParNuit() * nights * reservation.getNombreChambres();
            }
        }
        
        return total;
    }
    
    /**
     * Validates a reservation.
     * 
     * @param reservation Reservation to validate
     * @return true if valid, false otherwise
     */
    private boolean validateReservation(Reservation reservation) {
        if (reservation.getIdUtilisateur() <= 0) {
            System.err.println("Invalid user ID");
            return false;
        }
        
        if (reservation.getIdVol() == null && reservation.getIdHotel() == null) {
            System.err.println("Reservation must have at least a flight or hotel");
            return false;
        }
        
        if (reservation.getIdHotel() != null) {
            if (reservation.getDateCheckin() == null || reservation.getDateCheckout() == null) {
                System.err.println("Hotel reservation must have check-in and check-out dates");
                return false;
            }
            
            if (reservation.getDateCheckout().isBefore(reservation.getDateCheckin())) {
                System.err.println("Check-out date must be after check-in date");
                return false;
            }
        }
        
        return true;
    }
}
