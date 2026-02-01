package it.unina.uninaswap.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatClientProperties;

import it.unina.uninaswap.model.entity.Annuncio;
import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.util.ImageUtil;
import it.unina.uninaswap.util.UITheme;

public class ProfileView extends JPanel {

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

    private static final String CARD_STYLE =
            "arc: 20; background: #FFFFFF; border: 1,1,1,1,#D7E3F2;";

    private static final String SECTION_STYLE =
            "arc: 18; background: #F6FAFF; border: 1,1,1,1,#D7E3F2;";

    private JLabel lblFotoProfilo;
    private JLabel lblNomeCompleto;
    private JLabel lblEmail;
    private JLabel lblMatricola;
    private JLabel lblIndirizzo;
    private JLabel lblPreferenze;

    private JPanel annunciPreviewPanel;
    private JPanel recensioniPreviewPanel;

    private Studente currentStudente;

    private List<Annuncio> mieiAnnunci = new ArrayList<>();

    private List<RecensioneCardData> mieRecensioni = new ArrayList<>();
    private ImageIcon starIcon; 

    public interface AnnuncioClickListener {
        void onAnnuncioClick(Annuncio annuncio);
    }

    public interface AnnuncioEditListener {
        void onModificaAnnuncio(Annuncio annuncio);
    }

    public interface AnnuncioDeleteListener {
        void onEliminaAnnuncio(Annuncio annuncio);
    }

    private AnnuncioClickListener annuncioClickListener;
    private AnnuncioEditListener annuncioEditListener;
    private AnnuncioDeleteListener annuncioDeleteListener;

    public ProfileView(Studente studenteLoggato) {
        setLayout(new BorderLayout());
        setBackground(SURFACE);


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(SURFACE);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(SURFACE);
        scrollPane.setBackground(SURFACE);
        add(scrollPane, BorderLayout.CENTER);

        lblFotoProfilo = new JLabel();
        lblFotoProfilo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblFotoProfilo);
        mainPanel.add(Box.createVerticalStrut(14));

