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

public class ContollerSuppPrjt {

    @FXML
    private TextField PrjtName;

    @FXML
    private Button suppPrjt;

    @FXML
    private Button annulerPrjt;

    private Connection connection;

    @SuppressWarnings("unused")
	private Stage stage; // Référence à la fenêtre actuelle

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initialize() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thenotes", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void VeriferCreation(ActionEvent event) {
        String nomProjet = PrjtName.getText();
        if (nomProjet.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Champ vide", "Veuillez entrer le nom du projet.");
            return;
        }

        try {
            // Vérifier si le projet existe
            int idProjet = getIdProjet(nomProjet);
            if (idProjet == -1) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Projet non trouvé", "Le projet spécifié n'a pas été trouvé.");
                return;
            }

            // Supprimer le projet et ses tâches associées
            deleteProjet(idProjet);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Projet supprimé", "Le projet a été supprimé avec succès.");

            // Fermer la fenêtre de suppression une fois le projet supprimé
//            if (stage != null) {
//                stage.close();
//            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression", "Une erreur est survenue lors de la suppression du projet.");
        }
    }



    @FXML
    void AnnulerCreation(ActionEvent event) {
        // Fermer la fenêtre ou annuler l'opération selon vos besoins
//        if (stage != null) {
//            stage.close();
//        }
    	 PrjtName.setText("");
    	 Stage stage = (Stage) annulerPrjt.getScene().getWindow();
    	 
         stage.close();
    	 
    	
    }

    private int getIdProjet(String nomProjet) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT id_projet FROM projet WHERE nom_projet = ?");
        statement.setString(1, nomProjet);
        statement.execute();
        int idProjet = -1;
        while (statement.getResultSet().next()) {
            idProjet = statement.getResultSet().getInt("id_projet");
        }
        return idProjet;
    }

    private void deleteProjet(int idProjet) throws SQLException {
        // Supprimer le projet
        PreparedStatement deleteProjetStatement = connection.prepareStatement("DELETE FROM projet WHERE id_projet = ?");
        deleteProjetStatement.setInt(1, idProjet);
        deleteProjetStatement.executeUpdate();

        // Supprimer les tâches associées au projet
        PreparedStatement deleteTachesStatement = connection.prepareStatement("DELETE FROM tache WHERE id_projet = ?");
        deleteTachesStatement.setInt(1, idProjet);
        deleteTachesStatement.executeUpdate();
    }

    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
