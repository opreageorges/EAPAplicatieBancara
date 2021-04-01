package AplicatieBancara;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContDebit extends Cont{
    private final int maxim_de_zile_gol = 90;

    public ContDebit(String nume, LocalDate data_deschiderii, String iban, String bic, BigDecimal suma_disponibila, Card proprietar) {
        super(nume, data_deschiderii, iban, suma_disponibila, proprietar);
    }

    public ContDebit(String nume, LocalDate data_deschiderii, String iban, String bic, Card proprietar) {
        super(nume, data_deschiderii, iban, proprietar);
    }

}
