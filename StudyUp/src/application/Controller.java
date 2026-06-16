package application;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class Controller {
	@FXML
    private Label notificationLabel;
	int id_etudiant = LoginController.getId_etudiant();

    @FXML
    private TableView<Projet> tableView;

    @FXML
    private TableColumn<Projet, String> projetName;

    @FXML
    private TableColumn<Projet, List<Tache>> taches;
    @FXML
    private TextField searchField;

    private ObservableList<Projet> projetList = FXCollections.observableArrayList();
    
   
    @FXML
    private void initialize() {
    	 if (notificationBadge != null) {
    	        // Mettez à jour le badge de notification
    	        updateNotificationBadge();
    	        
    	        // Créez une instance de databaseNotification pour gérer les notifications
    	        databaseNotification dbManager = new databaseNotification();

    	        // Appelez le setter pour associer le label à la classe de gestion des notifications
    	        dbManager.setNotificationBadge(notificationBadge);
    	    } else {
    	        System.out.println("notificationBadge est null. Impossible de l'associer.");
    	    }
    	searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                // Parcourir la liste des projets pour trouver celui correspondant au texte de recherche
                for (Projet projet : tableView.getItems()) {
                    if (projet.getNomProjet().toLowerCase().contains(newValue.toLowerCase())) {
                        // Sélectionner le projet dans le TableView
                        tableView.getSelectionModel().select(projet);
                        // Déplacer la vue vers la position du projet sélectionné
                        tableView.scrollTo(projet);
                        break;
                    }
                }
            } else {
                // Effacer la sélection si le champ de recherche est vide
                tableView.getSelectionModel().clearSelection();
            }
        });
    
    	
        projetName.setCellValueFactory(new PropertyValueFactory<>("nomProjet"));
        projetName.setCellFactory(column -> {
            return new TableCell<Projet, String>() {
                final Button newTaskButton = new Button();
                final Button deleteTaskButton = new Button();
                final Button editTaskButton = new Button(); // Nouveau bouton "!"
                final Button openGraphes=new Button();

                {
                    try {
                        // Charger l'image pour le bouton "+"
                        Image addImage = new Image("add.png");
                        ImageView addImageView = new ImageView(addImage);
                        addImageView.setFitHeight(16); // Ajuster la hauteur de l'image
                        addImageView.setFitWidth(16); // Ajuster la largeur de l'image
                        newTaskButton.setGraphic(addImageView);

                        // Charger l'image pour le bouton "-"
                        Image deleteImage = new Image("supp.png");
                        ImageView deleteImageView = new ImageView(deleteImage);
                        deleteImageView.setFitHeight(16); // Ajuster la hauteur de l'image
                        deleteImageView.setFitWidth(16); // Ajuster la largeur de l'image
                        deleteTaskButton.setGraphic(deleteImageView);

                        // Charger l'image pour le bouton "!"
                        Image editImage = new Image("update.png");
                        ImageView editImageView = new ImageView(editImage);
                        editImageView.setFitHeight(16); // Ajuster la hauteur de l'image
                        editImageView.setFitWidth(16); // Ajuster la largeur de l'image
                        editTaskButton.setGraphic(editImageView);
                        
//                        // Charger l'image pour le bouton "!"
//                        Image diagram = new Image("graphe.png");
//                        ImageView grapheView = new ImageView(diagram);
//                        grapheView.setFitHeight(16); // Ajuster la hauteur de l'image
//                        grapheView.setFitWidth(16); // Ajuster la largeur de l'image
//                        openGraphes.setGraphic(grapheView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Configuration du bouton "+"
                    newTaskButton.setOnAction(event -> {
                        Projet projet = getTableView().getItems().get(getIndex());
                        openNewTaskForm(projet);
                    });

                    // Configuration du bouton "-"
                    deleteTaskButton.setOnAction(event -> {
                        Projet projet = getTableView().getItems().get(getIndex());
                        openDeleteTaskForm(projet);
                    });

                    // Configuration du bouton "!"
                    editTaskButton.setOnAction(event -> {
                        Projet projet = getTableView().getItems().get(getIndex());
                        openEditTaskForm(projet);
                    });
                    
//                    openGraphes.setOnAction(event ->{
//                    	 	Graphe graphe = new Graphe();
//                    	    
//                    	    // Appeler la méthode start() pour afficher le graphe
//                    	    graphe.start(new Stage());
//                    });

                    // Style des boutons
                    newTaskButton.setStyle("-fx-cursor:hand;-fx-background-color: transparent; -fx-background-radius: 0; -fx-padding: 0;");
                    deleteTaskButton.setStyle("-fx-cursor:hand;-fx-background-color: transparent; -fx-background-radius: 0; -fx-padding: 0;");
                    editTaskButton.setStyle("-fx-cursor:hand;-fx-background-color: transparent; -fx-background-radius: 0; -fx-padding: 0;");
                    openGraphes.setStyle("-fx-cursor:hand;-fx-background-color: transparent; -fx-background-radius: 0; -fx-padding: 0;");
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        Label projectNameLabel = new Label(item);
                        projectNameLabel.setStyle("-fx-padding: 0 10 0 0;");

                        // Ajout de l'espace entre les boutons avec des marges
                        HBox hbox = new HBox(projectNameLabel, newTaskButton, deleteTaskButton, editTaskButton);
                        hbox.setSpacing(10); // Ajouter un espacement de 10 pixels entre les boutons

                        setGraphic(hbox);
                    }
                }
            };
        });

        taches.setCellValueFactory(new PropertyValueFactory<>("taches"));

        taches.setCellFactory(column -> new TableCell<Projet, List<Tache>>() {
            @Override
            protected void updateItem(List<Tache> taches, boolean empty) {
                super.updateItem(taches, empty);
                if (taches == null || empty) {
                    setText(null);
                } else {
                    VBox vbox = new VBox(); // Utiliser un VBox pour afficher les détails des tâches verticalement
                    vbox.setSpacing(10); // Ajouter un espacement de 10 pixels entre les détails des tâches

                    for (Tache tache : taches) {
                        Label titreLabel = new Label("Titre: " + tache.getTitre());
                        Label prioriteLabel = new Label("Priorité: " + tache.getPriority());
                        Label statueLabel = new Label("Statut: " + tache.getStatue());
                        Label dateLabel = new Label("Date d'échéance: " + tache.getDueDate());
                        Label contenuLabel = new Label("Contenu: " + tache.getContenu());

                        vbox.getChildren().addAll(titreLabel, prioriteLabel, statueLabel, dateLabel, contenuLabel);
                        vbox.getChildren().add(new Label("\n")); // Ajouter un retour à la ligne entre chaque tâche
                    }

                    setGraphic(vbox); // Définir le VBox contenant les détails des tâches comme contenu de la cellule
                }
            }
        });




        loadProjetData();
    }

    private void openEditTaskForm(Projet projet) {
        try {
        	 FXMLLoader loader = new FXMLLoader(getClass().getResource("modTache.fxml"));
             Parent root = loader.load();

             ControllerModTache controllerModTache = loader.getController();
             controllerModTache.setController(this); // Passer la référence du contrôleur principal

             // Ici, vous pouvez configurer le contrôleur de modification de tâche avec les données du projet

             Stage stage = new Stage();
             stage.setScene(new Scene(root));
             stage.show();
             stage.setTitle("Éditer");
         	Image icon1 =new Image("update.png");
			stage.getIcons().add(icon1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   

    private void openDeleteTaskForm(Projet projet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("suppTache.fxml"));
            Parent root = loader.load();

            ControllerSuppTache controllerSuppTache = loader.getController();
            controllerSuppTache.setController(this); // Passer la référence du contrôleur principal au contrôleur de suppression
            controllerSuppTache.setProjet(projet); // Passer les données du projet

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            stage.setTitle("Supprimer");
        	Image icon1 =new Image("supp.png");
			stage.getIcons().add(icon1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void CreateProject(ActionEvent e) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("CreatePrjt.fxml"));
     
        Scene scene = new Scene(root);
        Stage newStage = new Stage();
        newStage.setTitle("Ajouter");
        Image icon1 =new Image("add-button_11205057.png");
  
		newStage.getIcons().add(icon1);
        newStage.setScene(scene);
        newStage.show();
       
    }

    private void loadProjetData() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/thenotes", "root", "")) {
            String query = "SELECT * FROM projet";
            PreparedStatement pstmt = con.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Projet projet = new Projet(rs.getInt("id_projet"), rs.getString("nom_projet"), loadTachesForProjet(rs.getInt("id_projet"), con));
                projetList.add(projet);
            }
            tableView.setItems(projetList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Tache> loadTachesForProjet(int projetId, Connection con) throws SQLException {
        List<Tache> tachesList = new ArrayList<>();
        String query = "SELECT * FROM tache WHERE id_projet = ?";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setInt(1, projetId);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Tache tache = new Tache(rs.getInt("id_tache"), rs.getInt("id_projet"), rs.getString("titre_tache"),
                    rs.getString("priority"), rs.getString("statue"), rs.getDate("due_date").toLocalDate(),
                    rs.getString("contenu"));

            tachesList.add(tache);
        }
        return tachesList;
    }



    private void openNewTaskForm(Projet projet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tache.fxml"));
            Parent root = loader.load();

            ControllerTache controllerTache = loader.getController();
            controllerTache.setProjet(projet);
            controllerTache.setTableView(tableView); // Passer la référence tableView

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter");
        	Image icon1 =new Image("pencil.png");
    		stage.getIcons().add(icon1);
            stage.showAndWait();

            // Rafraîchir le TableView après la fermeture de la fenêtre de la tâche
            tableView.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNewProject(Projet projet) {
        projetList.add(projet);
    }

    @FXML
    void SupprimerProjet(ActionEvent event) {
        Projet selectedProjet = tableView.getSelectionModel().getSelectedItem();
        if (selectedProjet != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText(null);
            alert.setContentText("Êtes-vous sûr de vouloir supprimer le projet sélectionné et toutes ses tâches associées ?");
         // Charger l'icône depuis un fichier
            Image image = new Image("X.png"); // Assurez-vous de remplacer "/path/to/your/icon.png" par le chemin réel de votre icône

            // Créer une vue d'image avec l'icône chargée
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(50); // Taille de l'icône
            imageView.setFitHeight(50);

            // Définir l'icône comme graphique de l'alerte
            alert.setGraphic(imageView);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                handleSuppButton(event);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun projet sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un projet à supprimer.");
            alert.showAndWait();
        }
    }

    @FXML
    void AnnulerCreation(ActionEvent event) {
        // Fermer la fenêtre actuelle
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleSuppButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SuppPrjt.fxml"));
            Parent root = loader.load();

            ContollerSuppPrjt controllerSuppPrjt = loader.getController();
            controllerSuppPrjt.setStage((Stage) tableView.getScene().getWindow()); // Passer la référence de la fenêtre actuelle

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Supprimer");
            Image icon1 =new Image("supp.png");
			stage.getIcons().add(icon1);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void refreshData() {
        projetList.clear();
        loadProjetData();
    }
    public Projet getProjetByTacheName(String nomTache) {
        for (Projet projet : projetList) {
            for (Tache tache : projet.getTaches()) {
                if (tache.getTitre().equals(nomTache)) {
                    return projet;
                }
            }
        }
        return null;
    }


    @FXML
    private Button wt,home,calendrier,timer,notif; // Assurez-vous que cette ligne est présente dans votre contrôleur
 
    public void openWhiteBoard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("WhiteBoard.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("WhiteBoard");
            Image icon1 =new Image("whiteboard.png");
            stage.getIcons().add(icon1);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void openTimer() {
    	try {
            @SuppressWarnings("unused")
			Stopwatch timer=new Stopwatch();
      
    	}catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    }
    public void goHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HomePage2.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);

            // Ajouter une animation de fondu lors de l'affichage de la scène
            FadeTransition ft = new FadeTransition(Duration.millis(1000), root);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();

            String css = this.getClass().getResource("home.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.setTitle("Home");
            Image icon1 = new Image("Study_up__2_-removebg-preview.png");
            stage.getIcons().add(icon1);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void openCalendar() {
        // Créer une nouvelle instance de TaskCalendriertest
        TaskCalendrietest taskCalendrier = new TaskCalendrietest(); // Vous devrez peut-être fournir des arguments au constructeur selon vos besoins

        // Créer une nouvelle instance de Calendriertest en passant taskCalendrier
        Calendriertest calendriertest = new Calendriertest(taskCalendrier);

        // Créer un nouveau stage pour afficher la page Calendriertest
        Stage stage = new Stage();
        stage.setTitle("Calendrier");
        Image icon1 =new Image("calendrier.png");
        stage.getIcons().add(icon1);
        // Appeler la méthode start de Calendriertest pour afficher la page
        calendriertest.start(stage);
    }
    @FXML
    private void openNotification(MouseEvent event) throws IOException {
    	 try {
    	        // Charger les notifications depuis la base de données
    	        databaseNotification dbManager = new databaseNotification();
    	        List<String> tasks = dbManager.loadAllTaskTitles();

    	        // Charger et afficher la page de notification
    	        FXMLLoader loader = new FXMLLoader(getClass().getResource("Notification.fxml"));
    	        Parent root = loader.load();

    	        NotificationController notificationController = loader.getController();
    	        notificationController.setTasks(tasks);

    	        Stage stage = new Stage();
    	        stage.setTitle("Notification");
    	        Image icon1 = new Image("notif.png");
    	        stage.getIcons().add(icon1);
    	        stage.setScene(new Scene(root));
    	        stage.show();
    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }
    }
    public void setTasks(int numberOfTasks) {
        // Mettre à jour le texte du badge de notification avec le nombre de tâches
        notificationBadge.setText(Integer.toString(numberOfTasks));
    }

    @FXML
    public Label notificationBadge;
    private void updateNotificationBadge() {
        if (notificationBadge != null) {
            databaseNotification dbManager = new databaseNotification(); 
            int incompleteCount = dbManager.getIncompleteTaskCount();
            if (incompleteCount > 0) {
                notificationBadge.setText(String.valueOf(incompleteCount));
                notificationBadge.setVisible(true);
            } else {
                notificationBadge.setVisible(false);
            }
        }
    }



    // Autres méthodes de la classe NotificationManager...

    // Setter pour le badge de notification
    public void setNotificationBadge(Label badge) {
        this.notificationBadge = badge;
        // Mettre à jour le badge lorsqu'il est initialisé
        updateNotificationBadge();
    }
  


//    public void openNotification() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("Notification.fxml"));
//            Parent root = loader.load();
//            Stage stage = new Stage();
//            Scene scene=new Scene(root);
//            stage.setScene(scene);
//            Image icon1 =new Image("notif.png");
//			stage.getIcons().add(icon1);
//			stage.setTitle("Notification");
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    //}

   
// pour profillllllllllllllllllll
    
    @FXML
    private void goToProfilePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profil.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de la page de profil (si nécessaire)
            ControllerProfil controllerProfil = loader.getController();

            // Configurer d'autres éléments de la page de profil si nécessaire
            controllerProfil.initialize(); // Pour initialiser les éléments de la page de profil

            Stage stage = new Stage();
            
            stage.setScene(new Scene(root));
            Image icon1 =new Image("profile.png");
            stage.getIcons().add(icon1);
            stage.setTitle("Profil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML Button dg;
    @FXML
    private void openGraphe() {
    	Graphe graphe = new Graphe();
	    Stage stage=new Stage();
	    // Appeler la méthode start() pour afficher le graphe
	    graphe.start(stage);
	    stage.setTitle("Suivi");
	    Image icon1 = new Image("graphe.png");

	    stage.getIcons().add(icon1);
    }


}
