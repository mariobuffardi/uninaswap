package it.unina.uninaswap.view;

import it.unina.uninaswap.model.entity.Annuncio;
import it.unina.uninaswap.model.entity.Offerta;
import it.unina.uninaswap.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.formdev.flatlaf.FlatClientProperties;

import java.awt.*;
import java.math.BigDecimal;

public class OffertaEditDialog extends JDialog {

    private final Offerta originalOfferta;
    private final Annuncio annuncio;

    private JTextField txtImporto;
    private JTextArea txtMessaggio;
    private JTextField txtOggettoOfferto;

    private JPanel rowImporto;
    private JPanel rowMessaggio;
    private JPanel rowOggetto;

    private JLabel lblHint;

    private boolean confirmed = false;
    private Offerta editedOfferta;

    public OffertaEditDialog(JFrame parent, Offerta offerta, Annuncio annuncio) {
        super(parent, computeDialogTitle(offerta, annuncio), true);
        this.originalOfferta = offerta;
        this.annuncio = annuncio;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(root);


        // HEADER
        String headerTitleText = computeDialogTitle(originalOfferta, this.annuncio);
        JPanel header = new JPanel(new BorderLayout(0, 6));
        JLabel title = new JLabel(headerTitleText);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(UITheme.PRIMARY_DARK);

        JLabel subtitle = new JLabel(
                "<html><b>Annuncio:</b> " + safe(this.annuncio != null ? this.annuncio.getTitolo() : null) +
                " &nbsp; <span style='color:gray'>( " + safe(this.annuncio != null ? this.annuncio.getTipologia() : null) + " )</span></html>"
        );
        subtitle.setForeground(UITheme.TEXT_SECONDARY);
        
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.CENTER);
        root.add(header, BorderLayout.NORTH);


