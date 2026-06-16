package application;

import java.util.List;

public class Projet {
    private int idProjet;
    private String nomProjet;
    private List<Tache> taches;

    
    public Projet(int idProjet, String nomProjet, List<Tache> taches) {
        this.idProjet = idProjet;
        this.nomProjet = nomProjet;
        this.taches = taches;
    }

    public int getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }

    public String getNomProjet() {
        return nomProjet;
    }

    public void setNomProjet(String nomProjet) {
        this.nomProjet = nomProjet;
    }

    public List<Tache> getTaches() {
        return taches;
    }

    public void setTaches(List<Tache> taches) {
        this.taches = taches;
    }
}
