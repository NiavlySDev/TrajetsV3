package fr.niavlys.dev.trajets.metier.switchs;

import static android.view.View.VISIBLE;

import fr.niavlys.dev.trajets.client.MainActivity;
import fr.niavlys.dev.trajets.metier.BDD;
import fr.niavlys.dev.trajets.metier.pages.Page;
import fr.niavlys.dev.trajets.metier.pages.Pages;
import fr.niavlys.dev.trajets.metier.transports.*;

public class SwitchJeSuisDans {
    public static void on() {
        MainActivity.addPrevisualisation("Je suis dans le ");
        if(Switchs.Je_Suis_Arrive.getSwitch().isChecked()){
            Switchs.Je_Suis_Arrive.getSwitch().setChecked(false);
        }
        if(Switchs.Je_Suis_Avec.getSwitch().isChecked()){
            Switchs.Je_Suis_Avec.getSwitch().setChecked(false);
        }
        if(Switchs.Je_Suis_A.getSwitch().isChecked()){
            Switchs.Je_Suis_A.getSwitch().setChecked(false);
        }
        if(Switchs.Direction.getSwitch().isChecked()){
            Switchs.Direction.getSwitch().setChecked(false);
        }

        int a = 0;
        int nbpage = 1;
        Pages pages = BDD.getPages();
        Page page = new Page(nbpage);
        pages.addPage(page);

        for(Tram tram : Tram.values()){
            if(a > 12){
                a=0;
                nbpage++;
                page = new Page(nbpage);
                pages.addPage(page);
                MainActivity.getNext().setVisibility(VISIBLE);
                MainActivity.getPrevious().setVisibility(VISIBLE);
            }
            else{
                page = pages.getPage(nbpage);
            }
            page.add(tram);
            a++;
        }
        for(Train train : Train.values()){
            if(a > 12){
                a=0;
                nbpage++;
                page = new Page(nbpage);
                pages.addPage(page);
                MainActivity.getNext().setVisibility(VISIBLE);
                MainActivity.getPrevious().setVisibility(VISIBLE);
            }
            else{
                page = pages.getPage(nbpage);
            }
            page.add(train);
            a++;
        }
        for(Metro metro : Metro.values()){
            if(a > 12){
                a=0;
                nbpage++;
                page = new Page(nbpage);
                pages.addPage(page);
                MainActivity.getNext().setVisibility(VISIBLE);
                MainActivity.getPrevious().setVisibility(VISIBLE);
            }
            else{
                page = pages.getPage(nbpage);
            }
            page.add(metro);
            a++;
        }
        for(RER rer : RER.values()){
            if(a > 12){
                a=0;
                nbpage++;
                page = new Page(nbpage);
                pages.addPage(page);
                MainActivity.getNext().setVisibility(VISIBLE);
                MainActivity.getPrevious().setVisibility(VISIBLE);
            }
            else{
                page = pages.getPage(nbpage);
            }
            page.add(rer);
            a++;
        }
        for(Bus bus : Bus.values()){
            if(a > 12){
                a=0;
                nbpage++;
                page = new Page(nbpage);
                pages.addPage(page);
                MainActivity.getNext().setVisibility(VISIBLE);
                MainActivity.getPrevious().setVisibility(VISIBLE);
            }
            else{
                page = pages.getPage(nbpage);
            }
            page.add(bus);
            a++;
        }
        BDD.getPages().afficherPageBoutons(1);
    }
}
