-- =====================================================
-- CLEAN UPDATE - Safe to run multiple times
-- =====================================================
USE voyage_affaires;

-- =====================================================
-- Delete ALL existing flights to start fresh
-- =====================================================
DELETE FROM vol;

-- =====================================================
-- Insert NEW Tunisian flights for 2026
-- =====================================================
INSERT INTO vol VALUES 
('TU001', 'Tunisair', 'Tunis-Carthage', 'Paris CDG', '2026-03-15 08:00:00', '2026-03-15 11:00:00', 280.00, 200, 'ECONOMIE', TRUE),
('TU002', 'Tunisair', 'Tunis-Carthage', 'London Heathrow', '2026-03-16 09:30:00', '2026-03-16 13:00:00', 320.00, 180, 'ECONOMIE', TRUE),
('TU003', 'Tunisair', 'Tunis-Carthage', 'Istanbul', '2026-03-17 14:00:00', '2026-03-17 17:30:00', 250.00, 150, 'ECONOMIE', TRUE),
('TU004', 'Tunisair Express', 'Tunis-Carthage', 'Barcelona', '2026-03-18 10:00:00', '2026-03-18 12:30:00', 290.00, 160, 'ECONOMIE', TRUE),
('AF005', 'Air France', 'Tunis-Carthage', 'New York JFK', '2026-03-20 07:00:00', '2026-03-20 14:00:00', 1450.00, 250, 'BUSINESS', TRUE),
('TU006', 'Tunisair', 'Tunis-Carthage', 'Dubai', '2026-03-22 23:00:00', '2026-03-23 06:00:00', 580.00, 220, 'BUSINESS', TRUE),
('NO007', 'Nouvelair', 'Monastir', 'Rome', '2026-03-25 15:00:00', '2026-03-25 16:30:00', 180.00, 180, 'ECONOMIE', TRUE),
('TU008', 'Tunisair', 'Tunis-Carthage', 'Cairo', '2026-03-28 11:00:00', '2026-03-28 13:30:00', 320.00, 170, 'ECONOMIE', TRUE);

-- =====================================================
-- Delete ALL existing hotels to start fresh
-- =====================================================
DELETE FROM hotel;

-- =====================================================
-- Insert ALL hotels (Tunisia-focused destinations)
-- =====================================================
INSERT INTO hotel VALUES 
('H001', 'Hilton Paris Opera', '108 Rue Saint-Lazare', 'Paris', 'France', 5, 250.00, TRUE),
('H002', 'Ibis Budget Paris', '15 Rue de la Convention', 'Paris', 'France', 2, 80.00, TRUE),
('H003', 'Marriott London', '140 Park Lane', 'London', 'UK', 5, 300.00, TRUE),
('H004', 'Barcelona Hotel Arts', 'Marina 19-21', 'Barcelona', 'Spain', 5, 280.00, TRUE),
('H005', 'Radisson Blu Istanbul', 'Ortakoy Mahallesi', 'Istanbul', 'Turkey', 5, 200.00, TRUE),
('H006', 'Armani Hotel Dubai', 'Burj Khalifa', 'Dubai', 'UAE', 5, 450.00, TRUE),
('H007', 'Four Seasons Cairo', 'Nile Plaza', 'Cairo', 'Egypt', 5, 280.00, TRUE),
('H008', 'Cavalieri Waldorf Astoria', 'Via Alberto Cadlolo 101', 'Rome', 'Italy', 5, 320.00, TRUE);

-- =====================================================
-- Verify the results
-- =====================================================
SELECT '✅ FLIGHTS FROM TUNISIA' AS 'Status';
SELECT id_vol, compagnie, origine, destination, 
       DATE_FORMAT(date_depart, '%Y-%m-%d %H:%i') as depart,
       DATE_FORMAT(date_arrivee, '%Y-%m-%d %H:%i') as arrivee,
       prix, classe
FROM vol 
ORDER BY date_depart;

SELECT '✅ HOTELS AT DESTINATIONS' AS 'Status';
SELECT id_hotel, nom, ville, pays, etoiles, prix_par_nuit 
FROM hotel 
ORDER BY ville;

SELECT '✅ SUMMARY' AS 'Status';
SELECT 
    (SELECT COUNT(*) FROM vol) as 'Total Flights',
    (SELECT COUNT(*) FROM hotel) as 'Total Hotels',
    (SELECT COUNT(*) FROM utilisateur) as 'Total Users';

-- =====================================================
-- ✅ UPDATE COMPLETE!
-- All flights now originate from Tunisia (2026)
-- =====================================================
