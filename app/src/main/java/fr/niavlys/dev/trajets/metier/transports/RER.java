package fr.niavlys.dev.trajets.metier.transports;

public enum RER implements Transport {

    RerC('C'),
    RerD('D');

    private String nom;

    RER(char nom) {
        this.nom = "RER "+nom+" ";
    }
    public String getNom() {
        return nom;
    }

    public static RER getByNom(String nom){
        for(RER rer : RER.values()){
            if(rer.getNom().equals(nom)){
                return rer;
            }
        }
        return null;
    }
}