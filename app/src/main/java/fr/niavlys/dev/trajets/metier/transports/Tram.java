package fr.niavlys.dev.trajets.metier.transports;

public enum Tram implements Transport {

    T4(4);

    private String nom;

    Tram(int nb) {
        this.nom = "Tramway "+nb+" ";
    }


    public String getNom() {
        return nom;
    }

    public static Tram getByNom(String nom){
        for(Tram tram : Tram.values()){
            if(tram.getNom().equals(nom)){
                return tram;
            }
        }
        return null;
    }
}
