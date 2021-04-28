package AplicatieBancara;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class ContDebit extends Cont{
    private final int maxim_de_zile_gol = 90;
    private LocalDate gol_din;

    public ContDebit(String nume, String iban, Card proprietar) {
        super(nume, iban, proprietar);
    }

    public ContDebit(String nume,String iban, BigDecimal suma_disponibila, Card proprietar, LocalDate gol_din) {
        super(nume, iban, suma_disponibila, proprietar);
        this.gol_din = gol_din;
    }

    public int golDe(){
        System.out.println("goldin: "+gol_din);
        if (gol_din != null) return (int)DAYS.between(gol_din, LocalDate.now());
        else return 0;
    }

    @Override
    public String getTip() {
        return "DEBIT";
    }

    @Override
    public void transferIntrePersoane(String iban_beneficiar, float suma_transferata) {

    }

    @Override
    public void plataFirma(String nume_firma, float suma_transferata) {

    }
}
