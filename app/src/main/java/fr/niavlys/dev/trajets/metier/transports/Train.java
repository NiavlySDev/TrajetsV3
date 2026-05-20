package fr.niavlys.dev.trajets.metier.transports;

import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;

public enum Train implements Transport {

    Transilien("Transilien "),
    Nomad("Nomad ");

    private String nom;

    Train(String nom) {
        this.nom = nom;
    }
    public String getNom() {
        return nom;
    }

    public static Train getByNom(String nom){
        for(Train train : Train.values()){
            if(train.getNom().equals(nom)){
                return train;
            }
        }
        return null;
    }
}
