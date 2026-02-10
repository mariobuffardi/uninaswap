package it.unina.uninaswap.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatClientProperties;

import it.unina.uninaswap.model.entity.Annuncio;
import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.util.ImageUtil;
import it.unina.uninaswap.util.UITheme;

public class AnnuncioDetailView extends JPanel {

    private static final String PROFILE_M_PATH = "/images/menuIcons/profileM.png";
    private static final String PROFILE_F_PATH = "/images/menuIcons/profileF.png";


    private static final Color SURFACE = Color.decode("#EAF2F9");
    private static final Color SURFACE_2 = Color.decode("#F6FAFF");
    private static final Color TITLE = UITheme.PRIMARY_DARK;
    private static final Color SUBTLE = UITheme.TEXT_SECONDARY;

    private static final String CARD_STYLE = "arc: 20; background: #FFFFFF; border: 1,1,1,1,#D7E3F2;";
    private static final String SECTION_STYLE = "arc: 18; background: #F6FAFF; border: 1,1,1,1,#D7E3F2;";

    private JButton btnBack;

    private JLabel lblMainPhoto;
    private JPanel thumbnailsPanel;

    private JLabel lblTitolo;
    private JLabel lblPrezzo;
    private JLabel lblDataPubblicazione;
    private JLabel lblTipologia;
    private JLabel lblCategoria;
    private JLabel lblOggettoRichiesto;
    private JLabel lblConsegna;

    private JTextArea txtDescrizione;

    private JLabel lblVenditoreFoto;
    private JLabel lblVenditoreNome;
    private JLabel lblVenditoreEmail;
    private JLabel lblVenditoreMatricola;
    private JButton btnVediProfiloVenditore;

    public enum OffertaAction {
        ACQUISTA, FAI_OFFERTA, RICHIEDI_REGALO, PROPONI_SCAMBIO
    }

    public interface AnnuncioOffertaListener {
        void onOffertaAction(Annuncio annuncio, OffertaAction action);
    }

    private AnnuncioOffertaListener annuncioOffertaListener;

    public void setAnnuncioOffertaListener(AnnuncioOffertaListener l) {
        this.annuncioOffertaListener = l;
    }

    private JPanel azioniPanel;
    private JButton btnAcquista;
    private JButton btnFaiOfferta;
    private JButton btnRichiediRegalo;
    private JButton btnProponiScambio;

    private Annuncio currentAnnuncio;
    private Studente currentVenditore;

    public AnnuncioDetailView() {
        setLayout(new BorderLayout());
        setBackground(SURFACE);

        // TOP BAR
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(10, 10, 10, 10));

        btnBack = new JButton("← Indietro");
        btnBack.setFont(btnBack.getFont().deriveFont(Font.BOLD, 14f));
        btnBack.setForeground(TITLE);
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        topBar.add(btnBack, BorderLayout.WEST);

        JLabel titoloPagina = new JLabel("Dettaglio annuncio", SwingConstants.CENTER);
        titoloPagina.setFont(titoloPagina.getFont().deriveFont(Font.BOLD, 18f));
        titoloPagina.setForeground(TITLE);
        topBar.add(titoloPagina, BorderLayout.CENTER);

        
        topBar.add(Box.createHorizontalStrut(100), BorderLayout.EAST);
        
        add(topBar, BorderLayout.NORTH);

