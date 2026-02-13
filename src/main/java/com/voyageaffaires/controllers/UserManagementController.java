package com.voyageaffaires.controllers;

import com.voyageaffaires.models.Utilisateur;
import com.voyageaffaires.services.UserService;
import com.voyageaffaires.utils.AlertUtil;
import com.voyageaffaires.utils.NavigationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for User Management screen.
 */
public class UserManagementController {
    
    @FXML
    private TextField searchField;
    
    @FXML
    private TableView<Utilisateur> usersTable;
    
    @FXML
    private TableColumn<Utilisateur, Integer> colId;
    
    @FXML
    private TableColumn<Utilisateur, String> colNom;
    
    @FXML
    private TableColumn<Utilisateur, String> colPrenom;
    
    @FXML
    private TableColumn<Utilisateur, String> colEmail;
    
    @FXML
    private TableColumn<Utilisateur, String> colTelephone;
    
    @FXML
    private TableColumn<Utilisateur, String> colDepartement;
    
    @FXML
    private TableColumn<Utilisateur, String> colRole;
    
    @FXML
    private TableColumn<Utilisateur, Void> colActions;
    
    private UserService userService;
    private ObservableList<Utilisateur> usersList;
    
    @FXML
    public void initialize() {
        userService = new UserService();
        
        // Configure table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("idUtilisateur"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colDepartement.setCellValueFactory(new PropertyValueFactory<>("departement"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        // Add action buttons
        addActionButtons();
        
        // Load users
        loadUsers();
    }
    
    private void loadUsers() {
        try {
            usersList = FXCollections.observableArrayList(userService.getAllUsers());
            usersTable.setItems(usersList);
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Chargement des utilisateurs", 
                              "Impossible de charger la liste des utilisateurs.");
            System.err.println("Error loading users: " + e.getMessage());
        }
    }
    
    private void addActionButtons() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Modifier");
            private final Button btnDelete = new Button("Supprimer");
            private final HBox pane = new HBox(10, btnEdit, btnDelete);
            
            {
                btnEdit.getStyleClass().add("secondary-button");
                btnDelete.getStyleClass().add("danger-button");
                
                btnEdit.setOnAction(event -> {
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    handleEditUser(user);
                });
                
                btnDelete.setOnAction(event -> {
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    handleDeleteUser(user);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }
    
    @FXML
    private void handleAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserFormView.fxml"));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Ajouter un Utilisateur");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(usersTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            
            dialogStage.showAndWait();
            
            // Reload users after dialog closes
            loadUsers();
            
        } catch (Exception e) {
            System.err.println("Error opening user form: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Erreur", "Erreur d'ouverture", 
                              "Impossible d'ouvrir le formulaire d'ajout.");
        }
    }
    
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        if (keyword.isEmpty()) {
            loadUsers();
        } else {
            usersList = FXCollections.observableArrayList(userService.searchUsers(keyword));
            usersTable.setItems(usersList);
        }
    }
    
    @FXML
    private void handleReset() {
        searchField.clear();
        loadUsers();
    }
    
    private void handleEditUser(Utilisateur user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserFormView.fxml"));
            Parent root = loader.load();
            
            // Get controller and set user to edit
            UserFormController controller = loader.getController();
            controller.setUser(user);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modifier l'Utilisateur");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(usersTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            
            dialogStage.showAndWait();
            
            // Reload users after dialog closes
            loadUsers();
            
        } catch (Exception e) {
            System.err.println("Error opening user form: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Erreur", "Erreur d'ouverture", 
                              "Impossible d'ouvrir le formulaire de modification.");
        }
    }
    
    private void handleDeleteUser(Utilisateur user) {
        boolean confirmed = AlertUtil.showConfirmation(
            "Confirmation",
            "Supprimer l'utilisateur?",
            "Êtes-vous sûr de vouloir supprimer " + user.getFullName() + "?"
        );
        
        if (confirmed) {
            if (userService.deleteUser(user.getIdUtilisateur())) {
                AlertUtil.showSuccess("Succès", "Utilisateur supprimé avec succès.");
                loadUsers();
            } else {
                AlertUtil.showError("Erreur", "Suppression échouée", 
                                  "Impossible de supprimer l'utilisateur.");
            }
        }
    }
    
    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) usersTable.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/fxml/DashboardView.fxml", 
                                     "Dashboard", 1280, 800);
        } catch (Exception e) {
            System.err.println("Error navigating back: " + e.getMessage());
        }
    }
}
