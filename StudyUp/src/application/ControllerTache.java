package application;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.TableView;

public class ControllerTache implements Initializable {

    private Projet projet;
    private TableView<Projet> tableView; 

    @FXML
    private TextField titreField;

    @FXML
    private ChoiceBox<String> priorityChoiceBox;
    

    @FXML
    private ChoiceBox<String> statueChoiceBox;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private TextArea contenuTextArea;

    private String[] priorities = {"urgent", "high", "normal", "low"};
    private String[] statues = {"to_do", "in_progress", "needs_review", "complete"};

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        priorityChoiceBox.getItems().addAll(priorities);
        statueChoiceBox.getItems().addAll(statues);
    }

    public void setProjet(Projet projet) {
        this.projet = projet;
    }

    @FXML
    private void saveTask() {
        String titre = titreField.getText();
        String priority = priorityChoiceBox.getValue();
        String statue = statueChoiceBox.getValue();
        LocalDate dueDate = dueDatePicker.getValue();
        String contenu = contenuTextArea.getText();

        if (titre.isEmpty() || priority == null || statue == null || dueDate == null || contenu.isEmpty()) {
            // Afficher un message d'erreur si tous les champs ne sont pas remplis
            System.out.println("Tous les champs sont obligatoires !");
            return;
        }

        // Insérer la nouvelle tâche dans la base de données
        insertTask(titre, priority, statue, dueDate, contenu);

        // Fermer la fenêtre après l'ajout de la tâche
        Stage stage = (Stage) titreField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancelTask() {
        // Fermer la fenêtre sans ajouter de nouvelle tâche
        Stage stage = (Stage) titreField.getScene().getWindow();
        stage.close();
    }

    public void setTableView(TableView<Projet> tableView) {
        this.tableView = tableView;
    }
    private void insertTask(String titre, String priority, String statue, LocalDate dueDate, String contenu) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/thenotes", "root", "")) {
            String query = "INSERT INTO tache (id_projet, titre_tache, priority, statue, due_date, contenu) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, projet.getIdProjet());
            pstmt.setString(2, titre);
            pstmt.setString(3, priority);
            pstmt.setString(4, statue);
            pstmt.setDate(5, Date.valueOf(dueDate));
            pstmt.setString(6, contenu);
            pstmt.executeUpdate();
            System.out.println("Tâche ajoutée avec succès !");
            
            // Ajouter la nouvelle tâche à la liste observable
            Tache nouvelleTache = new Tache(titre, priority, statue, dueDate);
            projet.getTaches().add(nouvelleTache);

            // Rafraîchir le TableView après l'insertion de la tâche
            tableView.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 

}
