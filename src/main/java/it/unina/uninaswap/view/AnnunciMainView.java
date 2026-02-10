package it.unina.uninaswap.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatClientProperties;

import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.util.UITheme;

public class AnnunciMainView extends JFrame {

    private JButton hamburgerMenuButton;
    private JButton filterButton;

    private AnnunciFilterPanel filterPanel;
    private AnnunciMenuPanel menuBar;

    private JPanel topBar;
    private JPanel topBarPanelWest;
    private JPanel topBarPanelEast;

    private JPanel searchBarWrap;
    private JPanel searchBarPanel;
    private JTextField txtSearchTop;
    private JComboBox<String> cmbCategoriaTop;
    private JButton btnSearchTop;

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
    private static final int SEARCH_H = 44;

    private static final Color BTN_PRIMARY = Color.decode("#1B415D");
    private static final Color BTN_HOVER   = Color.decode("#2A5E86");
    private static final Color BTN_PRESSED = Color.decode("#163245");
    private static final Color BTN_TEXT    = Color.WHITE;

    public AnnunciMainView(Studente studenteLoggato) {
        setTitle("UniNaSwap");
        setSize(1000, 750);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Icona finestra
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/altro/logo_unina.png"));
            setIconImage(logoIcon.getImage());
        } catch (Exception e) {
            System.err.println("Logo non trovato: " + e.getMessage());
        }

        // Header
        getContentPane().setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(SURFACE);


