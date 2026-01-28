package it.unina.uninaswap.view;

import it.unina.uninaswap.model.entity.Offerta;
import it.unina.uninaswap.util.UITheme;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


public class NotificationView extends JPanel {

    private int currentCols = 1;


    private static final int CARD_W = 420;
    private static final int CARD_H = 135;
    private static final int HGAP = 15;
    private static final int VGAP = 15;
    
    private static final Color SURFACE = Color.decode("#EAF2F9");
    private static final Color SURFACE_2 = Color.decode("#F6FAFF");
    private static final Color TITLE = UITheme.PRIMARY_DARK;
    private static final Color SUBTLE = UITheme.TEXT_SECONDARY;
    
    private static final String CARD_STYLE = "arc: 20; background: #FFFFFF; border: 1,1,1,1,#D7E3F2;";
    private static final String SECTION_STYLE = "arc: 18; background: #F6FAFF; border: 1,1,1,1,#D7E3F2;";
    
    private JPanel ricevutePanel;
    private JPanel inviatePanel;
    private JPanel ricevuteAccettatePanel;
    private JPanel inviateAccettatePanel;

    private List<OffertaNotificationData> offerteRicevute = new ArrayList<>();
    private List<OffertaNotificationData> offerteInviate = new ArrayList<>();
    private List<OffertaNotificationData> offerteRicevuteAccettate = new ArrayList<>();
    private List<OffertaNotificationData> offerteInviateAccettate = new ArrayList<>();

    public interface OffertaRicevutaListener {
        void onAccetta(Offerta offerta);

        void onRifiuta(Offerta offerta);

        void onApriAnnuncio(Offerta offerta);
    }

    public interface OffertaInviataListener {
        void onModifica(Offerta offerta);

        void onRitira(Offerta offerta);

        void onApriAnnuncio(Offerta offerta);

        void onLasciaRecensione(Offerta offerta);

        boolean canLasciaRecensione(Offerta offerta);
    }

    private OffertaRicevutaListener ricevutaListener;
    private OffertaInviataListener inviataListener;

    // Listener setters (backward compatibility)
    public void setOffertaRicevutaListener(OffertaRicevutaListener l) {
        this.ricevutaListener = l;
    }

    public void setOffertaInviataListener(OffertaInviataListener l) {
        this.inviataListener = l;
    }

