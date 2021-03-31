package AplicatieBancara;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContDebit extends Cont{
    private final int maxim_de_zile_gol;

    public ContDebit(String nume, LocalDate data_deschiderii, String iban, String bic, BigDecimal suma_disponibila, Card proprietar, int maxim_de_zile_gol) {
        super(nume, data_deschiderii, iban, bic, suma_disponibila, proprietar);
        this.maxim_de_zile_gol = maxim_de_zile_gol;
    }

    public ContDebit(String nume, LocalDate data_deschiderii, String iban, String bic, Card proprietar, int maxim_de_zile_gol) {
        super(nume, data_deschiderii, iban, bic, proprietar);
        this.maxim_de_zile_gol = maxim_de_zile_gol;
    }


    public void close(){

    }
}
