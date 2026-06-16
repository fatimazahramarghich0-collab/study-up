package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerModTache implements Initializable {

    private TableView<Tache> tableView;
    private Controller controller; 

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

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @FXML
    private void saveTask() {
        String titre = titreField.getText();

        if (titre.isEmpty()) {
            System.out.println("Le champ du titre est obligatoire !");
            return;
        }

        String priority = priorityChoiceBox.getValue();
        String statue = statueChoiceBox.getValue();
        LocalDate dueDate = dueDatePicker.getValue();
        String contenu = contenuTextArea.getText();

        Tache tache = getTaskByTitle(titre);

        if (tache == null) {
            System.out.println("Tâche non trouvée !");
            return;
        }

        updateTask(tache.getIdTache(), priority, statue, dueDate, contenu);

        controller.refreshData();

        Stage stage = (Stage) titreField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancelTask() {
        Stage stage = (Stage) titreField.getScene().getWindow();
        stage.close();
    }

    public void setTableView(TableView<Tache> tableView) {
        this.tableView = tableView;
    }

    private Tache getTaskByTitle(String titre) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/thenotes", "root", "")) {
            String query = "SELECT * FROM tache WHERE titre_tache = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, titre);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int idTache = rs.getInt("id_tache");
                int idProjet = rs.getInt("id_projet");
                String priority = rs.getString("priority");
                String statue = rs.getString("statue");
                LocalDate dueDate = rs.getDate("due_date").toLocalDate();
                String contenu = rs.getString("contenu");

                return new Tache(idTache, idProjet, titre, priority, statue, dueDate, contenu);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void updateTask(int idTache, String priority, String statue, LocalDate dueDate, String contenu) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/thenotes", "root", "")) {
            StringBuilder queryBuilder = new StringBuilder("UPDATE tache SET ");
            List<Object> params = new ArrayList<>();
            
            // Ajoutez les colonnes à mettre à jour en fonction des valeurs non nulles fournies
            if (priority != null) {
                queryBuilder.append("priority = ?, ");
                params.add(priority);
            }
            if (statue != null) {
                queryBuilder.append("statue = ?, ");
                params.add(statue);
            }
            if (dueDate != null) {
                queryBuilder.append("due_date = ?, ");
                params.add(java.sql.Date.valueOf(dueDate));
            }
            if (contenu != null) {
                queryBuilder.append("contenu = ?, ");
                params.add(contenu);
            }
            
            // Supprimez la virgule finale et ajoutez la clause WHERE
            queryBuilder.deleteCharAt(queryBuilder.length() - 2);
            queryBuilder.append("WHERE id_tache = ?");
            
            // Préparez et exécutez la requête avec les paramètres appropriés
            PreparedStatement pstmt = con.prepareStatement(queryBuilder.toString());
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            pstmt.setInt(params.size() + 1, idTache);
            
            // Exécutez la mise à jour
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Tâche mise à jour avec succès !");
            } else {
                System.out.println("Aucune tâche mise à jour !");
            }
        } catch (SQLException e) {
           System.out.println(e.getMessage());
        }
    }


    @SuppressWarnings("unused")
	private void refreshTableView() {
        if (tableView != null) {
            tableView.refresh();
        }
    }
}
