package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class Controler_signUp {

    @FXML
    private ImageView image_Insc;

    @FXML
    private TextField UserID;

    @FXML
    private TextField NomId;

    @FXML
    private TextField EMAILID;

    @FXML
    private PasswordField PASSWORDID;

    @FXML
    private PasswordField PASSCONFIRMATIONID;

    @FXML
    private Button Sign_id;

    private Stage stage;

    // Définition des informations de connexion à la base de données
    private static final String DB_URL = "jdbc:mysql://localhost:3306/thenotes";
    private static final String DB_USER = "root"; // Remplacez par votre nom d'utilisateur MySQL
    private static final String DB_PASSWORD = ""; // Remplacez par votre mot de passe MySQL

    // Méthode pour établir une connexion à la base de données
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Méthode pour inscrire un nouvel utilisateur dans la base de données
    private void registerUser(String nom, String prenom, String email, String password) {
        try (Connection conn = getConnection()) {
            // Insertion dans la table etudiant
            String sqlEtudiant = "INSERT INTO etudiant (nom, prenom) VALUES (?, ?)";
            int idEtudiant = -1; // Initialiser l'ID de l'étudiant à une valeur négative par défaut
            try (PreparedStatement statementEtudiant = conn.prepareStatement(sqlEtudiant, Statement.RETURN_GENERATED_KEYS)) {
                statementEtudiant.setString(1, nom);
                statementEtudiant.setString(2, prenom);
                int rowsInserted = statementEtudiant.executeUpdate();
                if (rowsInserted == 0) {
                    throw new SQLException("La création de l'étudiant a échoué, aucune ligne insérée.");
                }
                // Récupérer l'ID auto-incrémenté de l'étudiant
                try (ResultSet generatedKeys = statementEtudiant.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idEtudiant = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Échec de la récupération de l'ID auto-incrémenté de l'étudiant.");
                    }
                }
                System.out.println("Étudiant enregistré avec succès dans la table etudiant. ID: " + idEtudiant);
            }

            // Insertion dans la table s_authentifier
            String sqlAuthentifier = "INSERT INTO s_authentifier (nom, prenom, email, password, id_etudiant) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statementAuthentifier = conn.prepareStatement(sqlAuthentifier)) {
                statementAuthentifier.setString(1, nom);
                statementAuthentifier.setString(2, prenom);
                statementAuthentifier.setString(3, email);
                statementAuthentifier.setString(4, password);
                statementAuthentifier.setInt(5, idEtudiant);
                statementAuthentifier.executeUpdate();
                System.out.println("Utilisateur enregistré avec succès dans la table s_authentifier.");

            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'inscription de l'utilisateur dans la base de données : " + e.getMessage());
        }
    }

    @FXML
    private void SignUp(ActionEvent event) {
        System.out.println("Méthode SignUp() appelée."); // Vérifiez si la méthode est appelée
        // Récupérer les valeurs des champs

        String prenom = UserID.getText();
        String nom = NomId.getText();
        String email = EMAILID.getText();
        String password = PASSWORDID.getText();
        String confirmPassword = PASSCONFIRMATIONID.getText();

        // Affichez les valeurs récupérées pour vous assurer qu'elles sont correctes
        System.out.println("Prénom: " + prenom);
        System.out.println("Nom: " + nom);
        System.out.println("Email: " + email);
        System.out.println("Mot de passe: " + password);
        System.out.println("Confirmation du mot de passe: " + confirmPassword);

        // Vérifier si les champs sont valides et que le mot de passe est fort
        if (isValidUserInput(nom, prenom, email, password, confirmPassword) && isStrongPassword(password)) {
            System.out.println("Les champs sont valides et le mot de passe est fort."); // Vérifiez si cette condition est satisfaite
            // Enregistrer l'utilisateur dans la base de données
            registerUser(nom, prenom, email, password);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/LoginPage.fxml"));
                Parent root = loader.load();
                Scene scene = Sign_id.getScene(); // Obtenez la scène actuelle à partir de n'importe quel élément de la fenêtre
                scene.setRoot(root);
                String css = getClass().getResource("login.css").toExternalForm();
                scene.getStylesheets().add(css);
                stage.setWidth(300);
                stage.setHeight(270);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Les champs ne sont pas valides ou le mot de passe n'est pas assez fort."); // Vérifiez si cette condition est satisfaite
        }
    }

    // Méthode pour vérifier si les champs d'utilisateur sont valides
    private boolean isValidUserInput(String nom, String prenom, String email, String password, String confirmPassword) {
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            System.out.println("zczecezce");
            showAlert("Tous les champs doivent être remplis.");
            return false;
        }

        if (!isValidEmail(email)) {
            System.out.println("valide");
            showAlert("L'adresse e-mail n'est pas valide.");

            return false;
        }

        if (!isValidName(nom) || !isValidName(prenom)) {
            System.out.println("nom valide");
            showAlert("Le nom et le prénom doivent contenir uniquement des lettres.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            System.out.println("mot de passe ne valide");
            showAlert("Les mots de passe ne correspondent pas.");
            return false;
        }

        return true;
    }

    // Méthode pour vérifier si l'email est au bon format
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&-]+(?:\\.[a-zA-Z0-9_+&-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Méthode pour vérifier si le nom ou le prénom est dans un format valide
    private boolean isValidName(String name) {
        String nameRegex = "^[a-zA-Z]+$";
        Pattern pattern = Pattern.compile(nameRegex);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    // Méthode pour vérifier si le mot de passe est fort
    private boolean isStrongPassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setStage(Stage stage) {
        this.stage = stage;

    }

    @FXML
    public void goToLogin(ActionEvent event) throws IOException {
        System.out.println("Méthode goTLogin appelée !");

        // Ajoutez votre propre logique pour aller à la page d'inscription
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/LoginPage.fxml"));
        Parent root = loader.load();

        // Récupérer la scène actuelle à partir de n'importe quel élément de la fenêtre
        Scene scene = btnlink.getScene();

        // Changer la scène pour afficher la page d'inscription
        scene.setRoot(root);
        image_Insc.setFitHeight(150);
        image_Insc.setFitWidth(184);
        
        // Définir les nouvelles dimensions de la page LoginPage
        Stage stage = (Stage) scene.getWindow();
        stage.setWidth(300);
        stage.setHeight(270);
        
        // Charger le fichier CSS à chaque chargement de la page de connexion
        String css = getClass().getResource("login.css").toExternalForm();
        scene.getStylesheets().add(css);
    }

    @FXML
    private Label messageLabel2;

    @FXML
    private Button btnlink;


}
