package fr.niavlys.dev.trajets.metier.pages;

import android.annotation.SuppressLint;
import android.view.View;

import fr.niavlys.dev.trajets.R;
import fr.niavlys.dev.trajets.client.MainActivity;
import fr.niavlys.dev.trajets.metier.BDD;
import fr.niavlys.dev.trajets.metier.Bouton;
import fr.niavlys.dev.trajets.metier.Gare;
import fr.niavlys.dev.trajets.metier.personnes.Personne;
import fr.niavlys.dev.trajets.metier.transports.*;

import java.util.ArrayList;
import java.util.List;

public class Pages {

    private List<Page> pages;
    private int currentpage;

    public Pages(){
        this.currentpage = 0;
        this.pages=new ArrayList<>();
    }

    public void addPage(Page page){
        this.pages.add(page);
    }

    public Page getPage(int i){
        for(Page page : pages){
            if(page.getNumero() == i){
                this.currentpage = i;
                return page;
            }
        }
        return null;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void clear(){
        for(Page page : this.pages){
            page.clear();
        }
        this.pages.clear();
    }

    @SuppressLint("ResourceAsColor")
    public void afficherPageBoutons(int numeropage){
        Page page = BDD.getPages().getPage(numeropage);
        System.out.println(page.getNumero());
        System.out.println(page.getPage().size());
        int nbbouton=0;
        for(Transport transport : page.getPage()){
            nbbouton++;
            Bouton bouton = BDD.getBouton(nbbouton);
            bouton.get().setBackgroundColor(R.color.bus);
            if(transport instanceof Bus){
                bouton.get().setBackgroundColor(R.color.bus);
                bouton.get().setText(transport.getNom());
            }
            else if (transport instanceof Metro) {
                bouton.get().setBackgroundColor(R.color.metro);
                bouton.get().setText(transport.getNom());
            }
            else if (transport instanceof Tram) {
                bouton.get().setBackgroundColor(R.color.tram);
                bouton.get().setText(transport.getNom());
            }
            else if (transport instanceof Train) {
                bouton.get().setBackgroundColor(R.color.train);
                bouton.get().setText(transport.getNom());
            }
            else if (transport instanceof RER) {
                bouton.get().setBackgroundColor(R.color.rer);
                bouton.get().setText(transport.getNom());
            }
            else if (transport instanceof Gare) {
                bouton.get().setBackgroundColor(R.color.gare);
                bouton.get().setText(transport.getNom());
            }
            else if (transport instanceof Personne) {
                if(transport == Personne.Maman){
                    bouton.get().setBackgroundColor(R.color.maman);
                }
                else{
                    bouton.get().setBackgroundColor(R.color.papa);
                }
                bouton.get().setText(transport.getNom());
            }
            bouton.get().setVisibility(View.VISIBLE);
        }
    }

    public int getCurrentpage() {
        return currentpage;
    }

    public int getNext(){
        addCurrentpage();
        return getCurrentpage();
    }

    public int getPrevious(){
        removeCurrentpage();
        return getCurrentpage();
    }

    public void addCurrentpage() {
        BDD.hideAllBoutons();
        MainActivity.getNext().setVisibility(View.VISIBLE);
        MainActivity.getPrevious().setVisibility(View.VISIBLE);
        this.currentpage++;
        if(this.currentpage>=this.pages.size()){
            MainActivity.getNext().setVisibility(View.INVISIBLE);
            this.currentpage=this.pages.size();
        }
    }

    public void removeCurrentpage() {
        BDD.hideAllBoutons();
        MainActivity.getNext().setVisibility(View.VISIBLE);
        MainActivity.getPrevious().setVisibility(View.VISIBLE);
        this.currentpage--;
        if(currentpage<2){
            MainActivity.getPrevious().setVisibility(View.INVISIBLE);
            this.currentpage=1;
        }
    }
}
