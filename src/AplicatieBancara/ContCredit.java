package AplicatieBancara;

import java.math.BigDecimal;

public class ContCredit extends Cont {
    private final float dobanda = 10;
    private final BigDecimal limit;
    private BigDecimal datorie;

    public ContCredit(String nume,String iban, BigDecimal suma_disponibila, Card proprietar) {
        super(nume, iban, suma_disponibila, proprietar);
        limit = new BigDecimal(5000);
    }

    public ContCredit(String nume, String iban, Card proprietar) {
        super(nume, iban, proprietar);
        limit = new BigDecimal(5000);
    }

    public BigDecimal getDatorie() {
        if ( datorie != null )return datorie;
        else return BigDecimal.valueOf(0);
    }

    @Override
    public String getTip() {
        return "CREDIT";
    }

    @Override
    public void transferIntrePersoane(String iban_beneficiar, float suma_transferata) {

    }

    @Override
    public void plataFirma(String nume_firma, float suma_transferata) {

    }
}
