-- =====================================================
-- Voyage & Affaires - Database Schema
-- =====================================================

CREATE DATABASE IF NOT EXISTS voyage_affaires;
USE voyage_affaires;

-- =====================================================
-- Table: utilisateur (Users)
-- =====================================================
CREATE TABLE IF NOT EXISTS utilisateur (
    id_utilisateur INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telephone VARCHAR(20),
    departement VARCHAR(100),
    mot_de_passe VARCHAR(255) NOT NULL,
    role ENUM('EMPLOYE', 'MANAGER', 'ADMIN') NOT NULL DEFAULT 'EMPLOYE',
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actif BOOLEAN DEFAULT TRUE,
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: vol (Flights)
-- =====================================================
CREATE TABLE IF NOT EXISTS vol (
    id_vol VARCHAR(50) PRIMARY KEY,
    compagnie VARCHAR(100) NOT NULL,
    origine VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    date_depart DATETIME NOT NULL,
    date_arrivee DATETIME NOT NULL,
    prix DOUBLE NOT NULL,
    nombre_places INT NOT NULL,
    classe VARCHAR(50) DEFAULT 'ECONOMIE',
    disponible BOOLEAN DEFAULT TRUE,
    INDEX idx_destination (destination),
    INDEX idx_date_depart (date_depart)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: hotel (Hotels)
-- =====================================================
CREATE TABLE IF NOT EXISTS hotel (
    id_hotel VARCHAR(50) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    adresse VARCHAR(200),
    ville VARCHAR(100) NOT NULL,
    pays VARCHAR(100) DEFAULT 'France',
    etoiles INT CHECK (etoiles BETWEEN 1 AND 5),
    prix_par_nuit DOUBLE NOT NULL,
    disponible BOOLEAN DEFAULT TRUE,
    INDEX idx_ville (ville)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: reservation (Reservations)
-- =====================================================
CREATE TABLE IF NOT EXISTS reservation (
    id_reservation VARCHAR(50) PRIMARY KEY,
    date_creation DATE NOT NULL,
    statut VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE',
    montant_total DOUBLE NOT NULL,
    id_utilisateur INT NOT NULL,
    id_vol VARCHAR(50),
    id_hotel VARCHAR(50),
    date_checkin DATE,
    date_checkout DATE,
    nombre_chambres INT DEFAULT 1,
    motif_voyage TEXT,
    commentaire TEXT,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE,
    FOREIGN KEY (id_vol) REFERENCES vol(id_vol) ON DELETE SET NULL,
    FOREIGN KEY (id_hotel) REFERENCES hotel(id_hotel) ON DELETE SET NULL,
    INDEX idx_utilisateur (id_utilisateur),
    INDEX idx_statut (statut),
    INDEX idx_date_creation (date_creation)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: note_de_frais (Expense Reports)
-- =====================================================
CREATE TABLE IF NOT EXISTS note_de_frais (
    id_note INT PRIMARY KEY AUTO_INCREMENT,
    id_reservation VARCHAR(50) NOT NULL,
    date_submission DATE NOT NULL,
    montant_total DOUBLE NOT NULL,
    statut VARCHAR(50) DEFAULT 'EN_ATTENTE',
    justificatif VARCHAR(255),
    commentaire TEXT,
    FOREIGN KEY (id_reservation) REFERENCES reservation(id_reservation) ON DELETE CASCADE,
    INDEX idx_reservation (id_reservation),
    INDEX idx_statut (statut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: approbation (Approvals)
-- =====================================================
CREATE TABLE IF NOT EXISTS approbation (
    id_approbation INT PRIMARY KEY AUTO_INCREMENT,
    id_reservation VARCHAR(50) NOT NULL,
    id_manager INT NOT NULL,
    date_approbation DATE,
    statut VARCHAR(50) DEFAULT 'EN_ATTENTE',
    commentaire TEXT,
    FOREIGN KEY (id_reservation) REFERENCES reservation(id_reservation) ON DELETE CASCADE,
    FOREIGN KEY (id_manager) REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE,
    INDEX idx_reservation (id_reservation),
    INDEX idx_manager (id_manager)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: paiement (Payments)
-- =====================================================
CREATE TABLE IF NOT EXISTS paiement (
    id_paiement VARCHAR(50) PRIMARY KEY,
    id_reservation VARCHAR(50) NOT NULL,
    montant DOUBLE NOT NULL,
    date_paiement DATE NOT NULL,
    statut VARCHAR(50) DEFAULT 'EN_ATTENTE',
    mode_paiement VARCHAR(50),
    FOREIGN KEY (id_reservation) REFERENCES reservation(id_reservation) ON DELETE CASCADE,
    INDEX idx_reservation (id_reservation),
    INDEX idx_statut (statut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Insert Default Admin User
-- Password: admin123 (hashed with BCrypt)
-- =====================================================
INSERT INTO utilisateur (nom, prenom, email, telephone, departement, mot_de_passe, role) 
VALUES (
    'Admin',
    'Système',
    'admin@voyageaffaires.com',
    '20123456',
    'IT',
    '$2a$10$78a.1eRn9Kz9QNJuPVV1Hu72oGit3/4RisfkQLLbHV.1n7i6BLtHa',
    'ADMIN'
);

-- =====================================================
-- Insert Sample Flights
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
-- Insert Sample Hotels
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
-- Create Views for Reporting
-- =====================================================
CREATE OR REPLACE VIEW v_reservation_details AS
SELECT 
    r.id_reservation,
    r.date_creation,
    r.statut,
    r.montant_total,
    u.nom AS utilisateur_nom,
    u.prenom AS utilisateur_prenom,
    u.email AS utilisateur_email,
    v.compagnie AS vol_compagnie,
    v.origine,
    v.destination,
    v.date_depart,
    h.nom AS hotel_nom,
    h.ville AS hotel_ville,
    r.date_checkin,
    r.date_checkout
FROM reservation r
INNER JOIN utilisateur u ON r.id_utilisateur = u.id_utilisateur
LEFT JOIN vol v ON r.id_vol = v.id_vol
LEFT JOIN hotel h ON r.id_hotel = h.id_hotel;

-- =====================================================
-- End of Schema
-- =====================================================