        topBar = new JPanel(new BorderLayout(0, 0));
        topBar.setBackground(PRIMARY);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 0, 0, 60)),
                new EmptyBorder(10, 12, 10, 12)
        ));
        getContentPane().add(topBar, BorderLayout.NORTH);


        topBarPanelWest = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBarPanelWest.setOpaque(false);
        topBarPanelWest.setPreferredSize(new Dimension(100, TOP_BUTTON_PX));
        topBar.add(topBarPanelWest, BorderLayout.WEST);

        hamburgerMenuButton = createTopIconButton("/images/menuIcons/hamburgerMenu.png", TOP_ICON_PX, TOP_BUTTON_PX);
        topBarPanelWest.add(hamburgerMenuButton);

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
        } catch (Exception ignored) {
        }

        topBarPanelEast = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topBarPanelEast.setOpaque(false);
        topBarPanelEast.setPreferredSize(new Dimension(100, TOP_BUTTON_PX)); // Stessa larghezza di West
        topBar.add(topBarPanelEast, BorderLayout.EAST);

        // Menu Laterale
        menuBar = new AnnunciMenuPanel();
        JPanel menuWrap = new JPanel(new BorderLayout());
        menuWrap.setBackground(SURFACE);
        menuWrap.setBorder(new EmptyBorder(10, MENU_WRAP_LEFT_PAD, 10, 0));
        menuWrap.add(menuBar, BorderLayout.CENTER);
        getContentPane().add(menuWrap, BorderLayout.WEST);
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(SURFACE);
        getContentPane().add(contentWrapper, BorderLayout.CENTER);

        // Search Bar
        buildSearchBar();
        contentWrapper.add(searchBarWrap, BorderLayout.NORTH);


        // Pannello Filtri
        filterPanel = new AnnunciFilterPanel();
        JPanel filterWrap = new JPanel(new BorderLayout());
        filterWrap.setBackground(SURFACE);
        filterWrap.setBorder(new EmptyBorder(10, 0, 10, FILTER_WRAP_RIGHT_PAD));
        filterWrap.add(filterPanel, BorderLayout.CENTER);
        contentWrapper.add(filterWrap, BorderLayout.EAST);

        filterPanel.bindTopSearchControls(txtSearchTop, cmbCategoriaTop);
        btnSearchTop.addActionListener(e -> filterPanel.getBtnCerca().doClick());
        txtSearchTop.addActionListener(e -> filterPanel.getBtnCerca().doClick());

        // Layout Annunci
        centerLayout = new CardLayout();
        centerPanel = new JPanel(centerLayout);
        centerPanel.setBackground(SURFACE);
        centerPanel.setBorder(null);
        contentWrapper.add(centerPanel, BorderLayout.CENTER);

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

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(SURFACE);
        bottomPanel.setBorder(new EmptyBorder(0, 0, 6, 0));
        contentWrapper.add(bottomPanel, BorderLayout.SOUTH);

        showAnnunciView();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                resizeSidePanels();
            }
        });

        SwingUtilities.invokeLater(this::resizeSidePanels);
    }

    private void buildSearchBar() {
        searchBarWrap = new JPanel(new BorderLayout());
        searchBarWrap.setBackground(SURFACE);
        searchBarWrap.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        searchBarPanel = new JPanel(new GridBagLayout());
        searchBarPanel.setOpaque(true);
        searchBarPanel.setBackground(Color.WHITE);
        searchBarPanel.putClientProperty(FlatClientProperties.STYLE,
                "arc: 22; border: 1,1,1,1,#D7E3F2;");

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 1;

        // Textfield Cerca
        txtSearchTop = new JTextField();
        txtSearchTop.setPreferredSize(new Dimension(0, SEARCH_H));
        txtSearchTop.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cerca");
        txtSearchTop.putClientProperty(FlatClientProperties.STYLE,
                "arc: 18; background: #FFFFFF; border: 0,0,0,0;");

        // Categorie
        cmbCategoriaTop = new JComboBox<>();
        cmbCategoriaTop.addItem("Tutte");
        cmbCategoriaTop.addItem("Strumenti_musicali");
        cmbCategoriaTop.addItem("Libri");
        cmbCategoriaTop.addItem("Informatica");
        cmbCategoriaTop.addItem("Abbigliamento");
        cmbCategoriaTop.addItem("Arredo");
        cmbCategoriaTop.addItem("Altro");
        cmbCategoriaTop.setPreferredSize(new Dimension(0, SEARCH_H));

        cmbCategoriaTop.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                String text = (value == null) ? "" : value.toString();
                if ("Tutte".equals(text)) text = "Tutte le categorie";
                return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            }
        });

        cmbCategoriaTop.putClientProperty(FlatClientProperties.STYLE,
                "arc: 18; background: #FFFFFF; border: 0,0,0,0;");

        // Bottone Cerca
        btnSearchTop = createPrimaryPillTextButton("Cerca", new Dimension(110, SEARCH_H));

        JSeparator sep1 = new JSeparator(SwingConstants.VERTICAL);
        sep1.setPreferredSize(new Dimension(1, SEARCH_H - 10));
        sep1.setForeground(new Color(0, 0, 0, 30));

        JSeparator sep2 = new JSeparator(SwingConstants.VERTICAL);
        sep2.setPreferredSize(new Dimension(1, SEARCH_H - 10));
        sep2.setForeground(new Color(0, 0, 0, 30));


        c.gridx = 0;
        c.weightx = 1.0;
        c.insets = new Insets(6, 10, 6, 10);
        searchBarPanel.add(txtSearchTop, c);

        c.gridx = 1;
        c.weightx = 0;
        c.insets = new Insets(10, 0, 10, 0);
        searchBarPanel.add(sep1, c);

        c.gridx = 2;
        c.weightx = 0.55;
        c.insets = new Insets(6, 10, 6, 10);
        searchBarPanel.add(cmbCategoriaTop, c);

        c.gridx = 3;
        c.weightx = 0;
        c.insets = new Insets(10, 0, 10, 0);
        searchBarPanel.add(sep2, c);

        c.gridx = 4;
        c.weightx = 0;
        c.insets = new Insets(6, 10, 6, 10);
        searchBarPanel.add(btnSearchTop, c);


        filterButton = createPrimaryPillIconButton("/images/menuIcons/filter.png", 22, new Dimension(SEARCH_H, SEARCH_H));

        JPanel filterWrapRight = new JPanel(new BorderLayout());
        filterWrapRight.setOpaque(false);
        filterWrapRight.setBorder(new EmptyBorder(0, 10, 0, 0));
        filterWrapRight.add(filterButton, BorderLayout.CENTER);

        row.add(searchBarPanel, BorderLayout.CENTER);
        row.add(filterWrapRight, BorderLayout.EAST);

        searchBarWrap.add(row, BorderLayout.CENTER);
    }

    private JButton createPrimaryPillTextButton(String text, Dimension size) {
        PillButton b = new PillButton(text);
        b.setPreferredSize(size);
        b.setForeground(BTN_TEXT);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 14f));
        b.setFocusPainted(false);
        b.setFocusable(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setColors(BTN_PRIMARY, BTN_HOVER, BTN_PRESSED);
        b.setCornerRadius(size.height);
        return b;
    }

    private JButton createPrimaryPillIconButton(String resourcePath, int iconSizePx, Dimension size) {
        PillButton b = new PillButton("");
        b.setPreferredSize(size);
        b.setForeground(BTN_TEXT);
        b.setFocusPainted(false);
        b.setFocusable(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setColors(BTN_PRIMARY, BTN_HOVER, BTN_PRESSED);
        b.setCornerRadius(size.height);

        try {
            ImageIcon raw = new ImageIcon(getClass().getResource(resourcePath));
            Image scaled = raw.getImage().getScaledInstance(iconSizePx, iconSizePx, Image.SCALE_SMOOTH);
            b.setIcon(new ImageIcon(scaled));
        } catch (Exception ignored) {}

        return b;
    }

    private static class PillButton extends JButton {
        private Color normal = BTN_PRIMARY;
        private Color hover = BTN_HOVER;
        private Color pressed = BTN_PRESSED;

        private boolean isHover = false;
        private boolean isPressed = false;

        private int cornerRadius = 999;

        PillButton(String text) {
            super(text);

            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setRolloverEnabled(false);
            setMargin(new Insets(0, 0, 0, 0));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHover = false;
                    isPressed = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && isEnabled()) {
                        isPressed = true;
                        repaint();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    repaint();
                }
            });
        }

        void setColors(Color normal, Color hover, Color pressed) {
            this.normal = normal;
            this.hover = hover;
            this.pressed = pressed;
            repaint();
        }

        void setCornerRadius(int r) {
            this.cornerRadius = Math.max(0, r);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = normal;
                if (!isEnabled()) {
                    bg = normal.darker();
                } else if (isPressed) {
                    bg = pressed;
                } else if (isHover) {
                    bg = hover;
                }

                int w = getWidth();
                int h = getHeight();
                int arc = Math.min(cornerRadius, Math.min(w, h));

                Shape rr = new RoundRectangle2D.Float(0, 0, w, h, arc, arc);
                g2.setColor(bg);
                g2.fill(rr);

            } finally {
                g2.dispose();
            }


            super.paintComponent(g);
        }
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
        } catch (Exception ignored) {
        }

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

        int menuW = (int) (w * 0.18);
        menuW = Math.max(180, Math.min(260, menuW));
        if (menuBar != null) {
            menuBar.setPreferredSize(new Dimension(menuW, 0));
        }

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
        searchBarWrap.setVisible(true);
        filterButton.setVisible(true);

        hideFilterPanel();
        centerLayout.show(centerPanel, CARD_ANNUNCI);
    }

    public void showProfileView() {
        topBar.setVisible(true);
        searchBarWrap.setVisible(false);
        filterButton.setVisible(false);

        hideFilterPanel();
        centerLayout.show(centerPanel, CARD_PROFILE);
    }

    public void showReportView() {
        topBar.setVisible(true);
        searchBarWrap.setVisible(false);
        filterButton.setVisible(false);

        hideFilterPanel();
        centerLayout.show(centerPanel, CARD_REPORT);
    }

    public void showNotificationView() {
        topBar.setVisible(true);
        searchBarWrap.setVisible(false);
        filterButton.setVisible(false);

        hideFilterPanel();
        centerLayout.show(centerPanel, CARD_NOTIFICATION);
    }

    public void showAnnuncioDetailView() {
        topBar.setVisible(false);
        searchBarWrap.setVisible(false);

        hideFilterPanel();
        hideMenuBar();

        centerLayout.show(centerPanel, CARD_ANNUNCIO_DETAIL);
    }

    public void showVenditoreProfileView() {
        topBar.setVisible(false);
        searchBarWrap.setVisible(false);

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