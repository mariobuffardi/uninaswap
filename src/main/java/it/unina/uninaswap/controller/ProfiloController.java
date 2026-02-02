package it.unina.uninaswap.controller;

import it.unina.uninaswap.dao.*;
import it.unina.uninaswap.model.entity.Annuncio;
import it.unina.uninaswap.model.entity.Indirizzo;
import it.unina.uninaswap.model.entity.Recensione;
import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.model.entity.Transazione;
import it.unina.uninaswap.util.ImageUtil;
import it.unina.uninaswap.view.AnnunciMainView;
import it.unina.uninaswap.view.ProfileView;

import javax.swing.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProfiloController {

    private final HomeController homeController;
    private final AnnunciMainView view;
    private final AnnuncioDAO annuncioDAO;
    private final StudenteDAO studenteDAO;
    private final RecensioneDAO recensioneDAO;
    private final TransazioneDAO transazioneDAO;
    private final FotoDAO fotoDAO;
    private final IndirizzoDAO indirizzoDAO;
    private final Studente loggedIn;

    public ProfiloController(HomeController homeController, AnnunciMainView view, AnnuncioDAO annuncioDAO, StudenteDAO studenteDAO, RecensioneDAO recensioneDAO, TransazioneDAO transazioneDAO, FotoDAO fotoDAO, IndirizzoDAO indirizzoDAO, Studente loggedIn) {
        this.homeController = homeController;
        this.view = view;
        this.annuncioDAO = annuncioDAO;
        this.studenteDAO = studenteDAO;
        this.recensioneDAO = recensioneDAO;
        this.transazioneDAO = transazioneDAO;
        this.fotoDAO = fotoDAO;
        this.indirizzoDAO = indirizzoDAO;
        this.loggedIn = loggedIn;
    }

    public void refreshProfileView() {
        ProfileView profileView = view.getProfileView();
        profileView.setStudente(loggedIn);

        try {
            List<Indirizzo> indirizzi = indirizzoDAO.findByStudente(loggedIn.getMatricola());
            String indirizzoTxt = (indirizzi != null && !indirizzi.isEmpty())
                    ? (indirizzi.get(0).getVia() + " " + indirizzi.get(0).getCivico() + ", " +
                    indirizzi.get(0).getCap() + " " + indirizzi.get(0).getCitta())
                    : null;
            profileView.setIndirizzoInfo(indirizzoTxt);

            List<Annuncio> miei = annuncioDAO.findByStudente(loggedIn.getMatricola());
            if (miei != null) {
                for (Annuncio a : miei) {
                    try {
                        var foto = fotoDAO.findPrincipaleOrFirstByAnnuncio(a.getId());
                        if (foto != null) {
                            String path = foto.getPath();
                            if (path != null && !path.isBlank()) {
                                path = ImageUtil.normalizeAnnuncioFotoPath(path);
                                a.setFotoPrincipalePath(path);
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
            profileView.setMyAnnunci(miei);

            List<Recensione> recs = recensioneDAO.findRicevuteByStudente(loggedIn.getMatricola());
            List<ProfileView.RecensioneCardData> cardData = new ArrayList<>();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Recensione r : recs) {
                Transazione trans = transazioneDAO.findById(r.getIdTransazione());
                String dataStr = (trans != null && trans.getData() != null) ? trans.getData().format(fmt) : "-";

                Studente autore = studenteDAO.findByMatricola(r.getAutore());
                String nomeAut = (autore != null) ? autore.getNome() : "N/D";
                String cognomeAut = (autore != null) ? autore.getCognome() : "";
                String matAut = (autore != null) ? autore.getMatricola() : r.getAutore();

                cardData.add(new ProfileView.RecensioneCardData(
                        r.getTitolo(), r.getCorpo(), r.getValutazione(),
                        dataStr, nomeAut, cognomeAut, matAut));
            }

            profileView.setRecensioni(cardData);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore durante il caricamento del profilo:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshVenditoreProfileView(Studente venditore) {
        if (venditore == null || view.getVenditoreProfileView() == null)
            return;

        try {
            view.getVenditoreProfileView().setVenditore(venditore);

            List<Annuncio> list = annuncioDAO.findByStudente(venditore.getMatricola());
            if (list != null) {
                list.removeIf(a -> a == null || a.isConcluso());
                for (Annuncio a : list) {
                    try {
                        var foto = fotoDAO.findPrincipaleOrFirstByAnnuncio(a.getId());
                        if (foto != null) {
                            String path = foto.getPath();
                            if (path != null && !path.isBlank()) {
                                path = ImageUtil.normalizeAnnuncioFotoPath(path);
                                a.setFotoPrincipalePath(path);
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
            view.getVenditoreProfileView().setAnnunciVenditore(list);

            try {
                List<Recensione> recs = recensioneDAO.findRicevuteByStudente(venditore.getMatricola());
                List<ProfileView.RecensioneCardData> cardData = new ArrayList<>();
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                for (Recensione r : recs) {
                    Transazione trans = transazioneDAO.findById(r.getIdTransazione());
                    String dataStr = (trans != null && trans.getData() != null) ? trans.getData().format(fmt) : "-";

                    Studente autore = studenteDAO.findByMatricola(r.getAutore());
                    String nomeAut = (autore != null) ? autore.getNome() : "N/D";
                    String cognomeAut = (autore != null) ? autore.getCognome() : "";
                    String matAut = (autore != null) ? autore.getMatricola() : r.getAutore();

                    cardData.add(new ProfileView.RecensioneCardData(
                            r.getTitolo(), r.getCorpo(), r.getValutazione(),
                            dataStr, nomeAut, cognomeAut, matAut));
                }
                view.getVenditoreProfileView().setRecensioniVenditore(cardData);
            } catch (Exception e) {
                e.printStackTrace(); 
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore caricamento profilo venditore:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void handleBackFromVenditoreProfile() {
        if (homeController.getAnnuncioIdBeforeVenditoreProfile() == null || homeController.getDetailOriginBeforeVenditoreProfile() == null) {
            view.showAnnunciView();
            view.getFilterPanel().setVisible(false);
            return;
        }

        try {
            Annuncio a = annuncioDAO.findById(homeController.getAnnuncioIdBeforeVenditoreProfile());
            if (a == null) {
                view.showAnnunciView();
                view.getFilterPanel().setVisible(false);
                return;
            }


            homeController.getAnnunciController().openAnnuncioDetailCommon(a);

            switch (homeController.getDetailOriginBeforeVenditoreProfile()) {
                case HOME -> homeController.getAnnunciController().configureBackToHome();
                case PROFILE -> homeController.getAnnunciController().configureBackToProfile();
                case NOTIFICATION -> homeController.getAnnunciController().configureBackToNotification();
                case VENDITORE_PROFILE -> homeController.getAnnunciController().configureBackToVenditoreProfile();
            }
            homeController.setCurrentDetailOrigin(homeController.getDetailOriginBeforeVenditoreProfile());

            homeController.setAnnuncioIdBeforeVenditoreProfile(null);
            homeController.setDetailOriginBeforeVenditoreProfile(null);

        } catch (Exception ex) {
            ex.printStackTrace();
            view.showAnnunciView();
            view.getFilterPanel().setVisible(false);
        }
    }
}