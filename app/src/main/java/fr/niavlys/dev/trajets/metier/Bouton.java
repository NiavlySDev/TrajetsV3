package fr.niavlys.dev.trajets.metier;

import android.view.View;
import android.widget.Button;
import fr.niavlys.dev.trajets.client.MainActivity;
import fr.niavlys.dev.trajets.metier.personnes.Personne;
import fr.niavlys.dev.trajets.metier.transports.*;

public class Bouton{

    private Integer id;
    private Button bouton;

    public Bouton(int id, Button bouton){
        this.id = id;
        this.bouton=bouton;
        this.bouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Tram.getByNom(bouton.getText().toString()) != null){
                    Tram tram = Tram.getByNom(bouton.getText().toString());
                    MainActivity.addPrevisualisation(tram.getNom());
                }
                if(Train.getByNom(bouton.getText().toString()) != null){
                    Train train = Train.getByNom(bouton.getText().toString());
                    MainActivity.addPrevisualisation(train.getNom());
                }
                if(Metro.getByNom(bouton.getText().toString()) != null){
                    Metro metro = Metro.getByNom(bouton.getText().toString());
                    MainActivity.addPrevisualisation(metro.getNom());
                }
                if(RER.getByNom(bouton.getText().toString()) != null){
                    RER rer = RER.getByNom(bouton.getText().toString());
                    MainActivity.addPrevisualisation(rer.getNom());
                }
                if(Bus.getByNom(bouton.getText().toString()) != null){
                    Bus bus = Bus.getByNom(bouton.getText().toString());
                    MainActivity.addPrevisualisation(bus.getNom());
                }
                if(Gare.getByAffichage(bouton.getText().toString()) != null){
                    Gare gare = Gare.getByAffichage(bouton.getText().toString());
                    MainActivity.addPrevisualisation(gare.getPhrase());
                }
                if(Personne.getByNom(bouton.getText().toString()) != null){
                    Personne personne = Personne.getByNom(bouton.getText().toString());
                    MainActivity.addPrevisualisation(personne.getNom());
                }
                else{
                    return;
                }
            }
        });
    }

    public Button get(){
        return this.bouton;
    }
}
