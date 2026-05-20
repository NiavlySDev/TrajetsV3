package fr.niavlys.dev.trajets.metier.switchs;

import android.annotation.SuppressLint;
import android.widget.CompoundButton;
import android.widget.Switch;

import fr.niavlys.dev.trajets.R;
import fr.niavlys.dev.trajets.client.MainActivity;
import fr.niavlys.dev.trajets.metier.BDD;
import fr.niavlys.dev.trajets.metier.Bouton;

public class SwitchsListener {
    public static void initJeSuisAEtDirection(){
        MainActivity.resetPrevisualisation();
        Switch JeSuisA = Switchs.Je_Suis_A.getSwitch();
        JeSuisA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    MainActivity.addPrevisualisation("Je suis a ");
                    SwitchJeSuisAEtDirection.on();}
                else {off();}
            }
        });
        Switch Direction = Switchs.Direction.getSwitch();
        Direction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    MainActivity.addPrevisualisation("direction: ");
                    SwitchJeSuisAEtDirection.on();
                }
                else{off();}
            }
        });
    }

    public static void initJeSuisDans(){
        MainActivity.resetPrevisualisation();
        Switch JeSuisDans = Switchs.Je_Suis_Dans.getSwitch();
        JeSuisDans.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {SwitchJeSuisDans.on();}
                else {off();}
            }
        });
    }

    public static void initJeSuisAvecEtArrive(){
        MainActivity.resetPrevisualisation();
        Switch JeSuisAvec = Switchs.Je_Suis_Avec.getSwitch();
        JeSuisAvec.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    MainActivity.addPrevisualisation("Je suis avec ");
                    SwitchJeSuisAvecEtArrive.on();
                }
                else {off();}
            }
        });
        Switch JeSuisArrive = Switchs.Je_Suis_Arrive.getSwitch();
        JeSuisArrive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    MainActivity.addPrevisualisation("Je suis arrivé chez ");
                    SwitchJeSuisAvecEtArrive.on();
                }
                else {off();}
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    public static void off(){
        int a = 1;
        for(Bouton bouton : BDD.getBoutons().values()){
            bouton.get().setText("");
            bouton.get().setBackgroundColor(R.color.purple_200);
        }
        BDD.getPages().clear();
        BDD.hideAllBoutons();
    }
}
