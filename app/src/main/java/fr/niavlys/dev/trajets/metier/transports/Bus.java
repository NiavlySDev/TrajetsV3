package fr.niavlys.dev.trajets.metier.transports;


public enum Bus implements Transport {

    B13(13),
    B16(16),
    B17(17),
    B305(305);

    private String nom;

    private

    Bus(int nb) {
        this.nom = "Bus "+nb+" ";
    }

    public String getNom() {
        return nom;
    }

    public static Bus getByNom(String nom){
        for(Bus bus : Bus.values()){
            if(bus.getNom().equals(nom)){
                return bus;
            }
        }
        return null;
    }
}
