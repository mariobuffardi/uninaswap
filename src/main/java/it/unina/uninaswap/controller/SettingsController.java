package it.unina.uninaswap.controller;

import it.unina.uninaswap.dao.StudenteDAO;
import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.view.AnnunciMainView;
import it.unina.uninaswap.view.StudenteSettingsDialog;

import javax.swing.*;

public class SettingsController {

    private final HomeController homeController;
    private final AnnunciMainView view;
    private final StudenteDAO studenteDAO;
    private final Studente loggedIn;

    public SettingsController(HomeController homeController, AnnunciMainView view, StudenteDAO studenteDAO, Studente loggedIn) {
        this.homeController = homeController;
        this.view = view;
        this.studenteDAO = studenteDAO;
        this.loggedIn = loggedIn;
    }

    public void openSettingsDialog() {
        StudenteSettingsDialog dialog = new StudenteSettingsDialog(view, loggedIn);
        dialog.setVisible(true);

        if (!dialog.isConfirmed())
            return;

        Studente updated = dialog.getEditedStudente();
        if (updated == null)
            return;

        try {
            studenteDAO.update(updated);

            loggedIn.setNome(updated.getNome());
            loggedIn.setCognome(updated.getCognome());
            loggedIn.setEmail(updated.getEmail());
            loggedIn.setPassword(updated.getPassword());
            loggedIn.setSesso(updated.getSesso());
            loggedIn.setPreferisceSpedizione(updated.getPreferisceSpedizione());
            loggedIn.setPreferisceIncontroInUni(updated.getPreferisceIncontroInUni());

            homeController.getProfiloController().refreshProfileView();

            JOptionPane.showMessageDialog(view,
                    "Profilo aggiornato con successo.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore durante l'aggiornamento del profilo:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}