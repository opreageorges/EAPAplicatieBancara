package AplicatieBancara;

import java.time.LocalDate;
import java.util.ArrayList;

public class Card {
    private User proprietar;
    private long number;
    private int cvv_cvc;
    private ArrayList<Cont> conturi;

    public Card(User proprietar, int number, int cvv_cvc) {
        this.proprietar = proprietar;
        this.number = number;
        this.cvv_cvc = cvv_cvc;
        conturi = new ArrayList<>();
        auto_conturi();
    }
    public void add_cont(ContDebit cont){
        conturi.add(cont);
    }

    public void add_cont(ContCredit cont){
        conturi.add(cont);
    }

    //Mod temporar de a testa conturile
    private void auto_conturi(){
        add_cont(new ContDebit("Nume1", LocalDate.now(), "RO0001", "BIC1", this, 10));
        add_cont(new ContDebit("Nume2", LocalDate.now(), "RO0002", "BIC2", this, 10));
        add_cont(new ContDebit("Nume3", LocalDate.now(), "RO0003", "BIC3", this, 10));
        add_cont(new ContCredit("Nume4", LocalDate.now(), "RO0004", "BIC4", this));
        add_cont(new ContCredit("Nume5", LocalDate.now(), "RO0005", "BIC5", this));
        add_cont(new ContCredit("Nume6", LocalDate.now(), "RO0006", "BIC6", this));

    }
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Cardul ").append(number).append(" conturi:\n");
        for (Cont i : conturi ) s.append(i);
        return s.toString();
    }
}
