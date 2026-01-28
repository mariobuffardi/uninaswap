package it.unina.uninaswap.util;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

// Classe utily per applicare il tema unina a JFreeChart
public class ChartUtil {


    public static void applyUninaTheme(JFreeChart chart) {
        if (chart == null)
            return;

        chart.setBackgroundPaint(new Color(250, 250, 250));
        chart.setBorderVisible(false);

        if (chart.getTitle() != null) {
            chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 14));
            chart.getTitle().setPaint(UITheme.PRIMARY_DARK);
        }

        if (chart.getPlot() instanceof CategoryPlot) {
            CategoryPlot plot = (CategoryPlot) chart.getPlot();

            plot.setBackgroundPaint(Color.WHITE);
            plot.setOutlineVisible(false);

            plot.setRangeGridlinePaint(UITheme.BORDER_LIGHT);
            plot.setRangeGridlineStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    1.0f, new float[] { 2.0f, 3.0f }, 0.0f)); // dashed

            plot.setDomainGridlinesVisible(false);

            if (plot.getDomainAxis() != null) {
                plot.getDomainAxis().setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
                plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 11));
                plot.getDomainAxis().setLabelPaint(UITheme.PRIMARY_DARK);
                plot.getDomainAxis().setTickLabelPaint(Color.DARK_GRAY);
            }

            if (plot.getRangeAxis() != null) {
                plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
                plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 11));
                plot.getRangeAxis().setLabelPaint(UITheme.PRIMARY_DARK);
                plot.getRangeAxis().setTickLabelPaint(Color.DARK_GRAY);
            }

            if (plot.getRenderer() instanceof BarRenderer) {
                BarRenderer renderer = (BarRenderer) plot.getRenderer();

                renderer.setSeriesPaint(0, UITheme.PRIMARY);
                if (plot.getDataset() != null && plot.getDataset().getRowCount() > 1) {
                    renderer.setSeriesPaint(1, UITheme.ACCENT);
                }

                renderer.setDrawBarOutline(true);
                renderer.setSeriesOutlinePaint(0, UITheme.PRIMARY_DARK);
                if (plot.getDataset() != null && plot.getDataset().getRowCount() > 1) {
                    renderer.setSeriesOutlinePaint(1, new Color(180, 50, 30)); // darker accent
                }
            }
        }
    }
}