package it.unina.uninaswap.controller;

import javax.swing.JOptionPane;

import it.unina.uninaswap.dao.AnnuncioDAO;
import it.unina.uninaswap.dao.ConsegnaDAO;
import it.unina.uninaswap.dao.FotoDAO;
import it.unina.uninaswap.dao.IndirizzoDAO;
import it.unina.uninaswap.dao.OffertaDAO;
import it.unina.uninaswap.dao.RecensioneDAO;
import it.unina.uninaswap.dao.StudenteDAO;
import it.unina.uninaswap.dao.TransazioneDAO;
import it.unina.uninaswap.dao.impl.AnnuncioDAOImpl;
import it.unina.uninaswap.dao.impl.ConsegnaDAOImpl;
import it.unina.uninaswap.dao.impl.FotoDAOImpl;
import it.unina.uninaswap.dao.impl.IndirizzoDAOImpl;
import it.unina.uninaswap.dao.impl.OffertaDAOImpl;
import it.unina.uninaswap.dao.impl.RecensioneDAOImpl;
import it.unina.uninaswap.dao.impl.StudenteDAOImpl;
import it.unina.uninaswap.dao.impl.TransazioneDAOImpl;
import it.unina.uninaswap.model.entity.Annuncio;
import it.unina.uninaswap.model.entity.Offerta;
import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.view.AnnunciListView;
import it.unina.uninaswap.view.AnnunciMainView;
import it.unina.uninaswap.view.LoginView;
import it.unina.uninaswap.view.NotificationView;
import it.unina.uninaswap.view.VenditoreProfileView;

public class HomeController {

    private final AnnunciMainView view;

    private final AnnuncioDAO annuncioDAO = new AnnuncioDAOImpl();
    private final StudenteDAO studenteDAO = new StudenteDAOImpl();
    private final RecensioneDAO recensioneDAO = new RecensioneDAOImpl();
    private final TransazioneDAO transazioneDAO = new TransazioneDAOImpl();
    private final FotoDAO fotoDAO = new FotoDAOImpl();
    private final OffertaDAO offertaDAO = new OffertaDAOImpl();
    private final ConsegnaDAO consegnaDAO = new ConsegnaDAOImpl();
    private final IndirizzoDAO indirizzoDAO = new IndirizzoDAOImpl();

    private final Studente loggedIn;

    private Annuncio lastOpenedAnnuncio;
    private Studente lastOpenedVenditore;

    public enum DetailOrigin {
        HOME, PROFILE, NOTIFICATION, VENDITORE_PROFILE
    }

    private DetailOrigin currentDetailOrigin = DetailOrigin.HOME;

    private Integer annuncioIdBeforeVenditoreProfile = null;
    private DetailOrigin detailOriginBeforeVenditoreProfile = null;

    private final AnnunciController annunciController;
    private final OfferteController offerteController;
    private final ProfiloController profiloController;
    private final SettingsController settingsController;
    private final ReportController reportController;
    private final NotificationController notificationController;

    public HomeController(AnnunciMainView view, Studente s) {
        this.view = view;
        this.loggedIn = s;

        this.annunciController = new AnnunciController(this, view, annuncioDAO, studenteDAO, fotoDAO, loggedIn);
        this.offerteController = new OfferteController(this, view, annuncioDAO, offertaDAO, transazioneDAO, consegnaDAO, indirizzoDAO, studenteDAO, recensioneDAO, loggedIn);
        this.profiloController = new ProfiloController(this, view, annuncioDAO, studenteDAO, recensioneDAO, transazioneDAO, fotoDAO, indirizzoDAO, loggedIn);
        this.settingsController = new SettingsController(this, view, studenteDAO, loggedIn);
        this.reportController = new ReportController(view, offertaDAO, transazioneDAO, loggedIn);
        this.notificationController = new NotificationController(this, view, annuncioDAO, studenteDAO, offertaDAO, recensioneDAO, transazioneDAO, loggedIn);

        initDefaultState();
        initListeners();
        annunciController.refreshAnnunciFromFilters();
    }

    private void initDefaultState() {
        view.showAnnunciView();
        view.getMenuBarPanel().setVisible(false);
        view.getFilterPanel().setVisible(false);
    }

