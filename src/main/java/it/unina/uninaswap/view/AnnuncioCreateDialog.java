package it.unina.uninaswap.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.FlatClientProperties;

import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.util.ImageUtil;
import it.unina.uninaswap.util.UITheme;

public class AnnuncioCreateDialog extends JDialog {

    private JTextField txtTitolo;
    private JTextArea txtDescrizione;
    private JComboBox<String> cmbTipologia;
    private JComboBox<String> cmbCategoria;
    private JTextField txtPrezzo;
    private JTextField txtOggettoRichiesto;
    private JCheckBox chkSpedizione;
    private JCheckBox chkInUni;

    private JButton btnCrea;
    private JButton btnAnnulla;

    // Foto annuncio
    private JButton btnAggiungiFoto;
    private JPanel thumbnailContainer;
    private final List<FotoWrapper> fotoWrappers = new ArrayList<>();

    private boolean confirmed = false;

    private final Studente venditore; // studente loggato

    // Wrapper interno per gestire foto prima del salvataggio
    private static class FotoWrapper {
        File sourceFile; // file originale selezionato dall'utente
        String destinationFilename; // nome file destinazione
        boolean isPrincipale;

        FotoWrapper(File sourceFile, String destinationFilename, boolean isPrincipale) {
            this.sourceFile = sourceFile;
            this.destinationFilename = destinationFilename;
            this.isPrincipale = isPrincipale;
        }
    }

    public AnnuncioCreateDialog(JFrame parent, Studente venditore) {
        super(parent, "Nuovo annuncio", true);
        this.venditore = venditore;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(620, 650);
        setLocationRelativeTo(parent);

        buildUI();
        initListeners();
        updateFieldsByTipologia();
        refreshThumbnailPanel();
    }

