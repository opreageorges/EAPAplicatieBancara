package AplicatieBancara;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContCredit extends Cont {
    private float dobanda;
    private final int limit = 5000;

    public ContCredit(String nume,String iban, BigDecimal suma_disponibila, Card proprietar) {
        super(nume, iban, suma_disponibila, proprietar);
    }

    public ContCredit(String nume,String iban, Card proprietar) {
        super(nume, iban, proprietar);
    }
}