    private void initListeners() {
        // TOP BAR
        view.addHamburgerMenuListener(e -> view.toggleMenuBar());
        view.addFilterButtonListener(e -> view.toggleFilterPanel());

        // MENU LATERALE
        view.getMenuBarPanel().getHomeButton().addActionListener(e -> {
            view.showAnnunciView();
            view.getFilterPanel().setVisible(false);
            annunciController.refreshAnnunciFromFilters();
        });

        view.getMenuBarPanel().getProfileButton().addActionListener(e -> {
            profiloController.refreshProfileView();
            view.showProfileView();
        });

        view.getMenuBarPanel().getReportButton().addActionListener(e -> {
            reportController.refreshReportView();
            view.showReportView();
        });

        view.getMenuBarPanel().getNotificationButton().addActionListener(e -> {
            view.showNotificationView();
            notificationController.refreshNotificationView();
        });

        view.getMenuBarPanel().getSettingsButton().addActionListener(e -> settingsController.openSettingsDialog());
        view.getMenuBarPanel().getAddButton().addActionListener(e -> annunciController.openCreateAnnuncioDialog());
        view.getMenuBarPanel().getLogoutButton().addActionListener(e -> handleLogout());

        // FILTER PANEL: CERCA
        view.getFilterPanel().getBtnCerca().addActionListener(e -> {
            annunciController.refreshAnnunciFromFilters();
            view.getFilterPanel().setVisible(false);
            view.getFilterPanel().getParent().revalidate();
            view.getFilterPanel().getParent().repaint();
        });

        // ANNUNCI LIST VIEW (HOME)
        AnnunciListView listView = view.getAnnunciListView();
        if (listView != null) {
            listView.setAnnuncioClickListener(annunciController::openAnnuncioDetailFromHome);
            listView.setAnnuncioOffertaListener((annuncio, action) -> offerteController.handleAnnuncioOffertaAction(annuncio, action.name()));
        }

        // ANNUNCIO DETAIL VIEW
        view.getAnnuncioDetailView().setAnnuncioOffertaListener((annuncio, action) -> offerteController.handleAnnuncioOffertaAction(annuncio, action.name()));
        view.getAnnuncioDetailView().getBtnVediProfiloVenditore().addActionListener(e -> {
            if (lastOpenedVenditore == null) {
                JOptionPane.showMessageDialog(view, "Venditore non disponibile.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (loggedIn != null && lastOpenedVenditore.getMatricola() != null && lastOpenedVenditore.getMatricola().equals(loggedIn.getMatricola())) {
                profiloController.refreshProfileView();
                view.showProfileView();
                return;
            }
            annuncioIdBeforeVenditoreProfile = (lastOpenedAnnuncio != null) ? lastOpenedAnnuncio.getId() : null;
            detailOriginBeforeVenditoreProfile = currentDetailOrigin;
            profiloController.refreshVenditoreProfileView(lastOpenedVenditore);
            view.showVenditoreProfileView();
        });

        // PROFILE VIEW
        view.getProfileView().setAnnuncioClickListener(annunciController::openAnnuncioDetailFromProfile);
        view.getProfileView().setAnnuncioEditListener(annunciController::openAnnuncioEdit);
        view.getProfileView().setAnnuncioDeleteListener(annunciController::deleteAnnuncio);

        // VENDITORE PROFILE VIEW
        VenditoreProfileView vendView = view.getVenditoreProfileView();
        if (vendView != null) {
            vendView.getBtnBack().addActionListener(e -> profiloController.handleBackFromVenditoreProfile());
            vendView.setAnnuncioClickListener(annunciController::openAnnuncioDetailFromVenditoreProfile);
        }

        // NOTIFICATION VIEW
        NotificationView notificationView = view.getNotificationView();
        if (notificationView != null) {
            notificationView.setOffertaRicevutaListener(new NotificationView.OffertaRicevutaListener() {
                @Override
                public void onAccetta(Offerta offerta) {
                    offerteController.handleAcceptOffer(offerta);
                }

                @Override
                public void onRifiuta(Offerta offerta) {
                    offerteController.rifiutaOrRitiraOfferta(offerta);
                }

                @Override
                public void onApriAnnuncio(Offerta offerta) {
                    notificationController.openAnnuncioFromOfferta(offerta);
                }
            });

            notificationView.setOffertaInviataListener(new NotificationView.OffertaInviataListener() {
                @Override
                public void onApriAnnuncio(Offerta offerta) {
                    notificationController.openAnnuncioFromOfferta(offerta);
                }

                @Override
                public void onModifica(Offerta offerta) {
                    offerteController.handleEditOfferta(offerta);
                }

                @Override
                public void onRitira(Offerta offerta) {
                    offerteController.rifiutaOrRitiraOfferta(offerta);
                }

                @Override
                public boolean canLasciaRecensione(Offerta offerta) {
                    return notificationController.canLasciaRecensione(offerta);
                }

                @Override
                public void onLasciaRecensione(Offerta offerta) {
                    offerteController.handleLasciaRecensione(offerta);
                }
            });
        }
    }

    private void handleLogout() {
        view.dispose();
        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
    }

    // GETTER and SETTER 
    public AnnunciController getAnnunciController() {
        return annunciController;
    }

    public OfferteController getOfferteController() {
        return offerteController;
    }


    public ProfiloController getProfiloController() {
        return profiloController;
    }

    public ReportController getReportController() {
        return reportController;
    }

    public NotificationController getNotificationController() {
        return notificationController;
    }

    public Annuncio getLastOpenedAnnuncio() {
        return lastOpenedAnnuncio;
    }

    public void setLastOpenedAnnuncio(Annuncio lastOpenedAnnuncio) {
        this.lastOpenedAnnuncio = lastOpenedAnnuncio;
    }

    public Studente getLastOpenedVenditore() {
        return lastOpenedVenditore;
    }

    public void setLastOpenedVenditore(Studente lastOpenedVenditore) {
        this.lastOpenedVenditore = lastOpenedVenditore;
    }

    public DetailOrigin getCurrentDetailOrigin() {
        return currentDetailOrigin;
    }

    public void setCurrentDetailOrigin(DetailOrigin currentDetailOrigin) {
        this.currentDetailOrigin = currentDetailOrigin;
    }

    public Integer getAnnuncioIdBeforeVenditoreProfile() {
        return annuncioIdBeforeVenditoreProfile;
    }

    public void setAnnuncioIdBeforeVenditoreProfile(Integer annuncioIdBeforeVenditoreProfile) {
        this.annuncioIdBeforeVenditoreProfile = annuncioIdBeforeVenditoreProfile;
    }

    public DetailOrigin getDetailOriginBeforeVenditoreProfile() {
        return detailOriginBeforeVenditoreProfile;
    }

    public void setDetailOriginBeforeVenditoreProfile(DetailOrigin detailOriginBeforeVenditoreProfile) {
        this.detailOriginBeforeVenditoreProfile = detailOriginBeforeVenditoreProfile;
    }
}