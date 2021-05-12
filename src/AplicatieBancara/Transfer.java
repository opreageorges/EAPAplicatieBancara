package AplicatieBancara;

public interface Transfer {

    void transferIntrePersoane(Cont contul_beneficiarului, float suma_transferata) throws Exception;

    void plataFirma(String nume_firma, float suma_transferata) throws Exception;



}
