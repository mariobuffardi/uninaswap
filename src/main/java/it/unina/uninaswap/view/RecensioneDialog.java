package it.unina.uninaswap.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatClientProperties;

import it.unina.uninaswap.util.UITheme;

import java.awt.*;


 // Dialog per inserire una recensione (valutazione 1..5 + commento) 
public class RecensioneDialog extends JDialog {

    private boolean confirmed = false;

    private final JTextField txtTitolo;
    private final JSpinner spValutazione;
    private final JTextArea txtCorpo;

    public RecensioneDialog(Window owner, String titoloFinestra) {
        super(owner, titoloFinestra, ModalityType.APPLICATION_MODAL);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(14, 14, 14, 14));
        content.setBackground(UITheme.BACKGROUND_LIGHT);
        setContentPane(content);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(UITheme.BACKGROUND_LIGHT);

        // Titolo
        JPanel rowTit = new JPanel(new BorderLayout(6, 6));
        rowTit.setBackground(UITheme.BACKGROUND_LIGHT);
        JLabel lblTitolo = new JLabel("Titolo:");
        lblTitolo.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTitolo.setForeground(UITheme.PRIMARY_DARK);
        rowTit.add(lblTitolo, BorderLayout.NORTH);
        txtTitolo = new JTextField(32);
        rowTit.add(txtTitolo, BorderLayout.CENTER);
        form.add(rowTit);

        form.add(Box.createVerticalStrut(8));

        // Valutazione
        JPanel rowVal = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowVal.setBackground(UITheme.BACKGROUND_LIGHT);
        JLabel lblVal = new JLabel("Valutazione:");
        lblVal.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblVal.setForeground(UITheme.PRIMARY_DARK);
        rowVal.add(lblVal);
        spValutazione = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
        spValutazione.setPreferredSize(new Dimension(60, spValutazione.getPreferredSize().height));
        rowVal.add(spValutazione);
        form.add(rowVal);

        form.add(Box.createVerticalStrut(8));

        // Corpo
        JPanel rowTxt = new JPanel(new BorderLayout(6, 6));
        rowTxt.setBackground(UITheme.BACKGROUND_LIGHT);
        JLabel lblCorpo = new JLabel("Corpo:");
        lblCorpo.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblCorpo.setForeground(UITheme.PRIMARY_DARK);
        rowTxt.add(lblCorpo, BorderLayout.NORTH);
        txtCorpo = new JTextArea(6, 32);
        txtCorpo.setLineWrap(true);
        txtCorpo.setWrapStyleWord(true);
        JScrollPane scr = new JScrollPane(txtCorpo);
        scr.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        rowTxt.add(scr, BorderLayout.CENTER);
        form.add(rowTxt);

        content.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(UITheme.BACKGROUND_LIGHT);
        JButton btnAnnulla = new JButton("Annulla");
        btnAnnulla.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; background: #D7E3F2; foreground: #1B415D; hoverBackground: #C5D8EB; pressedBackground: #B0C9E0;");
        btnAnnulla.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        JButton btnInvia = new JButton("Invia");
        btnInvia.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; background: #1B415D; foreground: #FFFFFF; hoverBackground: #2A5E86; pressedBackground: #163245;");
        btnInvia.addActionListener(e -> {
            if (getTitolo() == null || getTitolo().isBlank()) {
                JOptionPane.showMessageDialog(this,
                        "Inserisci un titolo.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (getCorpo() == null || getCorpo().isBlank()) {
                JOptionPane.showMessageDialog(this,
                        "Inserisci il corpo della recensione.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            confirmed = true;
            dispose();
        });

        actions.add(btnAnnulla);
        actions.add(btnInvia);
        content.add(actions, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getTitolo() {
        return txtTitolo.getText();
    }

    public int getValutazione() {
        return (Integer) spValutazione.getValue();
    }

    public String getCorpo() {
        return txtCorpo.getText();
    }
}