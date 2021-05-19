package AplicatieBancara;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class ContDebit extends Cont{
    private final int maxim_de_zile_gol = 90;
    private final LocalDate gol_din;

    ContDebit(String nume, String iban, Card proprietar) {
        super(nume, iban, proprietar);
        this.gol_din = LocalDate.now();
    }

    ContDebit(String nume,String iban, BigDecimal suma_disponibila, Card proprietar, LocalDate gol_din) {
        super(nume, iban, suma_disponibila, proprietar);
        this.gol_din = gol_din;
    }

    public int golDe(){
        if (gol_din != null) return (int)DAYS.between(gol_din, LocalDate.now());
        else return 0;
    }

    public int getMaxim_de_zile_gol() {
        return maxim_de_zile_gol;
    }

    @Override
    public String makeInsert() {
        if (gol_din != null) return "`cont` VALUES('" + this.nume + "', '" + this.iban + "', " + this.suma_disponibila.floatValue() + ", " + this.proprietar.getNumber() + ", '" + getTip() + "', null, date(\"" + this.gol_din + "\") );";
        else return "`cont` VALUES('" + this.nume + "', '" + this.iban + "', " + this.suma_disponibila.floatValue() + ", " + this.proprietar.getNumber() + ", '" + getTip() + "', null, null );";
    }

    @Override
    public String getTip() {
        return "DEBIT";
    }

    @Override
    public void transferIntrePersoane(Cont contul_beneficiarului, float suma_transferata) throws Exception {
        if(suma_transferata < suma_disponibila.floatValue()){
            contul_beneficiarului.addMoney(suma_transferata);
            suma_disponibila = suma_disponibila.subtract(new BigDecimal(suma_transferata));
        }
        else throw new Exception("Suma este mai mare decat suma din cont!");
    }

    @Override
    public void plataFirma(String nume_firma, float suma_transferata) throws Exception{
        BigDecimal suma_transferata_big = new BigDecimal(suma_transferata);
        if(suma_disponibila.compareTo(suma_transferata_big) >= 0){
            suma_disponibila = suma_disponibila.subtract(suma_transferata_big);
        }
        else throw new Exception("Suma este mai mare decat suma din cont!");
    }

    @Override
    public String toString() {
        return "Contul " + nume + "    Suma disponibila este: " + suma_disponibila + "  Iban= " + iban + "  Gol de: " + golDe();
    }
}
