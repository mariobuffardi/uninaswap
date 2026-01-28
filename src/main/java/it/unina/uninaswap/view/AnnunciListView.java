package it.unina.uninaswap.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatClientProperties;

import it.unina.uninaswap.model.entity.Annuncio;
import it.unina.uninaswap.util.ImageUtil;
import it.unina.uninaswap.util.UITheme;

public class AnnunciListView extends JPanel {

    private int visibleCols = 4;

    private static final int CARD_W = 260;
    private static final int CARD_H = 280;
    private static final int HGAP = 24;
    private static final int VGAP = 24;

    private static final Color SURFACE = Color.decode("#EAF2F9");
    private static final Color SURFACE_2 = Color.decode("#F6FAFF");
    private static final Color CARD_BG = Color.WHITE;

    private static final Color TITLE = UITheme.PRIMARY_DARK;
    private static final Color SUBTLE = UITheme.TEXT_SECONDARY;

    private static final String CARD_STYLE = "arc: 20; " +
            "background: #FFFFFF; " +
            "border: 1,1,1,1,#D7E3F2;";

    // pannello che contiene tutte le card
    private JPanel cardsPanel;
    private JScrollPane scrollPane;

    // listener click su annuncio
    public interface AnnuncioClickListener {
        void onAnnuncioClick(Annuncio annuncio);
    }

    private AnnuncioClickListener annuncioClickListener;

    public enum OffertaAction {
        ACQUISTA, FAI_OFFERTA, RICHIEDI_REGALO, PROPONI_SCAMBIO
    }

    public interface AnnuncioOffertaListener {
        void onOffertaAction(Annuncio annuncio, OffertaAction action);
    }

    private AnnuncioOffertaListener annuncioOffertaListener;

    public void setAnnuncioOffertaListener(AnnuncioOffertaListener listener) {
        this.annuncioOffertaListener = listener;
    }

