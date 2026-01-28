package it.unina.uninaswap.util;

import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatLightLaf;


// TEMA UI GLOBALE PER UNINASWAP
public class UITheme {

    // Palette colori Unina
    public static final Color PRIMARY = new Color(0x1B415D);
    public static final Color PRIMARY_DARK = new Color(0x163245);
    public static final Color WHITE = Color.WHITE;
    public static final Color ACCENT = new Color(0xD93C25);

    // Colori derivati
    public static final Color BACKGROUND_LIGHT = new Color(0xF5F5F5);
    public static final Color TEXT_SECONDARY = new Color(0x666666);
    public static final Color BORDER_LIGHT = new Color(0xE0E0E0);


    public static void setup() {
        FlatLightLaf.setup();

        Font defaultFont = new Font("SansSerif", Font.PLAIN, 13);

        //  GENERALE
        UIManager.put("defaultFont", defaultFont);

        // Background generale leggermente grigio
        UIManager.put("Panel.background", BACKGROUND_LIGHT);
        UIManager.put("OptionPane.background", WHITE);

        // COMPONENTI ARROTONDATI 
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ComboBox.arc", 8);

        // BOTTONI 
        UIManager.put("Button.default.background", PRIMARY);
        UIManager.put("Button.default.foreground", WHITE);
        UIManager.put("Button.default.hoverBackground", PRIMARY_DARK);
        UIManager.put("Button.default.pressedBackground", PRIMARY_DARK);
        UIManager.put("Button.default.borderColor", PRIMARY);

        // Bottone primario (per azioni principali)
        UIManager.put("Button.background", PRIMARY);
        UIManager.put("Button.foreground", WHITE);
        UIManager.put("Button.hoverBackground", PRIMARY_DARK);
        UIManager.put("Button.pressedBackground", PRIMARY_DARK);

        // FOCUS E SELEZIONE 
        UIManager.put("Component.focusColor", PRIMARY);
        UIManager.put("Component.focusedBorderColor", PRIMARY);
        UIManager.put("TextComponent.selectionBackground", PRIMARY);
        UIManager.put("TextComponent.selectionForeground", WHITE);

        // SCROLLBAR 
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("ScrollBar.thumbArc", 6);
        UIManager.put("ScrollBar.thumbInsets", new java.awt.Insets(2, 2, 2, 2));
        UIManager.put("ScrollBar.track", BACKGROUND_LIGHT);
        UIManager.put("ScrollBar.thumb", new Color(0xC0C0C0));
        UIManager.put("ScrollBar.hoverTrackColor", BACKGROUND_LIGHT);

        // TABBED PANE 
        UIManager.put("TabbedPane.selectedBackground", PRIMARY);
        UIManager.put("TabbedPane.selectedForeground", WHITE);
        UIManager.put("TabbedPane.hoverColor", new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 30));

        // TABLE 
        UIManager.put("Table.selectionBackground", PRIMARY);
        UIManager.put("Table.selectionForeground", WHITE);
        UIManager.put("TableHeader.background", BACKGROUND_LIGHT);
        UIManager.put("TableHeader.foreground", PRIMARY_DARK);

        // TEXT FIELDS 
        UIManager.put("TextField.background", WHITE);
        UIManager.put("TextArea.background", WHITE);
        UIManager.put("ComboBox.background", WHITE);

        // BORDERS 
        UIManager.put("Component.borderColor", BORDER_LIGHT);
        UIManager.put("Button.borderWidth", 1);
    }


    // colore più chiaro (serve per hover effect)
    public static Color lighter(Color color, float factor) {
        return new Color(
                Math.min((int) (color.getRed() * factor), 255),
                Math.min((int) (color.getGreen() * factor), 255),
                Math.min((int) (color.getBlue() * factor), 255),
                color.getAlpha());
    }


    // colore più scuro
    public static Color darker(Color color, float factor) {
        return new Color(
                Math.max((int) (color.getRed() * factor), 0),
                Math.max((int) (color.getGreen() * factor), 0),
                Math.max((int) (color.getBlue() * factor), 0),
                color.getAlpha());
    }
}