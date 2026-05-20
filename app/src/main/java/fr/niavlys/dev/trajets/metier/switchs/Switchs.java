package fr.niavlys.dev.trajets.metier.switchs;

import android.widget.Switch;

public enum Switchs {

    Je_Suis_A,
    Je_Suis_Dans,
    Je_Suis_Avec,
    Je_Suis_Arrive,
    Direction,
    Maman,
    Papa,
    Patou;

    private Switch le_switch;

    Switchs(){

    }

    public void setSwitch(Switch le_switch){
        this.le_switch = le_switch;
    }

    public Switch getSwitch() {
        return le_switch;
    }
}
