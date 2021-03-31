package AplicatieBancara;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Cont {
    protected String nume;
    protected final LocalDate data_deschiderii;
    protected final String iban;
    protected final String bic;
    protected BigDecimal suma_disponibila;
    protected Card proprietar;

    // Creaza un cont cu o depunere initiala
    public Cont(String nume, LocalDate data_deschiderii, String iban, String bic, BigDecimal suma_disponibila, Card proprietar) {
        this.nume = nume;
        this.data_deschiderii = data_deschiderii;
        this.iban = iban;
        this.bic = bic;
        this.suma_disponibila = suma_disponibila;
        this.proprietar = proprietar;
    }

    // Creaza un cont gol
    public Cont(String nume, LocalDate data_deschiderii, String iban, String bic, Card proprietar) {
        this.nume = nume;
        this.data_deschiderii = data_deschiderii;
        this.iban = iban;
        this.bic = bic;
        this.suma_disponibila = new BigDecimal(0);
        this.proprietar = proprietar;
    }

    @Override
    public String toString() {
        return "\n   Contul " + nume + "\n" +
                "   Suma disponibila este: " + suma_disponibila +
                "\n    Iban='" + iban +
                "\n    BIC='" + bic;
    }
}
