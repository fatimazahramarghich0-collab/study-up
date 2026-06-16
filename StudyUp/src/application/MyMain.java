package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;



public class MyMain extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	

	@Override
	public void start(Stage stage) throws Exception {
		
		
//		public void switchToScene2(ActionEvent event) throws IOException {
//			  Parent root = FXMLLoader.load(getClass().getResource("Scene2.fxml"));
//			  stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//			  scene = new Scene(root);
//			  stage.setScene(scene);
//			  stage.show();
//			 }
//		
//		Parent root=FXMLLoader.load(getClass().getResource("HomePage.fxml"));
//		Scene scene_home=new Scene(root);
//		//String css=this.getClass().getResource("login.css").toExternalForm();
//		//scene_home.getStylesheets().add(css);
//		stage.setScene(scene_home);
//		stage.setTitle("Click up app (change)");
//		stage.setFullScreen(true);
//		stage.setFullScreenExitHint("Press q to quit full screen mode");
//		stage.setFullScreenExitKeyCombination(KeyCombination.valueOf("q"));
//		stage.show();
		
//		try {
//			
//			Parent root=FXMLLoader.load(getClass().getResource("LoginPage.fxml"));
//			Scene scene=new Scene(root,300,240);
//			String css=this.getClass().getResource("login.css").toExternalForm();
//			scene.getStylesheets().add(css);
//			Image icon1 =new Image("login_icon.png");
//			stage.getIcons().add(icon1);
//			
//			stage.setScene(scene);
//			stage.setResizable(false);
//			stage.setTitle("Login page");
//			stage.show();
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//		new Stopwatch();
		
		 try {
			
			 FXMLLoader loader = new FXMLLoader(getClass().getResource("HomePage.fxml"));
		        Parent root = loader.load();
		        
		        HomeController controller = loader.getController(); // Récupérer le contrôleur
		        controller.setStage(stage); // Passer la référence du stage au contrôleur
		        
		        Scene scene = new Scene(root, 650, 500);
		        String css = getClass().getResource("home.css").toExternalForm();
		        scene.getStylesheets().add(css);
		        stage.setTitle("Study Up");
		        stage.setResizable(false);
		        Image icon1 =new Image("Study_up__2_-removebg-preview.png");
		        stage.getIcons().add(icon1);
		        stage.setScene(scene);
		        stage.show();
		 }catch(Exception e) {
			 System.out.println(e.getMessage());
		 }

//		
//		    
//        try {
//            Parent root = FXMLLoader.load(getClass().getResource("dashb.fxml"));
//            Scene scene = new Scene(root);
//            stage.setScene(scene);
//            stage.setTitle("Tableau de bord");
//            
//          String css1 = this.getClass().getResource("sidebar.css").toExternalForm();
//          String css2=this.getClass().getResource("Project.css").toExternalForm();
//          
//          scene.getStylesheets().add(css1);
//          scene.getStylesheets().add(css2);
//            stage.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        
//		
//        try {
//            Parent root = FXMLLoader.load(getClass().getResource("Inscription.fxml"));
//            Scene scene = new Scene(root);
//
//            // Ajouter la feuille de style CSS à la scène
//            String css = this.getClass().getResource("application.css").toExternalForm();
//            scene.getStylesheets().add(css);
//            stage.setTitle("Inscription");
//            stage.setScene(scene);
//            
//            stage.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    
		//new Stopwatch();
		//afficher les para de settings quand je clique sur setting lorsque le time commenece
		//verifier les messages quand break et pomodoro sont terminees
		//afficher graphe(secteur par exemple ) de nombre de pomodor fait
		//design
	}


}
