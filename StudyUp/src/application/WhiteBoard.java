 package application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import javax.imageio.ImageIO;
import java.io.File;


public class WhiteBoard {
    @FXML
    private Canvas canvas;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private TextField brushSize;
    @FXML
    private CheckBox eraser;

    // Vous pouvez ajouter ici des méthodes pour gérer les événements ou la logique de votre tableau blanc
    
    public void initialize() {
    	GraphicsContext g=canvas.getGraphicsContext2D();
    	
    
    	canvas.setOnMouseDragged(e->{
    		double  size=Double.parseDouble(brushSize.getText());
    		double x =e.getX()-size/2;
    		double y =e.getY()-size/2;
    		if(eraser.isSelected()) {
    			g.clearRect(x,y,size,size);
    			
    		}else {
    			g.setFill(colorPicker.getValue());
    			g.fillRect(x,y,size,size);
    		}
    		
    	});
    	
    }

    public void onSave() {
        try {
            Image snapshot = canvas.snapshot(null, null);
            File desktop = new File(System.getProperty("user.home"), "Desktop");
            File painFolder = new File(desktop, "pain");
            painFolder.mkdir(); // Créer le dossier s'il n'existe pas déjà
            File file = new File(painFolder, "paint.png");
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
        } catch (Exception o) {
            System.out.println("Failed to save image : " + o);
        }
    }

    public void onExit() {
        Platform.exit();
    }
    
    public void drawRectangle() {
        double size = Double.parseDouble(brushSize.getText());
        double x = 100; // Position X arbitraire pour commencer
        double y = 100; // Position Y arbitraire pour commencer
        Rectangle rectangle = new Rectangle(x, y, size, size);
        rectangle.setFill(colorPicker.getValue());
        canvas.getGraphicsContext2D().fillRect(x, y, size, size);
    }

    public void drawCircle() {
        double size = Double.parseDouble(brushSize.getText());
        double x = 300; // Position X arbitraire pour commencer
        double y = 300; // Position Y arbitraire pour commencer
        Circle circle = new Circle(x, y, size / 2);
        circle.setFill(colorPicker.getValue());
        canvas.getGraphicsContext2D().fillOval(x, y, size, size);
    }
}