        // AREA SCROLLABILE
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(10, 20, 20, 20));
        content.setBackground(SURFACE);
        
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(SURFACE);
        scroll.setBackground(SURFACE);
        add(scroll, BorderLayout.CENTER);

        
        // SEZIONE FOTO
        JPanel fotoSection = buildSection("Foto");

        JPanel fotoContent = new JPanel(new BorderLayout());
        fotoContent.setOpaque(false);

        lblMainPhoto = new JLabel();
        lblMainPhoto.setHorizontalAlignment(SwingConstants.CENTER);
        lblMainPhoto.setBorder(new EmptyBorder(10, 10, 10, 10));

        fotoContent.add(lblMainPhoto, BorderLayout.CENTER);

        thumbnailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        thumbnailsPanel.setOpaque(false);
        fotoContent.add(thumbnailsPanel, BorderLayout.SOUTH);
        
        fotoSection.add(fotoContent, BorderLayout.CENTER);
        content.add(fotoSection);
        content.add(Box.createVerticalStrut(20));

        
        // SEZIONE DETTAGLI + DESCRIZIONE
        JPanel infoSection = buildSection("Dettagli annuncio");
        
        JPanel infoContainer = new JPanel(new BorderLayout(0, 16));
        infoContainer.setOpaque(false);
        

        //info
        JPanel infoCard = new JPanel(new GridLayout(0, 1, 0, 8));
        infoCard.setOpaque(true);
        infoCard.setBackground(Color.WHITE);
        infoCard.putClientProperty(FlatClientProperties.STYLE, CARD_STYLE);
        infoCard.setBorder(new EmptyBorder(14, 16, 14, 16));

        lblTitolo = new JLabel("Titolo: -");
        lblTitolo.setFont(lblTitolo.getFont().deriveFont(Font.BOLD, 15f));
        lblTitolo.setBackground(TITLE);

        lblPrezzo = new JLabel("Prezzo: -");
        lblPrezzo.setForeground(TITLE);
        
        lblDataPubblicazione = new JLabel("Data pubblicazione: -");
        lblDataPubblicazione.setForeground(SUBTLE);
        
        lblTipologia = new JLabel("Tipologia: -");
        lblTipologia.setForeground(SUBTLE);
        
        lblCategoria = new JLabel("Categoria: -");
        lblCategoria.setForeground(SUBTLE);
        
        lblOggettoRichiesto = new JLabel("Oggetto richiesto: -");
        lblOggettoRichiesto.setForeground(TITLE);
        
        lblConsegna = new JLabel("Consegna: -");
        lblConsegna.setForeground(TITLE);

        infoCard.add(lblTitolo);
        infoCard.add(lblPrezzo);
        infoCard.add(lblDataPubblicazione);
        infoCard.add(lblTipologia);
        infoCard.add(lblCategoria);
        infoCard.add(lblOggettoRichiesto);
        infoCard.add(lblConsegna);

        infoSection.add(infoCard, BorderLayout.NORTH);

        // riquadro descrizione
        JPanel descrPanel = new JPanel(new BorderLayout());
        descrPanel.setOpaque(false);
        
        JLabel lblDescTitle = new JLabel("Descrizione");
        lblDescTitle.setFont(lblDescTitle.getFont().deriveFont(Font.BOLD, 14f));
        lblDescTitle.setForeground(TITLE);
        lblDescTitle.setBorder(new EmptyBorder(0, 4, 6, 0));
        descrPanel.add(lblDescTitle, BorderLayout.NORTH);

        JPanel descrCard = new JPanel(new BorderLayout());
        descrCard.setOpaque(true);
        descrCard.setBackground(Color.WHITE);
        descrCard.putClientProperty(FlatClientProperties.STYLE, CARD_STYLE);
        descrCard.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtDescrizione = new JTextArea(6, 25);
        txtDescrizione.setLineWrap(true);
        txtDescrizione.setWrapStyleWord(true);
        txtDescrizione.setEditable(false);
        txtDescrizione.setOpaque(false);
        txtDescrizione.setBorder(null);

        JScrollPane scrDesc = new JScrollPane(txtDescrizione);
        scrDesc.setBorder(null);
        scrDesc.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrDesc.setOpaque(false);
        scrDesc.getViewport().setOpaque(false);
        
        descrCard.add(scrDesc, BorderLayout.CENTER);
        descrPanel.add(descrCard, BorderLayout.CENTER);

        infoContainer.add(descrPanel, BorderLayout.CENTER);
        infoSection.add(infoContainer, BorderLayout.CENTER);
        
        content.add(infoSection);
        content.add(Box.createVerticalStrut(20));

        
        // SEZIONE VENDITORE
        JPanel venditoreSection = buildSection("Venditore");

        JPanel venditoreCard = new JPanel(new BorderLayout());
        venditoreCard.setOpaque(true);
        venditoreCard.setBackground(Color.WHITE);
        venditoreCard.putClientProperty(FlatClientProperties.STYLE, CARD_STYLE);
        venditoreCard.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel vLeft = new JPanel();
        vLeft.setLayout(new BoxLayout(vLeft, BoxLayout.Y_AXIS));
        vLeft.setOpaque(false);

        lblVenditoreFoto = new JLabel();
        lblVenditoreFoto.setAlignmentX(Component.LEFT_ALIGNMENT);
        setVenditoreFotoDefault(null); // default iniziale

        vLeft.add(lblVenditoreFoto);
        vLeft.add(Box.createVerticalStrut(8));

        lblVenditoreNome = new JLabel("Nome: -");
        lblVenditoreNome.setForeground(TITLE);
        lblVenditoreNome.setFont(lblVenditoreNome.getFont().deriveFont(Font.BOLD, 14f));
        
        lblVenditoreEmail = new JLabel("Email: -");
        lblVenditoreEmail.setForeground(SUBTLE);
        
        lblVenditoreMatricola = new JLabel("Matricola: -");
        lblVenditoreMatricola.setForeground(SUBTLE);

        vLeft.add(lblVenditoreNome);
        vLeft.add(lblVenditoreEmail);
        vLeft.add(lblVenditoreMatricola);

        venditoreCard.add(vLeft, BorderLayout.CENTER);

        btnVediProfiloVenditore = new JButton("Vedi profilo venditore");
        stylePrimaryButton(btnVediProfiloVenditore);

        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnWrap.setOpaque(false);
        btnWrap.add(btnVediProfiloVenditore);

        venditoreCard.add(btnWrap, BorderLayout.SOUTH);

        venditoreSection.add(venditoreCard, BorderLayout.CENTER);
        
        
        content.add(venditoreSection);
        content.add(Box.createVerticalStrut(20));


        // SEZIONE AZIONI (offerte)
        JPanel azioniSection = buildSection("Azioni");

        azioniPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        azioniPanel.setOpaque(false);


        btnAcquista = new JButton("Acquista");
        stylePrimaryButton(btnAcquista);

        btnFaiOfferta = new JButton("Fai offerta");
        stylePrimaryButton(btnFaiOfferta);

        btnRichiediRegalo = new JButton("Richiedi");
        stylePrimaryButton(btnRichiediRegalo);

        btnProponiScambio = new JButton("Proponi scambio");
        stylePrimaryButton(btnProponiScambio);

        
        
        btnAcquista.addActionListener(e -> {
            if (annuncioOffertaListener != null && currentAnnuncio != null)
                annuncioOffertaListener.onOffertaAction(currentAnnuncio, OffertaAction.ACQUISTA);
        });
        btnFaiOfferta.addActionListener(e -> {
            if (annuncioOffertaListener != null && currentAnnuncio != null)
                annuncioOffertaListener.onOffertaAction(currentAnnuncio, OffertaAction.FAI_OFFERTA);
        });
        btnRichiediRegalo.addActionListener(e -> {
            if (annuncioOffertaListener != null && currentAnnuncio != null)
                annuncioOffertaListener.onOffertaAction(currentAnnuncio, OffertaAction.RICHIEDI_REGALO);
        });
        btnProponiScambio.addActionListener(e -> {
            if (annuncioOffertaListener != null && currentAnnuncio != null)
                annuncioOffertaListener.onOffertaAction(currentAnnuncio, OffertaAction.PROPONI_SCAMBIO);
        });

        azioniPanel.add(btnAcquista);
        azioniPanel.add(btnFaiOfferta);
        azioniPanel.add(btnRichiediRegalo);
        azioniPanel.add(btnProponiScambio);

        
        azioniSection.add(azioniPanel, BorderLayout.CENTER);
        content.add(azioniSection);
    }

    // Helpers Style
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

    private void stylePrimaryButton(JButton b) {
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; " +
                        "background: #1B415D; foreground: #FFFFFF; " +
                        "hoverBackground: #2A5E86; pressedBackground: #163245;");
        b.setFocusable(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
    }

    
    public void setData(Annuncio annuncio, Studente venditore, List<ImageIcon> foto) {
        this.currentAnnuncio = annuncio;
        this.currentVenditore = venditore;

        // FOTO ANNUNCIO
        thumbnailsPanel.removeAll();
        if (foto != null && !foto.isEmpty()) {
            setMainPhoto(foto.get(0));
            for (ImageIcon icon : foto) {
                JLabel thumb = new JLabel();
                Image t = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                thumb.setIcon(new ImageIcon(t));
                thumb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                thumb.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        setMainPhoto(icon);
                    }
                });
                thumbnailsPanel.add(thumb);
            }
        } else {
            ImageIcon rawIcon = ImageUtil.defaultForCategoria(annuncio.getCategoria());
            Image scaled = rawIcon.getImage().getScaledInstance(320, 240, Image.SCALE_SMOOTH);
            lblMainPhoto.setIcon(new ImageIcon(scaled));
        }
        thumbnailsPanel.revalidate();
        thumbnailsPanel.repaint();

        // INFO ANNUNCIO
        lblTitolo.setText("Titolo: " + safe(annuncio.getTitolo()));
        lblTipologia.setText("Tipologia: " + safe(annuncio.getTipologia()));
        lblCategoria.setText("Categoria: " + safe(annuncio.getCategoria()));

        LocalDate data = annuncio.getDataPubblicazione();
        lblDataPubblicazione.setText("Data pubblicazione: " + (data != null ? data.toString() : "-"));

        BigDecimal prezzo = annuncio.getPrezzo();
        lblPrezzo.setText(prezzo != null ? "Prezzo: " + prezzo.toPlainString() + " €" : "Prezzo: -");

        String ogg = annuncio.getOggettoRichiesto();
        lblOggettoRichiesto.setText("Oggetto richiesto: " + (ogg == null || ogg.isBlank() ? "-" : ogg));

        lblConsegna.setText("Consegna: " +
                buildConsegnaText(annuncio.isOffreSpedizione(), annuncio.isOffreIncontroInUni()));

        // DESCRIZIONE
        String descr = annuncio.getDescrizione();
        if (descr == null || descr.isBlank()) {
            txtDescrizione.setText("Nessuna descrizione fornita.");
        } else {
            txtDescrizione.setText(descr);
        }
        txtDescrizione.setCaretPosition(0);

        // INFO VENDITORE + FOTO default
        if (venditore != null) {
            lblVenditoreNome.setText("Nome: " + venditore.getNome() + " " + venditore.getCognome());
            lblVenditoreEmail.setText("Email: " + venditore.getEmail());
            lblVenditoreMatricola.setText("Matricola: " + venditore.getMatricola());
            setVenditoreFotoDefault(venditore);
        } else {
            lblVenditoreNome.setText("Nome: -");
            lblVenditoreEmail.setText("Email: -");
            lblVenditoreMatricola.setText("Matricola: -");
            setVenditoreFotoDefault(null);
        }
    }

    private void setMainPhoto(ImageIcon icon) {
        Image scaled = icon.getImage().getScaledInstance(320, 240, Image.SCALE_SMOOTH);
        lblMainPhoto.setIcon(new ImageIcon(scaled));
    }

    private String buildConsegnaText(boolean sped, boolean uni) {
        if (sped && uni)
            return "Spedizione + Incontro in Uni";
        if (sped)
            return "Spedizione";
        if (uni)
            return "Incontro in Uni";
        return "-";
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    private void setVenditoreFotoDefault(Studente s) {
        String path;
        if (s != null && s.getSesso() != null) {
            switch (s.getSesso()) {
                case F:
                    path = PROFILE_F_PATH;
                    break;
                case M:
                case Altro:
                default:
                    path = PROFILE_M_PATH;
                    break;
            }
        } else {
            path = PROFILE_M_PATH;
        }

        try {
            java.net.URL url = getClass().getResource(path);
            if (url == null) {
                lblVenditoreFoto.setIcon(null);
                return;
            }
            ImageIcon def = new ImageIcon(url);
            Image scaled = def.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            lblVenditoreFoto.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            lblVenditoreFoto.setIcon(null);
        }
    }

    public void updateAzioniForLoggedIn(String matricolaLoggedIn) {
        if (currentAnnuncio == null || azioniPanel == null)
            return;

        boolean isMine = (matricolaLoggedIn != null
                && currentAnnuncio.getMatricolaVenditore() != null
                && matricolaLoggedIn.equals(currentAnnuncio.getMatricolaVenditore()));

        if (isMine) {
            azioniPanel.setVisible(false);
            return;
        }
        azioniPanel.setVisible(true);

        // nascondo tutto e poi mostro solo quello giusto
        btnAcquista.setVisible(false);
        btnFaiOfferta.setVisible(false);
        btnRichiediRegalo.setVisible(false);
        btnProponiScambio.setVisible(false);

        String tip = currentAnnuncio.getTipologia();

        if ("Vendita".equals(tip)) {
            btnAcquista.setVisible(currentAnnuncio.getPrezzo() != null);
            btnFaiOfferta.setVisible(true);
        } else if ("Regalo".equals(tip)) {
            btnRichiediRegalo.setVisible(true);
        } else { // Scambio
            btnProponiScambio.setVisible(true);
        }

        azioniPanel.revalidate();
        azioniPanel.repaint();
    }

    // Getter per controller
    public JButton getBtnBack() {
        return btnBack;
    }

    public JButton getBtnVediProfiloVenditore() {
        return btnVediProfiloVenditore;
    }

    public Annuncio getCurrentAnnuncio() {
        return currentAnnuncio;
    }

    public Studente getCurrentVenditore() {
        return currentVenditore;
    }
}