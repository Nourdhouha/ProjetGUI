package com.voyageaffaires.dao;

import com.voyageaffaires.models.Hotel;
import com.voyageaffaires.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Hotel entity.
 */
public class HotelDAO {
    
    private Connection connection;
    
    public HotelDAO() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            System.err.println("Error establishing database connection: " + e.getMessage());
        }
    }
    
    /**
     * Finds a hotel by ID.
     * 
     * @param idHotel Hotel ID
     * @return Hotel object if found, null otherwise
     */
    public Hotel findById(String idHotel) {
        String query = "SELECT * FROM hotel WHERE id_hotel = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, idHotel);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToHotel(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding hotel by ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Retrieves all available hotels.
     * 
     * @return List of available hotels
     */
    public List<Hotel> findAllAvailable() {
        List<Hotel> hotels = new ArrayList<>();
        String query = "SELECT * FROM hotel WHERE disponible = TRUE ORDER BY ville, nom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                hotels.add(mapResultSetToHotel(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving hotels: " + e.getMessage());
        }
        return hotels;
    }
    
    /**
     * Searches hotels by city.
     * 
     * @param ville City name
     * @return List of hotels in the city
     */
    public List<Hotel> searchByCity(String ville) {
        List<Hotel> hotels = new ArrayList<>();
        String query = "SELECT * FROM hotel WHERE disponible = TRUE AND ville LIKE ? ORDER BY prix_par_nuit";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + ville + "%");
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                hotels.add(mapResultSetToHotel(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching hotels by city: " + e.getMessage());
        }
        return hotels;
    }
    
    /**
     * Searches hotels with multiple criteria.
     * 
     * @param ville City name (optional)
     * @param minEtoiles Minimum star rating (optional)
     * @param maxPrix Maximum price per night (optional)
     * @return List of matching hotels
     */
    public List<Hotel> searchHotelsAdvanced(String ville, Integer minEtoiles, Double maxPrix) {
        List<Hotel> hotels = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM hotel WHERE disponible = TRUE");
        
        if (ville != null && !ville.isEmpty()) {
            query.append(" AND ville LIKE ?");
        }
        if (minEtoiles != null && minEtoiles > 0) {
            query.append(" AND etoiles >= ?");
        }
        if (maxPrix != null && maxPrix > 0) {
            query.append(" AND prix_par_nuit <= ?");
        }
        query.append(" ORDER BY prix_par_nuit");
        
        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            int paramIndex = 1;
            
            if (ville != null && !ville.isEmpty()) {
                stmt.setString(paramIndex++, "%" + ville + "%");
            }
            if (minEtoiles != null && minEtoiles > 0) {
                stmt.setInt(paramIndex++, minEtoiles);
            }
            if (maxPrix != null && maxPrix > 0) {
                stmt.setDouble(paramIndex++, maxPrix);
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                hotels.add(mapResultSetToHotel(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching hotels (advanced): " + e.getMessage());
        }
        return hotels;
    }
    
    /**
     * Maps a ResultSet row to a Hotel object.
     * 
     * @param rs ResultSet
     * @return Hotel object
     * @throws SQLException if error occurs
     */
    private Hotel mapResultSetToHotel(ResultSet rs) throws SQLException {
        Hotel hotel = new Hotel();
        hotel.setIdHotel(rs.getString("id_hotel"));
        hotel.setNom(rs.getString("nom"));
        hotel.setAdresse(rs.getString("adresse"));
        hotel.setVille(rs.getString("ville"));
        hotel.setPays(rs.getString("pays"));
        hotel.setEtoiles(rs.getInt("etoiles"));
        hotel.setPrixParNuit(rs.getDouble("prix_par_nuit"));
        hotel.setDisponible(rs.getBoolean("disponible"));
        
        return hotel;
    }
}