        JLabel lblTitoloInfo = new JLabel("Informazioni studente");
        lblTitoloInfo.setForeground(TITLE);
        lblTitoloInfo.setFont(lblTitoloInfo.getFont().deriveFont(Font.BOLD, 18f));
        lblTitoloInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblTitoloInfo);
        mainPanel.add(Box.createVerticalStrut(10));

        JPanel infoCard = new JPanel(new BorderLayout());
        infoCard.setOpaque(true);
        infoCard.setBackground(Color.WHITE);
        infoCard.putClientProperty(FlatClientProperties.STYLE, CARD_STYLE);
        infoCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        infoCard.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 0, 6));
        infoPanel.setOpaque(false);

        lblNomeCompleto = new JLabel("Nome: -");
        lblEmail = new JLabel("Email: -");
        lblMatricola = new JLabel("Matricola: -");
        lblIndirizzo = new JLabel("Indirizzo: -");
        lblPreferenze = new JLabel("Preferenze consegna: -");

        styleInfoLabel(lblNomeCompleto);
        styleInfoLabel(lblEmail);
        styleInfoLabel(lblMatricola);
        styleInfoLabel(lblIndirizzo);
        styleInfoLabel(lblPreferenze);

        infoPanel.add(lblNomeCompleto);
        infoPanel.add(lblEmail);
        infoPanel.add(lblMatricola);
        infoPanel.add(lblIndirizzo);
        infoPanel.add(lblPreferenze);

        infoCard.add(infoPanel, BorderLayout.CENTER);

        mainPanel.add(infoCard);
        mainPanel.add(Box.createVerticalStrut(18));


        // I MIEI ANNUNCI
        JPanel annunciSection = buildSection("I miei annunci");
        annunciPreviewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, HGAP, VGAP));
        annunciPreviewPanel.setBorder(new EmptyBorder(6, 6, 6, 6));
        annunciPreviewPanel.setOpaque(false);

        JScrollPane annScroll = new JScrollPane(annunciPreviewPanel);
        annScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        annScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        annScroll.setBorder(null);
        annScroll.getViewport().setOpaque(false);
        annScroll.getViewport().setBackground(new Color(0, 0, 0, 0));
        annScroll.setOpaque(false);
        annScroll.setPreferredSize(new Dimension(0, CARD_H + 34));

        annunciSection.add(annScroll, BorderLayout.CENTER);

        mainPanel.add(annunciSection);
        mainPanel.add(Box.createVerticalStrut(16));


        // LE MIE RECENSIONI
        JPanel recensioniSection = buildSection("Le mie recensioni");
        recensioniPreviewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, HGAP, VGAP));
        recensioniPreviewPanel.setBorder(new EmptyBorder(6, 6, 6, 6));
        recensioniPreviewPanel.setOpaque(false);

        JScrollPane recScroll = new JScrollPane(recensioniPreviewPanel);
        recScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        recScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        recScroll.setBorder(null);
        recScroll.getViewport().setOpaque(false);
        recScroll.getViewport().setBackground(new Color(0, 0, 0, 0));
        recScroll.setOpaque(false);
        recScroll.setPreferredSize(new Dimension(0, REC_CARD_H + 34));

        recensioniSection.add(recScroll, BorderLayout.CENTER);

        mainPanel.add(recensioniSection);

        setStudente(studenteLoggato);
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

    public void setAnnuncioClickListener(AnnuncioClickListener listener) {
        this.annuncioClickListener = listener;
    }

    public void setAnnuncioEditListener(AnnuncioEditListener listener) {
        this.annuncioEditListener = listener;
    }

    public void setAnnuncioDeleteListener(AnnuncioDeleteListener listener) {
        this.annuncioDeleteListener = listener;
    }

    // Studente 
    public void setStudente(Studente studente) {
        this.currentStudente = studente;

        if (studente == null) {
            lblNomeCompleto.setText("Nome: -");
            lblEmail.setText("Email: -");
            lblMatricola.setText("Matricola: -");
            lblIndirizzo.setText("Indirizzo: -");
            lblPreferenze.setText("Preferenze consegna: -");
            setFotoProfiloDefaultBySesso(null, 120);
            return;
        }

        lblNomeCompleto.setText("Nome: " + studente.getNome() + " " + studente.getCognome());
        lblEmail.setText("Email: " + studente.getEmail());
        lblMatricola.setText("Matricola: " + studente.getMatricola());

        if (lblIndirizzo.getText() == null || lblIndirizzo.getText().startsWith("Indirizzo: -")) {
            lblIndirizzo.setText("Indirizzo: -");
        }

        String pref = "";
        if (studente.getPreferisceSpedizione())
            pref += "Spedizione ";
        if (studente.getPreferisceIncontroInUni()) {
            if (!pref.isEmpty())
                pref += "- ";
            pref += "Incontro in Uni";
        }
        lblPreferenze.setText("Preferenze consegna: " + (pref.isBlank() ? "-" : pref));

        setFotoProfiloDefaultBySesso(studente, 120);
    }

    public void setIndirizzoInfo(String indirizzo) {
        if (indirizzo == null || indirizzo.trim().isEmpty()) {
            lblIndirizzo.setText("Indirizzo: -");
        } else {
            lblIndirizzo.setText("Indirizzo: " + indirizzo);
        }
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

    // Annunci
    public void setMyAnnunci(List<Annuncio> annunci) {
        if (annunci == null) {
            this.mieiAnnunci = new ArrayList<>();
        } else {
            this.mieiAnnunci = new ArrayList<>(annunci);
        }
        refreshAnnunciPreview();
    }

    public void showMyAnnunciPreview(List<Annuncio> annunci) {
        setMyAnnunci(annunci);
    }

    private void refreshAnnunciPreview() {
        annunciPreviewPanel.removeAll();

        if (mieiAnnunci.isEmpty()) {
            JLabel lblVuoto = new JLabel("Nessun annuncio trovato", SwingConstants.CENTER);
            lblVuoto.setForeground(SUBTLE);
            lblVuoto.setBorder(new EmptyBorder(22, 10, 10, 10));
            annunciPreviewPanel.add(lblVuoto);
        } else {
            for (Annuncio a : mieiAnnunci) {
                annunciPreviewPanel.add(createMyAnnuncioCard(a));
            }
        }

        annunciPreviewPanel.revalidate();
        annunciPreviewPanel.repaint();
    }

    private JPanel createMyAnnuncioCard(Annuncio annuncio) {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE, CARD_STYLE);
        card.setBorder(new EmptyBorder(0, 0, 0, 0));

        Dimension fixed = new Dimension(CARD_W, CARD_H);
        card.setPreferredSize(fixed);
        card.setMinimumSize(fixed);
        card.setMaximumSize(fixed);

        // FOTO 
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

        infoPanel.add(Box.createVerticalStrut(4));

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

        //  AZIONI
        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 0));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(0, 12, 10, 12));
        actions.setPreferredSize(new Dimension(CARD_W - 24, 38));

        JButton btnModifica = new JButton("Modifica");
        stylePrimaryButton(btnModifica);
        btnModifica.setPreferredSize(new Dimension(0, 32));
        btnModifica.setMargin(new Insets(2, 10, 2, 10));
        btnModifica.addActionListener(e -> {
            if (annuncioEditListener != null) {
                annuncioEditListener.onModificaAnnuncio(annuncio);
            }
        });

        JButton btnElimina = new JButton("Elimina");
        styleDangerButton(btnElimina);
        btnElimina.setPreferredSize(new Dimension(0, 32));
        btnElimina.setMargin(new Insets(2, 10, 2, 10));
        btnElimina.addActionListener(e -> {
            if (annuncioDeleteListener == null) return;

            int res = JOptionPane.showConfirmDialog(
                    ProfileView.this,
                    "Vuoi eliminare questo annuncio?\nL'operazione non è reversibile.",
                    "Conferma eliminazione",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (res == JOptionPane.YES_OPTION) {
                annuncioDeleteListener.onEliminaAnnuncio(annuncio);
            }
        });

        actions.add(btnModifica);
        actions.add(btnElimina);
        card.add(actions, BorderLayout.SOUTH);

        // CLICK sulla card (non sui bottoni)
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Component deep = SwingUtilities.getDeepestComponentAt(card, e.getX(), e.getY());
                if (deep instanceof JButton) return;

                if (annuncioClickListener != null) {
                    annuncioClickListener.onAnnuncioClick(annuncio);
                }
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


    private void stylePrimaryButton(JButton b) {
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; " +
                        "background: #1B415D; foreground: #FFFFFF; " +
                        "hoverBackground: #2A5E86; pressedBackground: #163245;");
        b.setFocusable(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(false);
    }

    private void styleDangerButton(JButton b) {
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; " +
                        "background: #D93C25; foreground: #FFFFFF; " +
                        "hoverBackground: #B93522; pressedBackground: #8F2A1B;");
        b.setFocusable(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(false);
    }

    // RECENSIONI
    public static class RecensioneCardData {
        private final String titolo;
        private final String corpo;
        private final int valutazione;
        private final String dataTransazione;
        private final String nomeAutore;
        private final String cognomeAutore;
        private final String matricolaAutore;

        public RecensioneCardData(String titolo,
                                  String corpo,
                                  int valutazione,
                                  String dataTransazione,
                                  String nomeAutore,
                                  String cognomeAutore,
                                  String matricolaAutore) {
            this.titolo = titolo;
            this.corpo = corpo;
            this.valutazione = valutazione;
            this.dataTransazione = dataTransazione;
            this.nomeAutore = nomeAutore;
            this.cognomeAutore = cognomeAutore;
            this.matricolaAutore = matricolaAutore;
        }

        public String getTitolo() { return titolo; }
        public String getCorpo() { return corpo; }
        public int getValutazione() { return valutazione; }
        public String getDataTransazione() { return dataTransazione; }
        public String getNomeAutore() { return nomeAutore; }
        public String getCognomeAutore() { return cognomeAutore; }
        public String getMatricolaAutore() { return matricolaAutore; }
    }

    public void setRecensioni(List<RecensioneCardData> recensioni) {
        if (recensioni == null) {
            this.mieRecensioni = new ArrayList<>();
        } else {
            this.mieRecensioni = new ArrayList<>(recensioni);
        }
        refreshRecensioniPreview();
    }

    private void refreshRecensioniPreview() {
        recensioniPreviewPanel.removeAll();

        if (mieRecensioni.isEmpty()) {
            JLabel lbl = new JLabel("Nessuna recensione trovata.", SwingConstants.CENTER);
            lbl.setForeground(SUBTLE);
            lbl.setBorder(new EmptyBorder(22, 10, 10, 10));
            recensioniPreviewPanel.add(lbl);
        } else {
            for (RecensioneCardData r : mieRecensioni) {
                recensioniPreviewPanel.add(createRecensioneCard(r));
            }
        }

        recensioniPreviewPanel.revalidate();
        recensioniPreviewPanel.repaint();
    }

    private JPanel createRecensioneCard(RecensioneCardData r) {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE, CARD_STYLE);
        card.setBorder(new EmptyBorder(0, 0, 0, 0));

        Dimension fixed = new Dimension(REC_CARD_W, REC_CARD_H);
        card.setPreferredSize(fixed);
        card.setMinimumSize(fixed);
        card.setMaximumSize(fixed);

        // TOP: titolo + data
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
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

        // CENTER: autore + corpo
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

        // BOTTOM: valutazione con stelline
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

        // CLICK: dialog con recensione completa
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

    private void showRecensioneDialog(RecensioneCardData r) {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
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
        stylePrimaryButton(btnClose);
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

    // Getter
    public JPanel getAnnunciPreviewPanel() {
        return annunciPreviewPanel;
    }

    public JPanel getRecensioniPreviewPanel() {
        return recensioniPreviewPanel;
    }

    public Studente getCurrentStudente() {
        return currentStudente;
    }
}