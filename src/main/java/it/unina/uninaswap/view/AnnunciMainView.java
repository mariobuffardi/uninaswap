package it.unina.uninaswap.view;

import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AnnunciMainView extends JFrame {

    private JButton hamburgerMenuButton;
    private JButton filterButton;

    private AnnunciFilterPanel filterPanel;
    private AnnunciMenuPanel menuBar;

    private JPanel topBar;

    private JPanel topBarPanelWest;
    private JPanel topBarPanelEast;

    private CardLayout centerLayout;
    private JPanel centerPanel;

    private AnnunciListView annunciListView;
    private ProfileView profileView;
    private NotificationView notificationView;

    private ReportView reportView;

    private AnnuncioDetailView annuncioDetailView;
    private VenditoreProfileView venditoreProfileView;

    public static final String CARD_ANNUNCI = "ANNUNCI";
    public static final String CARD_PROFILE = "PROFILE";
    public static final String CARD_REPORT = "REPORT";
    public static final String CARD_NOTIFICATION = "CARD_NOTIFICATION";
    public static final String CARD_ANNUNCIO_DETAIL = "ANNUNCIO_DETAIL";
    public static final String CARD_VENDITORE_PROFILE = "VENDITORE_PROFILE";

    private static final Color PRIMARY = UITheme.PRIMARY;
    private static final Color SURFACE = Color.decode("#EAF2F9");

    private static final int TOP_ICON_PX = 50;
    private static final int TOP_BUTTON_PX = 70;

    private static final int MENU_WRAP_LEFT_PAD = 10;
    private static final int FILTER_WRAP_RIGHT_PAD = 10;

    public AnnunciMainView(Studente studenteLoggato) {
        setTitle("UniNaSwap");
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Icona finestra
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/altro/logo_unina.png"));
            setIconImage(logoIcon.getImage());
        } catch (Exception e) {
            System.err.println("Logo non trovato: " + e.getMessage());
        }

        getContentPane().setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(SURFACE);

        // TOP BAR
        topBar = new JPanel(new BorderLayout(0, 0));
        topBar.setBackground(PRIMARY);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 0, 0, 60)),
                new EmptyBorder(10, 12, 10, 12)
        ));
        getContentPane().add(topBar, BorderLayout.NORTH);

        // West (hamburger)
        topBarPanelWest = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBarPanelWest.setOpaque(false);
        topBar.add(topBarPanelWest, BorderLayout.WEST);

        hamburgerMenuButton = createTopIconButton("/images/menuIcons/hamburgerMenu.png", TOP_ICON_PX, TOP_BUTTON_PX);
        topBarPanelWest.add(hamburgerMenuButton);

        // Center (logo)
        JPanel topBarPanelCenter = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        topBarPanelCenter.setOpaque(false);
        topBar.add(topBarPanelCenter, BorderLayout.CENTER);

        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/altro/logo_unina.png"));
            int targetH = 72;
            double ratio = (double) logoIcon.getIconWidth() / Math.max(1, logoIcon.getIconHeight());
            int targetW = (int) Math.round(targetH * ratio);

            if (targetW > 320) {
                targetW = 320;
                targetH = (int) Math.round(targetW / ratio);
            }

            Image scaledLogo = logoIcon.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
            topBarPanelCenter.add(new JLabel(new ImageIcon(scaledLogo)));
        } catch (Exception ignored) {}

        // East (filter)
        topBarPanelEast = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topBarPanelEast.setOpaque(false);
        topBar.add(topBarPanelEast, BorderLayout.EAST);

        filterButton = createTopIconButton("/images/menuIcons/filter.png", TOP_ICON_PX, TOP_BUTTON_PX);
        topBarPanelEast.add(filterButton);

        //MENU LATERALE 
        menuBar = new AnnunciMenuPanel();
        JPanel menuWrap = new JPanel(new BorderLayout());
        menuWrap.setBackground(SURFACE);
        menuWrap.setBorder(new EmptyBorder(10, MENU_WRAP_LEFT_PAD, 10, 0));
        menuWrap.add(menuBar, BorderLayout.CENTER);
        getContentPane().add(menuWrap, BorderLayout.WEST);

        // FILTER PANEL
        filterPanel = new AnnunciFilterPanel();
        JPanel filterWrap = new JPanel(new BorderLayout());
        filterWrap.setBackground(SURFACE);
        filterWrap.setBorder(new EmptyBorder(10, 0, 10, FILTER_WRAP_RIGHT_PAD));
        filterWrap.add(filterPanel, BorderLayout.CENTER);
        getContentPane().add(filterWrap, BorderLayout.EAST);

        // CENTER 
        centerLayout = new CardLayout();
        centerPanel = new JPanel(centerLayout);
        centerPanel.setBackground(SURFACE);
        centerPanel.setBorder(null);
        getContentPane().add(centerPanel, BorderLayout.CENTER);

        annunciListView = new AnnunciListView();
        profileView = new ProfileView(studenteLoggato);
        reportView = new ReportView();
        notificationView = new NotificationView();
        annuncioDetailView = new AnnuncioDetailView();
        venditoreProfileView = new VenditoreProfileView();


        centerPanel.add(annunciListView, CARD_ANNUNCI);
        centerPanel.add(profileView, CARD_PROFILE);
        centerPanel.add(reportView, CARD_REPORT);
        centerPanel.add(notificationView, CARD_NOTIFICATION);
        centerPanel.add(annuncioDetailView, CARD_ANNUNCIO_DETAIL);
        centerPanel.add(venditoreProfileView, CARD_VENDITORE_PROFILE);

        // Bottom
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(SURFACE);
        bottomPanel.setBorder(new EmptyBorder(0, 0, 6, 0));
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        showAnnunciView();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                resizeSidePanels();
            }
        });
        
        SwingUtilities.invokeLater(this::resizeSidePanels);
    }

    private JButton createTopIconButton(String resourcePath, int iconSizePx, int buttonSizePx) {
        JButton b = new JButton();
        
        b.setPreferredSize(new Dimension(buttonSizePx, buttonSizePx));
        b.setMinimumSize(new Dimension(buttonSizePx, buttonSizePx));
        b.setMaximumSize(new Dimension(buttonSizePx, buttonSizePx));
        
        
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(0, 0, 0, 0));

        try {
            ImageIcon raw = new ImageIcon(getClass().getResource(resourcePath));
            Image scaled = raw.getImage().getScaledInstance(iconSizePx, iconSizePx, Image.SCALE_SMOOTH);
            b.setIcon(new ImageIcon(scaled));
        } catch (Exception ignored) {}

        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setOpaque(true);
                b.setContentAreaFilled(true);
                b.setBackground(new Color(255, 255, 255, 22));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setContentAreaFilled(false);
                b.setOpaque(false);
            }
        });

        return b;
    }

    private void resizeSidePanels() {
        int w = getWidth();

        // menu laterale
        int menuW = (int) (w * 0.18);
        menuW = Math.max(180, Math.min(260, menuW));
        if (menuBar != null) {
            menuBar.setPreferredSize(new Dimension(menuW, 0));
        }

        // filtro laterale
        int filterW = (int) (w * 0.22);
        filterW = Math.max(240, Math.min(320, filterW));
        if (filterPanel != null) {
            filterPanel.setPreferredSize(new Dimension(filterW, 0));
        }

        topBar.revalidate();
        topBar.repaint();
        revalidate();
    }

    public void addHamburgerMenuListener(ActionListener l) {
        hamburgerMenuButton.addActionListener(l);
    }

    public void addFilterButtonListener(ActionListener l) {
        filterButton.addActionListener(l);
    }

    public void toggleFilterPanel() {
        filterPanel.setVisible(!filterPanel.isVisible());
        filterPanel.getParent().revalidate();
        filterPanel.getParent().repaint();
    }

    public void toggleMenuBar() {
        menuBar.setVisible(!menuBar.isVisible());
        menuBar.getParent().revalidate();
        menuBar.getParent().repaint();
    }

    public void hideMenuBar() {
        menuBar.setVisible(false);
        menuBar.getParent().revalidate();
        menuBar.getParent().repaint();
    }

    public void showMenuBar() {
        menuBar.setVisible(true);
        menuBar.getParent().revalidate();
        menuBar.getParent().repaint();
    }

    public void hideFilterPanel() {
        filterPanel.setVisible(false);
        filterPanel.getParent().revalidate();
        filterPanel.getParent().repaint();
    }

    public void showFilterPanel() {
        filterPanel.setVisible(true);
        filterPanel.getParent().revalidate();
        filterPanel.getParent().repaint();
    }

    public void showAnnunciView() {
        topBar.setVisible(true);
        filterButton.setVisible(true);

        hideFilterPanel();
        centerLayout.show(centerPanel, CARD_ANNUNCI);
    }

    public void showProfileView() {
        topBar.setVisible(true);
        filterButton.setVisible(false);

        hideFilterPanel();
        centerLayout.show(centerPanel, CARD_PROFILE);
    }

    public void showReportView() {
        topBar.setVisible(true);
        filterButton.setVisible(false);

        hideFilterPanel();
        centerLayout.show(centerPanel, CARD_REPORT);
    }

    public void showNotificationView() {
        topBar.setVisible(true);
        filterButton.setVisible(false);

        hideFilterPanel();
        centerLayout.show(centerPanel, CARD_NOTIFICATION);
    }

    public void showAnnuncioDetailView() {
        topBar.setVisible(false);

        hideFilterPanel();
        hideMenuBar();

        centerLayout.show(centerPanel, CARD_ANNUNCIO_DETAIL);
    }

    public void showVenditoreProfileView() {
        topBar.setVisible(false);

        hideFilterPanel();
        hideMenuBar();

        centerLayout.show(centerPanel, CARD_VENDITORE_PROFILE);
    }

    public AnnunciMenuPanel getMenuBarPanel() {
        return menuBar;
    }

    public AnnunciFilterPanel getFilterPanel() {
        return filterPanel;
    }

    public ProfileView getProfileView() {
        return profileView;
    }

    public AnnunciListView getAnnunciListView() {
        return annunciListView;
    }

    public AnnuncioDetailView getAnnuncioDetailView() {
        return annuncioDetailView;
    }

    public VenditoreProfileView getVenditoreProfileView() {
        return venditoreProfileView;
    }

    public NotificationView getNotificationView() {
        return notificationView;
    }

    public ReportView getReportView() {
        return reportView;
    }
}