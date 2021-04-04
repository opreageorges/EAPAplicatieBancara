package AplicatieBancara;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Scanner;
import java.util.TreeSet;

import static java.lang.Integer.parseInt;

public class Connection {
    private static Connection con = null;
    private static File data_base;
    private static TreeSet<User> user_base;

    private Connection() {
        user_base = new TreeSet<>();
    }

    public boolean verfica_email(String mail) {
        for (User i : user_base) {
            if (i.getEmail().equals(mail))
                return true;
        }
        return false;
    }

//    Functie de care nu mai am nevoie
//    Functie pentru aflarea nr de utilizatori(nr de linii din fisier)
//    private static int nr_utilizatori(File Dbase) throws FileNotFoundException {
//
//        //Scanez fisierul si atata timp cat am o linie noua, cresc nr de linii.
//        Scanner downloader = new Scanner(Dbase);
//        int lines = 0;
//        while(true) {
//
//            //Cand arunca exceptia ca nu mai are linie noua opreste bucla
//            try {
//                downloader.nextLine();
//                lines++;
//            } catch (NoSuchElementException e) {
//                break;
//            }
//
//        }
//        downloader.close();
//        return lines;
//    }

    //Functie ce incarca utilizatorii din fisier
    private static File load() throws FileNotFoundException {

        //Deschid baza de clienti si creez obiectul care citeste din fisier
        File Dbase = new File("C:\\Facultate\\EAP\\EAP project\\DataBase\\Userbase.txt");
        Scanner downloader = new Scanner(Dbase);

//        Aflu cati utilizatori am deja stocati si creez un spatiu de stocare pentru ei
//        int nr_users = nr_utilizatori(Dbase);
//        user_base = new User[nr_users];

        while (downloader.hasNext()) {

            //Creez lista de utilizatori
            String[] one_user_data = new String[8];
            for (int i = 0; i < 8; i++) {
                one_user_data[i] = downloader.next();
            }
            User one_user = new User(one_user_data[0], one_user_data[1], one_user_data[2], LocalDate.of(parseInt(one_user_data[3]), Month.of(parseInt(one_user_data[4])), parseInt(one_user_data[5])), parseInt(one_user_data[6]), one_user_data[7]);

            user_base.add(one_user);
        }
        return Dbase;
    }

    //Functie ce salveaza un utilizator nou in fisier
    public void save_user(User user) throws IOException {
        //Creez un obiect care scrie in fisier
        //FileWriter user_saver = new FileWriter(data_base, true);

        //Scriu noul utilizator
        //user_saver.append(user.save()).append('\n');

        //Adaug noul utilizator si in baza de date activa
        user_base.add(user);

        //user_saver.close();

    }

    //Functie pentru a sterege un utilizator
    public void delete_user(User user){
        user_base.remove(user);
    }

    // Functie care salveaza din nou
    public void renew_users() {

        FileWriter user_saver;
        try {
            user_saver = new FileWriter(data_base);

            for (User i : user_base) {
                user_saver.append(i.save()).append('\n');
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


    // Simulare de conexiune
    public static Connection connect() throws FileNotFoundException {

        if (con == null) {

            //Creez obiectul care imita o conexiune la un server
            con = new Connection();

            //Pronesc functia de incarcare a datelor
            data_base = load();
        }

        return con;
    }
}
