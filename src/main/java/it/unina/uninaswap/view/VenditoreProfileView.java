package it.unina.uninaswap.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout; // Add missing import
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog; // Add missing import
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea; // Add missing import

import javax.swing.SwingUtilities; // Add missing import

import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import com.formdev.flatlaf.FlatClientProperties;

import it.unina.uninaswap.model.entity.Annuncio;
import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.util.ImageUtil;
import it.unina.uninaswap.util.UITheme;

public class VenditoreProfileView extends JPanel {


    private static final int CARD_W = 260;
    private static final int CARD_H = 280;
    private static final int HGAP = 15;
    private static final int VGAP = 5;

    private static final int REC_CARD_W = 260;
    private static final int REC_CARD_H = 220;

    private static final String STAR_PATH = "/images/altro/star.jpg";
    
    private static final Color SURFACE = Color.decode("#EAF2F9");
    private static final Color SURFACE_2 = Color.decode("#F6FAFF");

    private static final Color TITLE = UITheme.PRIMARY_DARK;
    private static final Color SUBTLE = UITheme.TEXT_SECONDARY;

    private static final String CARD_STYLE = "arc: 20; background: #FFFFFF; border: 1,1,1,1,#D7E3F2;";

    private static final String SECTION_STYLE = "arc: 18; background: #F6FAFF; border: 1,1,1,1,#D7E3F2;";


    private JButton btnBack;

    private JLabel lblFotoProfilo;
    private JLabel lblNomeCompleto;
    private JLabel lblEmail;
    private JLabel lblMatricola;
    private JLabel lblPreferenze;

    private JPanel annunciPreviewPanel;
    private JPanel recensioniPreviewPanel;

    private Studente currentVenditore;

    // stato annunci
    private List<Annuncio> annunciVenditore = new ArrayList<>();

    // stato recensioni
    private List<ProfileView.RecensioneCardData> recensioniVenditore = new ArrayList<>();
    private ImageIcon starIcon;

    // listener click su annuncio
    public interface AnnuncioClickListener {
        void onAnnuncioClick(Annuncio annuncio);
    }

    private AnnuncioClickListener annuncioClickListener;

    public void setAnnuncioClickListener(AnnuncioClickListener l) {
        this.annuncioClickListener = l;
    }

    public VenditoreProfileView() {
        setLayout(new BorderLayout());
        setBackground(SURFACE);

        // TOP BAR 
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(10, 12, 10, 12));

        btnBack = new JButton("← Indietro");
        // Stile semplice per il back button
        btnBack.setFont(btnBack.getFont().deriveFont(Font.BOLD, 14f));
        btnBack.setForeground(TITLE);
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        top.add(btnBack, BorderLayout.WEST);

        JLabel title = new JLabel("Profilo venditore", SwingConstants.CENTER);
        title.setForeground(TITLE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        top.add(title, BorderLayout.CENTER);


        Component spacer = Box.createHorizontalStrut(100);
        top.add(spacer, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // CONTENUTO SCROLLABILE 
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        mainPanel.setBackground(SURFACE);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(SURFACE);
        scrollPane.setBackground(SURFACE);
        add(scrollPane, BorderLayout.CENTER);

        // FOTO
        lblFotoProfilo = new JLabel();
        lblFotoProfilo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblFotoProfilo);
        mainPanel.add(Box.createVerticalStrut(14));

        // titolo info
        JLabel lblTitoloInfo = new JLabel("Informazioni venditore");
        lblTitoloInfo.setForeground(TITLE);
        lblTitoloInfo.setFont(lblTitoloInfo.getFont().deriveFont(Font.BOLD, 18f));
        lblTitoloInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblTitoloInfo);
        mainPanel.add(Box.createVerticalStrut(10));