    private void buildUI() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(14, 14, 14, 14));
        content.setBackground(UITheme.BACKGROUND_LIGHT);
        setContentPane(content);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        content.add(form, BorderLayout.CENTER);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.LINE_END;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;

        int row = 0;

        // Titolo
        c.gridx = 0;
        c.gridy = row;
        JLabel lblTitolo = new JLabel("Titolo*");
        lblTitolo.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitolo.setForeground(UITheme.PRIMARY_DARK);
        form.add(lblTitolo, c);
        
        c.gridx = 1;
        c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        txtTitolo = new JTextField(30);
        form.add(txtTitolo, c);
        row++;

        // Tipologia
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = row;
        JLabel lblTipologia = new JLabel("Tipologia*");
        lblTipologia.setForeground(UITheme.TEXT_SECONDARY);
        form.add(lblTipologia, c);
        
        c.gridx = 1;
        c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        cmbTipologia = new JComboBox<>(new String[] { "Vendita", "Scambio", "Regalo" });
        form.add(cmbTipologia, c);
        row++;

        // Categoria
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = row;
        JLabel lblCategoria = new JLabel("Categoria*");
        lblCategoria.setForeground(UITheme.TEXT_SECONDARY);
        form.add(lblCategoria, c);
        
        c.gridx = 1;
        c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        cmbCategoria = new JComboBox<>(new String[] {
                "Strumenti_musicali",
                "Libri",
                "Informatica",
                "Abbigliamento",
                "Arredo",
                "Altro"
        });
        form.add(cmbCategoria, c);
        row++;

        // Prezzo (solo Vendita)
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = row;
        JLabel lblPrezzo = new JLabel("Prezzo (€)");
        lblPrezzo.setForeground(UITheme.TEXT_SECONDARY);
        form.add(lblPrezzo, c);
        
        c.gridx = 1;
        c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        txtPrezzo = new JTextField(10);
        form.add(txtPrezzo, c);
        row++;

        // Oggetto richiesto (solo Scambio)
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = row;
        JLabel lblOggetto = new JLabel("Oggetto richiesto");
        lblOggetto.setForeground(UITheme.TEXT_SECONDARY);
        form.add(lblOggetto, c);
        
        c.gridx = 1;
        c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        txtOggettoRichiesto = new JTextField(30);
        form.add(txtOggettoRichiesto, c);
        row++;

        // Consegna 
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = row;
        JLabel lblConsegna = new JLabel("Consegna*");
        lblConsegna.setForeground(UITheme.TEXT_SECONDARY);
        form.add(lblConsegna, c);
        
        c.gridx = 1;
        c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        JPanel consegnaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        chkSpedizione = new JCheckBox("Spedizione");
        chkInUni = new JCheckBox("Incontro in Uni");
        consegnaPanel.add(chkSpedizione);
        consegnaPanel.add(chkInUni);
        form.add(consegnaPanel, c);
        row++;

        // Descrizione 
        c.gridx = 0;
        c.gridy = row;
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;
        JLabel lblDescrizione = new JLabel("Descrizione");
        lblDescrizione.setForeground(UITheme.TEXT_SECONDARY);
        form.add(lblDescrizione, c);
        
        c.gridx = 1;
        c.gridy = row;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        txtDescrizione = new JTextArea(5, 30);
        txtDescrizione.setLineWrap(true);
        txtDescrizione.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescrizione);
        form.add(scrollDesc, c);

        // Foto (con thumbnail visuali)
        row++;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = row;
        c.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Foto"), c);

        c.gridx = 1;
        c.gridy = row;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 0.0;
        form.add(buildFotoPanel(), c);

        // Bottoni
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        buttons.setBackground(it.unina.uninaswap.util.UITheme.BACKGROUND_LIGHT);
        btnAnnulla = new JButton("Annulla");
        btnCrea = new JButton("Crea");
        btnCrea.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; background: #1B415D; foreground: #FFFFFF; hoverBackground: #2A5E86; pressedBackground: #163245;");
        btnAnnulla.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; background: #D93C25; foreground: #FFFFFF; hoverBackground: #B93522; pressedBackground: #8F2A1B;");
        btnAnnulla.setFocusable(false);
        btnCrea.setFocusable(false);
        btnAnnulla.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAnnulla.setBorderPainted(false);
        btnAnnulla.setContentAreaFilled(true);
        btnAnnulla.setOpaque(false);        
        buttons.add(btnAnnulla);
        buttons.add(btnCrea);
        content.add(buttons, BorderLayout.SOUTH);
    }

    private void initListeners() {
        cmbTipologia.addActionListener(e -> updateFieldsByTipologia());

        btnAggiungiFoto.addActionListener(e -> addPhotosFromChooser());

        btnAnnulla.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        btnCrea.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
    }

    private JPanel buildFotoPanel() {
        JPanel p = new JPanel(new BorderLayout(5, 5));

        // Container orizzontale con scroll
        thumbnailContainer = new JPanel();
        thumbnailContainer.setLayout(new BoxLayout(thumbnailContainer, BoxLayout.X_AXIS));
        thumbnailContainer.setBorder(new EmptyBorder(5, 5, 5, 5));

        JScrollPane scroll = new JScrollPane(thumbnailContainer);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scroll.setPreferredSize(new Dimension(560, 220));
        scroll.setMinimumSize(new Dimension(560, 220));
        scroll.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        p.add(scroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        btnAggiungiFoto = new JButton("Aggiungi foto...");
        btnAggiungiFoto.setFocusable(false);
        actions.add(btnAggiungiFoto);
        p.add(actions, BorderLayout.SOUTH);

        return p;
    }

    private void refreshThumbnailPanel() {
        thumbnailContainer.removeAll();

        if (fotoWrappers.isEmpty()) {
            JLabel lbl = new JLabel("Nessuna foto selezionata (verrà usata l'immagine di default per categoria)");
            lbl.setForeground(Color.GRAY);
            thumbnailContainer.add(lbl);
        } else {
            for (int i = 0; i < fotoWrappers.size(); i++) {
                final int index = i;
                FotoWrapper fw = fotoWrappers.get(i);

                JPanel thumbCard = createThumbnailCard(fw, index);
                thumbnailContainer.add(thumbCard);
                thumbnailContainer.add(Box.createHorizontalStrut(8));
            }
        }

        thumbnailContainer.revalidate();
        thumbnailContainer.repaint();
    }

    private JPanel createThumbnailCard(FotoWrapper fw, int index) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(160, 190));
        card.setMaximumSize(new Dimension(160, 190));

        if (fw.isPrincipale) {
            card.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34), 2, true));
        } else {
            card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        }

        JButton btnRemove = new JButton("×");
        btnRemove.setFont(new Font("Arial", Font.BOLD, 16));
        btnRemove.setPreferredSize(new Dimension(26, 26));
        btnRemove.setMargin(new Insets(0, 0, 0, 0));
        btnRemove.setFocusPainted(false);
        btnRemove.setFocusable(false);
        btnRemove.setBackground(new Color(220, 53, 69));
        btnRemove.setForeground(Color.WHITE);
        btnRemove.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        btnRemove.addActionListener(e -> removeFoto(index));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
        top.setOpaque(false);
        top.add(btnRemove);
        card.add(top, BorderLayout.NORTH);

        ImageIcon thumb = ImageUtil.loadImageThumbnail(fw.sourceFile, 140, 120);

        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblImage.setVerticalAlignment(SwingConstants.CENTER);
        lblImage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (thumb != null) {
            lblImage.setIcon(thumb);
        } else {
            lblImage.setText("Immagine non disponibile");
            lblImage.setFont(new Font("Arial", Font.PLAIN, 10));
        }

        // clic sulla foto per impostarla come principale 
        lblImage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setAsPrincipal(index);
            }
        });

        card.add(lblImage, BorderLayout.CENTER);

        JLabel lblPrincipal = new JLabel(fw.isPrincipale ? "Impostata come principale" : " ");
        lblPrincipal.setHorizontalAlignment(SwingConstants.CENTER);
        lblPrincipal.setFont(new Font("Arial", Font.BOLD, 10));
        lblPrincipal.setForeground(new Color(34, 139, 34));
        card.add(lblPrincipal, BorderLayout.SOUTH);

        return card;
    }

    private void setAsPrincipal(int index) {
        for (int i = 0; i < fotoWrappers.size(); i++) {
            fotoWrappers.get(i).isPrincipale = (i == index);
        }
        refreshThumbnailPanel();
    }

    private void removeFoto(int index) {
        if (index >= 0 && index < fotoWrappers.size()) {
            fotoWrappers.remove(index);
            // Se era principale e restano foto, la prima diventa principale
            if (!fotoWrappers.isEmpty() && fotoWrappers.stream().noneMatch(f -> f.isPrincipale)) {
                fotoWrappers.get(0).isPrincipale = true;
            }
            refreshThumbnailPanel();
        }
    }

    private void addPhotosFromChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle("Seleziona foto annuncio");

        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter(
                "Immagini (jpg, jpeg, png)", "jpg", "jpeg", "png"));

        int res = chooser.showOpenDialog(this);
        if (res != JFileChooser.APPROVE_OPTION)
            return;

        File[] files = chooser.getSelectedFiles();
        if (files == null || files.length == 0)
            return;

        for (File f : files) {
            if (f == null || !f.exists())
                continue;

            String name = f.getName();
            String ext = "";
            int dot = name.lastIndexOf('.');
            if (dot >= 0)
                ext = name.substring(dot);

            
            String safeName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;

            boolean isPrincipale = fotoWrappers.isEmpty(); // prima foto = principale
            fotoWrappers.add(new FotoWrapper(f, safeName, isPrincipale));
        }

        refreshThumbnailPanel();
    }

    private void updateFieldsByTipologia() {
        String tipo = (String) cmbTipologia.getSelectedItem();

        if ("Vendita".equals(tipo)) {
            txtPrezzo.setEnabled(true);
            txtOggettoRichiesto.setEnabled(false);
            txtOggettoRichiesto.setText("");
        } else if ("Scambio".equals(tipo)) {
            txtPrezzo.setEnabled(false);
            txtPrezzo.setText("");
            txtOggettoRichiesto.setEnabled(true);
        } else { // Regalo
            txtPrezzo.setEnabled(false);
            txtPrezzo.setText("");
            txtOggettoRichiesto.setEnabled(false);
            txtOggettoRichiesto.setText("");
        }
    }


    public String getTitolo() {
        return txtTitolo.getText() != null ? txtTitolo.getText().trim() : "";
    }

    public String getDescrizione() {
        return txtDescrizione.getText() != null ? txtDescrizione.getText().trim() : "";
    }

    public String getTipologia() {
        return (String) cmbTipologia.getSelectedItem();
    }

    public String getCategoria() {
        return (String) cmbCategoria.getSelectedItem();
    }

    public String getPrezzoText() {
        return txtPrezzo.getText() != null ? txtPrezzo.getText().trim() : "";
    }

    public String getOggettoRichiesto() {
        return txtOggettoRichiesto.getText() != null ? txtOggettoRichiesto.getText().trim() : "";
    }

    public boolean isSpedizioneSelected() {
        return chkSpedizione.isSelected();
    }

    public boolean isInUniSelected() {
        return chkInUni.isSelected();
    }

    public Studente getVenditore() {
        return venditore;
    }


    // Restituisce i dati delle foto selezionate
    public List<FotoData> getFotoDataList() {
        List<FotoData> result = new ArrayList<>();
        for (FotoWrapper fw : fotoWrappers) {
            result.add(new FotoData(fw.sourceFile, fw.destinationFilename, fw.isPrincipale));
        }
        return result;
    }


    public static class FotoData {
        private final File sourceFile;
        private final String destinationFilename;
        private final boolean principale;

        public FotoData(File sourceFile, String destinationFilename, boolean principale) {
            this.sourceFile = sourceFile;
            this.destinationFilename = destinationFilename;
            this.principale = principale;
        }

        public File getSourceFile() { return sourceFile; }
        public String getDestinationFilename() { return destinationFilename; }
        public boolean isPrincipale() { return principale; }
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Errore", JOptionPane.ERROR_MESSAGE);
    }
    
    
    public boolean isConfirmed() {
        return confirmed;
    }
}