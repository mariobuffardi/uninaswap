package it.unina.uninaswap.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import it.unina.uninaswap.util.UITheme;

public class AnnunciFilterPanel extends JPanel {

    private JTextField boundSearchField;
    private JComboBox<String> boundCategoriaCombo;


    private JRadioButton rbTipoTutti;
    private JRadioButton rbVendita;
    private JRadioButton rbScambio;
    private JRadioButton rbRegalo;
    private JTextField txtPrezzoMin;
    private JTextField txtPrezzoMax;
    private JCheckBox chkSpedizione;
    private JCheckBox chkInUni;
    private JButton btnCerca;

    public AnnunciFilterPanel() {
        setPreferredSize(new Dimension(240, 0));
        setLayout(new GridBagLayout());
        setVisible(false);
        setBackground(UITheme.BACKGROUND_LIGHT);
        setBorder(new EmptyBorder(16, 12, 16, 12));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 12, 8, 12);
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        int row = 0;

        // Tipologia
        JLabel lblTipologia = createSectionLabel("Tipologia");
        c.gridx = 0;
        c.gridy = row++;
        add(lblTipologia, c);

        JPanel tipoPanel = new JPanel();
        tipoPanel.setLayout(new BoxLayout(tipoPanel, BoxLayout.Y_AXIS));
        tipoPanel.setOpaque(false);

        rbTipoTutti = new JRadioButton("Tutte", true);
        rbVendita = new JRadioButton("Vendita");
        rbScambio = new JRadioButton("Scambio");
        rbRegalo = new JRadioButton("Regalo");

        rbTipoTutti.setOpaque(false);
        rbVendita.setOpaque(false);
        rbScambio.setOpaque(false);
        rbRegalo.setOpaque(false);

        ButtonGroup gruppoTipo = new ButtonGroup();
        gruppoTipo.add(rbTipoTutti);
        gruppoTipo.add(rbVendita);
        gruppoTipo.add(rbScambio);
        gruppoTipo.add(rbRegalo);

        tipoPanel.add(rbTipoTutti);
        tipoPanel.add(rbVendita);
        tipoPanel.add(rbScambio);
        tipoPanel.add(rbRegalo);

        c.gridx = 0;
        c.gridy = row++;
        add(tipoPanel, c);

        c.gridx = 0;
        c.gridy = row++;
        c.insets = new Insets(12, 0, 12, 0);
        add(createLightSeparator(), c);
        c.insets = new Insets(8, 12, 8, 12);

        // Prezzo minimo
        JLabel lblPrezzoMin = createSectionLabel("Prezzo min");
        c.gridx = 0;
        c.gridy = row++;
        add(lblPrezzoMin, c);

        txtPrezzoMin = new JTextField();
        txtPrezzoMin.setPreferredSize(new Dimension(0, 32));
        c.gridx = 0;
        c.gridy = row++;
        add(txtPrezzoMin, c);

        // Prezzo massimo
        JLabel lblPrezzoMax = createSectionLabel("Prezzo max");
        c.gridx = 0;
        c.gridy = row++;
        add(lblPrezzoMax, c);

        txtPrezzoMax = new JTextField();
        txtPrezzoMax.setPreferredSize(new Dimension(0, 32));
        c.gridx = 0;
        c.gridy = row++;
        add(txtPrezzoMax, c);

        c.gridx = 0;
        c.gridy = row++;
        c.insets = new Insets(12, 0, 12, 0);
        add(createLightSeparator(), c);
        c.insets = new Insets(8, 12, 8, 12);

        // Consegna
        JLabel lblConsegna = createSectionLabel("Consegna");
        c.gridx = 0;
        c.gridy = row++;
        add(lblConsegna, c);

        chkSpedizione = new JCheckBox("Offre spedizione");
        chkInUni = new JCheckBox("Incontro in uni");
        chkSpedizione.setOpaque(false);
        chkInUni.setOpaque(false);

        JPanel consegnaPanel = new JPanel();
        consegnaPanel.setLayout(new BoxLayout(consegnaPanel, BoxLayout.Y_AXIS));
        consegnaPanel.setOpaque(false);
        consegnaPanel.add(chkSpedizione);
        consegnaPanel.add(chkInUni);

        c.gridx = 0;
        c.gridy = row++;
        add(consegnaPanel, c);

        c.gridx = 0;
        c.gridy = row++;
        c.insets = new Insets(12, 0, 12, 0);
        add(createLightSeparator(), c);
        c.insets = new Insets(8, 12, 8, 12);


        btnCerca = new JButton("Cerca");
        btnCerca.putClientProperty("JButton.buttonType", "default");
        btnCerca.setPreferredSize(new Dimension(0, 36));
        c.gridx = 0;
        c.gridy = row++;
        add(btnCerca, c);
    }

    public void bindTopSearchControls(JTextField searchField, JComboBox<String> categoriaCombo) {
        this.boundSearchField = searchField;
        this.boundCategoriaCombo = categoriaCombo;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        label.setForeground(UITheme.PRIMARY_DARK);
        return label;
    }

    private JSeparator createLightSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER_LIGHT);
        return sep;
    }


    public String getSearchText() {
        if (boundSearchField == null) return "";
        return boundSearchField.getText().trim();
    }

    public String getSelectedCategoria() {
        if (boundCategoriaCombo == null) return null;

        String value = (String) boundCategoriaCombo.getSelectedItem();
        if (value == null) return null;

        if ("Tutte".equals(value)) return null;
        return value;
    }

    public String getSelectedTipologia() {
        if (rbVendita.isSelected()) {
            return "Vendita";
        } else if (rbScambio.isSelected()) {
            return "Scambio";
        } else if (rbRegalo.isSelected()) {
            return "Regalo";
        } else {
            return null;
        }
    }

    public String getPrezzoMinText() {
        String text = txtPrezzoMin.getText().trim();
        return text.isEmpty() ? null : text;
    }

    public String getPrezzoMaxText() {
        String text = txtPrezzoMax.getText().trim();
        return text.isEmpty() ? null : text;
    }

    public boolean isSpedizioneSelected() {
        return chkSpedizione.isSelected();
    }

    public boolean isInUniSelected() {
        return chkInUni.isSelected();
    }

    public JButton getBtnCerca() {
        return btnCerca;
    }
}
