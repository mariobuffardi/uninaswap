package it.unina.uninaswap.view;

import it.unina.uninaswap.model.enums.TipoAnnuncio;
import it.unina.uninaswap.model.enums.TipoCategoria;
import it.unina.uninaswap.util.ChartUtil;
import it.unina.uninaswap.util.UITheme;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.CategoryDataset;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.math.BigDecimal;
import java.util.EnumMap;

public class ReportView extends JPanel {

    private final JTabbedPane tabs = new JTabbedPane();

    private final CardLayout venditeCards = new CardLayout();
    private final JPanel venditeRoot = new JPanel(venditeCards);

    private final CardLayout acquistiCards = new CardLayout();
    private final JPanel acquistiRoot = new JPanel(acquistiCards);

    private final JTable tableVenditeCategoria = new JTable();
    private final JTable tableVenditeTipologia = new JTable();

    private final JLabel lblTotArrivateVendite = new JLabel("Totale offerte arrivate: 0");
    private final JLabel lblTotAccettateVendite = new JLabel("Totale accettate: 0");
    private final JLabel lblMediaVendite = new JLabel("Media: -");
    private final JLabel lblMinVendite = new JLabel("Min: -");
    private final JLabel lblMaxVendite = new JLabel("Max: -");

    private final ChartPanel chartVenditeCategoria = new ChartPanel(null);
    private final ChartPanel chartVenditeTipologia = new ChartPanel(null);

    private final JTable tableAcquistiCategoria = new JTable();
    private final JTable tableAcquistiTipologia = new JTable();

    private final JLabel lblTotInviateAcquisti = new JLabel("Totale offerte inviate: 0");
    private final JLabel lblTotAccettateAcquisti = new JLabel("Totale accettate: 0");
    private final JLabel lblMediaAcquisti = new JLabel("Media: -");
    private final JLabel lblMinAcquisti = new JLabel("Min: -");
    private final JLabel lblMaxAcquisti = new JLabel("Max: -");

    private final ChartPanel chartAcquistiCategoria = new ChartPanel(null);
    private final ChartPanel chartAcquistiTipologia = new ChartPanel(null);

    private static final Color SURFACE = Color.decode("#EAF2F9");
    private static final Color SURFACE_2 = Color.decode("#F6FAFF");
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = Color.decode("#D7E3F2");
    private static final Color TITLE = UITheme.PRIMARY_DARK;
    private static final Color SUBTLE = UITheme.TEXT_SECONDARY;

    private static final String CARD_STYLE =
            "arc: 20; background: #FFFFFF; border: 1,1,1,1,#D7E3F2;";

    private static final String SOFT_STYLE =
            "arc: 16; background: #F6FAFF; border: 1,1,1,1,#D7E3F2;";

    public ReportView() {
        setLayout(new BorderLayout());
        setBackground(SURFACE);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel title = new JLabel("Report", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setForeground(TITLE);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.setBorder(new EmptyBorder(2, 2, 10, 2));
        titleRow.add(title, BorderLayout.WEST);
        add(titleRow, BorderLayout.NORTH);

        setupTable(tableVenditeCategoria, "Categoria", "Offerte arrivate", "Accettate");
        setupTable(tableVenditeTipologia, "Tipologia", "Offerte arrivate", "Accettate");
        setupTable(tableAcquistiCategoria, "Categoria", "Offerte inviate", "Accettate");
        setupTable(tableAcquistiTipologia, "Tipologia", "Offerte inviate", "Accettate");

        venditeRoot.setOpaque(false);
        venditeRoot.add(buildVenditeSummaryCard(), "SUMMARY");
        venditeRoot.add(buildVenditeChartsCard(), "CHARTS");

        acquistiRoot.setOpaque(false);
        acquistiRoot.add(buildAcquistiSummaryCard(), "SUMMARY");
        acquistiRoot.add(buildAcquistiChartsCard(), "CHARTS");

        tabs.setOpaque(false);
        tabs.putClientProperty("JTabbedPane.tabType", "underlined");
        tabs.putClientProperty("JTabbedPane.showTabSeparators", Boolean.FALSE);
        tabs.putClientProperty("JTabbedPane.selectedBackground", UITheme.PRIMARY);
        tabs.putClientProperty("JTabbedPane.selectedForeground", Color.WHITE);

        tabs.addTab("Vendite", venditeRoot);
        tabs.addTab("Acquisti", acquistiRoot);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);

        JPanel mainCard = createCard();
        mainCard.setLayout(new BorderLayout());
        mainCard.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainCard.add(tabs, BorderLayout.CENTER);

        outer.add(mainCard, BorderLayout.CENTER);
        add(outer, BorderLayout.CENTER);

        // default
        venditeCards.show(venditeRoot, "SUMMARY");
        acquistiCards.show(acquistiRoot, "SUMMARY");
    }


