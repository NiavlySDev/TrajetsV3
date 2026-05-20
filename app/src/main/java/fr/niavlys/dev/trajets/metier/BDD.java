package fr.niavlys.dev.trajets.metier;

import android.view.View;
import fr.niavlys.dev.trajets.client.MainActivity;
import fr.niavlys.dev.trajets.metier.pages.Pages;
import fr.niavlys.dev.trajets.metier.personnes.Numero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BDD {

    private static HashMap<Integer, Bouton> boutons = new HashMap<>();
    private static List<Numero> numeros = new ArrayList<>();
    private static Pages pages = new Pages();


    public static void addBouton(int i, Bouton bouton){
        boutons.put(i, bouton);
    }
    public static Bouton getBouton(int i) {
        return boutons.get(i);
    }
    public static HashMap<Integer, Bouton> getBoutons(){
        return boutons;
    }

    public static void hideAllBoutons(){
        for(Bouton bouton : BDD.getBoutons().values()){
            bouton.get().setVisibility(View.INVISIBLE);
        }
        MainActivity.getPrevious().setVisibility(View.INVISIBLE);
        MainActivity.getNext().setVisibility(View.INVISIBLE);
    }

    public static void addNumero(Numero numero){
        numeros.add(numero);
    }

    public static void deleteNumero(Numero numero){
        numeros.remove(numero);
    }

    public static List<Numero> getNumeros() {
        return numeros;
    }

    public static Pages getPages() {
        return pages;
    }
}
