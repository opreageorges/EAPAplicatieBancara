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

    public Cont(String nume, LocalDate data_deschiderii, String iban, String bic, BigDecimal suma_disponibila, Card proprietar) {
        this.nume = nume;
        this.data_deschiderii = data_deschiderii;
        this.iban = iban;
        this.bic = bic;
        this.suma_disponibila = suma_disponibila;
        this.proprietar = proprietar;
    }

    public Cont(String nume, LocalDate data_deschiderii, String iban, String bic, Card proprietar) {
        this.nume = nume;
        this.data_deschiderii = data_deschiderii;
        this.iban = iban;
        this.bic = bic;
        this.suma_disponibila = new BigDecimal(0);
        this.proprietar = proprietar;
    }
}
