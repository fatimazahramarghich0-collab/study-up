package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DatabaseUtil  {
	// Méthode pour récupérer les informations de l'étudiant à partir de l'ID_Authen
    public String[] recupererInfosEtudiant(int idAuthen) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thenotes", "root", "")) {
            // Préparer la requête SQL
            String query = "SELECT nom, prenom FROM etudiant WHERE id_etudiant = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, idAuthen);

            // Exécuter la requête SQL
            ResultSet resultSet = preparedStatement.executeQuery();

            // Récupérer les informations de l'étudiant
            if (resultSet.next()) {
                // Récupérer les valeurs des colonnes
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
        
                // Retourner les informations de l'étudiant sous forme de tableau de chaînes
                return new String[] {"Nom: " + nom, "Prénom: " + prenom};
            } else {
                // Aucun enregistrement trouvé pour cet ID_Authen
                throw new SQLException("Aucun étudiant trouvé avec l'ID_Authen spécifié.");
            }
        }
    }
    private static final String DB_URL = "jdbc:mysql://localhost:3306/thenotes";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    public static String getStudentNameByIdAuth(int idAuth) throws SQLException {
        String studentName = null;
        String query = "SELECT nom,prenom FROM etudiant WHERE id_authentification = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idAuth);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String prenom = rs.getString("prenom");
                String nom = rs.getString("nom").toUpperCase();
                String prenomCapitalized = Character.toUpperCase(prenom.charAt(0)) + prenom.substring(1).toLowerCase();
                studentName = "Bonjour " + nom + " " + prenomCapitalized;
            }

        }

        return studentName;
    }

    // Méthode pour récupérer l'ID de l'étudiant à partir de l'ID_Authen
    public int recupererIdEtudiant(int idAuthen) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thenotes", "root", "")) {
            // Préparer la requête SQL
            String query = "SELECT id_etudiant FROM s_authentifier WHERE id_authentification = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, 2);

            // Exécuter la requête SQL
            ResultSet resultSet = preparedStatement.executeQuery();

            // Vérifier si un enregistrement a été trouvé
            if (resultSet.next()) {
                // Récupérer l'ID de l'étudiant depuis le résultat de la requête
                int idEtudiant = resultSet.getInt("id_etudiant");
                return idEtudiant;
            } else {
                // Aucun enregistrement trouvé pour cet ID_Authen
                throw new SQLException("Aucun étudiant trouvé avec l'ID_Authen spécifié.");
            }
        }
    }
    public int recupererIdAuthentification() throws SQLException {
        // Initialisation de la variable pour stocker l'id_authentification récupérée
        int idAuthen = 0;
        
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thenotes", "root", "")) {
            // Préparer la requête SQL
            String query = "SELECT id_authentification FROM s_authentifier";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            // Exécuter la requête SQL
            ResultSet resultSet = preparedStatement.executeQuery();

            // Récupérer l'id_authentification
            if (resultSet.next()) {
                idAuthen = resultSet.getInt("id_authentification");
            }
        }
        
        // Retourner l'id_authentification récupérée
        return idAuthen;
    }
    @FXML
    private Label studentLabel;
    @SuppressWarnings("unused")
	private void afficherNomEtudiant() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/thenotes", "root", "")) {
            String query = "SELECT nom, prenom FROM etudiant WHERE id_authen = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1,recupererIdAuthentification() );
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String nom = rs.getString("nom").toUpperCase();
                String prenom = rs.getString("prenom");
                String prenomCapitalized = Character.toUpperCase(prenom.charAt(0)) + prenom.substring(1).toLowerCase();
                studentLabel.setText("Bonjour " + nom + " " + prenomCapitalized);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer les exceptions SQLException
        }
    }


}
    


