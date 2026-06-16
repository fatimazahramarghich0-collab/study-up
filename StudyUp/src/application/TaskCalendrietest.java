package application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class TaskCalendrietest {
    private SimpleStringProperty title;
    private SimpleObjectProperty<Statue> status;

    // Constructeur prenant une chaîne de caractères et un objet Statue
    public TaskCalendrietest(String title, Statue status) {
        this.title = new SimpleStringProperty(title);
        this.status = new SimpleObjectProperty<>(status);
    }
    
    // Constructeur pour le chargement depuis la base de données
    public TaskCalendrietest(String title, String status) {
        this.title = new SimpleStringProperty(title);
        // Convertir la chaîne en Enum
        for (Statue statue : Statue.values()) {
            if (statue.getValue().equals(status)) {
                this.status = new SimpleObjectProperty<>(statue);
                return; // Sortie de la boucle une fois que la correspondance est trouvée
            }
        }
        // Gérer le cas où la valeur de statut n'est pas valide
        System.err.println("Valeur de statut non valide : " + status);
        // Par défaut, définissez le statut sur une valeur par défaut ou lancez une exception appropriée.
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public Statue getStatus() {
        return status.get();
    }

    public void setStatus(Statue status) {
        this.status.set(status);
    }

    public SimpleObjectProperty<Statue> statusProperty() {
        return status;
    }
    public TaskCalendrietest() {
        // Vous pouvez initialiser les propriétés par défaut ici si nécessaire
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