        // FORM
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(10, 0, 10, 0));
        root.setBackground(UITheme.BACKGROUND_LIGHT);
        root.add(form, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        txtImporto = new JTextField();
        txtOggettoOfferto = new JTextField();

        txtMessaggio = new JTextArea(5, 24);
        txtMessaggio.setLineWrap(true);
        txtMessaggio.setWrapStyleWord(true);
        JScrollPane spMsg = new JScrollPane(txtMessaggio);
        spMsg.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        rowImporto = buildRow("Importo offerto (€)", txtImporto);
        rowMessaggio = buildRow("Messaggio", spMsg);
        rowOggetto = buildRow("Oggetto offerto", txtOggettoOfferto);

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(rowImporto, gbc);

        gbc.gridy++;
        form.add(rowMessaggio, gbc);

        gbc.gridy++;
        form.add(rowOggetto, gbc);

        // Hint
        lblHint = new JLabel();
        lblHint.setBorder(new EmptyBorder(6, 10, 0, 10));
        lblHint.setForeground(new Color(80, 80, 80));
        root.add(lblHint, BorderLayout.SOUTH);


        // BUTTONS
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setBackground(UITheme.BACKGROUND_LIGHT);
        JButton btnAnnulla = new JButton("Annulla");
        boolean isEdit = (originalOfferta != null && originalOfferta.getId() > 0);
        JButton btnConferma = new JButton(isEdit ? "Salva" : "Invia");
        btnConferma.putClientProperty(FlatClientProperties.STYLE,
            "arc: 12; background: #1B415D; foreground: #FFFFFF; hoverBackground: #2A5E86; pressedBackground: #163245;");
        btnConferma.setFocusable(false);
        btnAnnulla.putClientProperty(FlatClientProperties.STYLE,
            "arc: 12; background: #D93C25; foreground: #FFFFFF; hoverBackground: #B93522; pressedBackground: #8F2A1B;");
        btnAnnulla.setFocusable(false);
        btnAnnulla.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        btnAnnulla.setBorderPainted(false);
        btnAnnulla.setContentAreaFilled(true);
        btnAnnulla.setOpaque(false);
        root.add(buttons, BorderLayout.PAGE_END);

        buttons.add(btnAnnulla);

        buttons.add(btnConferma);
        getRootPane().setDefaultButton(btnConferma);
        
        prefillFields();
        applyTipologiaUI();

        btnAnnulla.addActionListener(e -> {
            confirmed = false;
            editedOfferta = null;
            dispose();
        });

        btnConferma.addActionListener(e -> onSave());

        pack();
        setMinimumSize(new Dimension(560, getPreferredSize().height));
        setLocationRelativeTo(parent);
    }
    



     // Titolo coerente con l'azione
    private static String computeDialogTitle(Offerta offerta, Annuncio annuncio) {
        boolean isEdit = (offerta != null && offerta.getId() > 0);
        if (isEdit) return "Modifica offerta";

        String tipo = (annuncio != null) ? annuncio.getTipologia() : null;
        if ("Vendita".equalsIgnoreCase(tipo)) return "Fai un'offerta";
        if ("Scambio".equalsIgnoreCase(tipo)) return "Proponi scambio";
        if ("Regalo".equalsIgnoreCase(tipo)) return "Richiedi";

        return "Nuova offerta";
    }


    private JPanel buildRow(String label, Component field) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setBorder(new TitledBorder(label));
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void applyTipologiaUI() {
        String tipo = (annuncio != null) ? annuncio.getTipologia() : null;

        boolean vendita = "Vendita".equalsIgnoreCase(tipo);
        boolean scambio = "Scambio".equalsIgnoreCase(tipo);
        boolean regalo  = "Regalo".equalsIgnoreCase(tipo);


        rowImporto.setVisible(vendita);
        rowOggetto.setVisible(scambio);
        rowMessaggio.setVisible(regalo || scambio || vendita); 

        if (vendita) {
            lblHint.setText("Vendita: importo obbligatorio. Messaggio facoltativo.");
        } else if (scambio) {
            lblHint.setText("Scambio: oggetto offerto obbligatorio. Importo deve restare vuoto.");
        } else if (regalo) {
            lblHint.setText("Regalo: messaggio obbligatorio. Importo e oggetto offerto devono restare vuoti.");
        } else {
            lblHint.setText("Compila i dati dell'offerta.");
        }

        revalidate();
        repaint();
    }

    private void prefillFields() {
        if (originalOfferta == null) return;

        if (originalOfferta.getImportoOfferto() != null) {
            txtImporto.setText(originalOfferta.getImportoOfferto().toPlainString());
        }
        if (originalOfferta.getMessaggio() != null) {
            txtMessaggio.setText(originalOfferta.getMessaggio());
        }
        if (originalOfferta.getOggettoOfferto() != null) {
            txtOggettoOfferto.setText(originalOfferta.getOggettoOfferto());
        }
    }

    private void onSave() {
        try {
            if (originalOfferta == null) {
                JOptionPane.showMessageDialog(this,
                        "Errore interno: offerta non inizializzata.",
                        "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String tipo = (annuncio != null) ? annuncio.getTipologia() : null;

            boolean vendita = "Vendita".equalsIgnoreCase(tipo);
            boolean scambio = "Scambio".equalsIgnoreCase(tipo);
            boolean regalo  = "Regalo".equalsIgnoreCase(tipo);

            String sImporto = (txtImporto.getText() != null) ? txtImporto.getText().trim() : "";
            String msg = (txtMessaggio.getText() != null) ? txtMessaggio.getText().trim() : "";
            String obj = (txtOggettoOfferto.getText() != null) ? txtOggettoOfferto.getText().trim() : "";


            if (vendita) {
                if (sImporto.isBlank()) {
                    JOptionPane.showMessageDialog(this,
                            "Per una vendita l'importo è obbligatorio.",
                            "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                BigDecimal nuovoImporto = new BigDecimal(sImporto);
                if (nuovoImporto.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(this,
                            "L'importo non può essere negativo.",
                            "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // sicurezza coerente con DB
                obj = "";
            }

            if (scambio) {
                if (obj.isBlank()) {
                    JOptionPane.showMessageDialog(this,
                            "Per uno scambio l'oggetto offerto è obbligatorio.",
                            "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // sicurezza coerente con DB
                sImporto = "";
            }

            if (regalo) {
                if (msg.isBlank()) {
                    JOptionPane.showMessageDialog(this,
                            "Per un regalo il messaggio è obbligatorio.",
                            "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // sicurezza coerente con DB
                sImporto = "";
                obj = "";
            }

            Offerta o = new Offerta();
            o.setId(originalOfferta.getId());
            o.setData(originalOfferta.getData());
            o.setStato(originalOfferta.getStato());
            o.setIdAnnuncio(originalOfferta.getIdAnnuncio());
            o.setMatricolaOfferente(originalOfferta.getMatricolaOfferente());

            // importo: se vuoto -> null
            if (!sImporto.isBlank()) {
                BigDecimal bd = new BigDecimal(sImporto);
                if (bd.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(this,
                            "L'importo non può essere negativo.",
                            "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                o.setImportoOfferto(bd);
            } else {
                o.setImportoOfferto(null);
            }

            o.setMessaggio(msg.isBlank() ? null : msg);
            o.setOggettoOfferto(obj.isBlank() ? null : obj);

            this.editedOfferta = o;
            this.confirmed = true;
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Importo non valido. Usa un numero, es. 10 o 10.50",
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }


    private String safe(String s) {
        return (s == null) ? "" : s;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Offerta getEditedOfferta() {
        return editedOfferta;
    }
}