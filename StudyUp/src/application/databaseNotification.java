package application;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Label;

public class databaseNotification {
	public List<String> loadAllTaskTitles() {
        List<String> taskTitles = new ArrayList<>();
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/thenotes", "root", "")) {
            String query = "SELECT titre_tache FROM tache WHERE statue != 'complete'";
            PreparedStatement pstmt = con.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("titre_tache");
                taskTitles.add(title);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taskTitles;
    }

	private Label notificationBadge;

    // Autres méthodes et fonctionnalités

    // Setter pour le badge de notification
    public void setNotificationBadge(Label badge) {
        this.notificationBadge = badge;
        // Mettre à jour le badge lorsqu'il est initialisé
        updateNotificationBadge();
    }

    // Méthode pour mettre à jour le badge de notification en fonction du nombre de tâches incomplètes
    private void updateNotificationBadge() {
        if (notificationBadge != null) {
            int incompleteCount = getIncompleteTaskCount();
            if (incompleteCount > 0) {
                notificationBadge.setText(String.valueOf(incompleteCount));
                notificationBadge.setVisible(true);
            } else {
                notificationBadge.setVisible(false);
            }
        }
    }

    // Méthode pour obtenir le nombre de tâches incomplètes
 // Méthode pour obtenir le nombre de tâches incomplètes
    public int getIncompleteTaskCount() {
        int count = 0;
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/thenotes", "root", "")) {
            String query = "SELECT COUNT(*) AS count FROM tache WHERE statue != 'complete'";
            PreparedStatement pstmt = con.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    public void refreshNotificationBadge() {
        // Mettre à jour le badge de notification
        updateNotificationBadge();
    }


}


