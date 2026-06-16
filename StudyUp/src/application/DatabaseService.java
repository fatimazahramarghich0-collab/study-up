package application;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/thenotes";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public List<Task> getTasksForDate(LocalDate date) {
        List<Task> tasks = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT titre_tache, statue FROM tache WHERE due_date = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setDate(1, Date.valueOf(date));
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String title1 = resultSet.getString("titre_tache");
                        Enum<Statue> status1 = Statue.TO_DO;
                        tasks.add(new Task(title1, status1, null));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }
}
