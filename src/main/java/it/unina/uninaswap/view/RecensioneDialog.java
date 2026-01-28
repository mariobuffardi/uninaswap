package it.unina.uninaswap.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        content.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(content);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        // Titolo
        JPanel rowTit = new JPanel(new BorderLayout(6, 6));
        rowTit.add(new JLabel("Titolo:"), BorderLayout.NORTH);
        txtTitolo = new JTextField(32);
        rowTit.add(txtTitolo, BorderLayout.CENTER);
        form.add(rowTit);

        form.add(Box.createVerticalStrut(8));

        // Valutazione
        JPanel rowVal = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowVal.add(new JLabel("Valutazione:"));
        spValutazione = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
        spValutazione.setPreferredSize(new Dimension(60, spValutazione.getPreferredSize().height));
        rowVal.add(spValutazione);
        form.add(rowVal);

        form.add(Box.createVerticalStrut(8));

        // Corpo
        JPanel rowTxt = new JPanel(new BorderLayout(6, 6));
        rowTxt.add(new JLabel("Corpo:"), BorderLayout.NORTH);
        txtCorpo = new JTextArea(6, 32);
        txtCorpo.setLineWrap(true);
        txtCorpo.setWrapStyleWord(true);
        JScrollPane scr = new JScrollPane(txtCorpo);
        scr.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        rowTxt.add(scr, BorderLayout.CENTER);
        form.add(rowTxt);

        content.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAnnulla = new JButton("Annulla");
        btnAnnulla.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        JButton btnInvia = new JButton("Invia");
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