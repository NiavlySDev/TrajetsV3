package fr.niavlys.dev.trajets.metier.personnes;

public enum Numero {

    Maman("0682377140"),
    Papa("0637038218"),
    Patou("0686525035");

    private String numero;

    Numero(String numero){
        this.numero = numero;
    }

    public String getNumero() {
        return numero;
    }
}
