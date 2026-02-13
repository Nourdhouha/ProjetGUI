package com.voyageaffaires.dao;

import com.voyageaffaires.models.Vol;
import com.voyageaffaires.utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Vol (Flight) entity.
 */
public class VolDAO {
    
    private Connection connection;
    
    public VolDAO() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            System.err.println("Error establishing database connection: " + e.getMessage());
        }
    }
    
    /**
     * Finds a flight by ID.
     * 
     * @param idVol Flight ID
     * @return Vol object if found, null otherwise
     */
    public Vol findById(String idVol) {
        String query = "SELECT * FROM vol WHERE id_vol = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, idVol);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToVol(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding flight by ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Retrieves all available flights.
     * 
     * @return List of available flights
     */
    public List<Vol> findAllAvailable() {
        List<Vol> vols = new ArrayList<>();
        String query = "SELECT * FROM vol WHERE disponible = TRUE ORDER BY date_depart";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                vols.add(mapResultSetToVol(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving flights: " + e.getMessage());
        }
        return vols;
    }
    
    /**
     * Searches flights by destination and date.
     * 
     * @param destination Destination city
     * @param dateDepart Departure date
     * @return List of matching flights
     */
    public List<Vol> searchFlights(String destination, LocalDateTime dateDepart) {
        List<Vol> vols = new ArrayList<>();
        String query = "SELECT * FROM vol WHERE disponible = TRUE AND destination LIKE ? " +
                      "AND DATE(date_depart) = DATE(?) ORDER BY prix";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + destination + "%");
            stmt.setTimestamp(2, Timestamp.valueOf(dateDepart));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                vols.add(mapResultSetToVol(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching flights: " + e.getMessage());
        }
        return vols;
    }
    
    /**
     * Searches flights with multiple criteria.
     * 
     * @param origine Origin city (optional)
     * @param destination Destination city (optional)
     * @param maxPrix Maximum price (optional)
     * @return List of matching flights
     */
    public List<Vol> searchFlightsAdvanced(String origine, String destination, Double maxPrix) {
        List<Vol> vols = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM vol WHERE disponible = TRUE");
        
        if (origine != null && !origine.isEmpty()) {
            query.append(" AND origine LIKE ?");
        }
        if (destination != null && !destination.isEmpty()) {
            query.append(" AND destination LIKE ?");
        }
        if (maxPrix != null && maxPrix > 0) {
            query.append(" AND prix <= ?");
        }
        query.append(" ORDER BY prix");
        
        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            int paramIndex = 1;
            
            if (origine != null && !origine.isEmpty()) {
                stmt.setString(paramIndex++, "%" + origine + "%");
            }
            if (destination != null && !destination.isEmpty()) {
                stmt.setString(paramIndex++, "%" + destination + "%");
            }
            if (maxPrix != null && maxPrix > 0) {
                stmt.setDouble(paramIndex++, maxPrix);
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                vols.add(mapResultSetToVol(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching flights (advanced): " + e.getMessage());
        }
        return vols;
    }
    
    /**
     * Maps a ResultSet row to a Vol object.
     * 
     * @param rs ResultSet
     * @return Vol object
     * @throws SQLException if error occurs
     */
    private Vol mapResultSetToVol(ResultSet rs) throws SQLException {
        Vol vol = new Vol();
        vol.setIdVol(rs.getString("id_vol"));
        vol.setCompagnie(rs.getString("compagnie"));
        vol.setOrigine(rs.getString("origine"));
        vol.setDestination(rs.getString("destination"));
        
        Timestamp dateDepart = rs.getTimestamp("date_depart");
        if (dateDepart != null) {
            vol.setDateDepart(dateDepart.toLocalDateTime());
        }
        
        Timestamp dateArrivee = rs.getTimestamp("date_arrivee");
        if (dateArrivee != null) {
            vol.setDateArrivee(dateArrivee.toLocalDateTime());
        }
        
        vol.setPrix(rs.getDouble("prix"));
        vol.setNombrePlaces(rs.getInt("nombre_places"));
        vol.setClasse(rs.getString("classe"));
        vol.setDisponible(rs.getBoolean("disponible"));
        
        return vol;
    }
}
