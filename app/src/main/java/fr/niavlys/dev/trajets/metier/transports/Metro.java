package fr.niavlys.dev.trajets.metier.transports;

public enum Metro implements Transport {

    m4(4),
    m14(14);

    private String nom;

    Metro(int nb) {
        this.nom = "Métro "+nb+" ";
    }

    public String getNom() {
        return nom;
    }

    public static Metro getByNom(String nom){
        for(Metro metro : Metro.values()){
            if(metro.getNom().equals(nom)){
                return metro;
            }
        }
        return null;
    }
}
