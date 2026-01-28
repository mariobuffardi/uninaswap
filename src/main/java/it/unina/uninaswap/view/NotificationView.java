package it.unina.uninaswap.view;

import it.unina.uninaswap.model.entity.Offerta;
import it.unina.uninaswap.util.UITheme;

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
    private static final int CARD_H = 125;
    private static final int HGAP = 12;
    private static final int VGAP = 12;

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

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        setBackground(UITheme.BACKGROUND_LIGHT);
        mainPanel.setBackground(UITheme.BACKGROUND_LIGHT);


        // OFFERTE RICEVUTE
        JPanel ricevuteSection = new JPanel(new BorderLayout());
        ricevuteSection.setBorder(new TitledBorder("Offerte ricevute (in attesa)"));

        ricevutePanel = new JPanel();
        ricevutePanel.setLayout(new GridLayout(0, currentCols, HGAP, VGAP));
        ricevutePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        ricevuteSection.add(ricevutePanel, BorderLayout.CENTER);
        mainPanel.add(ricevuteSection);
        mainPanel.add(Box.createVerticalStrut(16));

        // OFFERTE INVIATE (IN ATTESA)
        JPanel inviateSection = new JPanel(new BorderLayout());
        inviateSection.setBorder(new TitledBorder("Offerte inviate (in attesa)"));

        inviatePanel = new JPanel();
        inviatePanel.setLayout(new GridLayout(0, currentCols, HGAP, VGAP));
        inviatePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        inviateSection.add(inviatePanel, BorderLayout.CENTER);
        mainPanel.add(inviateSection);
        mainPanel.add(Box.createVerticalStrut(16));


        // OFFERTE RICEVUTE (ACCETTATE)
        JPanel ricevuteAccSection = new JPanel(new BorderLayout());
        ricevuteAccSection.setBorder(new TitledBorder("Offerte ricevute (Accettate)"));

        ricevuteAccettatePanel = new JPanel();
        ricevuteAccettatePanel.setLayout(new GridLayout(0, currentCols, HGAP, VGAP));
        ricevuteAccettatePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        ricevuteAccSection.add(ricevuteAccettatePanel, BorderLayout.CENTER);
        mainPanel.add(ricevuteAccSection);
        mainPanel.add(Box.createVerticalStrut(16));

        
        // OFFERTE INVIATE (ACCETTATE)
        JPanel inviateAccSection = new JPanel(new BorderLayout());
        inviateAccSection.setBorder(new TitledBorder("Offerte inviate (Accettate)"));

        inviateAccettatePanel = new JPanel();
        inviateAccettatePanel.setLayout(new GridLayout(0, currentCols, HGAP, VGAP));
        inviateAccettatePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

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
        buildSection(ricevutePanel, offerteRicevute, true,
                "Nessuna offerta ricevuta in attesa.");
    }

    private void refreshInviate() {
        buildSection(inviatePanel, offerteInviate, false,
                "Nessuna offerta inviata in attesa.");
    }

    private void refreshRicevuteAccettate() {
        buildSection(ricevuteAccettatePanel, offerteRicevuteAccettate, true,
                "Nessuna offerta ricevuta accettata.");
    }

    private void refreshInviateAccettate() {
        buildSection(inviateAccettatePanel, offerteInviateAccettate, false,
                "Nessuna offerta inviata accettata.");
    }

    private void buildSection(JPanel container, List<OffertaNotificationData> dataList, boolean ricevuta,
            String emptyMsg) {
        if (container == null)
            return;

        container.removeAll();

        if (dataList == null || dataList.isEmpty()) {
            JLabel lbl = new JLabel(emptyMsg, SwingConstants.LEFT);
            lbl.setBorder(new EmptyBorder(5, 5, 5, 5));
            container.setLayout(new BorderLayout());
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
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.PRIMARY, 2, true),
                new EmptyBorder(12, 12, 12, 12)));

        Dimension fixed = new Dimension(CARD_W, CARD_H);
        card.setPreferredSize(fixed);
        card.setMinimumSize(fixed);
        card.setMaximumSize(fixed);

        // TOP: titolo + meta (tipo + data)
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(8, 10, 0, 10));

        JLabel lblTitolo = new JLabel(data.getTitoloAnnuncio());
        lblTitolo.setFont(lblTitolo.getFont().deriveFont(Font.BOLD, 14f));
        lblTitolo.setForeground(UITheme.PRIMARY_DARK);
        top.add(lblTitolo);

        String tipo = (data.getTipologiaAnnuncio() == null || data.getTipologiaAnnuncio().isBlank())
                ? "-"
                : data.getTipologiaAnnuncio();

        String ruolo = ricevuta ? "Ricevuta" : "Inviata";
        JLabel lblMeta = new JLabel("Tipo: " + tipo + "  •  " + ruolo + " - " + safe(data.getDataOfferta()));
        lblMeta.setFont(lblMeta.getFont().deriveFont(Font.ITALIC, 11f));
        top.add(lblMeta);

        card.add(top, BorderLayout.NORTH);

        // CENTER
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(4, 10, 6, 10));

        String prefix = ricevuta ? "Da: " : "A: ";
        center.add(new JLabel(prefix + safe(data.getControparteDisplay())));

        // Importo
        if (data.getImportoDisplay() != null) {
            String imp = data.getImportoDisplay().trim();
            if (!imp.isEmpty() && !imp.equals("-") && !imp.equals("- €")) {
                center.add(new JLabel("Importo: " + imp));
            }
        }

        // Oggetto offerto 
        Offerta off = data.getOfferta();
        if (off != null && off.getOggettoOfferto() != null && !off.getOggettoOfferto().trim().isEmpty()) {
            center.add(new JLabel("Oggetto offerto: " + off.getOggettoOfferto().trim()));
        }

        // Prima riga del messaggio
        String preview = buildMessagePreview(off != null ? off.getMessaggio() : null);
        if (!preview.isBlank()) {
            JLabel lblMsg = new JLabel("Msg: " + preview);
            lblMsg.setFont(lblMsg.getFont().deriveFont(11f));
            center.add(lblMsg);
        }


        if (!ricevuta && off != null && off.getStato() != null && "Accettata".equals(off.getStato().name())) {
            boolean can = (inviataListener != null) && inviataListener.canLasciaRecensione(off);
            if (can) {
                JButton btnRec = new JButton("Lascia una recensione");
                btnRec.addActionListener(e -> {
                    if (inviataListener != null)
                        inviataListener.onLasciaRecensione(off);
                });
                center.add(Box.createVerticalStrut(4));
                center.add(btnRec);
            } else {
                JLabel lblDone = new JLabel("Recensione inviata");
                lblDone.setFont(lblDone.getFont().deriveFont(Font.ITALIC, 11f));
                center.add(Box.createVerticalStrut(4));
                center.add(lblDone);
            }
        }

        card.add(center, BorderLayout.CENTER);

        // Click card -> dialog dettagli
        // Se ricevuta ed è Accettata: non è più interagibile
        if (!(ricevuta && off != null && off.getStato() != null && "Accettata".equals(off.getStato().name()))) {
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showOffertaDialog(data, ricevuta);
                }
            });
        }

        return card;
    }

    private String safe(String s) {
        return (s == null) ? "-" : s;
    }

    private String buildMessagePreview(String msg) {
        if (msg == null)
            return "";
        String t = msg.trim();
        if (t.isEmpty())
            return "";
        // prima riga
        int idx = t.indexOf('\n');
        if (idx >= 0)
            t = t.substring(0, idx).trim();
        // limite caratteri
        if (t.length() > 60)
            t = t.substring(0, 60).trim() + "...";
        return t;
    }

    
    // DIALOG DETTAGLI
    private void showOffertaDialog(OffertaNotificationData data, boolean ricevuta) {
        Offerta offerta = data.getOfferta();
        if (offerta == null)
            return;

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Dettaglio offerta", Dialog.ModalityType.APPLICATION_MODAL);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(12, 12, 12, 12));
        dialog.setContentPane(content);

        // titolo
        JLabel title = new JLabel(data.getTitoloAnnuncio());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        content.add(title, BorderLayout.NORTH);

        // info
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        String tipo = (data.getTipologiaAnnuncio() == null || data.getTipologiaAnnuncio().isBlank())
                ? "-"
                : data.getTipologiaAnnuncio();

        info.add(new JLabel("Tipo annuncio: " + tipo));
        info.add(new JLabel("Data offerta: " + safe(data.getDataOfferta())));
        info.add(new JLabel((ricevuta ? "Offerente: " : "Venditore: ") + safe(data.getControparteDisplay())));

        if (data.getImportoDisplay() != null) {
            String imp = data.getImportoDisplay().trim();
            if (!imp.isEmpty() && !imp.equals("-") && !imp.equals("- €")) {
                info.add(new JLabel("Importo: " + imp));
            }
        }

        if (offerta.getOggettoOfferto() != null && !offerta.getOggettoOfferto().trim().isEmpty()) {
            info.add(new JLabel("Oggetto offerto: " + offerta.getOggettoOfferto().trim()));
        }

        info.add(Box.createVerticalStrut(8));

        JTextArea txtMsg = new JTextArea(offerta.getMessaggio() != null ? offerta.getMessaggio() : "-");
        txtMsg.setLineWrap(true);
        txtMsg.setWrapStyleWord(true);
        txtMsg.setEditable(false);
        JScrollPane scr = new JScrollPane(txtMsg);
        scr.setPreferredSize(new Dimension(520, 220));
        scr.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.add(info, BorderLayout.NORTH);

        JPanel msgBox = new JPanel(new BorderLayout());
        msgBox.setBorder(BorderFactory.createTitledBorder("Messaggio"));
        msgBox.add(scr, BorderLayout.CENTER);

        center.add(msgBox, BorderLayout.CENTER);

        content.add(center, BorderLayout.CENTER);

        // bottoni azione
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnVedi = new JButton("Vedi annuncio");
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
            btnAccetta.putClientProperty("JButton.buttonType", "default");
            btnAccetta.addActionListener(e -> {
                dialog.dispose();
                if (ricevutaListener != null) {
                    ricevutaListener.onAccetta(offerta);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Azione 'Accetta' non collegata al controller.",
                            "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });
            actions.add(btnAccetta);

            JButton btnRifiuta = new JButton("Rifiuta");
            btnRifiuta.setBackground(UITheme.ACCENT);
            btnRifiuta.setForeground(Color.WHITE);
            btnRifiuta.addActionListener(e -> {
                dialog.dispose();
                if (ricevutaListener != null) {
                    ricevutaListener.onRifiuta(offerta);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Azione 'Rifiuta' non collegata al controller.",
                            "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });
            actions.add(btnRifiuta);
        } else {
            JButton btnModifica = new JButton("Modifica");
            btnModifica.addActionListener(e -> {
                dialog.dispose();
                if (inviataListener != null) {
                    inviataListener.onModifica(offerta);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Azione 'Modifica' non collegata al controller.",
                            "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });
            actions.add(btnModifica);

            JButton btnRitira = new JButton("Ritira");
            btnRitira.addActionListener(e -> {
                dialog.dispose();
                if (inviataListener != null) {
                    inviataListener.onRitira(offerta);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Azione 'Ritira' non collegata al controller.",
                            "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });
            actions.add(btnRitira);
        }

        JButton btnChiudi = new JButton("Chiudi");
        btnChiudi.addActionListener(e -> dialog.dispose());
        actions.add(btnChiudi);

        content.add(actions, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    // RESPONSIVE HELPERS
    private int computeCols(int availableWidth) {
        int contentW = availableWidth - 20; // margine approx
        // CARD_W = 420
        if (contentW < CARD_W)
            return 1;
        int cols = contentW / (CARD_W + HGAP);
        return Math.max(1, cols);
    }

    private void updateColsAndRelayout() {
        if (ricevutePanel == null)
            return;

        Container parent = getParent();
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
            updatePanelLayout(ricevutePanel);
            updatePanelLayout(inviatePanel);
            updatePanelLayout(ricevuteAccettatePanel);
            updatePanelLayout(inviateAccettatePanel);
        }
    }

    private void updatePanelLayout(JPanel panel) {
        if (panel != null) {
            panel.setLayout(new GridLayout(0, currentCols, HGAP, VGAP));
            panel.revalidate();
            panel.repaint();
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