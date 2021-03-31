package AplicatieBancara;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class Connection {
    private static Connection con = null;
    private final String ip;
    private static File data_base;
    private static User[] user_base;

    private Connection(String ip) {
        this.ip = ip;
    }


    //Functie pentru aflarea nr de utilizatori(nr de linii din fisier)
    private static int nr_utilizatori(File Dbase) throws FileNotFoundException {

        //Scanez fisierul si atata timp cat am o linie noua, cresc nr de linii.
        Scanner downloader = new Scanner(Dbase);
        int lines = 0;
        while(true) {

            //Cand arunca exceptia ca nu mai are linie noua opreste bucla
            try {
                downloader.nextLine();
                lines++;
            } catch (NoSuchElementException e) {
                break;
            }

        }
        downloader.close();
        return lines;
    }

    //Functie ce incarca utilizatorii din fisier
    private static File load() throws FileNotFoundException {

        //Deschid baza de clienti si creez obiectul care citeste din fisier
        File Dbase = new File("C:\\Facultate\\EAP\\EAP project\\DataBase\\Userbase.txt");
        Scanner downloader = new Scanner(Dbase);

        //Aflu cati utilizatori am deja stocati si creez un spatiu de stocare pentru ei
        int nr_users = nr_utilizatori(Dbase);
        user_base = new User[nr_users];


        int user_number = 0;
        while(downloader.hasNext()) {

            //Creez lista de utilizatori
            String[] one_user_data = new String[8];
            for(int i = 0; i < 8; i++){
                one_user_data[i] = downloader.next();
            }
            User one_user = new User(one_user_data[0], one_user_data[1], one_user_data[2], LocalDate.of(parseInt(one_user_data[3]) , Month.of(parseInt(one_user_data[4])), parseInt(one_user_data[5])), parseInt(one_user_data[6]), one_user_data[7]);

            user_base[user_number] = one_user;
            user_number++;
        }
        return Dbase;
    }

    //Functie ce salveaza un utilizator nou in fisier
    public void save_user(User user) throws IOException {
        //Creez un obiect care scrie in fisier
        FileWriter user_saver = new FileWriter(data_base, true);

        //Scriu noul utilizator
        user_saver.append(user.save()).append('\n');

        //Adaug noul utilizator si in baza de date activa
        user_base = Arrays.copyOf(user_base, user_base.length + 1);
        user_base[user_base.length - 1] = user;

        user_saver.close();

    }

    public void log_in_account(String username, String pass){
        User matching_user;
        for(User i : user_base){
            if (i != null && i.getEmail().equals(username) && i.getParola().equals(pass)){
                matching_user = i;
                System.out.println("Bine ati revenit " + matching_user.getPrenume() + " " + matching_user.getNume());
            }
        }


    }

    // Simulare de conexiune
    public static Connection connect(String ip) throws FileNotFoundException {

        if (con == null) {

            //Creez obiectul care imita o conexiune la un server
            con = new Connection(ip);

            //Pronesc functia de incarcare a datelor
            data_base = load();

            //Printez ceva sa arate frumos
            System.out.println("Conexiunea la servarul " + ip + " al banci, a reusit.");
        }

        return con;
    }
}
