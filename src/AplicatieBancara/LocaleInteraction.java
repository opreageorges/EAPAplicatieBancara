package AplicatieBancara;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

import static java.lang.Math.pow;

public class LocaleInteraction {
    private static LocaleInteraction con = null;
    private static TreeSet<User> user_base;
    private static ArrayList<String> firme_partenere;
    private static DataBaseInteraction dbcon;

    ///////////////
    private static final String url = "jdbc:mysql://localhost:3306/aplicatiebancara";
    private static final String user = "root";
    private static final String password = "12345678mysql";
    // Seteaza ca true pentru a crea baza de date
    private static final boolean doStartUp = false;
    //////////////

    private LocaleInteraction() {
        user_base = new TreeSet<>();
        firme_partenere = new ArrayList<>();
    }

    public LocalDate get_data_from_cnp(long cnp) throws Exception {
        LocalDate out;
        if (cnp < pow(10, 12) || cnp > 99*pow(10,11)) throw new Exception("Numarul de cifre al cnpului este gresit");
        int temp = (int)(cnp / (long)pow(10,6));
        int zi,luna,an;
        an = temp % 100;
        temp = temp/100;
        if(an < 50) an += 2000;
        else an += 1900;

        luna = temp % 100;
        temp = temp/100;

        zi = temp % 100;

        out = LocalDate.of(an,luna,zi);
        return out;
    }

