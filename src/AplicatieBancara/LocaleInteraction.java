package AplicatieBancara;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

import static java.lang.Integer.parseInt;
import static java.lang.Math.pow;

public class LocaleInteraction {
    private static LocaleInteraction con = null;
    private static File data_base;
    private static TreeSet<User> user_base;
    private static ArrayList<String> firme_partenere;

    private LocaleInteraction() {
        user_base = new TreeSet<>();
        firme_partenere = new ArrayList<>();


    }

    public LocalDate get_data_from_cnp(long cnp) throws Exception {
        LocalDate out = LocalDate.now();
        if (cnp < pow(10, 12) || cnp > 99*pow(10,11)) throw new Exception("Numarul de cifre al cnpului este gresit");
        int temp = (int)(cnp / (long)pow(10,6));
        //System.out.println(temp);
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
    private static File load() throws FileNotFoundException {

        //Deschid baza de clienti si creez obiectul care citeste din fisier
        File Dbase = new File("DataBase/Userbase.txt");

        Scanner downloader = new Scanner(Dbase);
        downloader.useDelimiter("[,\n\r]+");
        while (downloader.hasNext()) {
            //Creez lista de utilizatori
            String[] one_user_data = new String[8];
            for (int i = 0; i < 8; i++) {
                one_user_data[i] = downloader.next();
            }
            User one_user = new User(one_user_data[0], one_user_data[1], one_user_data[2], LocalDate.of(parseInt(one_user_data[3]), Month.of(parseInt(one_user_data[4])), parseInt(one_user_data[5])), parseInt(one_user_data[6]), one_user_data[7]);

            user_base.add(one_user);
        }
        downloader.close();

        File firme_file = new File("DataBase/FirmePartenere.txt");
        Scanner downParteneri = new Scanner(firme_file);
        downParteneri.useDelimiter(",");
        while (downParteneri.hasNext()) {
            firme_partenere.add(downParteneri.next());
        }
        downParteneri.close();

        return Dbase;
    }

    public ArrayList<String> getFirme_partenere() {
        return firme_partenere;
    }

    //Functie ce salveaza un utilizator nou in fisier
    public void save_user(User user){
        user_base.add(user);
    }

    public int prelucrare_cnp(long cnp) {
        return (int)(((cnp / (long) pow(10, 12)) * (long) pow(10, 6)) + (cnp % (long) (pow(10, 6))));
    }

    public void changeUserPass(User u, String newpass){
        u.setParola(newpass);
    }

    //Functie pentru a sterege un utilizator
    public void delete_user(User user){
        user_base.remove(user);
    }

    public void delete_user(User user, int nr_imp_cnp) throws Exception {
        if(user.getNumere_importante_cnp() == nr_imp_cnp)
            user_base.remove(user);
        else
            throw new Exception("CNP incorect!");
    }

    // Functie care salveaza din nou
    public void renew_users() {

        FileWriter user_saver,user_data_saver;
        try {
            user_saver = new FileWriter(data_base);

            for (User i : user_base) {
                user_saver.append(i.save()).append('\n');
                user_data_saver = new FileWriter( "DataBase/UserInfo/" + i.getEmail() + i.getNumere_importante_cnp()+".txt");
                for (Card j : i.getCarduri()){
                    for(Cont t : j.getConturi()){
                        user_data_saver.append(t.getNume()).append(",").append(t.getSuma_disponibila().toString()).append(",");
                        switch (t.getTip()){
                            case "DEBIT":
                                user_data_saver.append(String.valueOf(((ContDebit) t).golDe()));
                                break;
                            case "CREDIT":
                                user_data_saver.append(((ContCredit) t).getDatorie().toString());
                                break;
                        }
                        user_data_saver.append(",").append(t.getTip()).append(",");
                    }
                    user_data_saver.append("NEXTCARD,");
                }

                user_data_saver.close();
            }

            user_saver.close();

        } catch (IOException e) {
            e.printStackTrace();
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

    public Cont get_cont(String nume_cont, User user){
        return user.getCont(nume_cont);
    }

    // Simulare de conexiune
    public static LocaleInteraction connect() throws FileNotFoundException {

        if (con == null) {

            //Creez obiectul care imita o conexiune la un server
            con = new LocaleInteraction();

            //Pronesc functia de incarcare a datelor
            data_base = load();
        }

        return con;
    }
}
