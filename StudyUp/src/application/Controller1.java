package application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;

public class Controller1 {

    @FXML
    private TextField PrjtName;

    @FXML
    private Button creePrjt;

    
    
    @FXML
    private Button annulerPrjt;
    int id_etudiant = LoginController.getId_etudiant();

    @FXML
    public void VeriferCreation(ActionEvent e) {
        // Récupérer le nom du projet à partir du champ de texte
        String projectName = PrjtName.getText();

        // Vérifier si le projet existe déjà dans la base de données
        if (projectExists(projectName)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Projet existant", "Un projet avec le même nom existe déjà.");
            return; // Sortir de la méthode sans insérer le projet
        }

        // Insérer le projet dans la base de données
        insertProject(projectName);

        // Fermer la fenêtre actuelle (le formulaire de création de projet)
        Stage stage = (Stage) creePrjt.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void AnnulerCreation(ActionEvent e) {
        // Réinitialiser le formulaire si nécessaire
        PrjtName.clear();

        // Fermer la fenêtre actuelle
        Stage stage = (Stage) annulerPrjt.getScene().getWindow();
        stage.close();
    }

    private boolean projectExists(String projectName) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/thenotes", "root", "");

            String query = "SELECT COUNT(*) FROM projet WHERE nom_projet = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, projectName);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }

            con.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Méthode pour récupérer l'ID de l'étudiant à partir de l'ID_Authen
//    public int recupererIdEtudiant(int idAuthen) throws SQLException {
//        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thenotes", "root", "")) {
//            // Préparer la requête SQL
//            String query = "SELECT id_etudiant FROM s_authentifier WHERE id_authentification = ?";
//            PreparedStatement preparedStatement = connection.prepareStatement(query);
//            preparedStatement.setInt(1, idAuthen);
//
//            // Exécuter la requête SQL
//            ResultSet resultSet = preparedStatement.executeQuery();
//
//            // Vérifier si un enregistrement a été trouvé
//            if (resultSet.next()) {
//                // Récupérer l'ID de l'étudiant depuis le résultat de la requête
//                int idEtudiant = resultSet.getInt("id_etudiant");
//                return idEtudiant;
//            } else {
//                // Aucun enregistrement trouvé pour cet ID_Authen
//                throw new SQLException("Aucun étudiant trouvé avec l'ID_Authen spécifié.");
//            }
//        }
//    }
//    

    private void insertProject(String projectName) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/thenotes", "root", "");

            String query = "INSERT INTO projet (nom_projet, date_de_creation, id_etudiant) VALUES (?, CURRENT_DATE(), ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
           // int idetudiant=recupererIdEtudiant(recupererIdAuthentification());
            pstmt.setString(1, projectName);
            pstmt.setInt(2, id_etudiant); // Remplacer '1' par l'ID de l'étudiant, ou récupérer l'ID à partir d'une autre source

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Le projet a été inséré avec succès !");
            }

            con.close();
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