    public AnnunciListView() {
        setLayout(new BorderLayout());
        setBackground(SURFACE);

        cardsPanel = new JPanel(new GridLayout(0, visibleCols, HGAP, VGAP));
        cardsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        cardsPanel.setBackground(SURFACE);
        cardsPanel.setOpaque(false);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(SURFACE);
        wrapper.setBorder(null);
        wrapper.add(cardsPanel, BorderLayout.NORTH);

        scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(SURFACE);
        scrollPane.setBackground(SURFACE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);


        scrollPane.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateColsAndRelayout();
            }
        });

        SwingUtilities.invokeLater(this::updateColsAndRelayout);

        showAnnunci(java.util.Collections.emptyList());
    }

    public void setAnnuncioClickListener(AnnuncioClickListener listener) {
        this.annuncioClickListener = listener;
    }

    public void showAnnunci(List<Annuncio> annunci) {
        cardsPanel.removeAll();

        if (annunci == null || annunci.isEmpty()) {
            // vista vuota più carina
            JPanel empty = new JPanel(new BorderLayout());
            empty.setOpaque(false);
            empty.setBorder(new EmptyBorder(30, 10, 10, 10));

            JLabel lblVuoto = new JLabel("Nessun annuncio trovato", SwingConstants.CENTER);
            lblVuoto.setForeground(TITLE);
            lblVuoto.setFont(lblVuoto.getFont().deriveFont(Font.BOLD, 16f));

            JLabel sub = new JLabel("Prova a modificare i filtri o aggiorna la ricerca.", SwingConstants.CENTER);
            sub.setForeground(SUBTLE);
            sub.setFont(sub.getFont().deriveFont(Font.PLAIN, 12f));

            empty.add(lblVuoto, BorderLayout.NORTH);
            empty.add(sub, BorderLayout.CENTER);

            cardsPanel.setLayout(new BorderLayout());
            cardsPanel.add(empty, BorderLayout.NORTH);

        } else {
            cardsPanel.setLayout(new GridLayout(0, visibleCols, HGAP, VGAP));

            for (Annuncio a : annunci) {
                cardsPanel.add(wrapFixedCard(createAnnuncioCard(a), a));
            }
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    /**
     * Wrapper trasparente che centra una card di dimensione fissa dentro la cella
     * GridLayout.
     * Così la card resta sempre CARD_W x CARD_H.
     */
    private JComponent wrapFixedCard(JPanel card, Annuncio annuncio) {
        JPanel cell = new JPanel(new GridBagLayout());
        cell.setOpaque(false);
        cell.add(card);

        // click anche se clicchi nel vuoto della cella
        cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cell.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Component deep = SwingUtilities.getDeepestComponentAt(cell, e.getX(), e.getY());
                if (deep instanceof JButton)
                    return;

                // se clicchi dentro la card ok, se clicchi fuori uguale
                if (annuncioClickListener != null) {
                    annuncioClickListener.onAnnuncioClick(annuncio);
                }
            }
        });

        return cell;
    }

    private JPanel createAnnuncioCard(Annuncio annuncio) {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(true);
        card.setBackground(CARD_BG);

        // FlatLaf: angoli arrotondati + bordo soft
        card.putClientProperty(FlatClientProperties.STYLE, CARD_STYLE);

        // dimensione fissa (come richiesto)
        Dimension fixed = new Dimension(CARD_W, CARD_H);
        card.setPreferredSize(fixed);
        card.setMinimumSize(fixed);
        card.setMaximumSize(fixed);

        // ===== FOTO =====
        JPanel photoWrap = new JPanel(new BorderLayout());
        photoWrap.setOpaque(false);
        photoWrap.setBorder(new EmptyBorder(12, 12, 6, 12));

        JLabel lblFoto = new JLabel();
        lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
        lblFoto.setOpaque(true);
        lblFoto.setBackground(SURFACE_2);
        lblFoto.putClientProperty(FlatClientProperties.STYLE, "arc: 16;");

        String fotoNomeFile = annuncio.getFotoPrincipalePath();
        ImageIcon baseIcon = ImageUtil.annuncioImageFromDbPath(fotoNomeFile);
        if (baseIcon == null)
            baseIcon = ImageUtil.defaultForCategoria(annuncio.getCategoria());

        String key = (fotoNomeFile != null ? fotoNomeFile : "cat_" + annuncio.getCategoria());
        ImageIcon icon = ImageUtil.scaled(baseIcon, 150, 150);
        lblFoto.setIcon(icon);

        photoWrap.add(lblFoto, BorderLayout.CENTER);
        card.add(photoWrap, BorderLayout.NORTH);

        // INFO
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(6, 14, 10, 14));

        JLabel lblTitolo = new JLabel(annuncio.getTitolo());
        lblTitolo.setFont(lblTitolo.getFont().deriveFont(Font.BOLD, 16f));
        lblTitolo.setForeground(TITLE);
        infoPanel.add(lblTitolo);

        infoPanel.add(Box.createVerticalStrut(6));

        JLabel lblTipo = new JLabel(annuncio.getTipologia() + " • " + annuncio.getCategoria());
        lblTipo.setForeground(SUBTLE);
        lblTipo.setFont(lblTipo.getFont().deriveFont(Font.PLAIN, 11.5f));
        infoPanel.add(lblTipo);

        BigDecimal prezzo = annuncio.getPrezzo();
        if (prezzo != null) {
            infoPanel.add(Box.createVerticalStrut(8));
            JLabel lblPrezzo = new JLabel("€ " + prezzo.toPlainString());
            lblPrezzo.setForeground(TITLE);
            lblPrezzo.setFont(lblPrezzo.getFont().deriveFont(Font.BOLD, 16f));
            infoPanel.add(lblPrezzo);
        }

        card.add(infoPanel, BorderLayout.CENTER);

        // AZIONI
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(0, 10, 10, 10));

        String tip = annuncio.getTipologia();

        if ("Vendita".equals(tip)) {
            JButton btnAcquista = new JButton("Acquista");
            stylePrimaryButton(btnAcquista);
            btnAcquista.addActionListener(e -> {
                if (annuncioOffertaListener != null) {
                    annuncioOffertaListener.onOffertaAction(annuncio, OffertaAction.ACQUISTA);
                }
            });

            JButton btnOfferta = new JButton("Fai offerta");
            styleSecondaryButton(btnOfferta);
            btnOfferta.addActionListener(e -> {
                if (annuncioOffertaListener != null) {
                    annuncioOffertaListener.onOffertaAction(annuncio, OffertaAction.FAI_OFFERTA);
                }
            });

            actions.add(btnAcquista);
            actions.add(btnOfferta);

        } else if ("Regalo".equals(tip)) {
            JButton btnRichiedi = new JButton("Richiedi");
            stylePrimaryButton(btnRichiedi);
            btnRichiedi.addActionListener(e -> {
                if (annuncioOffertaListener != null) {
                    annuncioOffertaListener.onOffertaAction(annuncio, OffertaAction.RICHIEDI_REGALO);
                }
            });
            actions.add(btnRichiedi);

        } else { // Scambio
            JButton btnScambio = new JButton("Proponi scambio");
            stylePrimaryButton(btnScambio);
            btnScambio.addActionListener(e -> {
                if (annuncioOffertaListener != null) {
                    annuncioOffertaListener.onOffertaAction(annuncio, OffertaAction.PROPONI_SCAMBIO);
                }
            });
            actions.add(btnScambio);
        }

        card.add(actions, BorderLayout.SOUTH);

        // ===== CLICK sulla card (non sui bottoni) =====
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Component deep = SwingUtilities.getDeepestComponentAt(card, e.getX(), e.getY());
                if (deep instanceof JButton)
                    return;

                if (annuncioClickListener != null) {
                    annuncioClickListener.onAnnuncioClick(annuncio);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // leggero hover (bordo un filo più “presente”)
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

        return card;
    }

