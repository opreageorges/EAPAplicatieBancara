package AplicatieBancara;

import java.time.LocalDate;
import java.util.Objects;

public class User implements Comparable<User> {
    private final String prenume;
    private final String nume;
    private final String email;
    private final LocalDate data_nasterii;

    // primul numar si ultimele 6 numere, restul ar fi irelevante pentru ca sunt data nasterii
    private final int numere_importante_cnp;

    private String parola;
    private Card[] carduri;

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

    public User(String prenume, String nume, String email, LocalDate data_nasterii, int numere_importante_cnp, String parola) {
        this.prenume = prenume;
        this.nume = nume;
        this.email = email;
        this.data_nasterii = data_nasterii;
        this.numere_importante_cnp = numere_importante_cnp;
        this.parola = parola;
        carduri = new Card[0];
    }


    public String save(){
        return prenume + " " + nume + " " + email + " " + data_nasterii.getYear() + " " + data_nasterii.getMonthValue() + " " + data_nasterii.getDayOfMonth() + " " + numere_importante_cnp + " " + parola;
    }

    public void adauga_card(Card card){
        carduri = new Card[carduri.length + 1];
        carduri[carduri.length - 1] = card;
    }

    public String info_carduri(){
        if (carduri != null) {
            StringBuilder s = new StringBuilder();
            for (Card i : carduri) if(i != null) s.append(i.toString());
            return s.toString();
        }
        return "Nu aveti inca un card";
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
