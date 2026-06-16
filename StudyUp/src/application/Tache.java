package application;

import java.time.LocalDate;

public class Tache {
	
    private String titre;
    private String priority;
    private String statue;
    private LocalDate dueDate;
    private int idTache;
    private String contenu;
    private int idProjet;
    
    public Tache(String titre, String priority, String statue, LocalDate dueDate) {
        this.titre = titre;
        this.priority = priority;
        this.statue = statue;
        this.dueDate = dueDate;
    }
    public Tache(int idTache, String titre, String priority, String statue, LocalDate dueDate) {
        this.idTache = idTache;
        this.titre = titre;
        this.priority = priority;
        this.statue = statue;
        this.dueDate = dueDate;
    }
    public Tache(int idTache, int idProjet, String titre, String priority, String statue, LocalDate dueDate, String contenu) {
        this.idTache = idTache;
        this.idProjet = idProjet;
        this.titre = titre;
        this.priority = priority;
        this.statue = statue;
        this.dueDate = dueDate;
        this.contenu = contenu;
    }

    public int getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    public int getIdTache() {
        return idTache;
    }
    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
}
