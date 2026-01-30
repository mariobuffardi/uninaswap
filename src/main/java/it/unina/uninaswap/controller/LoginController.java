package it.unina.uninaswap.controller;

import it.unina.uninaswap.dao.IndirizzoDAO;
import it.unina.uninaswap.dao.StudenteDAO;
import it.unina.uninaswap.dao.impl.IndirizzoDAOImpl;
import it.unina.uninaswap.dao.impl.StudenteDAOImpl;
import it.unina.uninaswap.dao.util.DBConnection;
import it.unina.uninaswap.model.entity.Indirizzo;
import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.view.AnnunciMainView;
import it.unina.uninaswap.view.LoginView;

import java.sql.Connection;

public class LoginController {

    private final LoginView view;
    private final StudenteDAO studenteDAO;
    private final IndirizzoDAO indirizzoDAO;

    public LoginController(LoginView view) {
        this.view = view;
        this.studenteDAO = new StudenteDAOImpl();
        this.indirizzoDAO = new IndirizzoDAOImpl();

        this.view.addLoginListener(e -> handleLogin());

        this.view.addSwitchToRegisterListener(e -> {
            this.view.clearRegisterFields();
            this.view.showRegisterCard();
        });
        this.view.addBackToLoginListener(e -> {
            this.view.clearRegisterFields();
            this.view.showLoginCard();
        });
        this.view.addRegisterListener(e -> handleRegistration());
    }

    private void handleLogin() {
        String email = view.getEmail();
        String password = view.getPassword();

        if (email.isEmpty() || password.isEmpty()) {
            view.showError("Inserisci email e password.");
            return;
        }

        try {
            Studente s = studenteDAO.findByEmailAndPassword(email, password);
            if (s == null) {
                view.showError("Credenziali non valide.");
                return;
            }

            AnnunciMainView home = new AnnunciMainView(s);
            new HomeController(home, s);
            home.setVisible(true);
            view.dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            view.showError("Errore durante il login: " + ex.getMessage());
        }
    }

    private void handleRegistration() {
        if (!validateRegistrationInput()) return;

        String email = view.getRegEmail();

        try {
            if (studenteDAO.existsByEmail(email)) {
                view.showError("L'email '" + email + "' è già in uso.");
                return;
            }

            Studente s = new Studente();
            s.setNome(view.getRegNome());
            s.setCognome(view.getRegCognome());
            s.setEmail(email);
            s.setPassword(view.getRegPassword());
            s.setSesso(view.getRegSesso());
            s.setPreferisceSpedizione(view.isRegPreferisceSpedizione());
            s.setPreferisceIncontroInUni(view.isRegPreferisceIncontroInUni());

            Indirizzo i = new Indirizzo();
            i.setVia(view.getRegVia());
            i.setCitta(view.getRegCitta());
            i.setStato(view.getRegStato());
            i.setCivico(Integer.parseInt(view.getRegCivico()));
            i.setCap(Integer.parseInt(view.getRegCap()));

            String matricola;

            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false);
                
                matricola = studenteDAO.insertAndReturnMatricola(s, conn);

                i.setMatricolaStudente(matricola);
                indirizzoDAO.insert(i, conn);

                conn.commit();
            }

            view.showSuccess("Registrazione completata!\nLa tua matricola è: " + matricola);
            view.clearRegisterFields();
            view.switchToLoginCardPrefillEmail(email);

        } catch (Exception ex) {
            ex.printStackTrace();
            view.showError("Errore durante la registrazione: " + ex.getMessage());
        }
    }

    private boolean validateRegistrationInput() {
        if (view.getRegNome().isEmpty() || view.getRegCognome().isEmpty() || view.getRegEmail().isEmpty() ||
                view.getRegPassword().isEmpty() || view.getRegVia().isEmpty() || view.getRegCivico().isEmpty() ||
                view.getRegCap().isEmpty() || view.getRegCitta().isEmpty() || view.getRegStato().isEmpty()) {
            view.showError("Tutti i campi sono obbligatori.");
            return false;
        }

        String email = view.getRegEmail();
        if (!email.contains("@") || !email.contains(".")) {
            view.showError("Formato email non valido.");
            return false;
        }

        if (view.getRegPassword().length() < 6) {
            view.showError("La password deve contenere almeno 6 caratteri.");
            return false;
        }

        try {
            int civico = Integer.parseInt(view.getRegCivico());
            int cap = Integer.parseInt(view.getRegCap());
            if (civico <= 0 || cap <= 0) {
                view.showError("CAP e Civico devono essere numeri positivi.");
                return false;
            }
        } catch (NumberFormatException e) {
            view.showError("CAP e Civico devono essere valori numerici.");
            return false;
        }

        if (!view.isRegPreferisceSpedizione() && !view.isRegPreferisceIncontroInUni()) {
            view.showError("Seleziona almeno una preferenza di consegna.");
            return false;
        }

        return true;
    }
}