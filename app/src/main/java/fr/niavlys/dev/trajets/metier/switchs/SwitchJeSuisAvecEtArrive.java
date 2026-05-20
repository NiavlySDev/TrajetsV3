package fr.niavlys.dev.trajets.metier.switchs;

import static android.view.View.VISIBLE;

import fr.niavlys.dev.trajets.client.MainActivity;
import fr.niavlys.dev.trajets.metier.BDD;
import fr.niavlys.dev.trajets.metier.pages.Page;
import fr.niavlys.dev.trajets.metier.pages.Pages;
import fr.niavlys.dev.trajets.metier.personnes.Personne;

public class SwitchJeSuisAvecEtArrive {
    public static void on() {
        SwitchsListener.off();
        int a = 1;
        int nbpage = 1;
        Pages pages = BDD.getPages();
        Page page = new Page(nbpage);
        pages.addPage(page);
        for(Personne personne  : Personne.values()){
            if(a > 12){
                a=0;
                nbpage++;
                page = new Page(nbpage);
                pages.addPage(page);
                MainActivity.getNext().setVisibility(VISIBLE);
            }
            else{
                page = pages.getPage(nbpage);
            }
            page.add(personne);
            a++;
        }
        BDD.getPages().afficherPageBoutons(1);
    }
}
