package AplicatieBancara;

import java.math.BigDecimal;

public class ContDebit extends Cont{
    private final int maxim_de_zile_gol = 90;

    public ContDebit(String nume,String iban, BigDecimal suma_disponibila, Card proprietar) {
        super(nume, iban, suma_disponibila, proprietar);
    }

    public ContDebit(String nume, String iban, Card proprietar) {
        super(nume, iban, proprietar);
    }

}
