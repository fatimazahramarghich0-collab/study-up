package application;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ControllerProfil {
    @FXML
    private TextField titreField;

    @FXML
    private TextField titreField1;

    @FXML
    private TextField titreField111;

    public void initialize() {
        try {
            int etudiantId = LoginController.getId_etudiant();
            System.out.println("ID de l'étudiant récupéré : " + etudiantId);
            if (etudiantId != 0) {
                // Récupérer les informations d'authentification
                String[] authentifierInfo = getAuthentificationInformation(etudiantId);
                if (authentifierInfo != null && authentifierInfo.length >= 3) {
                    System.out.println("Informations d'authentification récupérées : " + authentifierInfo[0] + " " + authentifierInfo[1] + " " + authentifierInfo[2]);
                    // Afficher les informations d'authentification dans les champs de texte
                    titreField.setText(authentifierInfo[0]); // nom
                    titreField1.setText(authentifierInfo[1]); // prenom
                    titreField111.setText(authentifierInfo[2]); // email
                }
            } else {
                System.out.println("ID d'étudiant non valide : " + etudiantId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String[] getAuthentificationInformation(int etudiantId) throws SQLException {
        String[] authentifierInfo = null;
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/thenotes", "root", "")) {
            String query = "SELECT nom, prenom, email FROM s_authentifier WHERE id_etudiant = ?";
            try (PreparedStatement pstmt = con.prepareStatement(query)) {
                pstmt.setInt(1, etudiantId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String nom = rs.getString("nom");
                        String prenom = rs.getString("prenom");
                        String email = rs.getString("email");
                        authentifierInfo = new String[]{nom, prenom, email};
                    }
                }
            }
        }
        return authentifierInfo;
    }
}
