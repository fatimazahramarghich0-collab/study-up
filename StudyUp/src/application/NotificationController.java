package application;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class NotificationController {

    @FXML
    private ListView<Task> notificationListView;

    public void setTasks(List<String> taskTitles) {
        System.out.println("Méthode setTasks appelée."); // Message de journalisation

        if (taskTitles != null) {
            List<Task> tasks = new ArrayList<>();
            for (String title : taskTitles) {
                Task task = new Task(title, Statue.IN_PROGRESS, Priority.NORMAL); // Remplacez les valeurs null par des valeurs appropriées
                tasks.add(task);
            }
            notificationListView.getItems().clear(); // Effacez d'abord les éléments existants
            notificationListView.getItems().addAll(tasks); // Utiliser addAll() pour ajouter tous les éléments de la liste

            // Ajouter un message de journalisation pour vérifier si les notifications sont ajoutées
            System.out.println("Notifications ajoutées : " + tasks.size());
            
            notificationListView.setCellFactory(new Callback<ListView<Task>, ListCell<Task>>() {
                @Override
                public ListCell<Task> call(ListView<Task> listView) {
                    return new ListCell<Task>() {
                        @SuppressWarnings("unused")
						@Override
                        protected void updateItem(Task task, boolean empty) {
                            super.updateItem(task, empty);
                            if (task != null) {
                                setText(task.getTitle());
                               // @SuppressWarnings("unused")
								String priorityColor = "#ffffff";
                                String priorityText = "";// Par défaut, si la priorité n'est pas définie
                                
                                //setStyle("-fx-background-color: " + priorityColor + ";");
                            } else {
                                setText(null);
                                setStyle(null);
                            }
                        }
                    };
                }
            });
        } else {
            // Gérer le cas où taskTitles est null
        }
    }
}
