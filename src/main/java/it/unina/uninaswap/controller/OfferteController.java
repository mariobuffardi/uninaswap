package it.unina.uninaswap.controller;

import java.awt.Window;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import it.unina.uninaswap.dao.AnnuncioDAO;
import it.unina.uninaswap.dao.ConsegnaDAO;
import it.unina.uninaswap.dao.IndirizzoDAO;
import it.unina.uninaswap.dao.OffertaDAO;
import it.unina.uninaswap.dao.RecensioneDAO;
import it.unina.uninaswap.dao.StudenteDAO;
import it.unina.uninaswap.dao.TransazioneDAO;
import it.unina.uninaswap.model.entity.Annuncio;
import it.unina.uninaswap.model.entity.Indirizzo;
import it.unina.uninaswap.model.entity.Offerta;
import it.unina.uninaswap.model.entity.Recensione;
import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.model.entity.Transazione;
import it.unina.uninaswap.model.enums.StatoOfferta;
import it.unina.uninaswap.view.AccettaOffertaDialog;
import it.unina.uninaswap.view.AnnunciMainView;
import it.unina.uninaswap.view.OffertaEditDialog;
import it.unina.uninaswap.view.RecensioneDialog;

public class OfferteController {

    private final HomeController homeController;
    private final AnnunciMainView view;
    private final AnnuncioDAO annuncioDAO;
    private final OffertaDAO offertaDAO;
    private final TransazioneDAO transazioneDAO;
    private final ConsegnaDAO consegnaDAO;
    private final IndirizzoDAO indirizzoDAO;
    private final StudenteDAO studenteDAO;
    private final RecensioneDAO recensioneDAO;
    private final Studente loggedIn;

    public OfferteController(HomeController homeController, AnnunciMainView view, AnnuncioDAO annuncioDAO, OffertaDAO offertaDAO, TransazioneDAO transazioneDAO, ConsegnaDAO consegnaDAO, IndirizzoDAO indirizzoDAO, StudenteDAO studenteDAO, RecensioneDAO recensioneDAO, Studente loggedIn) {
        this.homeController = homeController;
        this.view = view;
        this.annuncioDAO = annuncioDAO;
        this.offertaDAO = offertaDAO;
        this.transazioneDAO = transazioneDAO;
        this.consegnaDAO = consegnaDAO;
        this.indirizzoDAO = indirizzoDAO;
        this.studenteDAO = studenteDAO;
        this.recensioneDAO = recensioneDAO;
        this.loggedIn = loggedIn;
    }

