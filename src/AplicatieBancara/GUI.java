package AplicatieBancara;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Month;
import java.util.Scanner;
import java.time.LocalDate;

import static java.lang.Long.parseLong;
import static java.lang.Math.pow;
import static javax.xml.bind.DatatypeConverter.parseInt;

public class GUI {
    private final static String server_ip = "10.10.10.10";
    private static Connection con;

    //Functie pentru a salva doar numere importante
    private static long prelucrare_cnp(long cnp){
        return ((cnp / (long) pow(10, 12)) * (long) pow(10, 6)) + (cnp % (long) (pow(10, 6)));
    }

    // Functia de inregistrare
    private static int register(Scanner input){
        //Cer datele pe rand de la utilizator
        System.out.println("Introduceti emailul");
        String email = input.next();

        System.out.println("Introduceti prenumele");
        String prenume = input.next();

        System.out.println("Introduceti numele");
        String nume = input.next();

        //Citesc datele care pot avea o forma gresita
        int zi, luna, an;
        LocalDate date = LocalDate.now();
        long cnp = 0;

        boolean bool_date_numerice = true;
        while(bool_date_numerice) {
            try {
                bool_date_numerice = false;
                String temp;

                System.out.println("Introduceti data nasterii, in format numeric\nZiua:");
                temp = input.next();
                zi = parseInt(temp);

                System.out.println("Luna:");
                temp = input.next();
                luna = parseInt(temp);

                System.out.println("Anul:");
                temp = input.next();
                an = parseInt(temp);

                // Verific ca data nasterii sa aiba o putin de sens
                if(zi > 31 || zi < 0 || luna > 12 || luna < 0 || an < 0){
                    System.out.println("Data nasterii este inexistenta");
                    bool_date_numerice = true;
                }

                if(LocalDate.now().getYear() - an < 18 ){
                    System.out.println("Aceasta banca nu accepta clienti minori");
                    return -1;
                }

                System.out.println("Introduceti cnpul:");
                temp = input.next();
                cnp = parseLong(temp);

                //Verific daca cnp-ul are numarul minim de cifre
                if( cnp < (long)pow(10,12) ) {
                    bool_date_numerice = true;
                    System.out.println("CNP incorect");
                }

                date = LocalDate.of(an, Month.of(luna), zi);
            } catch (Exception e) {
                e.printStackTrace();
                bool_date_numerice = true;
                System.out.println("Datele introduse sunt eronate");
            }
        }

        // Cand citesc parola verific daca este scrisa corect
        String pass = null;
        boolean get_pas = true;
        while (get_pas) {
            System.out.println("Introduceti parola");
            pass = input.next();

            System.out.println("Introduceti parola iar");
            if(pass.equals(input.next())) {
                get_pas = false;
            }
            else{
                System.out.println("Parolele nu sunt la fel");
            }
        }

        // Prelucrez cnp-ul, creez noul utilizator, apoi il bag in baza de date
        cnp = prelucrare_cnp(cnp);

        User add_new = new User(prenume, nume, email, date, (int)cnp, pass);

        System.out.println(add_new);

        try {
            con.save_user(add_new);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    //Functia de autentificare in cont
    private static int log_in(Scanner input){

        System.out.println("Email");
        String username = input.next();

        System.out.println("Parola");
        String pass = input.next();

        con.log_in_account(username, pass);

        return 0;
    }


    public static void main(String[] args) {

        try {
            con = Connection.connect(server_ip);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (true) {
            //Interfata text temporara pana fac GUI
            boolean exit = false;
            System.out.println("Ce doriti sa faceti?\n 1:Inregistrare \n 2:Logare \n 3:Exit\n");
            Scanner input = new Scanner(System.in);

            int cazuri;
            try {
                cazuri = input.nextInt();
            } catch (Exception e) {
                break;
            }

            int i = 0;
            switch (cazuri){
                case 1:
                    i = register(input);
                    break;

                case 2:
                    i = log_in(input);
                    break;

                default:
                    exit = true;
                    break;
            }
            if (i == -1)
                System.out.println("Datele introduse au avut o eroare");


            if (exit)
                break;
        }

    }
}
