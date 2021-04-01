package AplicatieBancara;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContCredit extends Cont {
    private float dobanda;
    private final int limit = 5000;

    public ContCredit(String nume, LocalDate data_deschiderii, String iban, String bic, BigDecimal suma_disponibila, Card proprietar) {
        super(nume, data_deschiderii, iban, suma_disponibila, proprietar);
    }

    public ContCredit(String nume, LocalDate data_deschiderii, String iban, String bic, Card proprietar) {
        super(nume, data_deschiderii, iban, proprietar);
    }
}
