package fr.niavlys.dev.trajets.metier.switchs;

import static android.view.View.VISIBLE;

import fr.niavlys.dev.trajets.client.MainActivity;
import fr.niavlys.dev.trajets.metier.BDD;
import fr.niavlys.dev.trajets.metier.Gare;
import fr.niavlys.dev.trajets.metier.pages.Page;
import fr.niavlys.dev.trajets.metier.pages.Pages;

public class SwitchJeSuisAEtDirection {
    public static void on(){
        int a = 1;
        int nbpage = 1;
        Pages pages = BDD.getPages();
        Page page = new Page(nbpage);
        pages.addPage(page);
        for(Gare gare  : Gare.values()){
            if(a > 12){
                a=0;
                nbpage++;
                page = new Page(nbpage);
                pages.addPage(page);
                MainActivity.getNext().setVisibility(VISIBLE);
                System.out.println(pages.getPages().size());
            }
            else{
                page = pages.getPage(nbpage);
            }
            page.add(gare);
            a++;
        }
        BDD.getPages().afficherPageBoutons(1);
    }
}

