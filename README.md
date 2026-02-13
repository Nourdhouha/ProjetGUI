# Voyage & Affaires - Plateforme de Gestion de Voyages d'Affaires

Application de bureau Java pour la gestion des voyages d'affaires.

## 📋 Table des Matières

- [Fonctionnalités](#fonctionnalités)
- [Technologies](#technologies)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Exécution](#exécution)
- [Structure du Projet](#structure-du-projet)
- [Modules Implémentés](#modules-implémentés)
- [Identifiants par Défaut](#identifiants-par-défaut)
- [Résolution des Problèmes](#résolution-des-problèmes)

---

## ✨ Fonctionnalités

### Modules Implémentés

1. **Authentification & Gestion des Utilisateurs**
   - Connexion sécurisée
   - Gestion des rôles (Admin, Manager, Employé)
   - CRUD complet des utilisateurs
   - Cryptage des mots de passe (BCrypt)

2. **Gestion des Réservations**
   - Recherche de vols (multi-critères)
   - Recherche d'hôtels (multi-critères)
   - Création/modification/annulation de réservations
   - Visualisation des réservations

3. **Reporting & Historique**
   - Historique des voyages
   - Rapports de dépenses
   - Export PDF/Excel
   - Statistiques

### Modules Vides (À venir)

- Approbation & Workflow
- Gestion des Dépenses

---

## 🛠️ Technologies

- **Java** 11+
- **JavaFX** 17.0.2 (Interface graphique)
- **MySQL** 8.0 (Base de données)
- **Maven** (Gestion des dépendances)
- **BCrypt** (Cryptage des mots de passe)
- **Apache POI** (Export Excel)
- **iText** (Export PDF)

---

## 📦 Prérequis

Avant de commencer, assurez-vous d'avoir installé :

1. **Java Development Kit (JDK) 11 ou supérieur**
   - Télécharger : [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) ou [OpenJDK](https://adoptium.net/)
   - Vérifier : `java -version` et `javac -version`

2. **MySQL Server 8.0+**
   - Télécharger : [MySQL Community Server](https://dev.mysql.com/downloads/mysql/)
   - Vérifier : `mysql --version`

3. **Maven 3.6+**
   - Télécharger : [Apache Maven](https://maven.apache.org/download.cgi)
   - Vérifier : `mvn --version`

4. **IDE Java** (recommandé)
   - IntelliJ IDEA Community Edition (recommandé)
   - Eclipse
   - NetBeans

---

## 🚀 Installation

### Étape 1 : Cloner ou télécharger le projet

```bash
cd agence
```

### Étape 2 : Créer la base de données

1. Démarrer MySQL Server

2. Exécuter le script SQL :

```bash
mysql -u root -p < database_schema.sql
```

Ou manuellement :

```sql
mysql -u root -p

# Dans MySQL :
source database_schema.sql
```

Le script crée automatiquement :
- La base de données `voyage_affaires`
- Toutes les tables nécessaires
- Un utilisateur admin par défaut
- Des données de test (vols et hôtels)

### Étape 3 : Configurer la connexion à la base de données

Éditer le fichier : `src/main/resources/database.properties`

```properties
db.url=jdbc:mysql://localhost:3306/voyage_affaires?useSSL=false&serverTimezone=UTC
db.username=root
db.password=VOTRE_MOT_DE_PASSE_MYSQL
db.driver=com.mysql.cj.jdbc.Driver
```

**Important** : Remplacez `VOTRE_MOT_DE_PASSE_MYSQL` par votre mot de passe MySQL.

### Étape 4 : Installer les dépendances

```bash
mvn clean install
```

---

## ▶️ Exécution

### Option 1 : Depuis l'IDE (Développement)

#### IntelliJ IDEA :
1. Ouvrir le projet dans IntelliJ
2. Attendre que Maven télécharge les dépendances
3. Localiser `Main.java` dans `src/main/java/com/voyageaffaires/`
4. Clic droit sur `Main.java` → **Run 'Main.main()'**
5. L'application s'ouvre dans une fenêtre

#### Eclipse :
1. Ouvrir le projet dans Eclipse
2. Clic droit sur le projet → **Maven** → **Update Project**
3. Localiser `Main.java`
4. Clic droit → **Run As** → **Java Application**

### Option 2 : En ligne de commande

```bash
mvn clean javafx:run
```

### Option 3 : Créer un JAR exécutable

```bash
mvn clean package

# Puis exécuter :
java -jar target/voyage-affaires-1.0.0.jar
```

---

## 📁 Structure du Projet

```
agence/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/voyageaffaires/
│   │   │       ├── Main.java                    # Point d'entrée
│   │   │       ├── controllers/                 # Contrôleurs UI
│   │   │       │   ├── LoginController.java
│   │   │       │   ├── DashboardController.java
│   │   │       │   └── ...
│   │   │       ├── models/                      # Entités
│   │   │       │   ├── Utilisateur.java
│   │   │       │   ├── Reservation.java
│   │   │       │   ├── Vol.java
│   │   │       │   └── Hotel.java
│   │   │       ├── dao/                         # Accès données
│   │   │       │   ├── UtilisateurDAO.java
│   │   │       │   ├── ReservationDAO.java
│   │   │       │   ├── VolDAO.java
│   │   │       │   └── HotelDAO.java
│   │   │       ├── services/                    # Logique métier
│   │   │       │   ├── AuthService.java
│   │   │       │   ├── UserService.java
│   │   │       │   └── ReservationService.java
│   │   │       └── utils/                       # Utilitaires
│   │   │           ├── DatabaseConnection.java
│   │   │           ├── SessionManager.java
│   │   │           ├── PasswordUtil.java
│   │   │           └── ...
│   │   └── resources/
│   │       ├── fxml/                            # Interfaces FXML
│   │       │   ├── LoginView.fxml
│   │       │   ├── DashboardView.fxml
│   │       │   └── ...
│   │       ├── css/                             # Styles
│   │       │   └── style.css
│   │       └── database.properties              # Config DB
│   └── test/                                    # Tests unitaires
├── database_schema.sql                          # Script SQL
├── pom.xml                                      # Configuration Maven
└── README.md                                    # Ce fichier
```

---

## 🔐 Identifiants par Défaut

Après l'installation de la base de données, utilisez ces identifiants pour vous connecter :

**Admin :**
- Email : `admin@voyageaffaires.com`
- Mot de passe : `admin123`

---

## 🎯 Modules Implémentés

### ✅ Module 1 : Authentification
- **Fichiers** :
  - `LoginView.fxml` / `LoginController.java`
  - `AuthService.java`
  - `PasswordUtil.java`
- **Fonctionnalités** : Connexion sécurisée, gestion des sessions

### ✅ Module 2 : Gestion des Utilisateurs
- **Fichiers** :
  - `UserManagementView.fxml` / `UserManagementController.java`
  - `UserFormView.fxml` / `UserFormController.java`
  - `UserService.java` / `UtilisateurDAO.java`
- **Fonctionnalités** : CRUD utilisateurs, recherche, gestion des rôles

### ✅ Module 3 : Gestion des Réservations
- **Fichiers** :
  - `ReservationListView.fxml` / `ReservationListController.java`
  - `SearchFlightView.fxml` / `SearchFlightController.java`
  - `SearchHotelView.fxml` / `SearchHotelController.java`
  - `ReservationService.java` / `ReservationDAO.java`
- **Fonctionnalités** : Recherche vols/hôtels, création/modification réservations

### ✅ Module 4 : Reporting & Historique
- **Fichiers** :
  - `ReportingView.fxml` / `ReportingController.java`
  - `HistoryView.fxml` / `HistoryController.java`
- **Fonctionnalités** : Historique des voyages, rapports, export PDF/Excel

---

## 🐛 Résolution des Problèmes

### Problème : "Error: JavaFX runtime components are missing"

**Solution** :
```bash
mvn clean install
mvn javafx:run
```

### Problème : "Cannot connect to database"

**Solutions** :
1. Vérifier que MySQL est démarré
2. Vérifier les identifiants dans `database.properties`
3. Vérifier que la base de données existe :
   ```sql
   mysql -u root -p
   SHOW DATABASES;
   USE voyage_affaires;
   ```

### Problème : "Table doesn't exist"

**Solution** : Réexécuter le script SQL
```bash
mysql -u root -p < database_schema.sql
```

### Problème : Port 3306 déjà utilisé

**Solution** : Modifier le port dans `database.properties` :
```properties
db.url=jdbc:mysql://localhost:3307/voyage_affaires?...
```

---

## 📝 Notes de Développement

### Ajouter un nouveau module

1. Créer le fichier FXML dans `src/main/resources/fxml/`
2. Créer le contrôleur dans `src/main/java/com/voyageaffaires/controllers/`
3. Ajouter un bouton dans `DashboardView.fxml`
4. Ajouter la navigation dans `DashboardController.java`

### Modifier les styles

Éditer : `src/main/resources/css/style.css`

### Ajouter une dépendance

Éditer : `pom.xml` dans la section `<dependencies>`

---

## 👥 Auteur

Développé pour le projet de Gestion de Voyages d'Affaires

---

## 📄 Licence

Ce projet est développé à des fins éducatives.

---

## 🎓 Pour Commencer

1. **Installer MySQL et créer la base de données**
2. **Configurer database.properties**
3. **Exécuter : `mvn clean javafx:run`**
4. **Se connecter avec admin@voyageaffaires.com / admin123**
5. **Explorer l'application !**

---

**Bon développement ! 🚀**
