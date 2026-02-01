package it.unina.uninaswap.view;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;


import it.unina.uninaswap.model.entity.Annuncio;
import it.unina.uninaswap.model.entity.Indirizzo;
import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.util.UITheme;


public class AccettaOffertaDialog extends JDialog {

    public static class Result {
        private final boolean spedizione;
        private final boolean incontroInUni;
        private final Integer idIndirizzo;
        private final LocalDate dataIncontro;
        private final String sedeUniversita;
        private final String fasciaOraria;

        public Result(boolean spedizione,
                      boolean incontroInUni,
                      Integer idIndirizzo,
                      LocalDate dataIncontro,
                      String sedeUniversita,
                      String fasciaOraria) {
            this.spedizione = spedizione;
            this.incontroInUni = incontroInUni;
            this.idIndirizzo = idIndirizzo;
            this.dataIncontro = dataIncontro;
            this.sedeUniversita = sedeUniversita;
            this.fasciaOraria = fasciaOraria;
        }

        public boolean isSpedizione()     { return spedizione; }
        public boolean isIncontroInUni()  { return incontroInUni; }
        public Integer getIdIndirizzo()   { return idIndirizzo; }
        public LocalDate getDataIncontro(){ return dataIncontro; }
        public String getSedeUniversita() { return sedeUniversita; }
        public String getFasciaOraria()   { return fasciaOraria; }
    }


    private JRadioButton rdbSpedizione;
    private JRadioButton rdbInUni;

    private JComboBox<Indirizzo> cmbIndirizzi;

    private JTextField txtDataIncontro;
    private JTextField txtSedeUni;
    private JTextField txtFasciaOraria;

    private Result result = null;

    public AccettaOffertaDialog(AnnunciMainView parent, Annuncio annuncio, Studente acquirente, List<Indirizzo> indirizziAcquirente) {

        super(parent, "Accetta offerta - modalità consegna", true);

        setSize(520, 400);
        setLocationRelativeTo(parent);

        JPanel content = new JPanel();
        content.setBorder(new EmptyBorder(14,14,14,14));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(it.unina.uninaswap.util.UITheme.BACKGROUND_LIGHT);
        setContentPane(content);

        // Header
        JLabel lblTitolo = new JLabel(
                "<html><b>Annuncio:</b> " + annuncio.getTitolo() +
                "<br/><b>Acquirente:</b> " + acquirente.getNome() + " " + acquirente.getCognome() +
                " (" + acquirente.getMatricola() + ")</html>"
        );
        lblTitolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTitolo.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitolo.setForeground(it.unina.uninaswap.util.UITheme.PRIMARY_DARK);
        content.add(lblTitolo);
        content.add(Box.createVerticalStrut(10));

        // Info preferenze
        String pref = "";
        if (acquirente.getPreferisceSpedizione()) pref += "Spedizione ";
        if (acquirente.getPreferisceIncontroInUni()) {
            if (!pref.isEmpty()) pref += "- ";
            pref += "Incontro in Uni";
        }
        JLabel lblPref = new JLabel("Preferenze acquirente: " + (pref.isBlank() ? "-" : pref));
        lblPref.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPref.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblPref.setForeground(it.unina.uninaswap.util.UITheme.TEXT_SECONDARY);
        content.add(lblPref);
        content.add(Box.createVerticalStrut(10));

        // Radio buttons modalità
        boolean canSpedire = acquirente.getPreferisceSpedizione()
                && indirizziAcquirente != null
                && !indirizziAcquirente.isEmpty();

        boolean canInUni = acquirente.getPreferisceIncontroInUni();

        rdbSpedizione = new JRadioButton("Spedizione all'indirizzo salvato");
        rdbInUni      = new JRadioButton("Incontro in università");

        rdbSpedizione.setEnabled(canSpedire);
        rdbInUni.setEnabled(canInUni);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rdbSpedizione);
        bg.add(rdbInUni);

        // default
        if (canSpedire) {
            rdbSpedizione.setSelected(true);
        } else if (canInUni) {
            rdbInUni.setSelected(true);
        }

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        radioPanel.add(rdbSpedizione);
        radioPanel.add(rdbInUni);

        content.add(radioPanel);
        content.add(Box.createVerticalStrut(10));

