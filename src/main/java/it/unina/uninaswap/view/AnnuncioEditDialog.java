package it.unina.uninaswap.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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

import it.unina.uninaswap.model.entity.Annuncio;
import it.unina.uninaswap.model.entity.Foto;
import it.unina.uninaswap.util.ImageUtil;
import it.unina.uninaswap.util.UITheme;

public class AnnuncioEditDialog extends JDialog {

    private final Annuncio original;
    private Annuncio edited;

    private JTextField txtTitolo;
    private JTextArea txtDescrizione;
    private JComboBox<String> cmbTipologia;
    private JComboBox<String> cmbCategoria;
    private JTextField txtOggettoRichiesto;
    private JTextField txtPrezzo;
    private JCheckBox chkSpedizione;
    private JCheckBox chkInUni;

    // Foto (thumbnail visuali)
    private JPanel thumbnailContainer;
    private final List<FotoWrapper> fotoWrappers = new ArrayList<>();

    private boolean confirmed = false;

    // Wrapper interno per gestire foto in edit
    private static class FotoWrapper {
        Integer fotoId; // null se nuova foto non ancora salvata
        String filename; // SOLO nome file 
        File sourceFile; // solo per nuove foto
        boolean isPrincipale;
        boolean isDeleted;

        FotoWrapper(Integer fotoId, String filename, boolean isPrincipale) {
            this.fotoId = fotoId;
            this.filename = filename;
            this.sourceFile = null;
            this.isPrincipale = isPrincipale;
            this.isDeleted = false;
        }

        FotoWrapper(File sourceFile, String filename, boolean isPrincipale) {
            this.fotoId = null;
            this.filename = filename;
            this.sourceFile = sourceFile;
            this.isPrincipale = isPrincipale;
            this.isDeleted = false;
        }

        boolean isNew() {
            return fotoId == null;
        }
    }