    public void logthis(String input, String variabila){
        try {
            FileWriter logger = new FileWriter("DataBase/Log", true);
            switch (input) {
                case "MAINMENU":
                    logger.append("Utilizatorul a deschis meniul principal ").append(String.valueOf(LocalDateTime.now())).append("\n");
                    break;
                case "TRANZACTIE":
                    logger.append("Utilizatorul ").append(variabila).append(" a deschis meniul de tranzctii ").append(String.valueOf(LocalDateTime.now())).append("\n");
                    break;
                case "TRANOM":
                    logger.append("Utilizatorul ").append(variabila).append(" a realizat o tranzactie cu un alt client ").append(String.valueOf(LocalDateTime.now())).append("\n");
                    break;
                case "TRANFIRMA":
                    logger.append("Utilizatorul ").append(variabila).append(" a realizat o tranzactie cu o firma partenera ").append(String.valueOf(LocalDateTime.now())).append("\n");
                    break;
                case "CARDINTER":
                    logger.append("Utilizatorul ").append(variabila).append(" a deschis meniu de interactiune cu un card ").append(String.valueOf(LocalDateTime.now())).append("\n");
                    break;
                case "DESCCONT":
                    logger.append("Utilizatorul ").append(variabila).append(" a deschis un cont bancar ").append(String.valueOf(LocalDateTime.now())).append("\n");
                    break;
                case "ICHCONT":
                    logger.append("Utilizatorul ").append(variabila).append(" a inchis un cont bancar ").append(String.valueOf(LocalDateTime.now())).append("\n");
                    break;
                case "ALMCONT":
                    logger.append("Utilizatorul ").append(variabila).append(" a alimentat un cont bancar ").append(String.valueOf(LocalDateTime.now())).append("\n");
                    break;
                case "ICHCARD":
                    logger.append("Utilizatorul ").append(variabila).append(" a inchis un card ").append(String.valueOf(LocalDateTime.now())).append("\n");
                    break;
                case "LOGIN":
                    logger.append("Utilizatorul a intrat in meniul de logare ").append(String.valueOf(LocalDateTime.now())).append("\n");
                    break;
                case "REGISTER":
                    logger.append("Utilizatorul a intrat in meniul de inregistrare ").append(String.valueOf(LocalDateTime.now())).append("\n");
                    break;
                case "LOGGED":
                    logger.append("Utilizatorul ").append(variabila).append(" s-a logat ").append(String.valueOf(LocalDateTime.now())).append("\n");
                    break;
                case "CHDPASS":
                    logger.append("Utilizatorul ").append(variabila).append(" si-a schimbat parola ").append(String.valueOf(LocalDateTime.now())).append("\n");
                    break;
                case "PASS":
                    logger.append("Utilizatorul ").append(variabila).append(" a intrat in meniu de schimbare a parolei ").append(String.valueOf(LocalDateTime.now())).append("\n");
                    break;
                default:
                    logger.append("Utilizatorul a facut ceva nedefinit ").append(String.valueOf(LocalDateTime.now())).append("\n");
                    break;
            }
            logger.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void verifica_parola(String pass1, String pass2) throws Exception {
        if(!pass1.equals(pass2)) throw new Exception("Parolele nu coincid!");
        else if(pass1.length() < 8 ) throw new Exception("Parola nu poate fi mai mica de 8 caractere!");
    }

    public void verifica_parola_veche( String old_pass, User logged_user) throws Exception {
        if(!logged_user.getParola().equals(old_pass)) throw new Exception("Parola nu este corecta!");
    }

    public boolean verifica_email(String mail) {
        for (User i : user_base) {
            if (i.getEmail().equals(mail))
                return true;
        }
        return false;
    }

    public Cont get_user_bnk_cont(String mail, String iban){
        for(User i : user_base){
            if(i.getEmail().equals(mail))
                return i.getCont(iban);
        }
        return null;
    }

    //Functie ce incarca utilizatorii din fisier

    private static void load() throws FileNotFoundException {
        File firme_file = new File("DataBase/FirmePartenere.txt");
        Scanner downParteneri = new Scanner(firme_file);
        downParteneri.useDelimiter(",");
        while (downParteneri.hasNext()) {
            firme_partenere.add(downParteneri.next());
        }
        downParteneri.close();

        user_base = dbcon.getUsersFromDB();
        for(User u : user_base){
            u.setCarduri(dbcon.getUserCardsFromDB(u));
            for (Card c : u.getCarduri()){
                c.setConturi(dbcon.getCardContsFromDB(c));
            }
        }
    }

    public ArrayList<String> getFirme_partenere() {
        return firme_partenere;
    }

    //Functie ce salveaza un utilizator nou in fisier
    public void save_user(User user){
        user_base.add(user);
        dbcon.insertElement(user.makeInsert());
    }

    public int prelucrare_cnp(long cnp) {
        return (int)(((cnp / (long) pow(10, 12)) * (long) pow(10, 6)) + (cnp % (long) (pow(10, 6))));
    }

    public void changeUserPass(User u, String newpass){
        u.setParola(newpass);
    }

    //Functie pentru a sterege un utilizator

    public void delete_user(User user, int nr_imp_cnp) throws Exception {
        if(user.getNumere_importante_cnp() == nr_imp_cnp){
            user_base.remove(user);
            dbcon.deleteElement(user.makeDelete());
        }
        else
            throw new Exception("CNP incorect!");
    }

    public void renew_users() {
        for (User u : user_base){
            if (u.isHasLoggedIn()){
                System.out.println(u);
                for (Card c : u.getCarduri()){
                    for (Cont cont : c.getConturi()){
                        dbcon.deleteElement(cont.makeDelete());
                    }
                    dbcon.deleteElement(c.makeDelete());
                }
                dbcon.deleteElement(u.makeDelete());
                dbcon.refreshCon();
                dbcon.insertElement(u.makeInsert());
                for (Card c : u.getCarduri()){
                    dbcon.insertElement(c.makeInsert());
                    for (Cont cont : c.getConturi()){
                        dbcon.insertElement(cont.makeInsert());
                    }
                }
            }
        }
    }

    public User log_in_account(String username, String pass) {
        User matching_user;
        for (User i : user_base) {
            if (i != null && i.getEmail().equals(username) && i.getParola().equals(pass)) {
                matching_user = i;
                System.out.println("Bine ati revenit " + matching_user.getPrenume() + " " + matching_user.getNume());
                return matching_user;
            }
        }

        return null;
    }

    public static LocaleInteraction connect() throws FileNotFoundException {
        if (con == null) {

            // Creez obiectul care se ocupa de interactiunea cu fisiere locale
            con = new LocaleInteraction();

            // Creez obiectul care se ocupa de interactiunea cu baza de date
            dbcon = DataBaseInteraction.getInstance(url, user, password);

            if (doStartUp) dbcon.startup();

            // Pronesc functia de incarcare a datelor
            load();
        }

        return con;
    }
}
