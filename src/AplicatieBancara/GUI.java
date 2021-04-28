package AplicatieBancara;

//WIP

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;


public class GUI implements ActionListener {
    private static Connection con;
    private final JFrame frame;
    private final ArrayList<JButton> buttons;

    private GUI(){
        frame = new JFrame("Aplicatie Bancara");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                System.out.println("test");
                con.renew_users();
            }
        });

        ImageIcon icon = new ImageIcon("Icon.png");
        frame.setIconImage(icon.getImage());
        buttons = new ArrayList<>(3);
        buttons.add(new JButton("Autentificare"));
        buttons.add(new JButton("Register"));
        buttons.add(new JButton("Exit"));

    }

    private void mainMenu(){
        frame.getContentPane().removeAll();
        frame.setPreferredSize(new Dimension(800,1000));
        JPanel panel = new JPanel();
        frame.add(panel, BorderLayout.CENTER);

        panel.setBorder(BorderFactory.createEmptyBorder(200,200,200,200));
        panel.setLayout(new GridLayout(0,1));

        for(JButton i: buttons){
            i.addActionListener(this);
            panel.add(i);
        }

        frame.pack();
        frame.setVisible(true);
    }

    private void mainUI(User logged_user){

        frame.getContentPane().removeAll();
        frame.setLayout(new GridLayout(0,1));
        frame.setPreferredSize(new Dimension(1000,600));

        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setBorder(BorderFactory.createEmptyBorder(50,50,50,50));
        panel.setLayout(new GridLayout(0,1,50,50));

        // Info Carduri
        ArrayList<Card> carduri_user;
        carduri_user = logged_user.infoCarduri();

        //// TESTING! POSIBIL BOOOM
        for( int i = 0; i <3; i++ ) logged_user.adaugaCard(new Card(logged_user));
        for (Card i : logged_user.getCarduri()){
            i.deschideCont("DEBIT", "Testing DEBIT");
            i.deschideCont("CREDIT", "Testing CREDIT");
            i.deschideCont("DEBIT", "Testing DEBIT");
            i.deschideCont("CREDIT", "Testing CREDIT");
            i.deschideCont("DEBIT", "Testing DEBIT");
            i.deschideCont("CREDIT", "Testing CREDIT");
            i.deschideCont("DEBIT", "Testing DEBIT");
            i.deschideCont("CREDIT", "Testing CREDIT");
            i.deschideCont("DEBIT", "Testing DEBIT");
            i.deschideCont("CREDIT", "Testing CREDIT");
            i.deschideCont("DEBIT", "Testing DEBIT");
            i.deschideCont("CREDIT", "Testing CREDIT");
        }

        /////

        switch (carduri_user.size()) {
            case 0:
                JLabel fara_carduri = new JLabel("Nu aveti inca un card la banca noasta");
                fara_carduri.setBorder(BorderFactory.createLineBorder(Color.black));
                panel.add(fara_carduri);
                break;
            case 1:
                JPanel panou_card = new JPanel();
                panou_card.setLayout(new GridLayout(0,2));
                panou_card.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Cardul dumneavoasta:"));




                panel.add(panou_card);
                break;

            default:
                JPanel panou_carduri = new JPanel();
                panou_carduri.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Cardurile dumneavoasta:"));
                panou_carduri.setLayout(new BorderLayout());

                Long[] numere_carduri = new Long[logged_user.getCarduri().size()];

                for (int i =0 ; i < logged_user.getCarduri().size(); i++){
                    numere_carduri[i] = logged_user.getCarduri().get(i).getNumber();
                }

                JComboBox<Long> select_card = new JComboBox<>(numere_carduri);
                select_card.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Alegeti cardul:"));

                panou_carduri.add(select_card, BorderLayout.NORTH);

                ArrayList<JPanel> panouri_card_conturi = new ArrayList<>();

                for(Card i : logged_user.getCarduri()){
                    panouri_card_conturi.add(new JPanel());
                    for(Cont j : i.getConturi()){
                        JPanel panou_cont = new JPanel();
                        panou_cont.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Contul " + j.getNume()));
                        panou_cont.setLayout(new GridLayout(2,2));
                        panou_cont.add(new JLabel("Iban: " + j.getIban()));

                        switch (j.getTip()){
                            case "DEBIT":
                                panou_cont.add(new JLabel("    Gol de : " + ((ContDebit) j).golDe() + " zile"));
                                panou_cont.add(new JLabel("Suma disponibila: " + j.getSuma_disponibila()));
                                break;
                            case "CREDIT":
                                panou_cont.add(Box.createGlue());
                                panou_cont.add(new JLabel("Suma disponibila: " + j.getSuma_disponibila()));
                                panou_cont.add(new JLabel("Datorie: " + ((ContCredit) j).getDatorie()));
                                break;
                        }
                        panouri_card_conturi.get(panouri_card_conturi.size()-1).add(panou_cont);
                    }
                }

                panou_carduri.add(panouri_card_conturi.get(0),BorderLayout.CENTER);

                select_card.addActionListener(e -> {
                    panou_carduri.remove(1);
                    panou_carduri.add(panouri_card_conturi.get(select_card.getSelectedIndex()),BorderLayout.CENTER);
                    panou_carduri.updateUI();
                });

                panel.add(panou_carduri);
                break;
        }

        // Deconectare
        JButton deconectare = new JButton("Deconectare");
        panel.add(deconectare);

        deconectare.addActionListener(e -> logIn());

        frame.add(panel);

        frame.pack();
        frame.setVisible(true);
    }

    private void logIn(){
        frame.getContentPane().removeAll();

        frame.setLayout(new GridLayout(0,1));

        frame.setPreferredSize(new Dimension(1200,600));

        JPanel panel = new JPanel();

        frame.add(panel);

        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        panel.setLayout(new GridLayout(3,3,10,10));

        // Email
        panel.add(Box.createGlue());
        JTextField email = new JTextField();

        email.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black,5,true),"Email"));

        panel.add(email);

        //Info Email
        JLabel email_gresit = new JLabel("");
        panel.add(email_gresit);

        panel.add(Box.createGlue());


        //Parola
        JTextField parola = new JPasswordField();
        parola.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black,5,true),"Parola"));

        panel.add(parola);

        //Info Parola

        JLabel parola_gresita = new JLabel("");
        panel.add(parola_gresita);

        //Inpoi
        JButton inapoi = new JButton("Inapoi");

        inapoi.addActionListener(e -> mainMenu());

        panel.add(inapoi);
        panel.add(Box.createGlue());

        //Log in

        JButton login = new JButton("Autentificare");
        login.addActionListener(e -> {
            if (con.verifica_email(email.getText())){
                email_gresit.setText("");
                User logged_user = con.log_in_account(email.getText(), parola.getText());
                if( logged_user != null ) mainUI(logged_user);
                else parola_gresita.setText("Parola este gresita");
            }
            else email_gresit.setText("Emailul nu este asociat unui cont");
        });


        panel.add(login);

        frame.pack();
        frame.setVisible(true);

    }

    private void register(){
        frame.getContentPane().removeAll();

        frame.setLayout(new GridLayout(0,1));

        frame.setPreferredSize(new Dimension(1200,300));

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        panel.setLayout(new GridLayout(0,3,10,10));

        frame.add(panel);

        // Nume Prenume Email

        JTextField nume = new JTextField();
        JTextField prenume = new JTextField();
        JTextField email = new JTextField();

        nume.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black,5,true),"Nume"));
        prenume.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black,5,true),"Prenume"));
        email.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black,5,true),"Email"));

        panel.add(nume);
        panel.add(prenume);
        panel.add(email);

        // CNP & Info CNP & Info parola

        JLabel parola_gresita = new JLabel();
        panel.add(parola_gresita);

        JFormattedTextField cnp = new JFormattedTextField();
        cnp.setValue(0L);
        cnp.setText("");
        panel.add(cnp);

        JLabel cnp_gresit = new JLabel();
        panel.add(cnp_gresit);

        cnp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black,5,true),"CNP"));

        // Parola

        JTextField pass1 = new JPasswordField();
        JTextField pass2 = new JPasswordField();

        pass1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black,5,true),"Parola"));
        pass2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black,5,true),"Reintroduceti parola"));

        panel.add(pass1);
        panel.add(Box.createGlue());
        panel.add(pass2);

        // Done & exit

        JButton done = new JButton("Creaza contul");
        JButton exit = new JButton("Inapoi");

        done.setBorder(BorderFactory.createBevelBorder(0));
        exit.setBorder(done.getBorder());

        panel.add(exit);

        // Errmsg
        JLabel errmsg = new JLabel();
        panel.add(errmsg);

        panel.add(done);

        // Actiuni butoane

        exit.addActionListener(e -> mainMenu());

        final LocalDate[] data_nasterii = new LocalDate[1];

        done.addActionListener(e -> {

            if(!pass1.getText().equals(pass2.getText())) parola_gresita.setText("Parolele nu coincid!");
            else if(pass1.getText().length() < 8 ) parola_gresita.setText("Parola este mai mica de 8 caractere");
            else parola_gresita.setText("");

            try {
                con.verifica_parola(pass1.getText(), pass2.getText());
                parola_gresita.setText("");
            }
            catch (Exception exception){
                parola_gresita.setText(exception.getMessage());
            }

            try{
                data_nasterii[0] = con.get_data_from_cnp((long)cnp.getValue());
                cnp_gresit.setText("");
            } catch (Exception exception) {
                cnp_gresit.setText(exception.getMessage());
            }

            if (con.verifica_email(email.getText())) errmsg.setText("Acest mail este deja folosit");
            else errmsg.setText("");

            if(parola_gresita.getText().length() == 0 && cnp_gresit.getText().length() == 0 && errmsg.getText().length() == 0){
                con.save_user(new User(prenume.getText(), nume.getText(), email.getText(),data_nasterii[0], con.prelucrare_cnp((long)cnp.getValue()),pass1.getText() ));
                int[] but_index = {0,1,2,4,6,8};
                for (int i : but_index){
                    JTextField t = (JTextField) panel.getComponent(i);
                    t.setText("");
                }
            }

        });

        frame.pack();
        frame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        switch (buttons.indexOf((JButton) e.getSource())){
            case 0:
                logIn();
                break;

            case 1:
                register();
                break;

            default:
                frame.dispose();
                break;
        }


    }


    public static void main(String[] args) {
        GUI gui = new GUI();

        try {
            con = Connection.connect();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        gui.mainMenu();
        con.renew_users();

    }


}
