package application;

public class Task {
    private String title;
    private Enum<Statue> status; // Utilisez Enum<Statue> pour le type d'état
    private Priority priority;

    // Modifier le deuxième paramètre pour prendre un Enum<Statue> plutôt qu'un Statue
    public Task(String title, Enum<Statue> status, Priority priority) {
        this.title = title;
        this.status = status;
        this.priority = priority;
    }

    

	public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Enum<Statue> getStatus() {
        return status;
    }

    public void setStatus(Enum<Statue> status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", priority=" + priority +
                '}';
    }

	
	}