private void stylePrimaryButton(JButton b) {
    b.putClientProperty(FlatClientProperties.STYLE,
            "arc: 12; " +
            "background: #1B415D; foreground: #FFFFFF; " +
            "hoverBackground: #2A5E86; pressedBackground: #163245;");
    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    // niente focus ring
    b.setFocusPainted(false);
    b.setFocusable(false);
}

private void styleSecondaryButton(JButton b) {
    b.putClientProperty(FlatClientProperties.STYLE,
            "arc: 12; " +
            "background: #EAF2F9; foreground: #1B415D; " +
            "hoverBackground: #DCEAF7; pressedBackground: #CFE3F5; " +
            "border: 1,1,1,1,#BFD3EA;");
    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    // niente focus ring
    b.setFocusPainted(false);
    b.setFocusable(false);
}



    // (rimasta per compatibilità)
    private ImageIcon getDefaultIconForCategoria(String categoria) {
        String path;
        switch (categoria) {
            case "Strumenti_musicali":
                path = "/images/categories/strumenti.png";
                break;
            case "Libri":
                path = "/images/categories/libri.jpg";
                break;
            case "Informatica":
                path = "/images/categories/informatica.jpg";
                break;
            case "Abbigliamento":
                path = "/images/categories/abbigliamento.jpg";
                break;
            case "Arredo":
                path = "/images/categories/arredo.jpg";
                break;
            default:
                path = "/images/categories/altro.jpg";
                break;
        }
        return new ImageIcon(getClass().getResource(path));
    }

    private int currentCols = -1;

    private int computeCols(int viewportWidth) {
        int available = viewportWidth - 30; // conservativo (padding + scrollbar)
        int colWidth = CARD_W + HGAP;
        int cols = available / colWidth;
        return Math.max(1, cols);
    }

    private void updateColsAndRelayout() {
        int w = scrollPane.getViewport().getWidth();
        if (w <= 0)
            return;

        int newCols = computeCols(w);
        if (newCols != currentCols) {
            currentCols = newCols;
            this.visibleCols = newCols;

            GridLayout gl = new GridLayout(0, newCols, HGAP, VGAP);
            cardsPanel.setLayout(gl);

            cardsPanel.revalidate();
            cardsPanel.repaint();
        }
    }
}