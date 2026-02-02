package it.unina.uninaswap.controller;

import it.unina.uninaswap.dao.AnnuncioDAO;
import it.unina.uninaswap.dao.OffertaDAO;
import it.unina.uninaswap.dao.RecensioneDAO;
import it.unina.uninaswap.dao.StudenteDAO;
import it.unina.uninaswap.dao.TransazioneDAO;
import it.unina.uninaswap.model.entity.Annuncio;
import it.unina.uninaswap.model.entity.Offerta;
import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.model.entity.Transazione;
import it.unina.uninaswap.view.AnnunciMainView;
import it.unina.uninaswap.view.NotificationView;

import javax.swing.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificationController {

    private final HomeController homeController;
    private final AnnunciMainView view;
    private final AnnuncioDAO annuncioDAO;
    private final StudenteDAO studenteDAO;
    private final OffertaDAO offertaDAO;
    private final RecensioneDAO recensioneDAO;
    private final TransazioneDAO transazioneDAO;
    private final Studente loggedIn;

    public NotificationController(HomeController homeController, AnnunciMainView view, AnnuncioDAO annuncioDAO, StudenteDAO studenteDAO, OffertaDAO offertaDAO, RecensioneDAO recensioneDAO, TransazioneDAO transazioneDAO, Studente loggedIn) {
        this.homeController = homeController;
        this.view = view;
        this.annuncioDAO = annuncioDAO;
        this.studenteDAO = studenteDAO;
        this.offertaDAO = offertaDAO;
        this.recensioneDAO = recensioneDAO;
        this.transazioneDAO = transazioneDAO;
        this.loggedIn = loggedIn;
    }

    public void openAnnuncioFromOfferta(Offerta offerta) {
        if (offerta == null)
            return;

        try {
            Annuncio annuncio = annuncioDAO.findById(offerta.getIdAnnuncio());
            if (annuncio == null) {
                JOptionPane.showMessageDialog(view,
                        "Annuncio non trovato (forse rimosso).\n",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (homeController.getAnnunciController().openAnnuncioDetailCommon(annuncio)) {
                homeController.setCurrentDetailOrigin(HomeController.DetailOrigin.NOTIFICATION);
                homeController.getAnnunciController().configureBackToNotification();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore apertura annuncio dall'offerta:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshNotificationView() {
        NotificationView notificationView = view.getNotificationView();
        if (notificationView == null || loggedIn == null)
            return;

        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            List<Offerta> ricevute = offertaDAO.findRicevuteInAttesaPerVenditore(loggedIn.getMatricola());
            ArrayList<NotificationView.OffertaNotificationData> ricevuteData = new ArrayList<>();

            for (Offerta o : ricevute) {
                Annuncio a = annuncioDAO.findById(o.getIdAnnuncio());
                Studente offerente = studenteDAO.findByMatricola(o.getMatricolaOfferente());

                String titolo = (a != null) ? a.getTitolo() : "Annuncio #" + o.getIdAnnuncio();
                String controparte = (offerente != null)
                        ? offerente.getNome() + " " + offerente.getCognome() + " (" + offerente.getMatricola() + ")"
                        : o.getMatricolaOfferente();

                String dataStr = (o.getData() != null) ? o.getData().format(fmt) : "";
                String importo = (o.getImportoOfferto() != null)
                        ? o.getImportoOfferto().toPlainString() + " €"
                        : "-";

                String tipoAnn = (a != null) ? a.getTipologia() : "-";

                ricevuteData.add(new NotificationView.OffertaNotificationData(
                        o, titolo, tipoAnn, controparte, dataStr, importo));
            }
            notificationView.setOfferteRicevute(ricevuteData);

            List<Offerta> inviate = offertaDAO.findInviateInAttesaPerOfferente(loggedIn.getMatricola());
            ArrayList<NotificationView.OffertaNotificationData> inviateData = new ArrayList<>();

            for (Offerta o : inviate) {
                Annuncio a = annuncioDAO.findById(o.getIdAnnuncio());
                Studente venditore = (a != null) ? studenteDAO.findByMatricola(a.getMatricolaVenditore()) : null;

                String titolo = (a != null) ? a.getTitolo() : "Annuncio #" + o.getIdAnnuncio();
                String controparte = (venditore != null)
                        ? venditore.getNome() + " " + venditore.getCognome() + " (" + venditore.getMatricola() + ")"
                        : (a != null ? a.getMatricolaVenditore() : "-");

                String dataStr = (o.getData() != null) ? o.getData().format(fmt) : "";
                String importo = (o.getImportoOfferto() != null)
                        ? o.getImportoOfferto().toPlainString() + " €"
                        : "-";

                String tipoAnn = (a != null) ? a.getTipologia() : "-";

                inviateData.add(new NotificationView.OffertaNotificationData(
                        o, titolo, tipoAnn, controparte, dataStr, importo));
            }
            notificationView.setOfferteInviate(inviateData);

            // Offerte ACCETTATE
            List<Offerta> ricevuteAcc = offertaDAO.findRicevutePerVenditoreByStato(loggedIn.getMatricola(),
                    "Accettata");
            ArrayList<NotificationView.OffertaNotificationData> ricevuteAccData = new ArrayList<>();
            for (Offerta o : ricevuteAcc) {
                Annuncio a = annuncioDAO.findById(o.getIdAnnuncio());
                Studente offerente = studenteDAO.findByMatricola(o.getMatricolaOfferente());

                String titolo = (a != null) ? a.getTitolo() : "Annuncio #" + o.getIdAnnuncio();
                String controparte = (offerente != null)
                        ? offerente.getNome() + " " + offerente.getCognome() + " (" + offerente.getMatricola() + ")"
                        : o.getMatricolaOfferente();
                String dataStr = (o.getData() != null) ? o.getData().format(fmt) : "";
                String importo = (o.getImportoOfferto() != null)
                        ? o.getImportoOfferto().toPlainString() + " €"
                        : "-";
                String tipoAnn = (a != null) ? a.getTipologia() : "-";

                ricevuteAccData.add(new NotificationView.OffertaNotificationData(
                        o, titolo, tipoAnn, controparte, dataStr, importo));
            }
            notificationView.setOfferteRicevuteAccettate(ricevuteAccData);

            List<Offerta> inviateAcc = offertaDAO.findInviatePerOfferenteByStato(loggedIn.getMatricola(), "Accettata");
            ArrayList<NotificationView.OffertaNotificationData> inviateAccData = new ArrayList<>();
            for (Offerta o : inviateAcc) {
                Annuncio a = annuncioDAO.findById(o.getIdAnnuncio());
                Studente venditore = (a != null) ? studenteDAO.findByMatricola(a.getMatricolaVenditore()) : null;

                String titolo = (a != null) ? a.getTitolo() : "Annuncio #" + o.getIdAnnuncio();
                String controparte = (venditore != null)
                        ? venditore.getNome() + " " + venditore.getCognome() + " (" + venditore.getMatricola() + ")"
                        : (a != null ? a.getMatricolaVenditore() : "-");
                String dataStr = (o.getData() != null) ? o.getData().format(fmt) : "";
                String importo = (o.getImportoOfferto() != null)
                        ? o.getImportoOfferto().toPlainString() + " €"
                        : "-";
                String tipoAnn = (a != null) ? a.getTipologia() : "-";

                inviateAccData.add(new NotificationView.OffertaNotificationData(
                        o, titolo, tipoAnn, controparte, dataStr, importo));
            }
            notificationView.setOfferteInviateAccettate(inviateAccData);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore durante il caricamento notifiche:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean canLasciaRecensione(Offerta offerta) {
        try {
            if (offerta == null)
                return false;
            Transazione t = transazioneDAO.findByOffertaAccettata(offerta.getId());
            if (t == null)
                return false;
            return !recensioneDAO.existsByTransazioneAndAutore(t.getId(), loggedIn.getMatricola());
        } catch (Exception ex) {
            return false;
        }
    }
}