package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ControllerSuppTache {

    @FXML
    private TextField TacheName;

    @FXML
    private Button suppTache;

    @FXML
    private Button annulertache;

    private Connection connection;
    private Controller controller; // Référence au contrôleur principal
    private Projet projet; // Référence au projet

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setProjet(Projet projet) {
        this.projet = projet;
    }

    public void initialize() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thenotes", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void supprimerTache(ActionEvent event) {
        String nomTache = TacheName.getText();
        if (nomTache.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Champ vide", "Veuillez entrer le nom de la tâche.");
            return;
        }

        try {
            // Supprimer la tâche
            deleteTache(nomTache);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Tâche supprimée", "La tâche a été supprimée avec succès.");

            // Supprimer la tâche de la liste du projet dans le contrôleur principal
            if (controller != null && projet != null) {
                projet.getTaches().removeIf(tache -> tache.getTitre().equals(nomTache));
                controller.refreshData(); // Mettre à jour le TableView
            }

            // Fermer la fenêtre de suppression après la suppression de la tâche
            Stage stage = (Stage) annulertache.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression", "Une erreur est survenue lors de la suppression de la tâche.");
        }
    }

    @FXML
    void annulerTache(ActionEvent event) {
        // Fermer la fenêtre ou annuler l'opération selon vos besoins
        Stage stage = (Stage) annulertache.getScene().getWindow();
        stage.close();
    }

    private void deleteTache(String nomTache) throws SQLException {
        // Supprimer la tâche
        PreparedStatement deleteTacheStatement = connection.prepareStatement("DELETE FROM tache WHERE titre_tache = ?");
        deleteTacheStatement.setString(1, nomTache);
        deleteTacheStatement.executeUpdate();
    }

    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
