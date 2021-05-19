package AplicatieBancara;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class DataBaseInteraction {
    private static DataBaseInteraction singleinstance;
    private static Connection jdbccon;
    public static Statement coninter;

    private DataBaseInteraction(String url, String user, String password){
        try {
            jdbccon = DriverManager.getConnection(url, user, password);
            coninter = jdbccon.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static DataBaseInteraction getInstance(String url, String user, String password){
        if (singleinstance == null) singleinstance = new DataBaseInteraction(url, user, password) ;
        return singleinstance;
    }

    public void refreshCon(){
        try {
            coninter.close();
            coninter = jdbccon.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void insertElement(String element){
        try {
            coninter.execute("INSERT INTO `aplicatiebancara`." + element);
        } catch (SQLException ignored) {

        }
    }

    public void deleteElement(String element){
        try {
            coninter.executeUpdate("DELETE FROM " + element + ";");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public TreeSet<User> getUsersFromDB(){
        TreeSet<User> users = new TreeSet<>();

        try {
            ResultSet user_data = coninter.executeQuery("SELECT * FROM user");
            while (user_data.next()){
                String prenume = user_data.getString(1);
                String nume = user_data.getString(2);
                String email = user_data.getString(3);
                LocalDate data_nasterii = user_data.getDate(4).toLocalDate();
                int numere_cnp = user_data.getInt(5);
                String parola = user_data.getString(6);

                users.add(new User(prenume, nume, email, data_nasterii, numere_cnp, parola));
            }
            user_data.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return users;
    }

    public ArrayList<Card> getUserCardsFromDB(User u){
        ArrayList<Card> user_cards = new ArrayList<>();
        try {
            ResultSet card_data = coninter.executeQuery("SELECT * FROM aplicatiebancara.card where proprietar_cnp = " + u.getNumere_importante_cnp() + ";");
            while (card_data.next()) {
                user_cards.add(new Card(u, card_data.getLong(1), card_data.getInt(2)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        return user_cards;
    }

    public ArrayList<Cont> getCardContsFromDB(Card c){
        ArrayList<Cont> card_conts = new ArrayList<>();
        try {
            ResultSet cont_data = coninter.executeQuery("SELECT * FROM aplicatiebancara.cont where proprietar_number = " + c.getNumber() + ";");
            while (cont_data.next()) {
                String nume = cont_data.getString(1);
                String iban = cont_data.getString(2);
                BigDecimal suma_diponibil = BigDecimal.valueOf(cont_data.getFloat(3));
                switch (cont_data.getString(5)){
                    case "DEBIT":
                        LocalDate gol_din = cont_data.getDate(7).toLocalDate();
                        ContDebit cont = new ContDebit(nume, iban, suma_diponibil, c, gol_din);
                        if (cont.golDe() < cont.getMaxim_de_zile_gol()) card_conts.add(cont);
                    case "CREDIT":
                        BigDecimal datorie = BigDecimal.valueOf(cont_data.getFloat(6));
                        card_conts.add(new ContCredit(nume, iban, suma_diponibil, c,datorie));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return card_conts;
    }

    // Metode prentru crearea de la 0 a bazei de date
    public void startup(){
        try {
            coninter.executeUpdate("DROP DATABASE `aplicatiebancara`");
            coninter.executeUpdate("CREATE DATABASE `aplicatiebancara` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */; ");
            coninter.executeUpdate("USE `aplicatiebancara`");
            recreate_all();
            insert_all();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void recreate_all() throws SQLException {
        coninter.executeUpdate("" +
                "CREATE TABLE `user` ( `prenume` varchar(45) NOT NULL, \n" +
                "`nume` varchar(45) NOT NULL, \n" +
                "`email` varchar(45) NOT NULL, \n" +
                "`data_nasterii` date NOT NULL, \n" +
                "`numerecnp` int NOT NULL, \n" +
                "`parola` varchar(45) NOT NULL, \n" +
                "PRIMARY KEY (`email`,`numerecnp`), \n" +
                "UNIQUE KEY `numerecnp_UNIQUE` (`numerecnp`), \n" +
                "UNIQUE KEY `email_UNIQUE` (`email`)); ");
        coninter.executeUpdate("" +
                "CREATE TABLE `card` (\n" +
                "  `numar` bigint NOT NULL,\n" +
                "  `cvc_cvv` int NOT NULL,\n" +
                "  `proprietar_cnp` int NOT NULL,\n" +
                "  PRIMARY KEY (`numar`),\n" +
                "  KEY `proprietar_card_idx` (`proprietar_cnp`),\n" +
                "  CONSTRAINT `proprietar_card` FOREIGN KEY (`proprietar_cnp`) REFERENCES `user` (`numerecnp`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                "); ");
        coninter.executeUpdate("" +
                "CREATE TABLE `cont` (\n" +
                "  `nume` varchar(45) NOT NULL,\n" +
                "  `iban` varchar(45) NOT NULL,\n" +
                "  `suma_disponibila` float DEFAULT '0',\n" +
                "  `proprietar_number` bigint NOT NULL,\n" +
                "  `tip` enum('CREDIT','DEBIT') DEFAULT NULL,\n" +
                "  `datorie` float DEFAULT NULL,\n" +
                "  `gol_din` date DEFAULT NULL,\n" +
                "  PRIMARY KEY (`iban`),\n" +
                "  KEY `proprietar_number_idx` (`proprietar_number`),\n" +
                "  CONSTRAINT `proprietar_number` FOREIGN KEY (`proprietar_number`) REFERENCES `card` (`numar`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ");");
    }

    private void insert_all(){
        ArrayList<User> users = new ArrayList<>(100);
        Random random = new Random();
        for (int i = 0; i < 500; i++){
            int year = 1970 + random.nextInt(30) + 1;
            int month = random.nextInt(11) + 1;
            int day = random.nextInt(27) + 1;
            LocalDate data_nas = LocalDate.of(year, month, day);
            int cnp = 0;
            while (cnp <= 994011){
                cnp = cnp * 10 + random.nextInt(9);
            }
            users.add(new User("Prenume" + i, "Nume" + i, "Email" + i +"@cti.ro", data_nas, cnp,"conpa5555"));
        }
        for (User u : users){
            insertElement(u.makeInsert());
            for (int i = 0; i < random.nextInt(3); i++){
                u.adaugaCard(new Card(u));
            }
            for (Card c : u.getCarduri()){
                insertElement(c.makeInsert());
                for (int i = 0; i < random.nextInt(4); i++){
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    c.deschideCont("DEBIT", " CONTUL " + i);
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    c.deschideCont("CREDIT", " CONTUL " + i);
                }
                for (Cont i : c.getConturi()){
                    insertElement(i.makeInsert());
                }
            }
        }
    }


}
