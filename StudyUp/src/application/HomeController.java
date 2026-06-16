package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    protected Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    

   
    
    @FXML
    private void goToLogin(ActionEvent event) throws IOException {
        System.out.println("Méthode Login appelée !");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/LoginPage.fxml"));
        Scene loginScene = new Scene(loader.load());
		
		Image icon1 =new Image("login_icon.png");
		stage.getIcons().add(icon1);
		
        String css = getClass().getResource("login.css").toExternalForm();
        loginScene.getStylesheets().add(css);
        LoginController loginController = loader.getController();
        loginController.setStage(stage);
        stage.setScene(loginScene);
    }
  

    @FXML
    public void goToSignUp(ActionEvent event) throws IOException {
        System.out.println("Méthode goToSignUp appelée !");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Inscription.fxml"));
        Scene signUpScene = new Scene(loader.load());
        
		Image icon2 =new Image("login_icon.png");
		stage.getIcons().add(icon2);
		
        Controler_signUp signUpController = loader.getController();
        signUpController.setStage(stage);
        stage.setScene(signUpScene);
    }
    @FXML
    private WebView webview;
    @FXML
    private void showUserManual(ActionEvent event) {
        // URL du manuel utilisateur
        String manualUrl1 = "file:///C:/Users/Student/Documents/Me/CVvf.pdf";
        String manualUrl2 = "file:///C:/Users/Student/Downloads/CV_aya_ouzarf.pdf";
        String manualUrl3="file:///C:/Users/Student/Downloads/Professional%20Minimalist%20CV%20Resume%20(1).pdf";

        try {
            // Ouvrir l'URL dans le navigateur par défaut
        	 Desktop.getDesktop().browse(new URI(manualUrl3));
        	 Desktop.getDesktop().browse(new URI(manualUrl2));
            Desktop.getDesktop().browse(new URI(manualUrl1));
           
        } catch (IOException | URISyntaxException e) {
            // Gérer les erreurs
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le manuel utilisateur.");
        }
    }

    // Méthode utilitaire pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
