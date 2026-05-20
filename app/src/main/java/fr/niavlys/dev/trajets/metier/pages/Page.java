package fr.niavlys.dev.trajets.metier.pages;

import java.util.ArrayList;
import java.util.List;

import fr.niavlys.dev.trajets.metier.transports.Transport;

public class Page {

    private Integer numero;
    private Integer deja_affiche;
    private List<Transport> page;

    public Page(int nb){
        this.numero = nb;
        this.deja_affiche=-1;
        this.page = new ArrayList<>();
    }

    public void add(Transport elt){
        this.page.add(elt);
    }

    public List<Transport> getPage(){
        return this.page;
    }

    public void clear(){
        this.page.clear();
    }

    public Integer getNumero() {
        return numero;
    }
}
