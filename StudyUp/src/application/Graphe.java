package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Graphe extends Application {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/thenotes";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    @Override
    public void start(Stage stage) {
        // Créer les données pour le graphique à barres
        CategoryAxis xAxis1 = new CategoryAxis();
        NumberAxis yAxis1 = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis1, yAxis1);
        barChart.setTitle("Nombre de tâches complètes par semaine");

        // Créer une série pour le graphique à barres
        BarChart.Series<String, Number> series1 = new BarChart.Series<>();
        series1.setName("Tâches Complètes");

        // Créer les données pour le graphique circulaire
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Tâches du jour");

        // Se connecter à la base de données
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            // Obtenir la date de début et de fin de la semaine actuelle
            LocalDate currentDate = LocalDate.now();
            LocalDate startOfWeek = currentDate.with(DayOfWeek.MONDAY);
            LocalDate endOfWeek = startOfWeek.plusDays(6);

            // Ajouter les noms des jours de la semaine comme libellés des catégories sur l'axe des abscisses
            List<String> daysOfWeek = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                LocalDate date = startOfWeek.plusDays(i);
                String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
                xAxis1.getCategories().add(dayOfWeek);
                daysOfWeek.add(dayOfWeek);
            }

            // Exécuter une requête SQL pour obtenir le nombre de tâches complètes pour chaque jour de la semaine
            String sqlQueryBarChart = "SELECT DATE_FORMAT(due_date, '%Y-%m-%d') AS day, COUNT(*) AS count FROM tache WHERE due_date >= '" + startOfWeek + "' AND due_date <= '" + endOfWeek + "' AND statue = 'complete' GROUP BY DATE_FORMAT(due_date, '%Y-%m-%d')";
            ResultSet resultSetBarChart = statement.executeQuery(sqlQueryBarChart);

            // Créer un tableau pour stocker les données des tâches complètes pour chaque jour de la semaine
            int[] taskCounts = new int[7];

            // Ajouter les données du résultat de la requête au tableau des tâches complètes
            while (resultSetBarChart.next()) {
                String day = resultSetBarChart.getString("day");
                int count = resultSetBarChart.getInt("count");
                System.out.println("Jour : " + day + ", Nombre de tâches complètes : " + count);

                // Trouver l'index du jour dans la liste des jours de la semaine
                LocalDate dayDate = LocalDate.parse(day);
                int index = (dayDate.getDayOfWeek().getValue() % 7 == 0) ? 6 : dayDate.getDayOfWeek().getValue() % 7 - 1;
                taskCounts[index] = count;
            }

            // Ajouter les données du tableau des tâches complètes au graphique à barres
            for (int i = 0; i < 7; i++) {
                series1.getData().add(new BarChart.Data<>(xAxis1.getCategories().get(i), taskCounts[i]));
            }

            // Ajouter la série au graphique à barres
            barChart.getData().add(series1);

            // Exécuter une requête SQL pour obtenir le nombre de tâches actuelles pour chaque statue
            String sqlQueryPieChart = "SELECT statue, COUNT(*) AS count FROM tache WHERE due_date = '" + currentDate + "' GROUP BY statue";
            ResultSet resultSetPieChart = statement.executeQuery(sqlQueryPieChart);

            
         // Définir une palette de couleurs pour chaque statue
            Map<String, Color> colorPalette = new HashMap<>();
            colorPalette.put("TO_DO", Color.RED); // Exemple de couleur pour la statue TO_DO
            colorPalette.put("IN_PROGRESS", Color.BLUE); // Exemple de couleur pour la statue IN_PROGRESS
            colorPalette.put("NEEDS_REVIEW", Color.GREEN); // Exemple de couleur pour la statue NEEDS_REVIEW
            colorPalette.put("COMPLETE", Color.YELLOW); // Exemple de couleur pour la statue COMPLETE
            
            System.out.println("Couleurs définies dans la palette :");
            for (Map.Entry<String, Color> entry : colorPalette.entrySet()) {
                System.out.println("Statut : " + entry.getKey() + ", Couleur : " + entry.getValue());
            }
            
            // Ajouter les données du résultat de la requête au graphique circulaire
            boolean tasksExist = false; // Variable pour vérifier si des tâches existent pour la date actuelle
            while (resultSetPieChart.next()) {
                String status = resultSetPieChart.getString("statue");
                int count = resultSetPieChart.getInt("count");
                // Vérifiez si la statue a une couleur définie dans la palette
                Color statusColor = colorPalette.get(status);
               
                if (statusColor != null) {
                	// Si une couleur est définie pour cette statue, utilisez-la
                    PieChart.Data newData = new PieChart.Data(status, count);
                
                //pieChart.getData().add(new PieChart.Data(status, count));
                newData.getNode().setStyle("-fx-pie-color: #" + Integer.toHexString(statusColor.hashCode()).substring(0,6) + ";");
                pieChart.getData().add(newData);
                System.out.println("Les couleurs sont appliquees pour le statut : " + status);                }
                else {
                	 // Si aucune couleur n'est définie pour cette statue, utilisez une couleur par défaut
                    pieChart.getData().add(new PieChart.Data(status, count));
                    System.out.println("Aucune couleur n'est définie pour le statut : " + status);

                }
                tasksExist = true;
                System.out.println("Statut : " + status + ", Nombre de tâches : " + count);
            }

            // Si le graphique circulaire est vide, ajoutez une tranche sans tâche avec une couleur personnalisée
            if (!tasksExist) {
                PieChart.Data noTaskData = new PieChart.Data("Aucune tâche", 1); // Ajoutez une tranche avec une seule tâche fictive
                pieChart.getData().add(noTaskData);
                if (noTaskData.getNode() != null) {
                    noTaskData.getNode().setStyle("-fx-pie-color: #CCCCCC;"); // Appliquer une couleur personnalisée
                }
            }

            // Créer un conteneur de mise en page horizontal (HBox) pour organiser les graphiques côte à côte
            HBox hbox = new HBox(barChart, pieChart);

            // Créer la scène
            Scene scene = new Scene(hbox, 1000, 600);

            // Dénir la feuille de style pour la scène
            scene.getStylesheets().add(getClass().getResource("styleGraphe.css").toExternalForm());

            // Définir la scène sur le stage principal
            stage.setScene(scene);

            // Afficher la scène
            stage.show();

            // Enregistrer les graphiques dans un fichier
            saveChartsToFile(hbox, "graphique.png");

        } catch (Exception e) {
            System.out.println("Une erreur s'est produite : " + e.getMessage());
        }
    }

    
        private void saveChartsToFile(HBox container, String fileName) {
            // Créer un objet SnapshotParameters pour définir les paramètres de capture
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT); // Si votre graphique a un fond transparent, sinon, définissez la couleur souhaitée

            // Capturer l'image du conteneur contenant les graphiques
            WritableImage snapshot = container.snapshot(parameters, null);

            // Créer un fichier de sortie
            File file = new File(fileName);
            System.out.println(file.getAbsolutePath());

            try {
                // Enregistrer l'image capturée dans le fichier
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
                System.out.println("Les graphiques ont été enregistrés dans : " + fileName);
            } catch (IOException e) {
                System.out.println("Une erreur s'est produite lors de l'enregistrement des graphiques : " + e.getMessage());
            }
        }
       
    }
