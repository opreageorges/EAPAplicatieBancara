package AplicatieBancara;

import java.math.BigDecimal;

public class ContCredit extends Cont {
    private final float dobanda = 10;
    private final int limit = 5000;

    public ContCredit(String nume,String iban, BigDecimal suma_disponibila, Card proprietar) {
        super(nume, iban, suma_disponibila, proprietar);
    }

    public ContCredit(String nume,String iban, Card proprietar) {
        super(nume, iban, proprietar);
    }
}