    // VENDITE - SUMMARY
    private JPanel buildVenditeSummaryCard() {
        JPanel root = new JPanel();
        root.setOpaque(false);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(6, 6, 6, 6));

        // 1) RIEPILOGO (card bianca)
        JPanel summary = createCard();
        summary.setLayout(new BorderLayout(10, 10));
        summary.setBorder(new EmptyBorder(14, 14, 14, 14));

        summary.add(sectionHeader("Riepilogo vendite"), BorderLayout.NORTH);
        summary.add(buildTopSummaryContent(
                lblTotArrivateVendite,
                lblTotAccettateVendite,
                lblMediaVendite,
                lblMinVendite,
                lblMaxVendite), BorderLayout.CENTER);

        // 2) TABELLE 
        JPanel tables = createCard();
        tables.setLayout(new BorderLayout());
        tables.setBorder(new EmptyBorder(14, 14, 14, 14));
        tables.add(sectionHeader("Tabelle"), BorderLayout.NORTH);

        JTabbedPane tabTabelle = createInnerTabs();
        tabTabelle.addTab("Per categoria", wrapTable(tableVenditeCategoria));
        tabTabelle.addTab("Per tipologia", wrapTable(tableVenditeTipologia));
        tables.add(tabTabelle, BorderLayout.CENTER);

        // 3) BOTTOM azioni 
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnMostraGrafici = new JButton("Mostra grafici");
        stylePrimaryButton(btnMostraGrafici);
        btnMostraGrafici.addActionListener(e -> venditeCards.show(venditeRoot, "CHARTS"));
        bottom.add(btnMostraGrafici);

        root.add(summary);
        root.add(Box.createVerticalStrut(12));
        root.add(tables);
        root.add(bottom);

