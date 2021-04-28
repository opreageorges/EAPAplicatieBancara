package AplicatieBancara;

public interface Transfer {

    void transferIntrePersoane(String iban_beneficiar, float suma_transferata);


    void plataFirma(String nume_firma, float suma_transferata);



}
