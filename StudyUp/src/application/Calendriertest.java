package application;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class Calendriertest extends Application {

    private ComboBox<Integer> monthComboBox;
    private ComboBox<Integer> yearComboBox;
    private GridPane gridPane;
    private YearMonth currentYearMonth;
    private TableView<TaskCalendrietest> taskTableView;
    @SuppressWarnings("unused")
	private TaskCalendrietest taskCalendrier;

    // Constructeur prenant une instance de TaskCalendriertest
    public Calendriertest(TaskCalendrietest taskCalendrier) {
        this.taskCalendrier = taskCalendrier;
    }

    @Override
    public void start(Stage primaryStage) {
        currentYearMonth = YearMonth.now();

        monthComboBox = new ComboBox<>();
        for (int month = 1; month <= 12; month++) {
            monthComboBox.getItems().add(month);
        }
        monthComboBox.setValue(currentYearMonth.getMonthValue());

        yearComboBox = new ComboBox<>();
        for (int year = 2000; year <= 2100; year++) {
            yearComboBox.getItems().add(year);
        }
        yearComboBox.setValue(currentYearMonth.getYear());

        monthComboBox.setOnAction(e -> updateCalendar());
        yearComboBox.setOnAction(e -> updateCalendar());

        Button prevButton = new Button("<<");
        prevButton.setOnAction(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            updateComboBoxes();
            updateCalendar();
        });

        Button nextButton = new Button(">>");
        nextButton.setOnAction(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            updateComboBoxes();
            updateCalendar();
        });

        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.getStyleClass().add("calendar-grid");

        GridPane header = new GridPane();
        header.add(prevButton, 0, 0);
        header.add(monthComboBox, 1, 0);
        header.add(yearComboBox, 2, 0);
        header.add(nextButton, 3, 0);
        GridPane.setHalignment(prevButton, HPos.LEFT);
        GridPane.setHalignment(nextButton, HPos.RIGHT);

        Text titleText = new Text("Calendrier");
        titleText.getStyleClass().add("calendar-title");

        HBox titleBox = new HBox(titleText);
        titleBox.setAlignment(Pos.CENTER);

        HBox headerBox = new HBox(header);
        headerBox.setAlignment(Pos.CENTER);

        taskTableView = new TableView<>();
       
        TableColumn<TaskCalendrietest, String> titleColumn = new TableColumn<>("Titre");
        TableColumn<TaskCalendrietest, String> statusColumn = new TableColumn<>("Statut");
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty().asString());
        
        setupTableColumns();

        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.add(titleBox, 0, 0);
        root.add(headerBox, 0, 1);
        setupWeekdays();
        root.add(gridPane, 0, 2);
        root.add(taskTableView, 0, 3);

        Scene scene = new Scene(root, 400, 600);
        primaryStage.setTitle("Calendrier");

        String cssPath = getClass().getResource("Style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        primaryStage.setScene(scene);
        primaryStage.show();

        updateCalendar();
    }

    private void updateComboBoxes() {
        monthComboBox.setValue(currentYearMonth.getMonthValue());
        yearComboBox.setValue(currentYearMonth.getYear());
    }

    private void updateCalendar() {
        gridPane.getChildren().clear();
        int firstDayOfMonth = currentYearMonth.atDay(1).getDayOfWeek().getValue();
        int daysInMonth = currentYearMonth.lengthOfMonth();

        setupWeekdays();

        int row = 1;
        int col = firstDayOfMonth - 1;
        for (int day = 1; day <= daysInMonth; day++) {
            Button button = new Button(Integer.toString(day));
            button.getStyleClass().add("day-button");
            button.setOnAction(e -> {
                LocalDate selectedDate = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonthValue(), Integer.parseInt(button.getText()));
                System.out.println("Date sélectionnée : " + selectedDate);
                loadTasksForDate(selectedDate);
            });
            gridPane.add(button, col, row);
            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }
    }

    private void setupWeekdays() {
        String[] weekdays = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};

        for (int i = 0; i < 7; i++) {
            Text dayLabel = new Text(weekdays[i]);
            gridPane.add(dayLabel, i, 0);
        }
    }

    private static final String DB_URL = "jdbc:mysql://localhost:3306/thenotes";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public List<TaskCalendrietest> loadTasksForDate(LocalDate date) {
        List<TaskCalendrietest> tasks = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT titre_tache, statue FROM tache WHERE due_date = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                // Utilisation de java.sql.Timestamp pour inclure également l'heure
                statement.setObject(1, java.sql.Timestamp.valueOf(date.atStartOfDay()));
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String title = resultSet.getString("titre_tache");
                        // Récupération du statut de la base de données
                        String status = resultSet.getString("statue");
                        // Vérification de la validité du statut
                        if (isValidStatue(status)) {
                            tasks.add(new TaskCalendrietest(title, status));
                        } else {
                            // Gestion du cas où le statut n'est pas valide
                            System.err.println("Statut non valide récupéré de la base de données : " + status);
                            // Vous pouvez attribuer une valeur par défaut ou lancer une exception ici
                            tasks.add(new TaskCalendrietest(title, "default")); // Exemple d'attribution d'une valeur par défaut
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        taskTableView.getItems().clear();
        taskTableView.getItems().addAll(tasks);
        return tasks;
    }

    // Méthode pour vérifier si le statut est valide
    private boolean isValidStatue(String status) {
        for (Statue statue : Statue.values()) {
            if (statue.getValue().equals(status)) {
                return true;
            }
        }
        return false;
    }

    public Calendriertest() {
        // Vous pouvez initialiser ici si nécessaire, ou laisser vide
    }

    @SuppressWarnings("unchecked")
	private void setupTableColumns() {
        TableColumn<TaskCalendrietest, String> titleColumn = new TableColumn<>("Titre");
        TableColumn<TaskCalendrietest, String> statusColumn = new TableColumn<>("Statut");
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        statusColumn.setCellValueFactory(cellData -> {
            SimpleObjectProperty<Statue> statusProperty = cellData.getValue().statusProperty();
            if (statusProperty != null && statusProperty.get() != null) {
                return new SimpleStringProperty(statusProperty.get().getValue());
            } else {
                return new SimpleStringProperty(""); // ou une autre valeur par défaut
            }
        });


        taskTableView.getColumns().addAll(titleColumn, statusColumn);
    }

    public static void main(String[] args) {
        launch(args);
    }
}