        return root;
    }

    
    // VENDITE - CHARTS
    private JPanel buildVenditeChartsCard() {
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lbl = new JLabel("Grafici vendite");
        lbl.setForeground(TITLE);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));

        JButton btnBack = new JButton("Indietro");
        styleNavButton(btnBack);
        btnBack.addActionListener(e -> venditeCards.show(venditeRoot, "SUMMARY"));

        headerRow.add(lbl, BorderLayout.WEST);
        headerRow.add(btnBack, BorderLayout.EAST);
        root.add(headerRow, BorderLayout.NORTH);

        JPanel chartsCard = createCard();
        chartsCard.setLayout(new BorderLayout());
        chartsCard.setBorder(new EmptyBorder(14, 14, 14, 14));
        chartsCard.add(sectionHeader("Grafici"), BorderLayout.NORTH);

        // charts panel background
        chartVenditeCategoria.setOpaque(false);
        chartVenditeTipologia.setOpaque(false);

        JTabbedPane tabCharts = createInnerTabs();
        tabCharts.addTab("Per categoria", wrapChart(chartVenditeCategoria));
        tabCharts.addTab("Per tipologia", wrapChart(chartVenditeTipologia));

        chartsCard.add(tabCharts, BorderLayout.CENTER);
        root.add(chartsCard, BorderLayout.CENTER);

        return root;
    }

    
    // ACQUISTI - SUMMARY
    private JPanel buildAcquistiSummaryCard() {
        JPanel root = new JPanel();
        root.setOpaque(false);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel summary = createCard();
        summary.setLayout(new BorderLayout(10, 10));
        summary.setBorder(new EmptyBorder(14, 14, 14, 14));

        summary.add(sectionHeader("Riepilogo acquisti"), BorderLayout.NORTH);
        summary.add(buildTopSummaryContent(
                lblTotInviateAcquisti,
                lblTotAccettateAcquisti,
                lblMediaAcquisti,
                lblMinAcquisti,
                lblMaxAcquisti), BorderLayout.CENTER);

        JPanel tables = createCard();
        tables.setLayout(new BorderLayout());
        tables.setBorder(new EmptyBorder(14, 14, 14, 14));
        tables.add(sectionHeader("Tabelle"), BorderLayout.NORTH);

        JTabbedPane tabTabelle = createInnerTabs();
        tabTabelle.addTab("Per categoria", wrapTable(tableAcquistiCategoria));
        tabTabelle.addTab("Per tipologia", wrapTable(tableAcquistiTipologia));
        tables.add(tabTabelle, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnMostraGrafici = new JButton("Mostra grafici");
        stylePrimaryButton(btnMostraGrafici);
        btnMostraGrafici.addActionListener(e -> acquistiCards.show(acquistiRoot, "CHARTS"));
        bottom.add(btnMostraGrafici);

        root.add(summary);
        root.add(Box.createVerticalStrut(12));
        root.add(tables);
        root.add(bottom);

        return root;
    }


    // ACQUISTI - CHARTS
    private JPanel buildAcquistiChartsCard() {
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lbl = new JLabel("Grafici acquisti");
        lbl.setForeground(TITLE);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));

        JButton btnBack = new JButton("Indietro");
        styleNavButton(btnBack);
        btnBack.addActionListener(e -> acquistiCards.show(acquistiRoot, "SUMMARY"));

        headerRow.add(lbl, BorderLayout.WEST);
        headerRow.add(btnBack, BorderLayout.EAST);
        root.add(headerRow, BorderLayout.NORTH);

        JPanel chartsCard = createCard();
        chartsCard.setLayout(new BorderLayout());
        chartsCard.setBorder(new EmptyBorder(14, 14, 14, 14));
        chartsCard.add(sectionHeader("Grafici"), BorderLayout.NORTH);

        chartAcquistiCategoria.setOpaque(false);
        chartAcquistiTipologia.setOpaque(false);

        JTabbedPane tabCharts = createInnerTabs();
        tabCharts.addTab("Per categoria", wrapChart(chartAcquistiCategoria));
        tabCharts.addTab("Per tipologia", wrapChart(chartAcquistiTipologia));

        chartsCard.add(tabCharts, BorderLayout.CENTER);
        root.add(chartsCard, BorderLayout.CENTER);

        return root;
    }
    

    // COMMON UI BUILDERS
    private JPanel buildTopSummaryContent(JLabel lblTot1, JLabel lblTot2, JLabel lblMedia, JLabel lblMin, JLabel lblMax) {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BorderLayout(12, 12));

        // totals
        JPanel totals = new JPanel();
        totals.setOpaque(false);
        totals.setLayout(new BoxLayout(totals, BoxLayout.Y_AXIS));

        styleSummaryLabel(lblTot1, true);
        styleSummaryLabel(lblTot2, true);
        totals.add(lblTot1);
        totals.add(Box.createVerticalStrut(6));
        totals.add(lblTot2);

        // stats (soft card)
        JPanel statsCard = new JPanel(new GridLayout(1, 3, 10, 0));
        statsCard.setBackground(SURFACE_2);
        statsCard.putClientProperty(FlatClientProperties.STYLE, SOFT_STYLE);
        statsCard.setBorder(new EmptyBorder(10, 12, 10, 12));

        styleSummaryLabel(lblMedia, false);
        styleSummaryLabel(lblMin, false);
        styleSummaryLabel(lblMax, false);

        statsCard.add(lblMedia);
        statsCard.add(lblMin);
        statsCard.add(lblMax);

        JPanel statsWrap = new JPanel(new BorderLayout(0, 6));
        statsWrap.setOpaque(false);

        JLabel statsTitle = new JLabel("Statistiche importo (solo Vendita)");
        statsTitle.setForeground(SUBTLE);
        statsTitle.setFont(statsTitle.getFont().deriveFont(Font.PLAIN, 12f));

        statsWrap.add(statsTitle, BorderLayout.NORTH);
        statsWrap.add(statsCard, BorderLayout.CENTER);

        content.add(totals, BorderLayout.NORTH);
        content.add(statsWrap, BorderLayout.CENTER);

        return content;
    }

    private void styleSummaryLabel(JLabel lbl, boolean strong) {
        lbl.setForeground(strong ? TITLE : UITheme.PRIMARY_DARK);
        lbl.setFont(lbl.getFont().deriveFont(strong ? Font.BOLD : Font.PLAIN, strong ? 13.5f : 13f));
    }

    private JPanel sectionHeader(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel lbl = new JLabel(text);
        lbl.setForeground(TITLE);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);

        p.add(lbl, BorderLayout.NORTH);
        p.add(Box.createVerticalStrut(6), BorderLayout.CENTER);
        p.add(sep, BorderLayout.SOUTH);
        p.setBorder(new EmptyBorder(0, 0, 10, 0));

        return p;
    }

    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setOpaque(true);
        card.putClientProperty(FlatClientProperties.STYLE, CARD_STYLE);
        return card;
    }

    private JTabbedPane createInnerTabs() {
        JTabbedPane t = new JTabbedPane();
        t.setOpaque(false);
        t.putClientProperty("JTabbedPane.tabType", "underlined");
        t.putClientProperty("JTabbedPane.showTabSeparators", Boolean.FALSE);
        return t;
    }

    private JComponent wrapTable(JTable table) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(8, 6, 6, 6));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(null);
        sp.getViewport().setBackground(Color.WHITE);
        sp.setBackground(Color.WHITE);

        // “micro-cornice” interna molto soft (solo per dare stacco alla tabella)
        JPanel inner = new JPanel(new BorderLayout());
        inner.setBackground(Color.WHITE);
        inner.putClientProperty(FlatClientProperties.STYLE, "arc: 16; background: #FFFFFF; border: 1,1,1,1,#D7E3F2;");
        inner.setBorder(new EmptyBorder(8, 8, 8, 8));
        inner.add(sp, BorderLayout.CENTER);

        wrap.add(inner, BorderLayout.CENTER);
        return wrap;
    }

    private JComponent wrapChart(ChartPanel chartPanel) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(8, 6, 6, 6));

        JPanel inner = new JPanel(new BorderLayout());
        inner.setBackground(Color.WHITE);
        inner.putClientProperty(FlatClientProperties.STYLE, "arc: 16; background: #FFFFFF; border: 1,1,1,1,#D7E3F2;");
        inner.setBorder(new EmptyBorder(8, 8, 8, 8));
        inner.add(chartPanel, BorderLayout.CENTER);

        wrap.add(inner, BorderLayout.CENTER);
        return wrap;
    }

    private void stylePrimaryButton(JButton b) {
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; background: #1B415D; foreground: #FFFFFF; " +
                        "hoverBackground: #2A5E86; pressedBackground: #163245;");
        b.setFocusable(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton b) {
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; background: #EAF2F9; foreground: #1B415D; " +
                        "hoverBackground: #DCEAF7; pressedBackground: #CFE3F5; " +
                        "border: 1,1,1,1,#BFD3EA;");
        b.setFocusable(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void setupTable(JTable table, String c1, String c2, String c3) {
        DefaultTableModel model = new DefaultTableModel(new Object[]{c1, c2, c3}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table.setModel(model);

        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(0, 0, 0, 18));

        JTableHeader header = table.getTableHeader();
        header.setFont(header.getFont().deriveFont(Font.BOLD, 13f));
        header.setPreferredSize(new Dimension(header.getWidth(), 34));
        header.setBackground(SURFACE_2);
        header.setForeground(TITLE);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground((row % 2 == 0) ? Color.WHITE : new Color(248, 248, 250));
                }

                if (c instanceof JLabel lbl) {
                    lbl.setBorder(new EmptyBorder(0, 10, 0, 10));
                    lbl.setHorizontalAlignment(column > 0 ? SwingConstants.RIGHT : SwingConstants.LEFT);
                }

                return c;
            }
        });
    }

    private void styleNavButton(JButton b) {
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; " +
                        "background: #EAF2F9; foreground: #1B415D; " +
                        "hoverBackground: #DCEAF7; pressedBackground: #CFE3F5; " +
                        "border: 1,1,1,1,#BFD3EA; " +
                        "margin: 8,16,8,16;"); // padding interno (FlatLaf)
        b.setFocusable(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // dimensione minima così non resta striminzito
        b.setPreferredSize(new Dimension(140, 38));
        b.setMinimumSize(new Dimension(140, 38));
        b.setFont(b.getFont().deriveFont(Font.BOLD, 12.5f));
    }


    // API per Controller
    public void setData(ReportData data) {
        if (data == null) return;

        // VENDITE total
        lblTotArrivateVendite.setText("Totale offerte arrivate: " + data.venditePerCategoria.totaleArrivate);
        lblTotAccettateVendite.setText("Totale accettate: " + data.venditePerCategoria.totaleAccettate);

        lblMediaVendite.setText("Media: " + formatMoney(data.mediaVendite));
        lblMinVendite.setText("Min: " + formatMoney(data.minVendite));
        lblMaxVendite.setText("Max: " + formatMoney(data.maxVendite));

        DefaultTableModel mvCat = (DefaultTableModel) tableVenditeCategoria.getModel();
        mvCat.setRowCount(0);
        for (TipoCategoria cat : TipoCategoria.values()) {
            mvCat.addRow(new Object[]{
                    cat.toString(),
                    data.venditePerCategoria.getArrivate(cat),
                    data.venditePerCategoria.getAccettate(cat)
            });
        }
        mvCat.addRow(new Object[]{"Totale", data.venditePerCategoria.totaleArrivate, data.venditePerCategoria.totaleAccettate});

        DefaultTableModel mvTipo = (DefaultTableModel) tableVenditeTipologia.getModel();
        mvTipo.setRowCount(0);
        for (TipoAnnuncio t : TipoAnnuncio.values()) {
            mvTipo.addRow(new Object[]{
                    t.toString(),
                    data.venditePerTipologia.getArrivate(t),
                    data.venditePerTipologia.getAccettate(t)
            });
        }
        mvTipo.addRow(new Object[]{"Totale", data.venditePerTipologia.totaleArrivate, data.venditePerTipologia.totaleAccettate});

        // ACQUISTI totals
        lblTotInviateAcquisti.setText("Totale offerte inviate: " + data.acquistiPerCategoria.totaleArrivate);
        lblTotAccettateAcquisti.setText("Totale accettate: " + data.acquistiPerCategoria.totaleAccettate);

        lblMediaAcquisti.setText("Media: " + formatMoney(data.mediaAcquisti));
        lblMinAcquisti.setText("Min: " + formatMoney(data.minAcquisti));
        lblMaxAcquisti.setText("Max: " + formatMoney(data.maxAcquisti));

        DefaultTableModel maCat = (DefaultTableModel) tableAcquistiCategoria.getModel();
        maCat.setRowCount(0);
        for (TipoCategoria cat : TipoCategoria.values()) {
            maCat.addRow(new Object[]{
                    cat.toString(),
                    data.acquistiPerCategoria.getArrivate(cat),
                    data.acquistiPerCategoria.getAccettate(cat)
            });
        }
        maCat.addRow(new Object[]{"Totale", data.acquistiPerCategoria.totaleArrivate, data.acquistiPerCategoria.totaleAccettate});

        DefaultTableModel maTipo = (DefaultTableModel) tableAcquistiTipologia.getModel();
        maTipo.setRowCount(0);
        for (TipoAnnuncio t : TipoAnnuncio.values()) {
            maTipo.addRow(new Object[]{
                    t.toString(),
                    data.acquistiPerTipologia.getArrivate(t),
                    data.acquistiPerTipologia.getAccettate(t)
            });
        }
        maTipo.addRow(new Object[]{"Totale", data.acquistiPerTipologia.totaleArrivate, data.acquistiPerTipologia.totaleAccettate});
    }

    // Charts setter
    public void setVenditeChartCategoria(JFreeChart chart) {
        styleChartToIntegers(chart);
        ChartUtil.applyUninaTheme(chart);
        chartVenditeCategoria.setChart(chart);
    }

    public void setVenditeChartTipologia(JFreeChart chart) {
        styleChartToIntegers(chart);
        ChartUtil.applyUninaTheme(chart);
        chartVenditeTipologia.setChart(chart);
    }

    public void setAcquistiChartCategoria(JFreeChart chart) {
        styleChartToIntegers(chart);
        ChartUtil.applyUninaTheme(chart);
        chartAcquistiCategoria.setChart(chart);
    }

    public void setAcquistiChartTipologia(JFreeChart chart) {
        styleChartToIntegers(chart);
        ChartUtil.applyUninaTheme(chart);
        chartAcquistiTipologia.setChart(chart);
    }

    private void styleChartToIntegers(JFreeChart chart) {
        if (chart == null) return;
        if (!(chart.getPlot() instanceof CategoryPlot plot)) return;
        if (!(plot.getRangeAxis() instanceof NumberAxis axis)) return;

        axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        axis.setAutoRangeIncludesZero(true);

        double max = findMaxValue(plot.getDataset());
        double upper = Math.max(1, max);
        axis.setRange(0, upper);
    }

    private double findMaxValue(CategoryDataset ds) {
        if (ds == null) return 0;
        double max = 0;
        for (int r = 0; r < ds.getRowCount(); r++) {
            for (int c = 0; c < ds.getColumnCount(); c++) {
                Number v = ds.getValue(r, c);
                if (v != null) max = Math.max(max, v.doubleValue());
            }
        }
        return max;
    }

    private String formatMoney(BigDecimal v) {
        if (v == null) return "-";
        return v.toPlainString() + " €";
    }


    public static class SectionByCategoria {
        private final EnumMap<TipoCategoria, Integer> arrivate = new EnumMap<>(TipoCategoria.class);
        private final EnumMap<TipoCategoria, Integer> accettate = new EnumMap<>(TipoCategoria.class);
        public int totaleArrivate = 0;
        public int totaleAccettate = 0;

        public void putArrivate(TipoCategoria c, int v) { arrivate.put(c, v); }
        public void putAccettate(TipoCategoria c, int v) { accettate.put(c, v); }

        public int getArrivate(TipoCategoria c) { return arrivate.getOrDefault(c, 0); }
        public int getAccettate(TipoCategoria c) { return accettate.getOrDefault(c, 0); }

        public void computeTotals() {
            totaleArrivate = 0;
            totaleAccettate = 0;
            for (TipoCategoria c : TipoCategoria.values()) {
                totaleArrivate += getArrivate(c);
                totaleAccettate += getAccettate(c);
            }
        }
    }

    public static class SectionByTipologia {
        private final EnumMap<TipoAnnuncio, Integer> arrivate = new EnumMap<>(TipoAnnuncio.class);
        private final EnumMap<TipoAnnuncio, Integer> accettate = new EnumMap<>(TipoAnnuncio.class);
        public int totaleArrivate = 0;
        public int totaleAccettate = 0;

        public void putArrivate(TipoAnnuncio t, int v) { arrivate.put(t, v); }
        public void putAccettate(TipoAnnuncio t, int v) { accettate.put(t, v); }

        public int getArrivate(TipoAnnuncio t) { return arrivate.getOrDefault(t, 0); }
        public int getAccettate(TipoAnnuncio t) { return accettate.getOrDefault(t, 0); }

        public void computeTotals() {
            totaleArrivate = 0;
            totaleAccettate = 0;
            for (TipoAnnuncio t : TipoAnnuncio.values()) {
                totaleArrivate += getArrivate(t);
                totaleAccettate += getAccettate(t);
            }
        }
    }

    public static class ReportData {
        public SectionByCategoria venditePerCategoria = new SectionByCategoria();
        public SectionByTipologia venditePerTipologia = new SectionByTipologia();

        public SectionByCategoria acquistiPerCategoria = new SectionByCategoria();
        public SectionByTipologia acquistiPerTipologia = new SectionByTipologia();

        // stats (solo tipologia Vendita)
        public BigDecimal mediaVendite, minVendite, maxVendite; // io venditore
        public BigDecimal mediaAcquisti, minAcquisti, maxAcquisti; // io acquirente
    }
}