package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import javafx.stage.Stage;

public class LoginController {
    @FXML
    private Button linkButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private static int id_etudiant;

    public static int getId_etudiant() {
        return id_etudiant;
    }

    public static void setId_etudiant(int id_etudiant) {
        LoginController.id_etudiant = id_etudiant;
    }
    @FXML
    private void login(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        //Connexion a la base 
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/thenotes", "root", "");
            String query = "SELECT id_etudiant FROM s_authentifier WHERE prenom = ? AND password = ?";
            try (PreparedStatement statement = con.prepareStatement(query)) {
                if (!username.isEmpty() && !password.isEmpty()) {
                    statement.setString(1, username);
                    statement.setString(2, password);

                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                    	setId_etudiant(resultSet.getInt("id_etudiant"));
                        
                        afficherMessage("Connexion réussie !");
                        Parent root = FXMLLoader.load(getClass().getResource("dashb.fxml"));
                        
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        Scene scene = new Scene(root,1230,680);
                        String css=this.getClass().getResource("sidebar.css").toExternalForm();
            			scene.getStylesheets().add(css);
                        stage.setScene(scene);
                        stage.setResizable(true);
                        stage.show();
                    } else {
                        afficherMessage("Nom d'utilisateur ou mot de passe incorrect!");
                    }
                    resultSet.close();
                } else {
                    afficherMessage("Remplissez les deux champs!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

        

    private static void afficherMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    @FXML
    private ImageView image_Insc;
    private Scene previousScene;

    @FXML
    public void goToSignUp(ActionEvent event) throws IOException {
        System.out.println("Méthode goToSignUp appelée !");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Inscription.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = linkButton.getScene();
        scene.setRoot(root);
        stage.setHeight(500);
        stage.setWidth(627);
    }

    @FXML
    public void goToPreviousScene(ActionEvent event) {
        if (previousScene != null) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(previousScene);
            stage.show();
        }
    }


	@SuppressWarnings("unused")
	private Stage stage;
	public void setStage(Stage stage) {
		// TODO Auto-generated method stub
		this.stage=stage;
	}
}