    public AnnuncioEditDialog(Window parent, Annuncio annuncio, List<Foto> fotoList) {
        super(parent, "Modifica annuncio", ModalityType.APPLICATION_MODAL);
        this.original = annuncio;
        this.edited = cloneAnnuncio(annuncio);

        // Carica foto esistenti
        if (fotoList != null) {
            for (Foto f : fotoList) {
                String filenameOnly = onlyFilename(f.getPath());
                fotoWrappers.add(new FotoWrapper(f.getId(), filenameOnly, f.isPrincipale()));
            }
        }
        // se ci sono foto ma nessuna principale, la prima diventa principale
        ensureSinglePrincipalOnWrappers();

        initUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private static String onlyFilename(String path) {
        if (path == null)
            return null;
        String p = path.trim();
        int slash = p.lastIndexOf('/');
        return (slash >= 0) ? p.substring(slash + 1) : p;
    }

    private Annuncio cloneAnnuncio(Annuncio a) {
        Annuncio c = new Annuncio();
        c.setId(a.getId());
        c.setTitolo(a.getTitolo());
        c.setDescrizione(a.getDescrizione());
        c.setDataPubblicazione(a.getDataPubblicazione());
        c.setTipologia(a.getTipologia());
        c.setCategoria(a.getCategoria());
        c.setOggettoRichiesto(a.getOggettoRichiesto());
        c.setConcluso(a.isConcluso());
        c.setPrezzo(a.getPrezzo());
        c.setOffreSpedizione(a.isOffreSpedizione());
        c.setOffreIncontroInUni(a.isOffreIncontroInUni());
        c.setMatricolaVenditore(a.getMatricolaVenditore());
        return c;
    }

    private void initUI() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(content);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        content.add(formScroll, BorderLayout.CENTER);

        // Titolo
        txtTitolo = new JTextField(edited.getTitolo() != null ? edited.getTitolo() : "", 30);
        form.add(labeled("Titolo *", txtTitolo));

        // Descrizione
        txtDescrizione = new JTextArea(
                edited.getDescrizione() != null ? edited.getDescrizione() : "",
                3, 30);
        txtDescrizione.setLineWrap(true);
        txtDescrizione.setWrapStyleWord(true);
        form.add(labeled("Descrizione", new JScrollPane(txtDescrizione)));

        // Tipologia
        cmbTipologia = new JComboBox<>(new String[] { "Vendita", "Scambio", "Regalo" });
        cmbTipologia.setSelectedItem(edited.getTipologia());
        form.add(labeled("Tipologia *", cmbTipologia));

        // Categoria
        cmbCategoria = new JComboBox<>(new String[] {
                "Strumenti_musicali", "Libri", "Informatica",
                "Abbigliamento", "Arredo", "Altro"
        });
        cmbCategoria.setSelectedItem(edited.getCategoria());
        form.add(labeled("Categoria *", cmbCategoria));

        // Oggetto richiesto
        txtOggettoRichiesto = new JTextField(
                edited.getOggettoRichiesto() != null ? edited.getOggettoRichiesto() : "",
                30);
        form.add(labeled("Oggetto richiesto (solo Scambio)", txtOggettoRichiesto));

        // Prezzo
        txtPrezzo = new JTextField();
        if (edited.getPrezzo() != null) {
            txtPrezzo.setText(edited.getPrezzo().toPlainString());
        }
        form.add(labeled("Prezzo (solo Vendita)", txtPrezzo));

        // Opzioni consegna
        JPanel consegnaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        chkSpedizione = new JCheckBox("Spedizione", edited.isOffreSpedizione());
        chkInUni = new JCheckBox("Incontro in Uni", edited.isOffreIncontroInUni());
        consegnaPanel.add(chkSpedizione);
        consegnaPanel.add(chkInUni);
        form.add(labeled("Consegna *", consegnaPanel));

        // FOTO (thumbnail visuali)
        JPanel fotoPanel = new JPanel(new BorderLayout(5, 5));
        fotoPanel.setBorder(
                BorderFactory.createTitledBorder("Foto annuncio (clic sulla foto per impostare la principale)"));

        thumbnailContainer = new JPanel();
        thumbnailContainer.setLayout(new BoxLayout(thumbnailContainer, BoxLayout.X_AXIS));
        thumbnailContainer.setBorder(new EmptyBorder(5, 5, 5, 5));

        JScrollPane fotoScroll = new JScrollPane(thumbnailContainer);
        fotoScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        fotoScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        fotoScroll.setPreferredSize(new Dimension(560, 190));
        fotoScroll.setMinimumSize(new Dimension(560, 190));
        fotoScroll.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        fotoPanel.add(fotoScroll, BorderLayout.CENTER);

        JPanel fotoButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddFoto = new JButton("Aggiungi foto...");
        fotoButtons.add(btnAddFoto);
        fotoPanel.add(fotoButtons, BorderLayout.SOUTH);

        form.add(Box.createVerticalStrut(10));
        form.add(fotoPanel);

        // Pulsanti OK / Annulla
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAnnulla = new JButton("Annulla");
        JButton btnSalva = new JButton("Salva modifiche");
        btnSalva.putClientProperty("JButton.buttonType", "default");
        buttons.add(btnAnnulla);
        buttons.add(btnSalva);
        content.add(buttons, BorderLayout.SOUTH);

        // Listener combobox tipologia -> abilita/disabilita campi
        cmbTipologia.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
                updateFieldsByTipologia();
        });
        updateFieldsByTipologia();

        // Listener foto
        btnAddFoto.addActionListener(e -> onAddFoto());

        // Listener bottoni
        btnAnnulla.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        btnSalva.addActionListener(e -> onSave());

        refreshThumbnailPanel();
    }

    private JPanel labeled(String label, JComponent comp) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JLabel lbl = new JLabel(label);
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(comp, BorderLayout.CENTER);
        panel.setBorder(new EmptyBorder(5, 0, 5, 0));
        return panel;
    }


    // Tipologia -> abilita/disabilita campi
    private void updateFieldsByTipologia() {
        String tipo = (String) cmbTipologia.getSelectedItem();

        if ("Vendita".equals(tipo)) {
            txtPrezzo.setEnabled(true);

            txtOggettoRichiesto.setText("");
            txtOggettoRichiesto.setEnabled(false);

        } else if ("Scambio".equals(tipo)) {
            txtOggettoRichiesto.setEnabled(true);

            txtPrezzo.setText("");
            txtPrezzo.setEnabled(false);

        } else {
            txtPrezzo.setText("");
            txtPrezzo.setEnabled(false);

            txtOggettoRichiesto.setText("");
            txtOggettoRichiesto.setEnabled(false);
        }
    }


    // FOTO: gestione UI
    private void refreshThumbnailPanel() {
        thumbnailContainer.removeAll();

        List<FotoWrapper> visible = fotoWrappers.stream().filter(fw -> !fw.isDeleted).toList();

        if (visible.isEmpty()) {
            JLabel lbl = new JLabel("Nessuna foto: verrà usata l'immagine di default per categoria.");
            lbl.setForeground(Color.GRAY);
            thumbnailContainer.add(lbl);
        } else {
            for (FotoWrapper fw : visible) {
                int index = fotoWrappers.indexOf(fw);
                JPanel thumbCard = createThumbnailCard(fw, index);
                thumbnailContainer.add(thumbCard);
                thumbnailContainer.add(Box.createHorizontalStrut(10));
            }
        }

        thumbnailContainer.revalidate();
        thumbnailContainer.repaint();
    }

    private JPanel createThumbnailCard(FotoWrapper fw, int index) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(150, 165));
        card.setMaximumSize(new Dimension(150, 165));

        if (fw.isPrincipale) {
            card.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34), 2, true));
        } else {
            card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        }

        // X remove
        JButton btnRemove = new JButton("×");
        btnRemove.setFont(new Font("Arial", Font.BOLD, 16));
        btnRemove.setPreferredSize(new Dimension(26, 26));
        btnRemove.setMargin(new Insets(0, 0, 0, 0));
        btnRemove.setFocusPainted(false);
        btnRemove.setBackground(new Color(220, 53, 69));
        btnRemove.setForeground(Color.WHITE);
        btnRemove.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        btnRemove.addActionListener(e -> removeFoto(index));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
        top.setOpaque(false);
        top.add(btnRemove);
        card.add(top, BorderLayout.NORTH);

        // Image center (clickable)
        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblImage.setVerticalAlignment(SwingConstants.CENTER);
        lblImage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        ImageIcon thumb;
        if (fw.isNew()) {
            thumb = ImageUtil.loadImageThumbnail(fw.sourceFile, 130, 115);
        } else {
            // fw.filename è solo nome file -> DB path = images/annunci/<file>
            thumb = ImageUtil.annuncioImageFromDbPath("images/annunci/" + fw.filename);
            thumb = ImageUtil.scaled(thumb, 130, 115);
        }

        if (thumb != null) {
            lblImage.setIcon(thumb);
        } else {
            lblImage.setText("Immagine non disponibile");
            lblImage.setFont(new Font("Arial", Font.PLAIN, 10));
        }

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
            FotoWrapper w = fotoWrappers.get(i);
            if (w.isDeleted) {
                w.isPrincipale = false;
                continue;
            }
            w.isPrincipale = (i == index);
        }
        ensureSinglePrincipalOnWrappers();
        refreshThumbnailPanel();
    }

    private void removeFoto(int index) {
        if (index < 0 || index >= fotoWrappers.size())
            return;

        FotoWrapper removed = fotoWrappers.get(index);
        removed.isDeleted = true;
        removed.isPrincipale = false;

        // Se restano foto visibili, garantisci una principale
        ensureSinglePrincipalOnWrappers();
        refreshThumbnailPanel();
    }

    private void onAddFoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle("Seleziona foto annuncio");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Immagini (jpg, jpeg, png)", "jpg", "jpeg", "png"));

        int res = chooser.showOpenDialog(this);
        if (res != JFileChooser.APPROVE_OPTION)
            return;

        File[] files = chooser.getSelectedFiles();
        if (files == null || files.length == 0)
            return;

        for (File f : files) {
            if (f == null || !f.exists())
                continue;

            String ext = "";
            String name = f.getName();
            int dot = name.lastIndexOf('.');
            if (dot >= 0)
                ext = name.substring(dot);

            String safeName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;

            boolean isPrincipale = fotoWrappers.stream().noneMatch(w -> !w.isDeleted && w.isPrincipale);
            fotoWrappers.add(new FotoWrapper(f, safeName, isPrincipale));
        }

        ensureSinglePrincipalOnWrappers();
        refreshThumbnailPanel();
    }

    private void ensureSinglePrincipalOnWrappers() {
        List<FotoWrapper> visible = fotoWrappers.stream().filter(w -> !w.isDeleted).toList();
        if (visible.isEmpty())
            return;

        // se nessuna principale -> prima principale
        if (visible.stream().noneMatch(w -> w.isPrincipale)) {
            visible.get(0).isPrincipale = true;
        }

        // se più di una -> tieni la prima e spegni le altre
        boolean found = false;
        for (FotoWrapper w : visible) {
            if (!w.isPrincipale)
                continue;
            if (!found)
                found = true;
            else
                w.isPrincipale = false;
        }
    }


    // Salvataggio
    private void onSave() {
        try {
            String titolo = txtTitolo.getText().trim();
            if (titolo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Inserisci un titolo.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            edited.setTitolo(titolo);

            String descr = txtDescrizione.getText().trim();
            edited.setDescrizione(descr.isEmpty() ? null : descr);

            String tipologia = (String) cmbTipologia.getSelectedItem();
            String categoria = (String) cmbCategoria.getSelectedItem();
            edited.setTipologia(tipologia);
            edited.setCategoria(categoria);

            // Oggetto richiest
            if ("Scambio".equals(tipologia)) {
                String objReq = txtOggettoRichiesto.getText().trim();
                if (objReq.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Per gli annunci di Scambio devi indicare l'oggetto richiesto.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                edited.setOggettoRichiesto(objReq);
            } else {
                edited.setOggettoRichiesto(null);
            }

            // Prezzo
            String prezzoText = txtPrezzo.getText().trim();
            if ("Vendita".equals(tipologia)) {
                if (prezzoText.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Per gli annunci di Vendita il prezzo è obbligatorio.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                prezzoText = prezzoText.replace(",", ".");
                BigDecimal prezzo = new BigDecimal(prezzoText);
                if (prezzo.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(this,
                            "Il prezzo non può essere negativo.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                edited.setPrezzo(prezzo);
            } else {
                edited.setPrezzo(null);
            }

            // Consegna
            edited.setOffreSpedizione(chkSpedizione.isSelected());
            edited.setOffreIncontroInUni(chkInUni.isSelected());

            if (!chkSpedizione.isSelected() && !chkInUni.isSelected()) {
                JOptionPane.showMessageDialog(this,
                        "Devi selezionare almeno una modalità di consegna.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Foto: garantisci principale unica (se ci sono foto)
            ensureSinglePrincipalOnWrappers();

            // Copia nuove foto nella directory resources (+ target/classes best effort)
            for (FotoWrapper fw : fotoWrappers) {
                if (fw.isNew() && !fw.isDeleted) {
                    try {
                        ImageUtil.copyToAnnunciDirs(fw.sourceFile, fw.filename);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this,
                                "Errore copia foto:\n" + ex.getMessage(),
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            confirmed = true;
            dispose();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this,
                    "Prezzo non valido. Usa un numero (es. 10 oppure 10.50).",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }



    public boolean isConfirmed() {
        return confirmed;
    }

    public Annuncio getEditedAnnuncio() {
        return confirmed ? edited : null;
    }

    /**
     * IMPORTANTISSIMO:
     * - path DB = "images/annunci/<filename>"
     * - principale PRIMA in lista (per evitare errore col trigger)
     */
    public List<Foto> getEditedFotoList() {
        if (!confirmed)
            return new ArrayList<>();

        List<Foto> result = new ArrayList<>();

        for (FotoWrapper fw : fotoWrappers) {
            if (fw.isDeleted)
                continue;

            Foto f = new Foto();
            if (!fw.isNew()) {
                f.setId(fw.fotoId);
            }
            f.setIdAnnuncio(edited.getId());
            f.setPath("images/annunci/" + fw.filename);
            f.setPrincipale(fw.isPrincipale);

            result.add(f);
        }

        // Safety: una sola principale
        if (!result.isEmpty() && result.stream().noneMatch(Foto::isPrincipale)) {
            result.get(0).setPrincipale(true);
        }
        boolean found = false;
        for (Foto f : result) {
            if (!f.isPrincipale())
                continue;
            if (!found)
                found = true;
            else
                f.setPrincipale(false);
        }

        // ORDINAMENTO: principale prima (evita violazione col trigger)
        result.sort(Comparator.comparing(Foto::isPrincipale).reversed());
        return result;
    }
}