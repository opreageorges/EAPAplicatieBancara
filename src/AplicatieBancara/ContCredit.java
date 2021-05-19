package AplicatieBancara;

import java.math.BigDecimal;

public class ContCredit extends Cont {
    private final float dobanda = 10;
    private final BigDecimal limit;
    private BigDecimal datorie;

    public ContCredit(String nume,String iban, BigDecimal suma_disponibila, Card proprietar) {
        super(nume, iban, suma_disponibila, proprietar);
        limit = new BigDecimal(5000);
        datorie = BigDecimal.valueOf(0);
    }

    public ContCredit(String nume, String iban, Card proprietar) {
        super(nume, iban, proprietar);
        limit = new BigDecimal(5000);
        datorie = BigDecimal.valueOf(0);
    }

    public ContCredit(String nume,String iban, BigDecimal suma_disponibila, float datorie ,Card proprietar) {
        super(nume, iban, suma_disponibila, proprietar);
        limit = new BigDecimal(5000);
        this.datorie = BigDecimal.valueOf(datorie);
    }

    public BigDecimal getDatorie() {
        return datorie;
    }

    @Override
    public String makeInsert() {
        return "`cont` VALUES('" + this.nume + "', '" + this.iban + "', " + this.suma_disponibila.floatValue() + ", " + this.proprietar.getNumber() + ", '" + getTip() + "', " + this.datorie.floatValue() + ", null );";
    }

    @Override
    public void addMoney(float suma){
        BigDecimal suma_big = new BigDecimal(suma);
        BigDecimal zero = new BigDecimal(0);
        if(datorie.compareTo(zero) <= 0)
            suma_disponibila = suma_disponibila.add(suma_big);
        else if (datorie.compareTo(suma_big) >= 0)
            datorie = datorie.subtract(suma_big);
        else{
            suma_disponibila = suma_disponibila.add(suma_big.subtract(datorie));
            datorie = BigDecimal.valueOf(0);
        }
    }

    @Override
    public String getTip() {
        return "CREDIT";
    }

    @Override
    public void transferIntrePersoane(Cont contul_beneficiarului, float suma_transferata) throws Exception {
        BigDecimal suma_transferata_big = new BigDecimal(suma_transferata);

        if(suma_disponibila.compareTo(suma_transferata_big) < 0 && datorie.add(suma_transferata_big.add(suma_transferata_big.multiply(BigDecimal.valueOf(dobanda/100)))).compareTo(limit) > 0 )
            throw  new Exception("Depasiti limita de datorii");
        else if(suma_disponibila.compareTo(suma_transferata_big) < 0){
            suma_transferata_big = suma_transferata_big.subtract(suma_disponibila);
            suma_disponibila = BigDecimal.valueOf(0);
            datorie = datorie.add(suma_transferata_big.add(suma_transferata_big.multiply(BigDecimal.valueOf(dobanda/100))));
            contul_beneficiarului.addMoney(suma_transferata);
        }
        else {
            suma_disponibila = suma_disponibila.subtract(suma_transferata_big);
            contul_beneficiarului.addMoney(suma_transferata);
        }
    }

    @Override
    public void plataFirma(String nume_firma, float suma_transferata) throws Exception {
        BigDecimal suma_transferata_big = new BigDecimal(suma_transferata);
        if(suma_disponibila.compareTo(suma_transferata_big) < 0 && datorie.add(suma_transferata_big.add(suma_transferata_big.multiply(BigDecimal.valueOf(dobanda/100)))).compareTo(limit) > 0 )
            throw  new Exception("Depasiti limita de datorii");
        else if(suma_disponibila.compareTo(suma_transferata_big) < 0){
            suma_transferata_big = suma_transferata_big.subtract(suma_disponibila);
            suma_disponibila = BigDecimal.valueOf(0);
            datorie = datorie.add(suma_transferata_big.add(suma_transferata_big.multiply(BigDecimal.valueOf(dobanda/100))));
        }
        else {
            suma_disponibila = suma_disponibila.subtract(suma_transferata_big);
        }
    }

    @Override
    public String toString() {
        return "Contul " + nume + "    Suma disponibila este: " + suma_disponibila + "  Iban= " + iban + "  Datorie: " + datorie;
    }
}
