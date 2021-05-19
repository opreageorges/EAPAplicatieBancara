package AplicatieBancara;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;

public class GUI implements ActionListener {
    private static LocaleInteraction con;
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
        con.logthis("MAINMENU", "");
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

    // Interactiunea cu un card

    private void tranzactii(User logged_user, Card interaction_card, boolean[] b){
        if(!b[0]) {
            b[0] = true;
            con.logthis("TRANZACTIE", logged_user.getEmail());
            JFrame tranzactii_frame = makePpopup("Realizeaza tranzactii", b);
            tranzactii_frame.setPreferredSize(new Dimension(1000,300));
            tranzactii_frame.setLayout(new GridLayout(0,1));

            JPanel tranzactii_panel = new JPanel();
            tranzactii_panel.setLayout(new GridLayout(0,1));

            JComboBox<Cont> ibanuri_conturi = new JComboBox<>(interaction_card.getConturi().toArray(new Cont[0]));


            tranzactii_panel.add(ibanuri_conturi);

            // Tranzactie intre persoane

            JPanel panou_plata_om = new JPanel();
            panou_plata_om.setLayout(new GridLayout(0, 4));

            JButton plata_om = new JButton("Realizeaza un tranfer catre o persoana");

            JTextField email_beneficiar = new JTextField();
            email_beneficiar.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Emailul beneficiarului"));

            JTextField iban_beneficiar = new JTextField();
            iban_beneficiar.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Ibanul Beneficiarului"));

            JFormattedTextField suma_transferata= new JFormattedTextField();
            suma_transferata.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Suma transferata"));
            suma_transferata.setValue(0F);
            suma_transferata.setText("");

            plata_om.addActionListener(e->{
                if(con.verifica_email(email_beneficiar.getText())){
                    email_beneficiar.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Emailul beneficiarului"));
                    Cont cont_beneficiar = con.get_user_bnk_cont(email_beneficiar.getText(), iban_beneficiar.getText());
                    if(cont_beneficiar != null){
                        iban_beneficiar.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Ibanul Beneficiarului"));
                        try {
                            ((Cont)ibanuri_conturi.getSelectedItem()).transferIntrePersoane(cont_beneficiar, (float)suma_transferata.getValue());
                            suma_transferata.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Suma transferata"));
                            cardInteraction(logged_user, interaction_card, b);
                            tranzactii_panel.updateUI();
                            con.logthis("TRANOM", logged_user.getEmail());
                        } catch (Exception exception) {
                            suma_transferata.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), exception.getMessage()));
                        }
                    }
                    else
                        iban_beneficiar.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Iban inexistent"));
                }
                else
                    email_beneficiar.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Beneficiarul nu exista"));
            });

            panou_plata_om.add(plata_om);
            panou_plata_om.add(email_beneficiar);
            panou_plata_om.add(iban_beneficiar);
            panou_plata_om.add(suma_transferata);

            tranzactii_panel.add(panou_plata_om);

            // Tranzactie cu o firma

            JPanel panou_plata_firma = new JPanel();
            panou_plata_firma.setLayout(new GridLayout(0, 3));

            JButton plata_firma = new JButton("Realizeaza un tranfer catre o firma");

            JComboBox<String> firme_partenere = new JComboBox<>(con.getFirme_partenere().toArray(new String[0]));

            JFormattedTextField suma_transferata_firma = new JFormattedTextField();
            suma_transferata_firma.setValue(0F);
            suma_transferata_firma.setText("");
            suma_transferata_firma.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Suma transferata"));

            plata_firma.addActionListener(e -> {
                try {
                    ((Cont)ibanuri_conturi.getSelectedItem()).plataFirma((String) firme_partenere.getSelectedItem(), (float)suma_transferata_firma.getValue());
                    suma_transferata_firma.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Suma transferata"));
                    cardInteraction(logged_user, interaction_card, b);
                    tranzactii_panel.updateUI();
                    con.logthis("TRANFIRMA", logged_user.getEmail());
                } catch (Exception exception) {
                    suma_transferata_firma.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), exception.getMessage()));
                }
            });

            panou_plata_firma.add(plata_firma);
            panou_plata_firma.add(firme_partenere);
            panou_plata_firma.add(suma_transferata_firma);

            tranzactii_panel.add(panou_plata_firma);

            // Inapoi
            JButton inapoi = new JButton("Inapoi");

            inapoi.addActionListener(e -> tranzactii_frame.dispose());

            tranzactii_panel.add(inapoi);

            tranzactii_frame.add(tranzactii_panel);
            tranzactii_frame.pack();
            tranzactii_frame.setVisible(true);
        }
    }

    private void updatePanouInterCont(Card interaction_card, JPanel panel, JPanel panou_inter_cont) {
        panel.remove(0);
        JPanel panou_card_curent_nou = makePanouCard(interaction_card);
        panou_card_curent_nou.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Cardul " + interaction_card.getNumber() + ":"));
        panel.add(panou_card_curent_nou, 0);

        panou_inter_cont.remove(8);
        panou_inter_cont.remove(7);
        panou_inter_cont.add(Box.createGlue());
        panou_inter_cont.add(Box.createGlue());

        panel.updateUI();
    }

    private void cardInteraction(User logged_user, Card interaction_card, boolean[] alreadydoingstuff){
        frame.getContentPane().removeAll();

        con.logthis("CARDINTER", logged_user.getEmail());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0,1));


        // Arata cardul curent

        JPanel panou_card_curent = makePanouCard(interaction_card);
        panou_card_curent.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Cardul " + interaction_card.getNumber() + ":"));
        panel.add(panou_card_curent);

        // Adauga/sterge cont

        JPanel panou_inter_cont = new JPanel();
        panou_inter_cont.setLayout(new GridLayout(0,3));

        JButton adauga_cont = new JButton("Adauga cont");
        panou_inter_cont.add(adauga_cont);
        panou_inter_cont.add(Box.createGlue());
        panou_inter_cont.add(Box.createGlue());

        adauga_cont.addActionListener(e ->{
            if(panou_inter_cont.getComponent(1).getClass().toString().equals("class javax.swing.Box$Filler")){
                JTextField nume_cont = new JTextField();
                nume_cont.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Numele contului"));
                panou_inter_cont.remove(1);
                panou_inter_cont.add(nume_cont,1);

                JComboBox<String> tip_cont = new JComboBox<>(new String[]{"Debit", "Credit"});
                panou_inter_cont.remove(2);
                panou_inter_cont.add(tip_cont,2);

                panou_inter_cont.updateUI();
            }
            else{
                if(!((JTextField) panou_inter_cont.getComponent(1)).getText().equals("")) {
                    logged_user.deschideCont(interaction_card.getNumber(), ((JComboBox) panou_inter_cont.getComponent(2)).getSelectedItem().toString(), ((JTextField) panou_inter_cont.getComponent(1)).getText());

                    updatePanouInterCont(interaction_card, panel, panou_inter_cont);

                    con.logthis("DESCCONT", logged_user.getEmail());
                }
                else
                    ((JTextField) panou_inter_cont.getComponent(1)).setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Numele trebuie sa contina caractere!"));
            }
        });

        JButton sterge_cont = new JButton("Sterge cont bancar");
        panou_inter_cont.add(sterge_cont);
        panou_inter_cont.add(Box.createGlue());
        panou_inter_cont.add(Box.createGlue());

        sterge_cont.addActionListener(e-> {
            if(panou_inter_cont.getComponent(4).getClass().toString().equals("class javax.swing.Box$Filler")){
                JTextField nume_cont = new JTextField();
                nume_cont.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Numele contului"));
                panou_inter_cont.remove(4);
                panou_inter_cont.add(nume_cont,4);

                JTextField parola_utilizator = new JPasswordField();
                parola_utilizator.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Introduce-ti parola"));
                panou_inter_cont.remove(5);
                panou_inter_cont.add(parola_utilizator,5);

                panou_inter_cont.updateUI();
            }
            else{
                if(((JTextField)panou_inter_cont.getComponent(5)).getText().equals(logged_user.getParola())){
                    logged_user.inchideCont(interaction_card.getNumber(), ((JTextField)panou_inter_cont.getComponent(4)).getText().toUpperCase());
                    ((JTextField)panou_inter_cont.getComponent(5)).setText("");
                    ((JTextField)panou_inter_cont.getComponent(4)).setText("");

                    updatePanouInterCont(interaction_card, panel, panou_inter_cont);

                    con.logthis("ICHCONT", logged_user.getEmail());
                }
                else
                    ((JTextField) panou_inter_cont.getComponent(5)).setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Parola este gresita!"));
            }
        });

        //Alimenteaza cont

        JButton alim_cont = new JButton("Alimenteaza un cont");

        panou_inter_cont.add(alim_cont);
        panou_inter_cont.add(Box.createGlue());
        panou_inter_cont.add(Box.createGlue());

        alim_cont.addActionListener(e->{
            if (panou_inter_cont.getComponent(7).getClass().toString().equals("class javax.swing.Box$Filler") && interaction_card.getConturi().size() > 0){
                int temp = interaction_card.getConturi().size();
                String[] temp_ibanuri = new String[temp];
                for(int i = 0 ; i < temp; i++){
                    temp_ibanuri[i] = interaction_card.getConturi().get(i).getIban();
                }

                JComboBox<String> ibanuri_conturi = new JComboBox<>(temp_ibanuri);
                panou_inter_cont.remove(7);
                panou_inter_cont.add(ibanuri_conturi, 7);

                JFormattedTextField suma = new JFormattedTextField();
                suma.setValue(0F);
                suma.setText("");
                suma.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Introduce-ti suma pe care doriti sa o alimentati"));

                panou_inter_cont.remove(8);
                panou_inter_cont.add(suma);

                panel.updateUI();
            }
            else if(panou_inter_cont.getComponent(7).getClass().toString().equals("class javax.swing.JComboBox")){
                float suma = (float)((JFormattedTextField)panou_inter_cont.getComponent(8)).getValue();
                logged_user.getCont((String)((JComboBox)panou_inter_cont.getComponent(7)).getSelectedItem()).addMoney(suma);

                updatePanouInterCont(interaction_card, panel, panou_inter_cont);
                con.logthis("ALMCONT", logged_user.getEmail());
            }
        });

        panel.add(panou_inter_cont);
        // Tranzactii
        JButton realizeaza_tranzactie = new JButton("Realizeaza o tranzactie");

        realizeaza_tranzactie.addActionListener(e -> {
            if(interaction_card.getConturi().size() >= 1)
                tranzactii(logged_user, interaction_card, alreadydoingstuff);
        });

        panel.add(realizeaza_tranzactie);

        //Sterge cardul

        JButton sterge_cardul = new JButton("Sterge cardul");

        sterge_cardul.addActionListener(e -> {
            con.logthis("ICHCARD", logged_user.getEmail());
            sigurStergeCard(logged_user, alreadydoingstuff, interaction_card.getNumber());
        });

        panel.add(sterge_cardul);

        // Butonul de intoarcere

        JButton inapoi = new JButton("Inapoi");

        inapoi.addActionListener(e -> mainUI(logged_user));

        panel.add(inapoi);

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }


    // Un subprogram care face ferestre noi

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

    // UI-ul unui utilizator logat si subprogramele aferente acestui UI

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

    //Subprogram pentru schimbarea parolei
    private void changePass(User loggeduser, boolean[] b){
        if(!b[0]) {
            b[0] = true;
            JFrame changepassframe = makePpopup("Schimabre parola", b);

            con.logthis("PASS", loggeduser.getEmail());

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
                    con.logthis("CHDPASS", loggeduser.getEmail());
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
            mainpanel.setLayout(new GridLayout(0, 1));

            JLabel avertisment = new JLabel("Daca alegeti sa stergeti acest card toate conturile si toti banii de pe acestea o sa fie pierduti!");
            JLabel avertisment2 = new JLabel("Daca sunteti sigur introduceti parola in casuta de mai jos, daca nu inchideti fereastra");
            JTextField pass_field = new JPasswordField();

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
            mainpanel.add(avertisment2);
            mainpanel.add(pass_field);
            mainpanel.add(confirmation_button);

            sigurstergeframe.add(mainpanel);
            sigurstergeframe.pack();
            sigurstergeframe.setVisible(true);
        }
    }


    private void mainUI(User logged_user){
        // Pentru a nu deschide o infinitate de ferestre
        boolean[] alreadydoingstuff = new boolean[1];

        frame.getContentPane().removeAll();
        frame.setLayout(new GridLayout(0,1));
        frame.setPreferredSize(new Dimension(1000,600));

        con.logthis("LOGGED", logged_user.getEmail());

        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setBorder(BorderFactory.createEmptyBorder(50,50,50,50));
        panel.setLayout(new GridLayout(0,1,50,50));

        // Interactiune cu cardurile
        JPanel panou_carduri_si_parola = new JPanel();
        panou_carduri_si_parola.setLayout(new GridLayout(0,3));

        JButton creare_card = new JButton("Creaza un nou card");
        JButton interact_card = new JButton("Interactioneaza cu card");

        panou_carduri_si_parola.add(creare_card);
        panou_carduri_si_parola.add(interact_card);

        creare_card.addActionListener(e -> {
            logged_user.adaugaCard(new Card(logged_user));
            mainUI(logged_user);
        });

        // Info Carduri
        ArrayList<Card> carduri_user;
        carduri_user = logged_user.getCarduri();

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
                interact_card.addActionListener(e -> cardInteraction(logged_user, logged_user.getCarduri().get(0), alreadydoingstuff));

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
                interact_card.addActionListener(e -> {
                            if(select_card.getSelectedItem() != null) cardInteraction(logged_user, logged_user.getCarduri().get(select_card.getSelectedIndex()), alreadydoingstuff);
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

    // UI-ul principal
    private void logIn(){
        frame.getContentPane().removeAll();

        frame.setLayout(new GridLayout(0,1));

        frame.setPreferredSize(new Dimension(1200,600));

        JPanel panel = new JPanel();

        frame.add(panel);

        con.logthis("LOGIN", "");

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

        con.logthis("REGISTER", "");

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
            con = LocaleInteraction.connect();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Genereaza baza de date
        // dbcon.startup();
        gui.mainMenu();
        con.renew_users();

    }
}