    public NotificationView() {
        setLayout(new BorderLayout());
        setBackground(SURFACE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        mainPanel.setBackground(SURFACE);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(SURFACE);
        scrollPane.setBackground(SURFACE);
        add(scrollPane, BorderLayout.CENTER);



        // OFFERTE RICEVUTE
        JPanel ricevuteSection = buildSection("Offerte ricevute (in attesa");
        
        ricevutePanel = new JPanel();
        
        styleInnerPanel(ricevutePanel);
        ricevuteSection.add(ricevutePanel, BorderLayout.CENTER);
        mainPanel.add(ricevuteSection);
        mainPanel.add(Box.createVerticalStrut(20));

        // OFFERTE INVIATE (IN ATTESA)
        JPanel inviateSection = buildSection("Offerte inviate (in attesa)");
        
        inviatePanel = new JPanel();
        styleInnerPanel(inviatePanel);
        inviateSection.add(inviatePanel, BorderLayout.CENTER);
        mainPanel.add(inviateSection);
        mainPanel.add(Box.createVerticalStrut(20));


        // OFFERTE RICEVUTE (ACCETTATE)
        JPanel ricevuteAccSection = buildSection("Offerte ricevute (Accettate)");

        ricevuteAccettatePanel = new JPanel();
        styleInnerPanel(ricevuteAccettatePanel);

        ricevuteAccSection.add(ricevuteAccettatePanel, BorderLayout.CENTER);
        mainPanel.add(ricevuteAccSection);
        mainPanel.add(Box.createVerticalStrut(20));

        
        // OFFERTE INVIATE (ACCETTATE)
        JPanel inviateAccSection = buildSection("Offerte inviate (Accettate)");

        inviateAccettatePanel = new JPanel();
        
        styleInnerPanel(inviateAccettatePanel);
        inviateAccSection.add(inviateAccettatePanel, BorderLayout.CENTER);
        mainPanel.add(inviateAccSection);
        
        // Listener width
        scrollPane.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateColsAndRelayout();
            }
        });

        // Trigger iniziale
        SwingUtilities.invokeLater(this::updateColsAndRelayout);
    }


    private void styleInnerPanel(JPanel p) {
        // layout set dynamically
        p.setOpaque(false); // transparent to show section background
        p.setBorder(new EmptyBorder(6, 6, 6, 6));
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
        lbl.setBorder(new EmptyBorder(0, 4, 10, 4));
        section.add(lbl, BorderLayout.NORTH);

        return section;
    }

    
    // Data setters 
    public void setOfferteRicevute(List<OffertaNotificationData> list) {
        this.offerteRicevute = (list == null) ? new ArrayList<>() : new ArrayList<>(list);
        refreshRicevute();
    }

    public void setOfferteInviate(List<OffertaNotificationData> list) {
        this.offerteInviate = (list == null) ? new ArrayList<>() : new ArrayList<>(list);
        refreshInviate();
    }

    public void setOfferteRicevuteAccettate(List<OffertaNotificationData> list) {
        this.offerteRicevuteAccettate = (list == null) ? new ArrayList<>() : new ArrayList<>(list);
        refreshRicevuteAccettate();
    }

    public void setOfferteInviateAccettate(List<OffertaNotificationData> list) {
        this.offerteInviateAccettate = (list == null) ? new ArrayList<>() : new ArrayList<>(list);
        refreshInviateAccettate();
    }

    private void refreshRicevute() {
        buildContent(ricevutePanel, offerteRicevute, true, "Nessuna offerta ricevuta in attesa.");
    }

    private void refreshInviate() {
        buildContent(inviatePanel, offerteInviate, false, "Nessuna offerta inviata in attesa.");
    }

    private void refreshRicevuteAccettate() {
        buildContent(ricevuteAccettatePanel, offerteRicevuteAccettate, true, "Nessuna offerta ricevuta accettata.");
    }

    private void refreshInviateAccettate() {
        buildContent(inviateAccettatePanel, offerteInviateAccettate, false, "Nessuna offerta inviata accettata.");
    }

    private void buildContent(JPanel container, List<OffertaNotificationData> dataList, boolean ricevuta, String emptyMsg) {
        if (container == null)
            return;

        container.removeAll();

        if (dataList == null || dataList.isEmpty()) {
            container.setLayout(new BorderLayout());
            JLabel lbl = new JLabel(emptyMsg, SwingConstants.LEFT);
            lbl.setForeground(SUBTLE);
            lbl.setBorder(new EmptyBorder(8, 8, 8, 8));
            container.add(lbl, BorderLayout.NORTH);
            
        } else {
            container.setLayout(new GridLayout(0, currentCols, HGAP, VGAP));
            for (OffertaNotificationData data : dataList) {
                container.add(createCompactCard(data, ricevuta));
            }
        }

        container.revalidate();
        container.repaint();
    }

    
    // CARD COMPATTA (ANTEPRIMA)
    private JPanel createCompactCard(OffertaNotificationData data, boolean ricevuta) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(8, 6));
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE, CARD_STYLE);

        Dimension fixed = new Dimension(CARD_W, CARD_H);
        card.setPreferredSize(fixed);
        card.setMinimumSize(fixed);
        card.setMaximumSize(fixed);

        // TOP
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(12, 14, 0, 14));

        JLabel lblTitolo = new JLabel(data.getTitoloAnnuncio());
        lblTitolo.setFont(lblTitolo.getFont().deriveFont(Font.BOLD, 14.5f));
        lblTitolo.setForeground(TITLE);
        top.add(lblTitolo);

        String tipo = (data.getTipologiaAnnuncio() == null || data.getTipologiaAnnuncio().isBlank())
                ? "-"
                : data.getTipologiaAnnuncio();

        String ruolo = ricevuta ? "Ricevuta" : "Inviata";

        JLabel lblMeta = new JLabel("Tipo: " + tipo + " • " + ruolo + " • " + safe(data.getDataOfferta()));
        lblMeta.setFont(lblMeta.getFont().deriveFont(Font.ITALIC, 11.5f));
        lblMeta.setForeground(SUBTLE);
        top.add(lblMeta);

        card.add(top, BorderLayout.NORTH);

        // CENTER
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(6, 14, 8, 14));

        String prefix = ricevuta ? "Da: " : "A: ";
        JLabel lblUser = new JLabel(prefix + safe(data.getControparteDisplay()));
        lblUser.setForeground(TITLE);
        center.add(lblUser);
        
        // Importo
        if (data.getImportoDisplay() != null) {
            String imp = data.getImportoDisplay().trim();
            if (!imp.isEmpty() && !imp.equals("-") && !imp.equals("- €")) {
                JLabel lblImp = new JLabel("Importo: " + imp);
                lblImp.setForeground(TITLE);
                lblImp.setFont(lblImp.getFont().deriveFont(Font.BOLD));
                center.add(lblImp);
                }
        }

        // Oggetto offerto 
        Offerta off = data.getOfferta();
        if (off != null && off.getOggettoOfferto() != null && !off.getOggettoOfferto().trim().isEmpty()) {
            JLabel lblObj = new JLabel("Offre: " + off.getOggettoOfferto().trim());
            lblObj.setForeground(TITLE);
            center.add(lblObj);
            }

        // Prima riga del messaggio


        if (!ricevuta && off != null && off.getStato() != null && "Accettata".equals(off.getStato().name())) {
            boolean can = (inviataListener != null) && inviataListener.canLasciaRecensione(off);
            if (can) {
            	JButton btnRec = new JButton("Lascia una recensione");
            	btnRec.putClientProperty(FlatClientProperties.STYLE, "arc: 10; padding: 2,8,2,8; background: #EAF2F9; foreground: #1B415D; hoverBackground: #D7E3F2;");
            	btnRec.setBorderPainted(false);
            	btnRec.addActionListener(e -> {
                    if (inviataListener != null)
                        inviataListener.onLasciaRecensione(off);
                });
                center.add(Box.createVerticalStrut(6));
                center.add(btnRec);
            }
        }

        card.add(center, BorderLayout.CENTER);


        // Click interaction
        // Se ricevuta e accettata -> no action. Altrimenti -> apri dialog
        boolean isRicAcc = (ricevuta && off != null && off.getStato() != null && "Accettata".equals(off.getStato().name()));

        if (!isRicAcc) {
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                	Component deep = SwingUtilities.getDeepestComponentAt(card, e.getX(), e.getY());
                    if (deep instanceof JButton)
                        return;

                    showOffertaDialog(data, ricevuta);
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
        }

        return card;
    }

    private String safe(String s) {
        return (s == null) ? "-" : s;
    }

    
    // DIALOG DETTAGLI
    private void showOffertaDialog(OffertaNotificationData data, boolean ricevuta) {
        Offerta offerta = data.getOfferta();
        if (offerta == null)
            return;

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Dettaglio offerta", Dialog.ModalityType.APPLICATION_MODAL);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(16, 16, 16, 16));
        content.setBackground(SURFACE);
        dialog.setContentPane(content);

        // titolo
        JLabel title = new JLabel(data.getTitoloAnnuncio());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setForeground(TITLE);
        content.add(title, BorderLayout.NORTH);

        // Info
        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setOpaque(false);

        // Info list
        JPanel infoList = new JPanel(new GridLayout(0, 1, 0, 4));
        infoList.setOpaque(false);

        infoList.add(makeInfoLabel("Tipo annuncio: " + safe(data.getTipologiaAnnuncio())));
        infoList.add(makeInfoLabel("Data offerta: " + safe(data.getDataOfferta())));
        infoList.add(makeInfoLabel((ricevuta ? "Offerente: " : "Venditore: ") + safe(data.getControparteDisplay())));

        if (data.getImportoDisplay() != null) {
            String imp = data.getImportoDisplay().trim();
            if (!imp.isEmpty() && !imp.equals("-") && !imp.equals("- €")) {
                infoList.add(makeInfoLabel("Importo: " + imp));
                }
        }

        if (offerta.getOggettoOfferto() != null && !offerta.getOggettoOfferto().trim().isEmpty()) {
            infoList.add(makeInfoLabel("Oggetto offerto: " + offerta.getOggettoOfferto().trim()));
            }

        body.add(infoList, BorderLayout.NORTH);

        // Messaggio
        JPanel msgCard = new JPanel(new BorderLayout());
        msgCard.setOpaque(true);
        msgCard.setBackground(Color.WHITE);
        msgCard.putClientProperty(FlatClientProperties.STYLE, CARD_STYLE);
        msgCard.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        
        JTextArea txtMsg = new JTextArea(offerta.getMessaggio() != null ? offerta.getMessaggio() : "-");
        txtMsg.setLineWrap(true);
        txtMsg.setWrapStyleWord(true);
        txtMsg.setEditable(false);
        txtMsg.setOpaque(false);
        txtMsg.setBorder(null);

        JScrollPane scr = new JScrollPane(txtMsg);
        scr.setPreferredSize(new Dimension(500, 180));
        scr.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scr.setBorder(null);
        scr.setOpaque(false);
        scr.getViewport().setOpaque(false);


        msgCard.add(scr, BorderLayout.CENTER);
        body.add(msgCard, BorderLayout.CENTER);

        content.add(body, BorderLayout.CENTER);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        
        
        JButton btnVedi = new JButton("Vedi annuncio");
        styleSecondaryButton(btnVedi);
        btnVedi.addActionListener(e -> {
            dialog.dispose();
            if (ricevuta && ricevutaListener != null) {
                ricevutaListener.onApriAnnuncio(offerta);
            } else if (!ricevuta && inviataListener != null) {
                inviataListener.onApriAnnuncio(offerta);
            }
        });
        actions.add(btnVedi);

        if (ricevuta) {
            JButton btnAccetta = new JButton("Accetta");
            stylePrimaryButton(btnAccetta);
            btnAccetta.addActionListener(e -> {
                dialog.dispose();
                if (ricevutaListener != null) {
                    ricevutaListener.onAccetta(offerta);
                }
            });
            actions.add(btnAccetta);

            JButton btnRifiuta = new JButton("Rifiuta");
            styleDangerButton(btnRifiuta);
            btnRifiuta.addActionListener(e -> {
                dialog.dispose();
                if (ricevutaListener != null) {
                    ricevutaListener.onRifiuta(offerta);
                }
            });
            actions.add(btnRifiuta);
        } else {
            JButton btnModifica = new JButton("Modifica");
            stylePrimaryButton(btnModifica);
            btnModifica.addActionListener(e -> {
                dialog.dispose();
                if (inviataListener != null) {
                    inviataListener.onModifica(offerta);
                }
            });
            actions.add(btnModifica);

            JButton btnRitira = new JButton("Ritira");
            styleDangerButton(btnRitira);
            btnRitira.addActionListener(e -> {
                dialog.dispose();
                if (inviataListener != null) {
                    inviataListener.onRitira(offerta);
                }
            });
            actions.add(btnRitira);
        }

        JButton btnChiudi = new JButton("Chiudi");
        btnChiudi.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        btnChiudi.addActionListener(e -> dialog.dispose());
        actions.add(btnChiudi);

        content.add(actions, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    private JLabel makeInfoLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TITLE);
        return l;
    }

    private void stylePrimaryButton(JButton b) {
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; background: #1B415D; foreground: #FFFFFF; " +
                        "hoverBackground: #2A5E86; pressedBackground: #163245;");
        b.setFocusable(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
    }

    private void styleSecondaryButton(JButton b) {
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; background: #D7E3F2; foreground: #1B415D; " +
                        "hoverBackground: #C5D8EB; pressedBackground: #B0C9E0;");
        b.setFocusable(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
    }

    private void styleDangerButton(JButton b) {
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; background: #D93C25; foreground: #FFFFFF; " +
                        "hoverBackground: #B93522; pressedBackground: #8F2A1B;");
        b.setFocusable(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
    }

    
    
    // RESPONSIVE HELPERS
    private int computeCols(int availableWidth) {
        int contentW = availableWidth - 20;
        if (contentW < CARD_W)
            return 1;
        int cols = contentW / (CARD_W + HGAP);
        return Math.max(1, cols);
    }

    private void updateColsAndRelayout() {
        JScrollPane sp = null;
        for (Component c : getComponents()) {
            if (c instanceof JScrollPane) {
                sp = (JScrollPane) c;
                break;
            }
        }

        int w = (sp != null) ? sp.getViewport().getWidth() : getWidth();
        if (w <= 0)
            return;

        int newCols = computeCols(w);
        if (newCols != currentCols) {
            currentCols = newCols;
            refreshRicevute();
            refreshInviate();
            refreshRicevuteAccettate();
            refreshInviateAccettate();
        }
    }


    public static class OffertaNotificationData {
        private final Offerta offerta;
        private final String titoloAnnuncio;
        private final String tipologiaAnnuncio;
        private final String controparteDisplay;
        private final String dataOfferta;
        private final String importoDisplay;

        public OffertaNotificationData(Offerta offerta,
                String titoloAnnuncio,
                String tipologiaAnnuncio,
                String controparteDisplay,
                String dataOfferta,
                String importoDisplay) {
            this.offerta = offerta;
            this.titoloAnnuncio = titoloAnnuncio;
            this.tipologiaAnnuncio = tipologiaAnnuncio;
            this.controparteDisplay = controparteDisplay;
            this.dataOfferta = dataOfferta;
            this.importoDisplay = importoDisplay;
        }

        public OffertaNotificationData(Offerta offerta,
                String titoloAnnuncio,
                String controparteDisplay,
                String dataOfferta,
                String importoDisplay) {
            this(offerta, titoloAnnuncio, null, controparteDisplay, dataOfferta, importoDisplay);
        }

        public Offerta getOfferta() {
            return offerta;
        }

        public String getTitoloAnnuncio() {
            return titoloAnnuncio;
        }

        public String getTipologiaAnnuncio() {
            return tipologiaAnnuncio;
        }

        public String getControparteDisplay() {
            return controparteDisplay;
        }

        public String getDataOfferta() {
            return dataOfferta;
        }

        public String getImportoDisplay() {
            return importoDisplay;
        }
    }
}