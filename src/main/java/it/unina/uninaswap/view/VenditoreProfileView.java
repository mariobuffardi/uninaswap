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
import javax.swing.border.TitledBorder;

import it.unina.uninaswap.model.entity.Annuncio;
import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.util.ImageUtil;
import it.unina.uninaswap.util.UITheme;

public class VenditoreProfileView extends JPanel {


    private static final int CARD_W = 280;
    private static final int CARD_H = 310;
    private static final int HGAP = 15;
    private static final int VGAP = 5;

    private static final int REC_CARD_W = 260;
    private static final int REC_CARD_H = 220;

    private static final String STAR_PATH = "/images/altro/star.jpg";

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

        // TOP BAR 
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(new EmptyBorder(10, 12, 10, 12));

        btnBack = new JButton("← Indietro");
        top.add(btnBack, BorderLayout.WEST);

        JLabel title = new JLabel("Profilo venditore", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        top.add(title, BorderLayout.CENTER);

        top.add(Box.createHorizontalStrut(80), BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // CONTENUTO SCROLLABILE 
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // FOTO
        lblFotoProfilo = new JLabel();
        lblFotoProfilo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblFotoProfilo);
        mainPanel.add(Box.createVerticalStrut(14));

        // titolo info
        JLabel lblTitoloInfo = new JLabel("Informazioni venditore");
        lblTitoloInfo.setFont(lblTitoloInfo.getFont().deriveFont(Font.BOLD, 18f));
        lblTitoloInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblTitoloInfo);
        mainPanel.add(Box.createVerticalStrut(10));

        // info panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 0, 6));
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblNomeCompleto = new JLabel("Nome: -");
        lblEmail = new JLabel("Email: -");
        lblMatricola = new JLabel("Matricola: -");
        lblPreferenze = new JLabel("Preferenze consegna: -");

