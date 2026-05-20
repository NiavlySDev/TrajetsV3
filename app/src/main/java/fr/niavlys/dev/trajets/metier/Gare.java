package fr.niavlys.dev.trajets.metier;

import fr.niavlys.dev.trajets.metier.transports.Transport;

public enum Gare implements Transport {

    Evreux("Evreux", "la Gare D'Evreux"),
    SaintLazare("Saint Lazare", "Saint Lazare"),
    BFM("BFM", "Bibliotheque François Miterrand"),
    Juvisy("Juvisy", "Juvisy"),
    Danton("Danton", "Danton"),
    SaintMichel("Saint Michel", "Saint Michel Notre Dame"),
    Montparnasse("Montparnasse", "Montparnasse"),
    Houdan("Houdan", "Houdan"),
    VersaillesChantier("V-Chantiers", "Versailles Chantiers"),
    VersaillesChateau("V-Chateau", "Versailles Chateau Rive Gauche"),
    ParisAusterlitz("Austerlitz", "Paris Austerlitz"),
    GareDeLuon("GDL", "Gare de Lyon"),
    Bercy("Bercy", "Bercy")
    ;

    private String phrase;
    private String affichage;

    Gare(String affichage, String phrase) {
        this.phrase = phrase;
        this.affichage = affichage;
    }

    public String getNom() {
        return affichage;
    }

    public String getPhrase() {
        return phrase;
    }

    public static Gare getByAffichage(String affichage){
        for(Gare gare : Gare.values()){
            if(gare.getNom().equals(affichage)){
                return gare;
            }
        }
        return null;
    }
}
