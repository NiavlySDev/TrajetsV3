package fr.niavlys.dev.trajets.metier.personnes;

import android.widget.CompoundButton;
import fr.niavlys.dev.trajets.metier.BDD;
import fr.niavlys.dev.trajets.metier.switchs.Switchs;

public class Personnes {

    public static void initSwitchsPersonnes(){
        Switchs.Maman.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Numero numero = Numero.Maman;
                if(b){BDD.addNumero(numero);}
                else{BDD.deleteNumero(numero);}
            }
        });
        Switchs.Papa.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Numero numero = Numero.Papa;
                if(b){BDD.addNumero(numero);}
                else{BDD.deleteNumero(numero);}
            }
        });
        Switchs.Patou.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Numero numero = Numero.Patou;
                if(b){BDD.addNumero(numero);}
                else{BDD.deleteNumero(numero);}
            }
        });
    }

}