        infoPanel.add(lblNomeCompleto);
        infoPanel.add(lblEmail);
        infoPanel.add(lblMatricola);
        infoPanel.add(lblPreferenze);

        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(18));

        // SEZIONE ANNUNCI VENDITORE 
        JPanel annunciSection = new JPanel(new BorderLayout());
        annunciSection.setBorder(new TitledBorder("Annunci del venditore"));

        annunciPreviewPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, HGAP, VGAP));
        annunciPreviewPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Scroll orizzontale
        JScrollPane annScroll = new JScrollPane(annunciPreviewPanel);
        annScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        annScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        annScroll.setBorder(null);
        annScroll.setPreferredSize(new Dimension(0, CARD_H + 30));

        annunciSection.add(annScroll, BorderLayout.CENTER);

        mainPanel.add(annunciSection);
        mainPanel.add(Box.createVerticalStrut(20));

        // SEZIONE RECENSIONI VENDITORE 
        JPanel recensioniSection = new JPanel(new BorderLayout());
        recensioniSection.setBorder(new TitledBorder("Recensioni del venditore"));

        recensioniPreviewPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, HGAP, VGAP));
        recensioniPreviewPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Scroll orizzontale recensioni
        JScrollPane recScroll = new JScrollPane(recensioniPreviewPanel);
        recScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        recScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        recScroll.setBorder(null);
        recScroll.setPreferredSize(new Dimension(0, REC_CARD_H + 30));

        recensioniSection.add(recScroll, BorderLayout.CENTER);
        mainPanel.add(recensioniSection);

        // stato iniziale
        setVenditore(null);
        setAnnunciVenditore(null);
        setRecensioniVenditore(null);
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
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.PRIMARY, 2, true),
                new EmptyBorder(14, 14, 14, 14)));
        card.setBackground(Color.WHITE);

        Dimension fixed = new Dimension(CARD_W, CARD_H);
        card.setPreferredSize(fixed);
        card.setMinimumSize(fixed);
        card.setMaximumSize(fixed);

        // FOTO PRINCIPALE (larger)
        JLabel lblFoto = new JLabel();
        lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
        lblFoto.setBorder(new EmptyBorder(0, 0, 10, 0));

        String fotoNomeFile = annuncio.getFotoPrincipalePath();
        ImageIcon baseIcon = ImageUtil.annuncioImageFromDbPath(fotoNomeFile);
        if (baseIcon == null)
            baseIcon = ImageUtil.defaultForCategoria(annuncio.getCategoria());

        String key = (fotoNomeFile != null ? fotoNomeFile : "cat_" + annuncio.getCategoria());
        ImageIcon icon = ImageUtil.scaled(baseIcon, 140, 140);

        lblFoto.setIcon(icon);
        card.add(lblFoto, BorderLayout.NORTH);

        // INFO
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(6, 0, 10, 0));
        info.setOpaque(false);

        JLabel lblTitolo = new JLabel(annuncio.getTitolo());
        lblTitolo.setFont(lblTitolo.getFont().deriveFont(Font.BOLD, 16f));
        lblTitolo.setForeground(UITheme.PRIMARY_DARK);
        info.add(lblTitolo);

        info.add(Box.createVerticalStrut(6));

        JLabel lblTipo = new JLabel(annuncio.getTipologia() + " • " + annuncio.getCategoria());
        lblTipo.setForeground(UITheme.TEXT_SECONDARY);
        lblTipo.setFont(lblTipo.getFont().deriveFont(11f));
        info.add(lblTipo);

        BigDecimal prezzo = annuncio.getPrezzo();
        if (prezzo != null) {
            info.add(Box.createVerticalStrut(6));
            JLabel lblPrezzo = new JLabel("€ " + prezzo.toPlainString());
            lblPrezzo.setForeground(UITheme.PRIMARY_DARK);
            lblPrezzo.setFont(lblPrezzo.getFont().deriveFont(Font.BOLD, 16f));
            info.add(lblPrezzo);
        }

        card.add(info, BorderLayout.CENTER);

        // CLICK -> dettaglio
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (annuncioClickListener != null)
                    annuncioClickListener.onAnnuncioClick(annuncio);
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

        ImageIcon def = new ImageIcon(getClass().getResource(defaultPath));
        Image scaled = def.getImage().getScaledInstance(sizePx, sizePx, Image.SCALE_SMOOTH);
        lblFotoProfilo.setIcon(new ImageIcon(scaled));
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
        card.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        card.setBackground(Color.WHITE);

        Dimension fixed = new Dimension(REC_CARD_W, REC_CARD_H);
        card.setPreferredSize(fixed);
        card.setMinimumSize(fixed);
        card.setMaximumSize(fixed);

        // TOP
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(5, 8, 5, 8));

        JLabel lblTitolo = new JLabel(r.getTitolo());
        lblTitolo.setFont(lblTitolo.getFont().deriveFont(Font.BOLD));
        top.add(lblTitolo);

        JLabel lblData = new JLabel(r.getDataTransazione());
        lblData.setFont(lblData.getFont().deriveFont(Font.ITALIC, 11f));
        top.add(lblData);

        card.add(top, BorderLayout.NORTH);

        // CENTER
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(5, 8, 5, 8));
        center.setOpaque(false);

        String autore = r.getNomeAutore() + " " + r.getCognomeAutore() + " (" + r.getMatricolaAutore() + ")";
        JLabel lblAutore = new JLabel(autore);
        center.add(lblAutore);

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
        center.add(txtCorpoShort);

        card.add(center, BorderLayout.CENTER);

        // BOTTOM
        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
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
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        dialog.setContentPane(content);

        JLabel lblTitle = new JLabel(r.getTitolo());
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));
        content.add(lblTitle, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(5, 0, 5, 0));

        center.add(new JLabel("Data transazione: " + r.getDataTransazione()));
        center.add(new JLabel("Autore: " + r.getNomeAutore() + " " + r.getCognomeAutore()
                + " (" + r.getMatricolaAutore() + ")"));
        center.add(Box.createVerticalStrut(8));

        JTextArea txtFull = new JTextArea(r.getCorpo() != null ? r.getCorpo() : "-");
        txtFull.setLineWrap(true);
        txtFull.setWrapStyleWord(true);
        txtFull.setEditable(false);
        JScrollPane scr = new JScrollPane(txtFull);
        scr.setPreferredSize(new Dimension(400, 200));
        scr.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        center.add(scr);

        content.add(center, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        JPanel starsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        starsPanel.add(new JLabel("Valutazione: "));
        int v = Math.max(1, Math.min(5, r.getValutazione()));
        ImageIcon star = getStarIcon();
        for (int i = 0; i < v; i++) {
            starsPanel.add(new JLabel(star));
        }
        south.add(starsPanel, BorderLayout.WEST);

        JButton btnClose = new JButton("Chiudi");
        btnClose.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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