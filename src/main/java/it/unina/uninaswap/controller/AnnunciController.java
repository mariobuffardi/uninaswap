package it.unina.uninaswap.controller;

import it.unina.uninaswap.dao.AnnuncioDAO;
import it.unina.uninaswap.dao.FotoDAO;
import it.unina.uninaswap.dao.StudenteDAO;
import it.unina.uninaswap.model.entity.Annuncio;
import it.unina.uninaswap.model.entity.Foto;
import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.util.ImageUtil;
import it.unina.uninaswap.view.AnnunciMainView;
import it.unina.uninaswap.view.AnnuncioCreateDialog;
import it.unina.uninaswap.view.AnnuncioDetailView;
import it.unina.uninaswap.view.AnnuncioEditDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AnnunciController {

    private final HomeController homeController;
    private final AnnunciMainView view;
    private final AnnuncioDAO annuncioDAO;
    private final StudenteDAO studenteDAO;
    private final FotoDAO fotoDAO;
    private final Studente loggedIn;

    public AnnunciController(HomeController homeController, AnnunciMainView view, AnnuncioDAO annuncioDAO, StudenteDAO studenteDAO, FotoDAO fotoDAO, Studente loggedIn) {
        this.homeController = homeController;
        this.view = view;
        this.annuncioDAO = annuncioDAO;
        this.studenteDAO = studenteDAO;
        this.fotoDAO = fotoDAO;
        this.loggedIn = loggedIn;
    }

    public void refreshAnnunciFromFilters() {
        try {
            String search = view.getFilterPanel().getSearchText();
            if (search != null && search.isBlank())
                search = null;

            String categoria = view.getFilterPanel().getSelectedCategoria();
            String tipologia = view.getFilterPanel().getSelectedTipologia();

            BigDecimal prezzoMin = parseBigDecimalOrNull(view.getFilterPanel().getPrezzoMinText());
            BigDecimal prezzoMax = parseBigDecimalOrNull(view.getFilterPanel().getPrezzoMaxText());

            if (prezzoMin != null && prezzoMax != null && prezzoMin.compareTo(prezzoMax) > 0) {
                JOptionPane.showMessageDialog(view,
                        "Prezzo min non può essere maggiore di Prezzo max.",
                        "Errore filtri",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Boolean offreSpedizione = view.getFilterPanel().isSpedizioneSelected() ? Boolean.TRUE : null;
            Boolean offreInUni = view.getFilterPanel().isInUniSelected() ? Boolean.TRUE : null;

            List<Annuncio> annunci = annuncioDAO.findAttiviEsclusoStudente(
                    loggedIn.getMatricola(),
                    search,
                    categoria,
                    tipologia,
                    prezzoMin,
                    prezzoMax,
                    offreSpedizione,
                    offreInUni);
            // Valorizza fotoPrincipalePath
            if (annunci != null) {
                for (Annuncio a : annunci) {
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
            view.getAnnunciListView().showAnnunci(annunci);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore durante il caricamento annunci:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private BigDecimal parseBigDecimalOrNull(String s) {
        if (s == null)
            return null;
        String t = s.trim();
        if (t.isEmpty())
            return null;
        try {
            return new BigDecimal(t);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean openAnnuncioDetailCommon(Annuncio annuncioFromList) {
        if (annuncioFromList == null || annuncioFromList.getId() == null)
            return false;

        try {
            Annuncio full = annuncioDAO.findById(annuncioFromList.getId());
            if (full == null) {
                JOptionPane.showMessageDialog(view,
                        "Annuncio non trovato (forse rimosso).",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            Studente venditore = studenteDAO.findByMatricola(full.getMatricolaVenditore());

            List<Foto> fotoList = fotoDAO.findByAnnuncio(full.getId());
            if (fotoList == null)
                fotoList = Collections.emptyList();
            fotoList.sort(Comparator.comparing(Foto::isPrincipale).reversed());

            List<ImageIcon> icons = new ArrayList<>();
            for (Foto f : fotoList) {
                if (f == null || f.getPath() == null)
                    continue;
                ImageIcon ic = ImageUtil.annuncioImageFromDbPath(f.getPath());
                if (ic == null) {
                    continue;
                }
                if (ic.getIconWidth() > 0 && ic.getIconHeight() > 0) {
                    Image scaled = ic.getImage().getScaledInstance(320, -1, Image.SCALE_SMOOTH);
                    ic = new ImageIcon(scaled);
                }
                icons.add(ic);
            }

            view.getAnnuncioDetailView().setData(full, venditore, icons);
            view.getAnnuncioDetailView().updateAzioniForLoggedIn(loggedIn.getMatricola());

            homeController.setLastOpenedAnnuncio(full);
            homeController.setLastOpenedVenditore(venditore);

            view.showAnnuncioDetailView();
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore apertura dettaglio annuncio:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void openAnnuncioDetailFromHome(Annuncio annuncioFromList) {
        if (openAnnuncioDetailCommon(annuncioFromList)) {
            homeController.setCurrentDetailOrigin(HomeController.DetailOrigin.HOME);
            configureBackToHome();
        }
    }

    public void openAnnuncioDetailFromProfile(Annuncio annuncioFromList) {
        if (openAnnuncioDetailCommon(annuncioFromList)) {
            homeController.setCurrentDetailOrigin(HomeController.DetailOrigin.PROFILE);
            configureBackToProfile();
        }
    }

    public void openAnnuncioDetailFromVenditoreProfile(Annuncio annuncioFromList) {
        if (openAnnuncioDetailCommon(annuncioFromList)) {
            homeController.setCurrentDetailOrigin(HomeController.DetailOrigin.VENDITORE_PROFILE);
            configureBackToVenditoreProfile();
        }
    }

    public void openAnnuncioEdit(Annuncio annuncioFromProfile) {
        if (annuncioFromProfile == null || annuncioFromProfile.getId() == null)
            return;

        try {
            Annuncio full = annuncioDAO.findById(annuncioFromProfile.getId());
            if (full == null) {
                JOptionPane.showMessageDialog(view,
                        "Annuncio non trovato (forse rimosso).",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Foto> fotoList = fotoDAO.findByAnnuncio(full.getId());

            AnnuncioEditDialog dialog = new AnnuncioEditDialog(view, full, fotoList);
            dialog.setVisible(true);

            if (!dialog.isConfirmed())
                return;

            Annuncio edited = dialog.getEditedAnnuncio();
            List<Foto> editedFotos = dialog.getEditedFotoList();

            annuncioDAO.update(edited);

            fotoDAO.deleteByAnnuncio(edited.getId());
            if (editedFotos != null) {
                editedFotos.sort(java.util.Comparator.comparing(Foto::isPrincipale).reversed());
                boolean foundPrincipale = false;
                for (Foto x : editedFotos) {
                    if (!x.isPrincipale())
                        continue;
                    if (!foundPrincipale)
                        foundPrincipale = true;
                    else
                        x.setPrincipale(false);
                }
                if (!editedFotos.isEmpty() && editedFotos.stream().noneMatch(Foto::isPrincipale)) {
                    editedFotos.get(0).setPrincipale(true);
                }
            }

            for (Foto f : editedFotos) {
                f.setIdAnnuncio(edited.getId());
                fotoDAO.insert(f);
            }

            homeController.getProfiloController().refreshProfileView();
            refreshAnnunciFromFilters();

            JOptionPane.showMessageDialog(view,
                    "Annuncio aggiornato con successo.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore durante la modifica annuncio:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void openCreateAnnuncioDialog() {
        try {
            AnnuncioCreateDialog dialog = new AnnuncioCreateDialog(view, loggedIn);
            dialog.setVisible(true);

            if (!dialog.isConfirmed())
                return;

            Annuncio created = dialog.getCreatedAnnuncio();
            if (created == null)
                return;

            annuncioDAO.insert(created); // created ha già l'id valorizzato

            // SALVATAGGIO FOTO
            List<Foto> fotos = dialog.getCreatedFotoList();
            if (fotos != null && !fotos.isEmpty()) {
                // IMPORTANTE: con trigger DB che assegna la principale se manca,
                // inseriamo PRIMA la foto principale (ordinamento) per evitare violazione
                // unicità.
                fotos.sort(java.util.Comparator.comparing(Foto::isPrincipale).reversed());
                // Safety: al massimo una principale
                boolean foundPrincipale = false;
                for (Foto x : fotos) {
                    if (!x.isPrincipale())
                        continue;
                    if (!foundPrincipale)
                        foundPrincipale = true;
                    else
                        x.setPrincipale(false);
                }
                if (!fotos.isEmpty() && fotos.stream().noneMatch(Foto::isPrincipale)) {
                    fotos.get(0).setPrincipale(true);
                }

                for (Foto f : fotos) {
                    f.setIdAnnuncio(created.getId());
                    fotoDAO.insert(f);
                }
            }

            homeController.getProfiloController().refreshProfileView();
            refreshAnnunciFromFilters();

            JOptionPane.showMessageDialog(view,
                    "Annuncio creato con successo.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore durante la creazione annuncio:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void configureBackToHome() {
        AnnuncioDetailView detailView = view.getAnnuncioDetailView();
        JButton btnBack = detailView.getBtnBack();

        for (ActionListener l : btnBack.getActionListeners()) {
            btnBack.removeActionListener(l);
        }

        btnBack.addActionListener(e -> {
            view.showAnnunciView();
            view.getFilterPanel().setVisible(false);
        });
    }

    public void configureBackToProfile() {
        AnnuncioDetailView detailView = view.getAnnuncioDetailView();
        JButton btnBack = detailView.getBtnBack();

        for (ActionListener l : btnBack.getActionListeners()) {
            btnBack.removeActionListener(l);
        }

        btnBack.addActionListener(e -> {
            homeController.getProfiloController().refreshProfileView();
            view.showProfileView();
        });
    }

    public void configureBackToNotification() {
        AnnuncioDetailView detailView = view.getAnnuncioDetailView();
        JButton btnBack = detailView.getBtnBack();

        for (ActionListener l : btnBack.getActionListeners()) {
            btnBack.removeActionListener(l);
        }

        btnBack.addActionListener(e -> {
            view.showNotificationView();
            homeController.getNotificationController().refreshNotificationView();
        });
    }

    public void configureBackToVenditoreProfile() {
        AnnuncioDetailView detailView = view.getAnnuncioDetailView();
        JButton btnBack = detailView.getBtnBack();

        for (ActionListener l : btnBack.getActionListeners()) {
            btnBack.removeActionListener(l);
        }

        btnBack.addActionListener(e -> view.showVenditoreProfileView());
    }

    public void deleteAnnuncio(Annuncio annuncio) {
        try {
            annuncioDAO.delete(annuncio.getId(), loggedIn.getMatricola());
            homeController.getProfiloController().refreshProfileView();
            refreshAnnunciFromFilters();
            JOptionPane.showMessageDialog(view, "Annuncio eliminato.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Errore eliminazione annuncio:\n" + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
}