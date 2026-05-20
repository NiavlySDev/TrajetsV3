package fr.niavlys.dev.trajets.metier.personnes;

import fr.niavlys.dev.trajets.metier.transports.Transport;

public enum Personne implements Transport {

    Maman("Maman"),
    Papa("Papa");

    private String nom;

    Personne(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    public static Personne getByNom(String nom){
        for(Personne personnes : Personne.values()){
            if(personnes.getNom().equals(nom)){
                return personnes;
            }
        }
        return null;
    }
}