        // info panel
        JPanel infoCard = new JPanel(new BorderLayout());
        infoCard.setOpaque(true);
        infoCard.setBackground(Color.WHITE);
        infoCard.putClientProperty(FlatClientProperties.STYLE, CARD_STYLE);
        infoCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        infoCard.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 0, 6));
        infoPanel.setOpaque(false);
        
        
        lblNomeCompleto = new JLabel("Nome: -");
        lblEmail = new JLabel("Email: -");
        lblMatricola = new JLabel("Matricola: -");
        lblPreferenze = new JLabel("Preferenze consegna: -");


        styleInfoLabel(lblNomeCompleto);
        styleInfoLabel(lblEmail);
        styleInfoLabel(lblMatricola);
        styleInfoLabel(lblPreferenze);

        
        infoPanel.add(lblNomeCompleto);
        infoPanel.add(lblEmail);
        infoPanel.add(lblMatricola);
        infoPanel.add(lblPreferenze);

        infoCard.add(infoPanel, BorderLayout.CENTER);

        mainPanel.add(infoCard);
        mainPanel.add(Box.createVerticalStrut(20));

        // SEZIONE ANNUNCI VENDITORE 
        JPanel annunciSection = buildSection("Annunci del venditore");

        annunciPreviewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, HGAP, VGAP));
        annunciPreviewPanel.setOpaque(false);
        annunciPreviewPanel.setBorder(new EmptyBorder(6, 6, 6, 6));

        // Scroll orizzontale
        JScrollPane annScroll = new JScrollPane(annunciPreviewPanel);
        annScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        annScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        annScroll.setBorder(null);
        annScroll.getViewport().setOpaque(false);
        annScroll.setOpaque(false);
        annScroll.setPreferredSize(new Dimension(0, CARD_H + 34));
        
        annunciSection.add(annScroll, BorderLayout.CENTER);

        mainPanel.add(annunciSection);
        mainPanel.add(Box.createVerticalStrut(20));

        // SEZIONE RECENSIONI VENDITORE 
        JPanel recensioniSection = buildSection("Recensioni del venditore");

        recensioniPreviewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, HGAP, VGAP));
        recensioniPreviewPanel.setOpaque(false);
        recensioniPreviewPanel.setBorder(new EmptyBorder(6, 6, 6, 6));

        // Scroll orizzontale recensioni
        JScrollPane recScroll = new JScrollPane(recensioniPreviewPanel);
        recScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        recScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        recScroll.setBorder(null);
        recScroll.getViewport().setOpaque(false);
        recScroll.setOpaque(false);
        recScroll.setPreferredSize(new Dimension(0, REC_CARD_H + 34));
        
        
        recensioniSection.add(recScroll, BorderLayout.CENTER);
        mainPanel.add(recensioniSection);

        // stato iniziale
        setVenditore(null);
        setAnnunciVenditore(null);
        setRecensioniVenditore(null);
    }
    
    private void styleInfoLabel(JLabel l) {
        l.setForeground(TITLE);
        l.setFont(l.getFont().deriveFont(Font.PLAIN, 13.5f));
    }

    private JPanel buildSection(String title) {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(true);
        section.setBackground(SURFACE_2);
        section.putClientProperty(FlatClientProperties.STYLE, SECTION_STYLE);
        section.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel lbl = new JLabel(title);
        lbl.setForeground(TITLE);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 16f));
        lbl.setBorder(new EmptyBorder(0, 2, 10, 2));
        section.add(lbl, BorderLayout.NORTH);

        return section;
    }


    // API controller 
    public void setVenditore(Studente venditore) {
        this.currentVenditore = venditore;

        if (venditore == null) {
            lblNomeCompleto.setText("Nome: -");
            lblEmail.setText("Email: -");
            lblMatricola.setText("Matricola: -");
            lblPreferenze.setText("Preferenze consegna: -");
            setFotoProfiloDefaultBySesso(null, 120);
            return;
        }

        lblNomeCompleto.setText("Nome: " + venditore.getNome() + " " + venditore.getCognome());
        lblEmail.setText("Email: " + venditore.getEmail());
        lblMatricola.setText("Matricola: " + venditore.getMatricola());

        String pref = "";
        if (venditore.getPreferisceSpedizione())
            pref += "Spedizione";
        if (venditore.getPreferisceIncontroInUni()) {
            if (!pref.isEmpty())
                pref += " - ";
            pref += "Incontro in Uni";
        }
        lblPreferenze.setText("Preferenze consegna: " + (pref.isBlank() ? "-" : pref));

        setFotoProfiloDefaultBySesso(venditore, 120);
    }

    public void setAnnunciVenditore(List<Annuncio> annunci) {
        if (annunci == null)
            this.annunciVenditore = new ArrayList<>();
        else
            this.annunciVenditore = new ArrayList<>(annunci);

        refreshAnnunciPreview();
    }

    private void refreshAnnunciPreview() {
        annunciPreviewPanel.removeAll();

        if (annunciVenditore.isEmpty()) {
            JLabel lbl = new JLabel("Nessun annuncio trovato.", SwingConstants.CENTER);
            lbl.setForeground(SUBTLE);
            lbl.setBorder(new EmptyBorder(25, 10, 10, 10));
            annunciPreviewPanel.add(lbl);
        } else {
            for (Annuncio a : annunciVenditore) {
                annunciPreviewPanel.add(createAnnuncioCard(a));
            }
        }

        annunciPreviewPanel.revalidate();
        annunciPreviewPanel.repaint();
    }

    private JPanel createAnnuncioCard(Annuncio annuncio) {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE, CARD_STYLE);
        card.setBorder(new EmptyBorder(0, 0, 0, 0));

        Dimension fixed = new Dimension(CARD_W, CARD_H);
        card.setPreferredSize(fixed);
        card.setMinimumSize(fixed);
        card.setMaximumSize(fixed);

        // FOTO PRINCIPALE (larger)

        JPanel photoWrap = new JPanel(new BorderLayout());
        photoWrap.setOpaque(false);
        photoWrap.setBorder(new EmptyBorder(10, 12, 4, 12));
        JLabel lblFoto = new JLabel();
        lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
        lblFoto.setOpaque(true);
        lblFoto.setBackground(SURFACE_2);
        lblFoto.putClientProperty(FlatClientProperties.STYLE, "arc: 16;");


        String fotoNomeFile = annuncio.getFotoPrincipalePath();
        ImageIcon baseIcon = ImageUtil.annuncioImageFromDbPath(fotoNomeFile);
        if (baseIcon == null)
            baseIcon = ImageUtil.defaultForCategoria(annuncio.getCategoria());

        String key = (fotoNomeFile != null ? fotoNomeFile : "cat_" + annuncio.getCategoria());
        ImageIcon icon = ImageUtil.scaled(baseIcon, 130, 130);

        lblFoto.setIcon(icon);

        
        photoWrap.add(lblFoto, BorderLayout.CENTER);
        card.add(photoWrap, BorderLayout.NORTH);

        // INFO 
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(4, 14, 6, 14));

        JLabel lblTitolo = new JLabel(annuncio.getTitolo());
        lblTitolo.setFont(lblTitolo.getFont().deriveFont(Font.BOLD, 15.5f));
        lblTitolo.setForeground(TITLE);
        infoPanel.add(lblTitolo);

        infoPanel.add(Box.createVerticalStrut(6));

        JLabel lblTipo = new JLabel(annuncio.getTipologia() + " • " + annuncio.getCategoria());
        lblTipo.setForeground(SUBTLE);
        lblTipo.setFont(lblTipo.getFont().deriveFont(11.3f));
        infoPanel.add(lblTipo);

        BigDecimal prezzo = annuncio.getPrezzo();
        if (prezzo != null) {
            infoPanel.add(Box.createVerticalStrut(6));
            JLabel lblPrezzo = new JLabel("€ " + prezzo.toPlainString());
            lblPrezzo.setForeground(TITLE);
            lblPrezzo.setFont(lblPrezzo.getFont().deriveFont(Font.BOLD, 15.5f));
            infoPanel.add(lblPrezzo);
        }

        card.add(infoPanel, BorderLayout.CENTER);

        // CLICK -> dettaglio
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (annuncioClickListener != null)
                    annuncioClickListener.onAnnuncioClick(annuncio);
            }
            

            @Override
            public void mouseEntered(MouseEvent e) {
                card.putClientProperty(FlatClientProperties.STYLE,
                        "arc: 20; background: #FFFFFF; border: 1,1,1,1,#BFD3EA;");
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.putClientProperty(FlatClientProperties.STYLE, CARD_STYLE);
                card.repaint();
            }
        });

        return card;
    }

    private void setFotoProfiloDefaultBySesso(Studente s, int sizePx) {
        String defaultPath;
        if (s != null && s.getSesso() != null) {
            switch (s.getSesso()) {
                case F:
                    defaultPath = "/images/menuIcons/profileF.png";
                    break;
                case M:
                case Altro:
                default:
                    defaultPath = "/images/menuIcons/profileM.png";
                    break;
            }
        } else {
            defaultPath = "/images/menuIcons/profileM.png";
        }

        
        
        try {
            java.net.URL url = getClass().getResource(defaultPath);
            if (url == null) {
                lblFotoProfilo.setIcon(null);
                return;
            }
            ImageIcon def = new ImageIcon(url);
            Image scaled = def.getImage().getScaledInstance(sizePx, sizePx, Image.SCALE_SMOOTH);
            lblFotoProfilo.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            lblFotoProfilo.setIcon(null);
        }
    }

    public void setRecensioniVenditore(List<ProfileView.RecensioneCardData> recensioni) {
        if (recensioni == null)
            this.recensioniVenditore = new ArrayList<>();
        else
            this.recensioniVenditore = new ArrayList<>(recensioni);

        refreshRecensioniPreview();
    }

    private void refreshRecensioniPreview() {
        recensioniPreviewPanel.removeAll();

        if (recensioniVenditore.isEmpty()) {
            JLabel lbl = new JLabel("Nessuna recensione trovata.", SwingConstants.CENTER);
            lbl.setForeground(SUBTLE);
            lbl.setBorder(new EmptyBorder(25, 10, 10, 10));
            recensioniPreviewPanel.add(lbl);
        } else {
            for (ProfileView.RecensioneCardData r : recensioniVenditore) {
                recensioniPreviewPanel.add(createRecensioneCard(r));
            }
        }

        recensioniPreviewPanel.revalidate();
        recensioniPreviewPanel.repaint();
    }

    private JPanel createRecensioneCard(ProfileView.RecensioneCardData r) {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(true);        
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE, CARD_STYLE);
        card.setBorder(new EmptyBorder(0, 0, 0, 0));

        Dimension fixed = new Dimension(REC_CARD_W, REC_CARD_H);
        card.setPreferredSize(fixed);
        card.setMinimumSize(fixed);
        card.setMaximumSize(fixed);

        // TOP
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(12, 14, 6, 14));

        JLabel lblTitolo = new JLabel(r.getTitolo());
        lblTitolo.setForeground(TITLE);
        lblTitolo.setFont(lblTitolo.getFont().deriveFont(Font.BOLD, 15f));
        top.add(lblTitolo);

        JLabel lblData = new JLabel(r.getDataTransazione());
        lblData.setForeground(SUBTLE);
        lblData.setFont(lblData.getFont().deriveFont(Font.ITALIC, 11.5f));
        top.add(lblData);

        card.add(top, BorderLayout.NORTH);

        // CENTER
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(6, 14, 6, 14));
        center.setOpaque(false);

        String autore = r.getNomeAutore() + " " + r.getCognomeAutore() + " (" + r.getMatricolaAutore() + ")";
        JLabel lblAutore = new JLabel(autore);
        lblAutore.setForeground(TITLE);
        lblAutore.setFont(lblAutore.getFont().deriveFont(Font.PLAIN, 12.5f));
        center.add(lblAutore);
        
        center.add(Box.createVerticalStrut(6));

        String corpo = r.getCorpo();
        String shortBody = corpo;
        if (corpo != null && corpo.length() > 180) {
            shortBody = corpo.substring(0, 180) + "...";
        }

        JTextArea txtCorpoShort = new JTextArea(shortBody != null ? shortBody : "-");
        txtCorpoShort.setLineWrap(true);
        txtCorpoShort.setWrapStyleWord(true);
        txtCorpoShort.setEditable(false);
        txtCorpoShort.setOpaque(false);
        txtCorpoShort.setBorder(null);
        txtCorpoShort.setForeground(SUBTLE);
        txtCorpoShort.setFont(txtCorpoShort.getFont().deriveFont(Font.PLAIN, 12.5f));
        center.add(txtCorpoShort);

        card.add(center, BorderLayout.CENTER);

        // BOTTOM
        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 8));
        ratingPanel.setOpaque(false);
        ratingPanel.setBorder(new EmptyBorder(0, 14, 10, 14));
        ratingPanel.add(new JLabel("Valutazione: "));
        int v = Math.max(1, Math.min(5, r.getValutazione()));
        ImageIcon star = getStarIcon();
        for (int i = 0; i < v; i++) {
            ratingPanel.add(new JLabel(star));
        }

        card.add(ratingPanel, BorderLayout.SOUTH);

        // Click
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showRecensioneDialog(r);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                card.putClientProperty(FlatClientProperties.STYLE,
                        "arc: 20; background: #FFFFFF; border: 1,1,1,1,#BFD3EA;");
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.putClientProperty(FlatClientProperties.STYLE, CARD_STYLE);
                card.repaint();
            }
        });

        return card;
    }

    private ImageIcon getStarIcon() {
        if (starIcon == null) {
            ImageIcon raw = new ImageIcon(getClass().getResource(STAR_PATH));
            if (raw.getIconWidth() > 0 && raw.getIconHeight() > 0) {
                Image scaled = raw.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                starIcon = new ImageIcon(scaled);
            } else {
                starIcon = raw;
            }
        }
        return starIcon;
    }

    private void showRecensioneDialog(ProfileView.RecensioneCardData r) {
        JDialog dialog = new JDialog(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                "Dettaglio recensione",
                Dialog.ModalityType.APPLICATION_MODAL);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(12, 12, 12, 12));
        content.setBackground(SURFACE);
        dialog.setContentPane(content);

        JLabel lblTitle = new JLabel(r.getTitolo());
        lblTitle.setForeground(TITLE);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));
        content.add(lblTitle, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(5, 0, 5, 0));
        center.setOpaque(false);

        JLabel l1 = new JLabel("Data transazione: " + r.getDataTransazione());
        JLabel l2 = new JLabel("Autore: " + r.getNomeAutore() + " " + r.getCognomeAutore()
                + " (" + r.getMatricolaAutore() + ")");
        l1.setForeground(TITLE);
        l2.setForeground(TITLE);
        center.add(l1);
        center.add(l2);
        center.add(Box.createVerticalStrut(8));

        JTextArea txtFull = new JTextArea(r.getCorpo() != null ? r.getCorpo() : "-");
        txtFull.setLineWrap(true);
        txtFull.setWrapStyleWord(true);
        txtFull.setEditable(false);
        
        JScrollPane scr = new JScrollPane(txtFull);
        scr.setPreferredSize(new Dimension(420, 220));
        scr.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scr.setBorder(BorderFactory.createEmptyBorder());
        center.add(scr);

        content.add(center, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        
        JPanel starsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        starsPanel.setOpaque(false);
        starsPanel.add(new JLabel("Valutazione: "));
        int v = Math.max(1, Math.min(5, r.getValutazione()));
        ImageIcon star = getStarIcon();
        for (int i = 0; i < v; i++) {
            starsPanel.add(new JLabel(star));
        }
        south.add(starsPanel, BorderLayout.WEST);

        JButton btnClose = new JButton("Chiudi");
        btnClose.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; " +
                        "background: #1B415D; foreground: #FFFFFF; " +
                        "hoverBackground: #2A5E86; pressedBackground: #163245;");
        btnClose.setFocusable(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(true);

        btnClose.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(btnClose);
        south.add(btnPanel, BorderLayout.EAST);

        content.add(south, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // getter
    public JButton getBtnBack() {
        return btnBack;
    }

    public Studente getCurrentVenditore() {
        return currentVenditore;
    }
}