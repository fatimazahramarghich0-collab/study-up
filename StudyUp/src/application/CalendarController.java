package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class CalendarController implements Initializable {

    @FXML
    private DatePicker datePicker;

    @FXML
    private ListView<Task> taskListView;

    private DatabaseService databaseService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseService = new DatabaseService();

        // Définir la date maximale pour le DatePicker sur une date dans le futur
        datePicker.setShowWeekNumbers(true);

        // Définir l'action de sélection de date
        datePicker.setOnAction(event -> {
            LocalDate selectedDate = datePicker.getValue();
            updateTasks(selectedDate);
        });
    }

    private void updateTasks(LocalDate date) {
        if (date != null) {
            taskListView.getItems().clear();
            List<Task> tasks = databaseService.getTasksForDate(date);
            taskListView.getItems().addAll(tasks);
        }
    }

    // Méthode pour afficher les tâches
    @FXML
    private void showTasks() {
        LocalDate selectedDate = datePicker.getValue();
        updateTasks(selectedDate);
    }
}