        // Pannello SPEDIZIONE
        JPanel panelSpedizione = new JPanel(new BorderLayout(5,5));
        panelSpedizione.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT), "Spedizione"));
        panelSpedizione.setBackground(UITheme.WHITE);
        cmbIndirizzi = new JComboBox<>();
        if (indirizziAcquirente != null) {
            for (Indirizzo ind : indirizziAcquirente) {
                cmbIndirizzi.addItem(ind);
            }
        }

        // Renderer per mostrare l'indirizzo in modo leggibile
        cmbIndirizzi.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Indirizzo) {
                    Indirizzo i = (Indirizzo) value;
                    String label = i.getVia() + " " + i.getCivico()
                            + " - " + i.getCitta() + " (" + i.getCap() + ")";
                    setText(label);
                }
                return this;
            }
        });

        panelSpedizione.add(new JLabel("Indirizzo di spedizione:"), BorderLayout.NORTH);
        panelSpedizione.add(cmbIndirizzi, BorderLayout.CENTER);

        content.add(panelSpedizione);
        content.add(Box.createVerticalStrut(10));

        // Pannello INCONTRO IN UNI
        JPanel panelInUni = new JPanel();
        panelInUni.setLayout(new BoxLayout(panelInUni, BoxLayout.Y_AXIS));
        panelInUni.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT), "Incontro in università"));
        panelInUni.setBackground(UITheme.WHITE);
        panelInUni.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtDataIncontro = new JTextField();
        txtSedeUni      = new JTextField("Università Federico II - Monte Sant'Angelo");
        txtFasciaOraria = new JTextField("10:00 - 12:00");

        panelInUni.add(labeledRow("Data incontro (YYYY-MM-DD):", txtDataIncontro));
        panelInUni.add(Box.createVerticalStrut(5));
        panelInUni.add(labeledRow("Sede universitaria:", txtSedeUni));
        panelInUni.add(Box.createVerticalStrut(5));
        panelInUni.add(labeledRow("Fascia oraria:", txtFasciaOraria));

        content.add(panelInUni);
        content.add(Box.createVerticalStrut(10));

        // Abilita/disabilita sottopannelli in base alla modalità
        Runnable togglePanels = () -> {
            boolean useSped = rdbSpedizione.isSelected() && rdbSpedizione.isEnabled();
            setEnabledRecursive(panelSpedizione, useSped);

            boolean useInUni = rdbInUni.isSelected() && rdbInUni.isEnabled();
            setEnabledRecursive(panelInUni, useInUni);
        };
        rdbSpedizione.addActionListener(e -> togglePanels.run());
        rdbInUni.addActionListener(e -> togglePanels.run());
        togglePanels.run();

        // Bottoni
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(UITheme.BACKGROUND_LIGHT);
        JButton btnAnnulla = new JButton("Annulla");
        JButton btnConferma = new JButton("Conferma");
        btnConferma.setFocusable(false);
        btnConferma.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE,
            "arc: 12; background: #1B415D; foreground: #FFFFFF; hoverBackground: #2A5E86; pressedBackground: #163245;");
        btnAnnulla.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE,
            "arc: 12; background: #D93C25; foreground: #FFFFFF; hoverBackground: #B93522; pressedBackground: #8F2A1B;");
        btnAnnulla.setFocusable(false);
        btnAnnulla.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        btnAnnulla.setBorderPainted(false);
        btnAnnulla.setContentAreaFilled(true);
        btnAnnulla.setOpaque(false);

        buttons.add(btnAnnulla);
        buttons.add(btnConferma);
        content.add(buttons);

        btnAnnulla.addActionListener(e -> {
            result = null;
            dispose();
        });

        btnConferma.addActionListener(e -> onConfirm());

        // Se nessuna modalità è disponibile, avvisa e chiudi
        if (!canSpedire && !canInUni) {
            JOptionPane.showMessageDialog(this,
                    "L'acquirente non ha una modalità di consegna valida (spedizione o incontro in uni).",
                    "Impossibile accettare",
                    JOptionPane.ERROR_MESSAGE);
            result = null;
            dispose();
        }
    }

    private JPanel labeledRow(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(5,5));
        p.add(new JLabel(label), BorderLayout.WEST);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private static void setEnabledRecursive(Container container, boolean enabled) {
        if (container == null) return;

        // abilita/disabilita il container stesso
        container.setEnabled(enabled);

        // abilita/disabilita ricorsivamente i figli
        for (Component c : container.getComponents()) {
            c.setEnabled(enabled);
            if (c instanceof Container) {
                setEnabledRecursive((Container) c, enabled);
            }
        }
    }

    private void onConfirm() {
        try {
            if (rdbSpedizione.isSelected() && rdbSpedizione.isEnabled()) {
            	
                // SPEDIZIONE
                Indirizzo sel = (Indirizzo) cmbIndirizzi.getSelectedItem();
                if (sel == null) {
                    JOptionPane.showMessageDialog(this,
                            "Seleziona un indirizzo di spedizione.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                result = new Result(
                        true,   // spedizione
                        false,  // incontroInUni
                        sel.getId(),
                        null,   // dataIncontro
                        null,   // sede
                        null    // fascia
                );
                dispose();
                return;
            }

            if (rdbInUni.isSelected() && rdbInUni.isEnabled()) {
            	
                // INCONTRO IN UNI
                String dataStr = txtDataIncontro.getText().trim();
                String sede = txtSedeUni.getText().trim();
                String fascia = txtFasciaOraria.getText().trim();

                if (dataStr.isEmpty() || sede.isEmpty() || fascia.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Compila data, sede universitaria e fascia oraria.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LocalDate dataIncontro;
                try {
                    dataIncontro = LocalDate.parse(dataStr);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Data non valida. Usa il formato YYYY-MM-DD.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                result = new Result(
                        false, // spedizione
                        true, // incontroInUni
                        null, // idIndirizzo
                        dataIncontro,
                        sede,
                        fascia
                );
                dispose();
                return;
            }

            // Se arrivi qui, nessuna modalità selezionata
            JOptionPane.showMessageDialog(this,
                    "Seleziona una modalità di consegna.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Errore nella conferma della modalità di consegna:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public Result getResult() {
        return result;
    }
}