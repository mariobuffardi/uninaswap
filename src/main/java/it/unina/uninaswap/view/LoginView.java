package it.unina.uninaswap.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatClientProperties;

import net.miginfocom.swing.MigLayout;

public class LoginView extends JFrame {


    private static final Color PRIMARY = Color.decode("#1B415D");
    private static final Color DARK = Color.decode("#163245");
    private static final Color WHITE = Color.decode("#FFFFFF");


    private static final int HEADER_HEIGHT = 92;
    private static final int LEFT_WIDTH = 380;

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    // immagini
    private Image uninaLogoOriginal;
    private Image uninaswapHeroOriginal;

    private JLabel lblUninaLogo;
    private JLabel lblHero;

    public LoginView() {
        super("UniNaSwap - Login");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 560));
        setLocationRelativeTo(null);

        buildUI();

        SwingUtilities.invokeLater(this::refreshImages);

        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(WHITE);
        setContentPane(root);

        // Header 
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 0, 0, 40)),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));
        topBar.setPreferredSize(new Dimension(10, HEADER_HEIGHT));

        lblUninaLogo = new JLabel("", SwingConstants.CENTER);
        lblUninaLogo.setOpaque(false);

        uninaLogoOriginal = loadResourceImage("/images/altro/logo_unina.png");
        lblUninaLogo.setIcon(new ImageIcon(new BufferedFallback(1, 1).toImage()));

        topBar.add(lblUninaLogo, BorderLayout.CENTER);
        root.add(topBar, BorderLayout.NORTH);

        // Centro: sinistra login / destra immagine 
        JPanel center = new JPanel(new MigLayout(
                "insets 0, gap 0, fill",
                "[" + LEFT_WIDTH + "!,fill][grow,fill]",
                "[grow,fill]"
        ));
        center.setBackground(WHITE);
        root.add(center, BorderLayout.CENTER);

        JPanel left = buildLeftLoginPanel();
        JPanel right = buildRightHeroPanel();

        center.add(left, "growy");
        center.add(right, "grow");

        // resize listener per riscalare le immagini dinamicamente
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                refreshImages();
            }
        });

        lblHero.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                refreshImages();
            }
        });
    }

    private JPanel buildLeftLoginPanel() {
        JPanel left = new JPanel(new MigLayout(
                "insets 28, fillx, wrap",
                "[grow]",
                "[]8[]22[]6[]14[]6[]22[]push[]14[]"
        ));
        left.setBackground(DARK);

        left.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(255, 255, 255, 28)));

        JLabel title = new JLabel("Login");
        title.setForeground(WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));

        JLabel subtitle = new JLabel("Accedi con le credenziali UniNaSwap");
        subtitle.setForeground(new Color(235, 235, 235));
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 13f));

        left.add(title, "growx");
        left.add(subtitle, "growx");

        // Email
        JLabel lblEmail = new JLabel("Email");
        lblEmail.setForeground(WHITE);
        lblEmail.setFont(lblEmail.getFont().deriveFont(Font.BOLD, 13f));
        left.add(lblEmail, "growx");

        txtEmail = new JTextField();
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "email unina");
        txtEmail.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; background: #FFFFFF; foreground: #1A1A1A; "
                        + "focusWidth: 1; innerFocusWidth: 0;");
        txtEmail.setPreferredSize(new Dimension(10, 40));
        left.add(txtEmail, "growx, h 40!");

        // Password
        JLabel lblPass = new JLabel("Password");
        lblPass.setForeground(WHITE);
        lblPass.setFont(lblPass.getFont().deriveFont(Font.BOLD, 13f));
        left.add(lblPass, "growx");

        txtPassword = new JPasswordField();
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "password");
        txtPassword.putClientProperty(FlatClientProperties.STYLE,
                "arc: 12; background: #FFFFFF; foreground: #1A1A1A; "
                        + "focusWidth: 1; innerFocusWidth: 0;");
        txtPassword.setPreferredSize(new Dimension(10, 40));
        left.add(txtPassword, "growx, h 40!");

        // Button
        btnLogin = new JButton("Accedi");
        btnLogin.putClientProperty(FlatClientProperties.STYLE,
                "arc: 14; background: #1B415D; foreground: #FFFFFF; "
                        + "hoverBackground: #2A5E86; pressedBackground: #163245; "
                        + "focusWidth: 0;");
        btnLogin.setPreferredSize(new Dimension(10, 44));
        left.add(btnLogin, "growx, h 44!");

        // Footer
        left.add(Box.createVerticalStrut(0), "growy, push");

        JLabel footer = new JLabel("© UniNaSwap");
        footer.setForeground(new Color(210, 210, 210));
        footer.setFont(footer.getFont().deriveFont(Font.PLAIN, 12f));
        left.add(footer, "growx");

        // Enter = login
        txtPassword.addActionListener(e -> btnLogin.doClick());
        SwingUtilities.invokeLater(() -> getRootPane().setDefaultButton(btnLogin));

        return left;
    }

    private JPanel buildRightHeroPanel() {
        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(WHITE);
        right.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        lblHero = new JLabel("", SwingConstants.CENTER);
        lblHero.setOpaque(true);
        lblHero.setBackground(WHITE);

        uninaswapHeroOriginal = loadResourceImage("/images/altro/logo_uninaswap.png");
        lblHero.setIcon(new ImageIcon(new BufferedFallback(1, 1).toImage()));

        right.add(lblHero, BorderLayout.CENTER);
        return right;
    }


    private void refreshImages() {
        // Top logo
        if (uninaLogoOriginal != null && lblUninaLogo != null) {
            int targetH = 58;
            int targetW = Math.max(240, getWidth() - 120);
            Image scaled = scaleToFit(uninaLogoOriginal, targetW, targetH);
            lblUninaLogo.setIcon(new ImageIcon(scaled));
        }

        // Right hero 
        if (uninaswapHeroOriginal != null && lblHero != null) {
            int w = Math.max(240, lblHero.getWidth() - 20);
            int h = Math.max(240, lblHero.getHeight() - 20);
            Image scaled = scaleToCover(uninaswapHeroOriginal, w, h);
            lblHero.setIcon(new ImageIcon(scaled));
        }
    }

    private Image loadResourceImage(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) return null;
            return new ImageIcon(url).getImage();
        } catch (Exception e) {
            return null;
        }
    }


     // Scala mantenendo proporzioni: tutto l'img dentro il box (fit).
    private Image scaleToFit(Image src, int maxW, int maxH) {
        int sw = src.getWidth(null);
        int sh = src.getHeight(null);
        if (sw <= 0 || sh <= 0) return src;

        double scale = Math.min((double) maxW / sw, (double) maxH / sh);
        scale = Math.max(scale, 0.01);

        int tw = Math.max(1, (int) Math.round(sw * scale));
        int th = Math.max(1, (int) Math.round(sh * scale));

        return highQualityScale(src, tw, th);
    }


     // Scala mantenendo proporzioni: riempie tutto il box (cover), tagliando eventuale eccesso.
    private Image scaleToCover(Image src, int boxW, int boxH) {
        int sw = src.getWidth(null);
        int sh = src.getHeight(null);
        if (sw <= 0 || sh <= 0) return src;

        double scale = Math.max((double) boxW / sw, (double) boxH / sh);
        int tw = Math.max(1, (int) Math.round(sw * scale));
        int th = Math.max(1, (int) Math.round(sh * scale));

        Image scaled = highQualityScale(src, tw, th);

        // crop centrale
        BufferedFallback crop = new BufferedFallback(boxW, boxH);
        Graphics2D g = crop.g();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int x = (tw - boxW) / 2;
        int y = (th - boxH) / 2;

        g.drawImage(scaled, -x, -y, null);
        g.dispose();
        return crop.toImage();
    }

    private Image highQualityScale(Image src, int w, int h) {
        BufferedFallback out = new BufferedFallback(w, h);
        Graphics2D g = out.g();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        return out.toImage();
    }

    // Piccola utility interna per evitare dipendenze extra
    private static class BufferedFallback {
        private final java.awt.image.BufferedImage img;

        BufferedFallback(int w, int h) {
            img = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D g() {
            return img.createGraphics();
        }

        Image toImage() {
            return img;
        }
    }


    public String getEmail() {
        return txtEmail.getText().trim();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    public void addLoginListener(ActionListener listener) {
        btnLogin.addActionListener(listener);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Errore", JOptionPane.ERROR_MESSAGE);
    }
}