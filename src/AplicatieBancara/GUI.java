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
    private final ImageIcon icon;

    private GUI(){
        frame = new JFrame("Aplicatie Bancara");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                con.renew_users();
            }
        });

        icon = new ImageIcon("Icon.png");
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

    // UI-ul unui utilizator logat si subprogramele aferente acestui UI

    // Make popup

    private JFrame makePpopup(String titlu ,boolean[] b){
        JFrame popup = new JFrame(titlu);
        popup.setIconImage(icon.getImage());

        popup.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                b[0] = false;
            }
        });

        popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        return popup;
    }

    //Subprogram pentru schimbarea parolei
    private void changePass(User loggeduser, boolean[] b){
        if(!b[0]) {
            b[0] = true;
            JFrame changepassframe = makePpopup("Schimabre parola", b);

            JPanel mainpanel = new JPanel();
            mainpanel.setLayout(new GridLayout(4,2));

            //Campurile de text din fereastra de schimbare a parolei
            JTextField old_pass = new JPasswordField();
            JTextField new_pass1 = new JPasswordField();
            JTextField new_pass2 = new JPasswordField();
            JLabel old_pass_wrong = new JLabel("");
            JLabel new_pass_wrong = new JLabel("");

            old_pass.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black,5,true),"Parola Veche"));
            new_pass1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black,5,true),"Noua parola"));
            new_pass2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black,5,true),"Reintroduceti noua parola"));

            mainpanel.add(old_pass);
            mainpanel.add(old_pass_wrong);
            mainpanel.add(new_pass1);
            mainpanel.add(new_pass_wrong);
            mainpanel.add(new_pass2);
            mainpanel.add(Box.createGlue());

            //Butonul de a realiza schimbarea

            JButton commitnewpass = new JButton("Schimab Parola");

            commitnewpass.addActionListener(e -> {
                int a = 0;
                old_pass_wrong.setText("");
                new_pass_wrong.setText("");
                try {
                    con.verifica_parola_veche(old_pass.getText(), loggeduser);
                    a+=1;
                } catch (Exception exception) {
                    old_pass_wrong.setText(exception.getMessage());
                }
                try {
                    con.verifica_parola(new_pass1.getText(),new_pass2.getText());
                    a+=1;
                } catch (Exception exception) {
                    new_pass_wrong.setText(exception.getMessage());
                }
                if(a==2) {
                    con.changeUserPass(loggeduser, new_pass1.getText());
                    old_pass.setText("");
                    new_pass1.setText("");
                    new_pass2.setText("");
                }
            });

            mainpanel.add(commitnewpass);

            changepassframe.add(mainpanel);
            changepassframe.pack();
            changepassframe.setVisible(true);
        }
    }

    //Subprogram pentru a evita o situatie neplacute
    private void sigurStergeCard(User logged_user, boolean[] b, long card_number){
        if(!b[0]) {
            b[0] = true;
            JFrame sigurstergeframe = makePpopup("Stergerea cardului", b);

            JPanel mainpanel = new JPanel();
            mainpanel.setLayout(new GridLayout(3, 1));

            JLabel avertisment = new JLabel("Daca alegeti sa stergeti acest card toate cunturile si toti banii de pe acestea o sa fie pierduti!\n Daca sunteti sigur introduceti parola in casuta de mai jos, daca nu inchideti fereastra");
            JTextField pass_field = new JTextField();

            JButton confirmation_button = new JButton("Confirm");

            confirmation_button.addActionListener(e -> {
                try {
                    con.verifica_parola_veche(pass_field.getText(), logged_user);
                    sigurstergeframe.dispose();
                    logged_user.stergeCard(card_number);
                    mainUI(logged_user);
                } catch (Exception exception) {
                    avertisment.setText(exception.getMessage());
                }
            } );

            mainpanel.add(avertisment);
            mainpanel.add(pass_field);
            mainpanel.add(confirmation_button);

            sigurstergeframe.add(mainpanel);
            sigurstergeframe.pack();
            sigurstergeframe.setVisible(true);
        }
    }
    //Subprogram pentru crearea de panouri pentru carduri

    private JPanel makePanouCard(Card c){
        JPanel panou_out = new JPanel();
        panou_out.setLayout(new GridLayout(0,3));
        for(Cont j : c.getConturi()){
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
            panou_out.add(panou_cont);
        }
        return panou_out;
    }

    private void mainUI(User logged_user){
        // Pentru a nu deschide o infinitate de ferestre
        boolean[] alreadydoingstuff = new boolean[1];

        frame.getContentPane().removeAll();
        frame.setLayout(new GridLayout(0,1));
        frame.setPreferredSize(new Dimension(1000,600));

        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setBorder(BorderFactory.createEmptyBorder(50,50,50,50));
        panel.setLayout(new GridLayout(0,1,50,50));

        // Interactiune cu cardurile
        JPanel panou_carduri_si_parola = new JPanel();
        panou_carduri_si_parola.setLayout(new GridLayout(0,3));

        JButton creare_card = new JButton("Creaza un nou card");
        JButton sterge_card = new JButton("Sterge card");


        panou_carduri_si_parola.add(creare_card);
        panou_carduri_si_parola.add(sterge_card);

        creare_card.addActionListener(e -> {
            logged_user.adaugaCard(new Card(logged_user));

            for (int i=0; i<3;i++) {
                logged_user.deschideCont(logged_user.getCarduri().get(logged_user.getCarduri().size()-1).getNumber(),"CREDIT","TEST CREDIT " + i);
                logged_user.deschideCont(logged_user.getCarduri().get(logged_user.getCarduri().size()-1).getNumber(),"DEBIT","TEST DEBIT " + i);
            }

            mainUI(logged_user);
        });


        // Info Carduri
        ArrayList<Card> carduri_user;
        carduri_user = logged_user.infoCarduri();

        // Panoul de carduri
        switch (carduri_user.size()) {
            case 0:
                JLabel fara_carduri = new JLabel("Nu aveti inca un card la banca noasta");
                fara_carduri.setBorder(BorderFactory.createLineBorder(Color.black));
                panel.add(fara_carduri);
                break;
            case 1:
                //Inapoi la partea de panou

                JPanel panou_card = new JPanel();
                panou_card.setLayout(new GridLayout(0,1));
                panou_card.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Cardul " + logged_user.getCarduri().get(0).getNumber() + ":"));

                panou_card.add(makePanouCard(logged_user.getCarduri().get(0)));

                // Actiunea de stergere a cardului
                sterge_card.addActionListener(e -> sigurStergeCard(logged_user, alreadydoingstuff, logged_user.getCarduri().get(0).getNumber()));

                panel.add(panou_card);
                break;

            default:

                //Partea de panou
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
                    panouri_card_conturi.get(panouri_card_conturi.size()-1).add(makePanouCard(i));
                }

                panou_carduri.add(panouri_card_conturi.get(0),BorderLayout.CENTER);

                select_card.addActionListener(e -> {
                    panou_carduri.remove(1);
                    panou_carduri.add(panouri_card_conturi.get(select_card.getSelectedIndex()),BorderLayout.CENTER);
                    panou_carduri.updateUI();
                });

                // Actiunea de stergere a cardului
                sterge_card.addActionListener(e -> {
                            if(select_card.getSelectedItem() != null) sigurStergeCard(logged_user, alreadydoingstuff,  (long)select_card.getSelectedItem());
                });

                panel.add(panou_carduri);
                break;
        }


        //Schimbare parola
        JButton schimba_parola = new JButton("Schimba parola");

        schimba_parola.addActionListener(e -> changePass(logged_user, alreadydoingstuff));

        panou_carduri_si_parola.add(schimba_parola);
        panel.add(panou_carduri_si_parola);

        //Stergerea Contului

        JPanel panou_sterge_cont_si_deconectare = new JPanel();
        panou_sterge_cont_si_deconectare.setLayout(new GridLayout(0,3));
        JButton sterge_cont = new JButton("Sterge-ti contul");

        panou_sterge_cont_si_deconectare.add(sterge_cont);
        panou_sterge_cont_si_deconectare.add(Box.createGlue());

        sterge_cont.addActionListener(e -> {
            if (panou_sterge_cont_si_deconectare.getComponent(1).getClass().toString().equals("class javax.swing.Box$Filler")) {
                JFormattedTextField user_input_stergere_cont = new JFormattedTextField();
                user_input_stergere_cont.setValue(0L);
                user_input_stergere_cont.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black,5,true),"Daca sunte-ti sigur introduce-ti cnpul"));
                panou_sterge_cont_si_deconectare.remove(1);
                panou_sterge_cont_si_deconectare.add(user_input_stergere_cont,1);
                panou_sterge_cont_si_deconectare.updateUI();
            }
            else {
                try {
                    con.delete_user(logged_user, con.prelucrare_cnp((long)((JFormattedTextField)panou_sterge_cont_si_deconectare.getComponent(1)).getValue()));
                    mainMenu();
                } catch (Exception exception) {
                    ((JFormattedTextField)panou_sterge_cont_si_deconectare.getComponent(1)).setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black,5,true),exception.getMessage()));
                }
            }

        });

        // Deconectare

        JButton deconectare = new JButton("Deconectare");
        panou_sterge_cont_si_deconectare.add(deconectare);

        panel.add(panou_sterge_cont_si_deconectare);

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
        //gui.mainMenu();
        gui.mainUI(con.log_in_account("1","12345"));
        con.renew_users();

    }

}
