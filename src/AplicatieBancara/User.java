package AplicatieBancara;

import java.time.LocalDate;

public class User {
    private final String prenume;
    private final String nume;
    private final String email;
    private final LocalDate data_nasterii;

    // primul numar si ultimele 6 numere, restul ar fi irelevante pentru ca sunt data nasterii
    private final int numere_importante_cnp;

    private final String parola;
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

    public User(String prenume, String nume, String email, LocalDate data_nasterii, int numere_importante_cnp, String parola) {
        this.prenume = prenume;
        this.nume = nume;
        this.email = email;
        this.data_nasterii = data_nasterii;
        this.numere_importante_cnp = numere_importante_cnp;
        this.parola = parola;
    }


    public String save(){
        return prenume + " " + nume + " " + email + " " + data_nasterii.getYear() + " " + data_nasterii.getMonthValue() + " " + data_nasterii.getDayOfMonth() + " " + numere_importante_cnp + " " + parola;
    }

    public void adauga_card(Card card){
        carduri = new Card[carduri.length + 1];
        carduri[carduri.length - 1] = card;
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
}
