package AplicatieBancara;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Cont {
    protected String nume;
    protected final LocalDate data_deschiderii;
    protected final String iban;
    protected final String bic = "ROBCA420";
    protected BigDecimal suma_disponibila;
    protected Card proprietar;

    public String getNume() {
        return nume;
    }

    public String getIban() {
        return iban;
    }

    // Creaza un cont cu o depunere initiala
    public Cont(String nume, String iban,  BigDecimal suma_disponibila, Card proprietar) {
        this.nume = nume;
        this.iban = iban;
        this.data_deschiderii = LocalDate.now();
        this.suma_disponibila = suma_disponibila;
        this.proprietar = proprietar;
    }

    // Creaza un cont gol
    public Cont(String nume, String iban, Card proprietar) {
        this.nume = nume;
        this.iban = iban;
        this.data_deschiderii = LocalDate.now();
        this.suma_disponibila = new BigDecimal(0);
        this.proprietar = proprietar;
    }

    public void addMoney(int suma){
        BigDecimal suma_big = new BigDecimal(suma);
        suma_disponibila = suma_disponibila.add(suma_big);
    }

    @Override
    public String toString() {
        return "\n   Contul " + nume +
                "\n    Suma disponibila este: " + suma_disponibila +
                "\n    Iban='" + iban +
                "\n    BIC='" + bic;
    }
}