    public void handleAnnuncioOffertaAction(Annuncio annuncio, String actionName) {
        if (annuncio == null || annuncio.getId() == null)
            return;

        if (annuncio.getMatricolaVenditore() != null &&
                annuncio.getMatricolaVenditore().equals(loggedIn.getMatricola())) {
            JOptionPane.showMessageDialog(view,
                    "Non puoi fare offerte ai tuoi annunci.",
                    "Operazione non consentita",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            switch (actionName) {
                case "ACQUISTA" -> {
                    if (annuncio.getPrezzo() == null) {
                        JOptionPane.showMessageDialog(view,
                                "Prezzo non disponibile per questo annuncio.",
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int res = JOptionPane.showConfirmDialog(
                            view,
                            "Confermi l'acquisto a prezzo pieno (" + annuncio.getPrezzo() + " €)?",
                            "Conferma acquisto",
                            JOptionPane.YES_NO_OPTION);
                    if (res != JOptionPane.YES_OPTION)
                        return;

                    Offerta o = buildNewOffertaForAnnuncio(annuncio);
                    o.setImportoOfferto(annuncio.getPrezzo());
                    offertaDAO.insert(o);

                    JOptionPane.showMessageDialog(view, "Acquisto inviato come offerta!", "OK",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                case "FAI_OFFERTA", "RICHIEDI_REGALO", "PROPONI_SCAMBIO" -> {
                    Offerta bozza = buildNewOffertaForAnnuncio(annuncio);

                    OffertaEditDialog dialog = new OffertaEditDialog(view, bozza, annuncio);
                    dialog.setVisible(true);
                    if (!dialog.isConfirmed())
                        return;

                    // Validazione e costruzione Offerta
                    Offerta edited = validateAndBuildOfferta(dialog);
                    if (edited == null)
                        return;

                    offertaDAO.insert(edited);

                    JOptionPane.showMessageDialog(view, "Offerta inviata!", "OK", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            homeController.getNotificationController().refreshNotificationView();
            homeController.getReportController().refreshReportView();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view,
                    "Operazione non riuscita:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Offerta buildNewOffertaForAnnuncio(Annuncio annuncio) {
        Offerta o = new Offerta();
        o.setId(0);
        o.setData(LocalDate.now());
        o.setStato(StatoOfferta.In_Attesa);
        o.setIdAnnuncio(annuncio.getId());
        o.setMatricolaOfferente(loggedIn.getMatricola());
        return o;
    }

    public void handleEditOfferta(Offerta offertaBase) {
        if (offertaBase == null)
            return;

        try {
            Offerta offerta = offertaDAO.findById(offertaBase.getId());
            if (offerta == null) {
                JOptionPane.showMessageDialog(view,
                        "Offerta non trovata.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!"In_Attesa".equals(offerta.getStato().name())) {
                JOptionPane.showMessageDialog(view,
                        "Puoi modificare solo offerte in stato 'In_Attesa'.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                homeController.getNotificationController().refreshNotificationView();
                return;
            }

            Annuncio annuncio = annuncioDAO.findById(offerta.getIdAnnuncio());
            if (annuncio == null) {
                JOptionPane.showMessageDialog(view,
                        "Annuncio non trovato.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            OffertaEditDialog dialog = new OffertaEditDialog(view, offerta, annuncio);
            dialog.setVisible(true);

            if (!dialog.isConfirmed())
                return;

            // Validazione e costruzione Offerta 
            Offerta edited = validateAndBuildOfferta(dialog);
            if (edited == null)
                return;

            offertaDAO.updateContenuto(edited);

            JOptionPane.showMessageDialog(view,
                    "Offerta modificata con successo.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);

            homeController.getNotificationController().refreshNotificationView();
            homeController.getReportController().refreshReportView();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore durante la modifica dell'offerta:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void handleAcceptOffer(Offerta offertaBase) {
        if (offertaBase == null)
            return;

        try {
            Offerta offerta = offertaDAO.findById(offertaBase.getId());
            if (offerta == null) {
                JOptionPane.showMessageDialog(view,
                        "Offerta non trovata.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Annuncio annuncio = annuncioDAO.findById(offerta.getIdAnnuncio());
            if (annuncio == null) {
                JOptionPane.showMessageDialog(view,
                        "Annuncio non trovato.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Studente acquirente = studenteDAO.findByMatricola(offerta.getMatricolaOfferente());
            if (acquirente == null) {
                JOptionPane.showMessageDialog(view,
                        "Acquirente non trovato.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Indirizzo> indirizziAcquirente = indirizzoDAO.findByStudente(acquirente.getMatricola());

            AccettaOffertaDialog dialog = new AccettaOffertaDialog(view, annuncio, acquirente, indirizziAcquirente);
            dialog.setVisible(true);

            AccettaOffertaDialog.Result scelta = dialog.getResult();
            if (scelta == null)
                return;

            offertaDAO.updateStato(offerta.getId(), "Accettata");

            Transazione trans = transazioneDAO.findByOffertaAccettata(offerta.getId());
            if (trans == null) {
                JOptionPane.showMessageDialog(view,
                        "Transazione non trovata dopo l'accettazione (controlla trigger).",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                homeController.getNotificationController().refreshNotificationView();
                homeController.getReportController().refreshReportView();
                return;
            }

            String note = null;
            if (scelta.isSpedizione()) {
                Integer idInd = scelta.getIdIndirizzo();
                if (idInd == null) {
                    JOptionPane.showMessageDialog(view,
                            "Seleziona un indirizzo per la spedizione.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                consegnaDAO.creaConsegnaSpedizione(trans.getId(), idInd, note);

            } else {
                LocalDate dataIncontro = scelta.getDataIncontro();
                consegnaDAO.creaConsegnaInUni(
                        trans.getId(),
                        dataIncontro,
                        scelta.getSedeUniversita(),
                        scelta.getFasciaOraria(),
                        note);
            }

            JOptionPane.showMessageDialog(view,
                    "Offerta accettata e consegna registrata.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);

            homeController.getNotificationController().refreshNotificationView();
            homeController.getProfiloController().refreshProfileView();
            homeController.getReportController().refreshReportView();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore durante l'accettazione dell'offerta:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void handleLasciaRecensione(Offerta offertaBase) {
        if (offertaBase == null || loggedIn == null)
            return;

        try {
            Transazione t = transazioneDAO.findByOffertaAccettata(offertaBase.getId());
            if (t == null) {
                JOptionPane.showMessageDialog(view,
                        "Transazione non trovata per l'offerta accettata.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (recensioneDAO.existsByTransazioneAndAutore(t.getId(), loggedIn.getMatricola())) {
                JOptionPane.showMessageDialog(view,
                        "Hai già inviato una recensione per questa transazione.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                homeController.getNotificationController().refreshNotificationView();
                return;
            }

            Window owner = SwingUtilities.getWindowAncestor(view);
            RecensioneDialog dialog = new RecensioneDialog(owner, "Lascia una recensione");
            dialog.setVisible(true);

            if (!dialog.isConfirmed())
                return;

            Recensione r = new Recensione();
            r.setTitolo(dialog.getTitolo().trim());
            r.setCorpo(dialog.getCorpo().trim());
            r.setValutazione(dialog.getValutazione());
            r.setIdTransazione(t.getId());
            r.setAutore(loggedIn.getMatricola());
            r.setRecensito(t.getMatricolaVenditore());

            recensioneDAO.insert(r);

            JOptionPane.showMessageDialog(view,
                    "Recensione inviata.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);

            homeController.getNotificationController().refreshNotificationView();
            homeController.getProfiloController().refreshProfileView();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore durante l'invio della recensione:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void rifiutaOrRitiraOfferta(Offerta offerta) {
        try {
            offertaDAO.updateStato(offerta.getId(), "Rifiutata");
            homeController.getNotificationController().refreshNotificationView();
            homeController.getReportController().refreshReportView();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Errore durante l'operazione sull'offerta:\n" + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }


    private Offerta validateAndBuildOfferta(OffertaEditDialog dialog) {
        Offerta originalOfferta = dialog.getOriginalOfferta();
        Annuncio annuncio = dialog.getAnnuncio();

        if (originalOfferta == null) {
            dialog.showError("Errore interno: offerta non inizializzata.");
            return null;
        }

        String tipo = (annuncio != null) ? annuncio.getTipologia() : null;

        boolean vendita = "Vendita".equalsIgnoreCase(tipo);
        boolean scambio = "Scambio".equalsIgnoreCase(tipo);
        boolean regalo  = "Regalo".equalsIgnoreCase(tipo);

        String sImporto = dialog.getImportoText();
        String msg = dialog.getMessaggio();
        String obj = dialog.getOggettoOfferto();

        // Validazione in base alla tipologia
        if (vendita) {
            if (sImporto.isBlank()) {
                dialog.showError("Per una vendita l'importo è obbligatorio.");
                return null;
            }
            try {
                BigDecimal nuovoImporto = new BigDecimal(sImporto);
                if (nuovoImporto.compareTo(BigDecimal.ZERO) < 0) {
                    dialog.showError("L'importo non può essere negativo.");
                    return null;
                }
            } catch (NumberFormatException ex) {
                dialog.showError("Importo non valido. Usa un numero, es. 10 o 10.50");
                return null;
            }
            // sicurezza coerente con DB
            obj = "";
        }

        if (scambio) {
            if (obj.isBlank()) {
                dialog.showError("Per uno scambio l'oggetto offerto è obbligatorio.");
                return null;
            }
            // sicurezza coerente con DB
            sImporto = "";
        }

        if (regalo) {
            if (msg.isBlank()) {
                dialog.showError("Per un regalo il messaggio è obbligatorio.");
                return null;
            }
            // sicurezza coerente con DB
            sImporto = "";
            obj = "";
        }

        // Costruzione Offerta
        Offerta o = new Offerta();
        o.setId(originalOfferta.getId());
        o.setData(originalOfferta.getData());
        o.setStato(originalOfferta.getStato());
        o.setIdAnnuncio(originalOfferta.getIdAnnuncio());
        o.setMatricolaOfferente(originalOfferta.getMatricolaOfferente());

        // importo: se vuoto -> null
        if (!sImporto.isBlank()) {
            try {
                BigDecimal bd = new BigDecimal(sImporto);
                if (bd.compareTo(BigDecimal.ZERO) < 0) {
                    dialog.showError("L'importo non può essere negativo.");
                    return null;
                }
                o.setImportoOfferto(bd);
            } catch (NumberFormatException ex) {
                dialog.showError("Importo non valido. Usa un numero, es. 10 o 10.50");
                return null;
            }
        } else {
            o.setImportoOfferto(null);
        }

        o.setMessaggio(msg.isBlank() ? null : msg);
        o.setOggettoOfferto(obj.isBlank() ? null : obj);

        return o;
    }
}