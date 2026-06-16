package application;

import javax.swing.*;


import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Stopwatch implements ActionListener {
    private int sessions = 0; // Déclaration et initialisation de la variable sessions
    private JFrame frame;
    private JButton startButton;
    private JButton resetButton;
    private JLabel timeLabel;
    private int elapsedTime = 0;
    private int seconds = 0;
    private int minutes = 0;
    private int hours = 0;
    private boolean started = false;
    private int pomodoroDuration = 25; // Durée par défaut du Pomodoro en minutes
    private int breakDuration = 5; // Durée par défaut de la pause en minutes
    private Timer timer;
    private boolean isPomodoro = true; // Variable pour suivre si c'est un Pomodoro ou une pause

    private JMenuBar menuBar;
    private JMenu settingsMenu;
    private JMenuItem pomodoroItem;
    private JMenuItem breakItem;
    // Connexion à la base de données
    private Connection connection;

    public Stopwatch() {
    	 

        try {
            String url = "jdbc:mysql://localhost:3306/thenotes";
            String username = "root";
            String password = "";
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Erreur lors de la connexion à la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        frame = new JFrame("Minuteur-Pomodoro");
        frame.setSize(420, 300);
        frame.setLayout(new BorderLayout());
        
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


       
        // Création du menu
        menuBar = new JMenuBar();
        settingsMenu = new JMenu("Settings");
        pomodoroItem = new JMenuItem("Pomodoro Duration");
        breakItem = new JMenuItem("Break Duration");

        pomodoroItem.addActionListener(this);
        breakItem.addActionListener(this);

        settingsMenu.add(pomodoroItem);
        settingsMenu.add(breakItem);
        menuBar.add(settingsMenu);

        frame.setJMenuBar(menuBar);

        // Création des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startButton = new JButton("START");
        startButton.addActionListener(this);
        resetButton = new JButton("RESET");
        resetButton.addActionListener(this);
        buttonPanel.add(startButton);
        buttonPanel.add(resetButton);

        // Création du label
        timeLabel = new JLabel("00:00:00", JLabel.CENTER);
        timeLabel.setFont(new Font("Verdana", Font.PLAIN, 35));
        timeLabel.setBorder(BorderFactory.createBevelBorder(1));
        timeLabel.setOpaque(true);

        frame.add(timeLabel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            if (started == false) {
                if (pomodoroDuration == 25 && breakDuration == 5) {
                    // Si les paramètres n'ont pas été changés, afficher un message
                    JOptionPane.showMessageDialog(frame, "Change Pomodoro and Break durations in Settings before starting the timer.", "Settings Warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    started = true;
                    startButton.setText("STOP");
                    start();
                }
            } else {
                started = false;
                startButton.setText("START");
                stop();
            }
        }
        if (e.getSource() == resetButton) {
            started = false;
            startButton.setText("START");
            reset();
        }
        
        if (e.getSource() == pomodoroItem) {
            String input = JOptionPane.showInputDialog(frame, "Enter Pomodoro Duration (minutes):");
            try {
                pomodoroDuration = Integer.parseInt(input);
                JOptionPane.showMessageDialog(frame, "Pomodoro Duration set to " + pomodoroDuration + " minutes.");
//                if (!started) {
//                    start();
//                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (e.getSource() == breakItem) {
            String input = JOptionPane.showInputDialog(frame, "Enter Break Duration (minutes):");
            try {
                breakDuration = Integer.parseInt(input);
                JOptionPane.showMessageDialog(frame, "Break Duration set to " + breakDuration + " minutes.");
                if (!started) {
                    start();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private int currentTimerId = -1; // Déclaration de la variable currentTimerId

    void start() {
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                elapsedTime += 1000;
                updateTimeLabel();
                if (isPomodoro && elapsedTime >= pomodoroDuration * 60 * 1000) {
                    stop();
                    isPomodoro = false;
                    JOptionPane.showMessageDialog(frame, "Pomodoro finished! Take a break.");
                    elapsedTime = 0;
                    startButton.setText("START");

                    // Ajout du pomodoro seulement si c'est la première session
                    if (sessions == 0) {
                        try {
                            currentTimerId = ajouterPomodoro(pomodoroDuration, breakDuration); // Capturer l'ID du Pomodoro ajouté
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(frame, "Erreur lors de l'ajout du Pomodoro dans la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    // Mettre à jour la durée de la session
                    try {
                        mettreAJourSessionTime(currentTimerId, pomodoroDuration);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Erreur lors de la mise à jour de la durée de la session.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (!isPomodoro && elapsedTime >= breakDuration * 60 * 1000) {
                    stop();
                    elapsedTime = 0;
                    startButton.setText("START");
                    if (sessions < 4) {
                        sessions++; // Incrémenter le nombre de sessions après chaque pause
                    } else {
                        sessions = 0; // Réinitialiser le nombre de sessions à 0
                        JOptionPane.showMessageDialog(frame, "Pomodoro finished! Take a break.");
                        updateTimeLabel();
                        try {
                            ajouterPomodoro(null, breakDuration); // Ajouter un nouveau Pomodoro avec la valeur null
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(frame, "Erreur lors de l'ajout du Pomodoro dans la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }

            }
        });
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    void reset() {
        timer.stop();
        elapsedTime = 0;
        updateTimeLabel();
    }

    void updateTimeLabel() {
        hours = elapsedTime / 3600000;
        minutes = (elapsedTime / 60000) % 60;
        seconds = (elapsedTime / 1000) % 60;
        String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timeLabel.setText(time);
    }

    // Méthode pour ajouter le nombre de pomodoro à la base de données
    private int ajouterPomodoro(Integer pomodoroDuration, int breakDuration) throws SQLException {
        String insertQuery = "INSERT INTO timer (session_time, break_time) VALUES (?, ?)";
        PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
        if (pomodoroDuration != null) {
            insertStatement.setInt(1, pomodoroDuration);
        } else {
            insertStatement.setNull(1, java.sql.Types.INTEGER); // Insérer une valeur NULL pour le pomodoro
        }
        insertStatement.setInt(2, breakDuration);
        insertStatement.executeUpdate();

        // Récupérer l'ID généré pour le pomodoro
        ResultSet generatedKeys = insertStatement.getGeneratedKeys();
        int timerId = -1;
        if (generatedKeys.next()) {
            timerId = generatedKeys.getInt(1);
        } else {
            throw new SQLException("Échec de récupération de l'ID du pomodoro.");
        }

        // Fermer les ressources
        insertStatement.close();
        generatedKeys.close();

        return timerId;
    }


    // Ajout de la méthode mettreAJourSessionTime()
    private void mettreAJourSessionTime(int timerId, int sessionDuration) throws SQLException {
        String updateQuery = "UPDATE timer SET session_time = ? WHERE id_timer = ?";
        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
        updateStatement.setInt(1, sessionDuration);
        updateStatement.setInt(2, timerId);
        updateStatement.executeUpdate();
        updateStatement.close();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Stopwatch::new);
    }
}
