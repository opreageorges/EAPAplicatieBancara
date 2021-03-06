package AplicatieBancara;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class User implements Comparable<User> {
    private final String prenume;
    private final String nume;
    private final String email;
    private final LocalDate data_nasterii;
    private final Random generator;
    private final int numere_importante_cnp; // primul numar si ultimele 6 numere, restul ar fi irelevante pentru ca sunt data nasterii
    private String parola;
    private ArrayList<Card> carduri;
    private boolean hasLoggedIn = false;

    public void setHasLoggedIn() {
        this.hasLoggedIn = true;
    }

    public boolean isHasLoggedIn() {
        return hasLoggedIn;
    }

    public String getEmail() {
        return email;
    }

    public String getParola() {
        return parola;
    }

    public String getPrenume() {
        return prenume;
    }

    public String getNume() {
        return nume;
    }

    public int getNumere_importante_cnp() {
        return numere_importante_cnp;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }

    public void setCarduri(ArrayList<Card> carduri) {
        this.carduri = carduri;
    }

    public User(String prenume, String nume, String email, LocalDate data_nasterii, int numere_importante_cnp, String parola) {
        Random generator1 = new Random();
        long seedul = generator1.nextLong() + data_nasterii.getDayOfYear();
        this.prenume = prenume;
        this.nume = nume;
        this.email = email;
        this.data_nasterii = data_nasterii;
        this.numere_importante_cnp = numere_importante_cnp;
        this.parola = parola;
        carduri = new ArrayList<>();
        generator1 = new Random(seedul);
        generator = generator1;
    }

    public String makeInsert(){

        return "`user` VALUES('" + this.prenume + "', '" + this.nume + "', '" + this.email + "', date(\"" + this.data_nasterii + "\"), " + this.numere_importante_cnp+ ", '" + this.parola+ "');" ;
    }

    public String makeDelete(){
        return "user where numerecnp = " + this.numere_importante_cnp + ";";
    }

    public void adaugaCard(Card card){
        carduri.add(card);
    }

    public void stergeCard(long number){
        carduri.removeIf(i -> i.getNumber() == number);
    }

    public void deschideCont(long number, String tip, String nume){
        for ( Card i : carduri){
            if (i.getNumber() == number){
                i.deschideCont(tip, nume.toUpperCase());
                break;
            }
        }
    }

    public void inchideCont(long number, String nume_cont){
        for ( Card i : carduri) if(i.getNumber() == number) i.inchideCont(nume_cont);

    }

    public ArrayList<Card> getCarduri(){
        return carduri;
    }

    public Cont getCont(String nume_cont_or_iban){
        for (Card i : carduri){
            ArrayList<Cont> temp_conturi = i.getConturi();
            for (Cont c : temp_conturi){
                if ( c.getNume().equals(nume_cont_or_iban) || c.getIban().equals(nume_cont_or_iban)) return c;
            }
        }
        return null;
    }

    public int getRandomIntFromUser(int bound) {
        return generator.nextInt(bound);
    }

    public long getRandomLongFromUser(){
        return generator.nextLong();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return numere_importante_cnp == user.numere_importante_cnp && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, numere_importante_cnp);
    }

    @Override
    public String toString() {
        return "User{" +
                "prenume='" + prenume + '\'' +
                ", nume='" + nume + '\'' +
                ", email='" + email + '\'' +
                ", data_nasterii=" + data_nasterii +
                ", numere_importante_cnp=" + numere_importante_cnp +
                ", parola='" + parola + '\'' +
                '}';
    }

    @Override
    public int compareTo(User o) {
        if (this.equals(o)) return 0;
        return this.email.compareTo(o.email);
    }
}
