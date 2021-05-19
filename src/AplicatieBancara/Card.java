package AplicatieBancara;

import java.util.ArrayList;
import java.util.Objects;

public class Card {
    private final User proprietar;
    private final long number;
    private final int cvv_cvc;
    private ArrayList<Cont> conturi;

    public long getNumber() {
        return number;
    }

    public ArrayList<Cont> getConturi() {
        return conturi;
    }

    public void setConturi(ArrayList<Cont> conturi) {
        this.conturi = conturi;
    }

    public Card(User proprietar) {
        this.proprietar = proprietar;

        int tempcvc = 0;
        while (tempcvc<100) tempcvc = proprietar.getRandomIntFromUser(999);
        cvv_cvc = tempcvc;

        number = generateNumber();

        conturi = new ArrayList<>();

    }

    public Card(User proprietar, long number, int cvv_cvc) {
        this.proprietar = proprietar;
        this.number = number;
        this.cvv_cvc = cvv_cvc;
    }

    private long generateNumber() {
        long temp_number = 5L;
        while (temp_number < 50000000) {
            temp_number = temp_number*10 + proprietar.getRandomIntFromUser(9);
        }
        return temp_number;
    }

    private String generateIban(){

        int temp1 = 0;
        while(temp1 <100) temp1 = proprietar.getRandomIntFromUser(999);
        String iban = "RO" + temp1;

        long temp2 = 0L;
        while (temp2 < 10000000L) temp2 = proprietar.getRandomLongFromUser();
        iban = iban + "BNK" + temp2;
        return iban;
    }

    public void deschideCont(String tip, String nume){
        String iban = generateIban();
        switch (tip.toUpperCase()){
            case "CREDIT":
                conturi.add(new ContCredit(nume, iban, this));
                break;
            case "DEBIT":
                conturi.add(new ContDebit(nume, iban, this));
                break;
        }
    }

    public void inchideCont(String nume){
        conturi.removeIf(i -> i.nume.equals(nume));
    }

    public String makeInsert(){
        return "`card` VALUES(" + this.number + ", " + this.cvv_cvc + ", " + this.proprietar.getNumere_importante_cnp() + ");";
    }

    public String makeDelete(){
        return "card where numar = " + this.number + ";";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return number == card.number && cvv_cvc == card.cvv_cvc;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, cvv_cvc);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Cardul ").append(number).append(" conturi:\n");
        for (Cont i : conturi ) s.append(i);
        return s.toString();
    }
}
