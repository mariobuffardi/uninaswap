package it.unina.uninaswap.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatClientProperties;

import it.unina.uninaswap.model.enums.SessoStudente;

public class LoginView extends JFrame {

    private static final Color PRIMARY = Color.decode("#1B415D");
    private static final Color DARK = Color.decode("#163245");
    private static final Color WHITE = Color.decode("#FFFFFF");

    private static final String CARD_LOGIN = "LOGIN";
    private static final String CARD_REGISTER = "REGISTER";

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnGoRegisterLink;

    private JTextField regMatricola;
    private JTextField regEmail;
    private JTextField regNome;
    private JTextField regCognome;
    private JPasswordField regPassword;
    private JComboBox<SessoStudente> regSesso;
    private JCheckBox regPrefSpedizione;
    private JCheckBox regPrefIncontro;

    private JTextField regVia;
    private JTextField regCivico;
    private JTextField regCap;
    private JTextField regCitta;
    private JTextField regStato;

    private JButton btnRegister;
    private JButton btnBackToLogin;

    private JScrollPane registerScrollPane;

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private JLabel lblHero;
    private Image heroOriginal;
    private JLabel lblLogoTop;
    private Image logoOriginal;

    public LoginView() {
        super("UniNaSwap - Login / Registrazione");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);

        buildUI();

        setVisible(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        refreshImages();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(WHITE);
        setContentPane(root);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(PRIMARY);
        top.setBorder(new EmptyBorder(10, 16, 10, 16));
        top.setPreferredSize(new Dimension(10, 90));

        lblLogoTop = new JLabel("", SwingConstants.CENTER);
        logoOriginal = loadResourceImage("/images/altro/logo_unina.png");
        top.add(lblLogoTop, BorderLayout.CENTER);

        root.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(WHITE);
        root.add(center, BorderLayout.CENTER);

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(DARK);
        left.setPreferredSize(new Dimension(600, 10));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(DARK);

        cardPanel.add(buildLoginCard(), CARD_LOGIN);
        cardPanel.add(buildRegisterCard(), CARD_REGISTER);

        left.add(cardPanel, BorderLayout.CENTER);
        center.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(WHITE);
        right.setBorder(new EmptyBorder(14, 14, 14, 14));

        lblHero = new JLabel("", SwingConstants.CENTER);
        lblHero.setOpaque(true);
        lblHero.setBackground(WHITE);
        lblHero.putClientProperty(FlatClientProperties.STYLE, "arc: 18; border: 1,1,1,1,#E6E6E6");

        heroOriginal = loadResourceImage("/images/altro/logo_uninaswap.png");
        right.add(lblHero, BorderLayout.CENTER);

        center.add(right, BorderLayout.CENTER);

        // ridimensionamento immagini
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                refreshImages();
            }
        });
        lblHero.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                refreshImages();
            }
        });
    }


    private JPanel buildLoginCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(DARK);
        card.setBorder(new EmptyBorder(26, 26, 22, 26));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Login");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Accedi con le credenziali UniNaSwap");
        subtitle.setForeground(new Color(230,230,230));
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 13f));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(title);
        form.add(Box.createVerticalStrut(8));
        form.add(subtitle);
        form.add(Box.createVerticalStrut(22));

        form.add(label("Email"));
        txtEmail = field("email unina");
        form.add(txtEmail);
        form.add(Box.createVerticalStrut(14));

        form.add(label("Password"));
        txtPassword = new JPasswordField();
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "password");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(txtPassword);
        form.add(Box.createVerticalStrut(18));

        btnLogin = new JButton("Accedi");
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogin.putClientProperty(FlatClientProperties.STYLE,
                "arc: 14; background: #1B415D; foreground: #FFFFFF; hoverBackground: #2A5E86; pressedBackground: #163245; focusWidth: 0;");
        form.add(btnLogin);

        card.add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(new EmptyBorder(16, 0, 0, 0));

        btnGoRegisterLink = linkButton("Non hai ancora un account? Registrati!");
        btnGoRegisterLink.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottom.add(btnGoRegisterLink);

        bottom.add(Box.createVerticalStrut(10));

        JLabel footer = new JLabel("© UniNaSwap");
        footer.setForeground(new Color(200,200,200));
        footer.setFont(footer.getFont().deriveFont(Font.PLAIN, 12f));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottom.add(footer);

        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }


    private JPanel buildRegisterCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(DARK);
        card.setBorder(new EmptyBorder(18, 26, 18, 26));

        btnBackToLogin = linkButton("← Indietro");
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(btnBackToLogin, BorderLayout.WEST);
        top.setBorder(new EmptyBorder(0,0,10,0));
        card.add(top, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Registrazione");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(title);
        form.add(Box.createVerticalStrut(14));

        form.add(label("Matricola"));
        regMatricola = field("9 cifre, es. 000000001"); form.add(regMatricola); form.add(Box.createVerticalStrut(10));

        form.add(label("Email"));
        regEmail = field("email@unina.it"); form.add(regEmail); form.add(Box.createVerticalStrut(10));

        form.add(label("Nome"));
        regNome = field("Nome"); form.add(regNome); form.add(Box.createVerticalStrut(10));

        form.add(label("Cognome"));
        regCognome = field("Cognome"); form.add(regCognome); form.add(Box.createVerticalStrut(10));

        form.add(label("Password"));
        regPassword = new JPasswordField();
        regPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "min 6 caratteri");
        regPassword.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        regPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        regPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(regPassword);
        form.add(Box.createVerticalStrut(10));

        form.add(label("Sesso"));
        regSesso = new JComboBox<>(SessoStudente.values());
        regSesso.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        regSesso.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        regSesso.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(regSesso);
        form.add(Box.createVerticalStrut(10));

        form.add(label("Preferenze consegna"));
        JPanel pref = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pref.setOpaque(false);
        regPrefSpedizione = new JCheckBox("Spedizione");
        regPrefIncontro = new JCheckBox("Incontro in Uni");
        styleCheck(regPrefSpedizione);
        styleCheck(regPrefIncontro);
        pref.add(regPrefSpedizione);
        pref.add(regPrefIncontro);
        pref.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(pref);

        form.add(Box.createVerticalStrut(14));
        form.add(label("Indirizzo"));

        form.add(labelSmall("Via"));
        regVia = field("Via"); form.add(regVia); form.add(Box.createVerticalStrut(8));

        form.add(labelSmall("Civico"));
        regCivico = field("es. 12"); form.add(regCivico); form.add(Box.createVerticalStrut(8));

        form.add(labelSmall("CAP"));
        regCap = field("es. 80134"); form.add(regCap); form.add(Box.createVerticalStrut(8));

        form.add(labelSmall("Città"));
        regCitta = field("Napoli"); form.add(regCitta); form.add(Box.createVerticalStrut(8));

        form.add(labelSmall("Stato"));
        regStato = field("Italia"); form.add(regStato);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        registerScrollPane = scroll;
        card.add(scroll, BorderLayout.CENTER);

        btnRegister = new JButton("Registrati");
        btnRegister.putClientProperty(FlatClientProperties.STYLE,
                "arc: 14; background: #1B415D; foreground: #FFFFFF; hoverBackground: #D93C25; pressedBackground: #163245; focusWidth: 0;");
        btnRegister.setPreferredSize(new Dimension(10, 44));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(14, 0, 0, 0));
        bottom.add(btnRegister, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }


    public String getEmail() { return txtEmail.getText().trim(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public void addLoginListener(ActionListener l) { btnLogin.addActionListener(l); }
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Errore", JOptionPane.ERROR_MESSAGE);
    }

    public void addRegisterListener(ActionListener l) { btnRegister.addActionListener(l); }
    public void addSwitchToRegisterListener(ActionListener l) { btnGoRegisterLink.addActionListener(l); }
    public void addBackToLoginListener(ActionListener l) { btnBackToLogin.addActionListener(l); }

    public void showRegisterCard() { cardLayout.show(cardPanel, CARD_REGISTER); }
    public void showLoginCard() { cardLayout.show(cardPanel, CARD_LOGIN); }

    public void switchToLoginCardPrefillEmail(String email) {
        showLoginCard();
        if (email != null) txtEmail.setText(email.trim());
        txtPassword.requestFocusInWindow();
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Successo", JOptionPane.INFORMATION_MESSAGE);
    }

    public String getRegMatricola() { return regMatricola.getText().trim(); }
    public String getRegEmail() { return regEmail.getText().trim(); }
    public String getRegNome() { return regNome.getText().trim(); }
    public String getRegCognome() { return regCognome.getText().trim(); }
    public String getRegPassword() { return new String(regPassword.getPassword()); }
    public SessoStudente getRegSesso() { return (SessoStudente) regSesso.getSelectedItem(); }
    public boolean isRegPreferisceSpedizione() { return regPrefSpedizione.isSelected(); }
    public boolean isRegPreferisceIncontroInUni() { return regPrefIncontro.isSelected(); }

    public String getRegVia() { return regVia.getText().trim(); }
    public String getRegCivico() { return regCivico.getText().trim(); }
    public String getRegCap() { return regCap.getText().trim(); }
    public String getRegCitta() { return regCitta.getText().trim(); }
    public String getRegStato() { return regStato.getText().trim(); }


    private JLabel label(String s) {
        JLabel l = new JLabel(s);
        l.setForeground(Color.WHITE);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 13f));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0,0,6,0));
        return l;
    }

    private JLabel labelSmall(String s) {
        JLabel l = new JLabel(s);
        l.setForeground(new Color(230,230,230));
        l.setFont(l.getFont().deriveFont(Font.PLAIN, 12.5f));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0,0,4,0));
        return l;
    }

    private JTextField field(String placeholder) {
        JTextField f = new JTextField();
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        f.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private void styleCheck(JCheckBox cb) {
        cb.setOpaque(false);
        cb.setForeground(Color.WHITE);
        cb.setFocusPainted(false);
    }

    private JButton linkButton(String text) {
        JButton b = new JButton("<html><u>" + text + "</u></html>");
        b.setForeground(new Color(230,230,230));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(0,0,0,0));
        return b;
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

    private void refreshImages() {
        if (logoOriginal != null) {
            Image scaledLogo = logoOriginal.getScaledInstance(-1, 56, Image.SCALE_SMOOTH);
            lblLogoTop.setIcon(new ImageIcon(scaledLogo));
        }
        if (heroOriginal != null && lblHero != null && lblHero.getWidth() > 0 && lblHero.getHeight() > 0) {
            Image scaledHero = scaleToFit(heroOriginal, lblHero.getWidth(), lblHero.getHeight());
            lblHero.setIcon(new ImageIcon(scaledHero));
        }
    }

    private Image scaleToFit(Image src, int maxW, int maxH) {
        int sw = src.getWidth(null), sh = src.getHeight(null);
        if (sw <= 0 || sh <= 0) return src;
        double scale = Math.min((double) maxW / sw, (double) maxH / sh);
        int tw = Math.max(1, (int) Math.round(sw * scale));
        int th = Math.max(1, (int) Math.round(sh * scale));
        return src.getScaledInstance(tw, th, Image.SCALE_SMOOTH);
    }

    public void clearRegisterFields() {
        regMatricola.setText("");
        regEmail.setText("");
        regNome.setText("");
        regCognome.setText("");
        regPassword.setText("");

        regSesso.setSelectedIndex(0);

        regPrefSpedizione.setSelected(false);
        regPrefIncontro.setSelected(false);

        regVia.setText("");
        regCivico.setText("");
        regCap.setText("");
        regCitta.setText("");
        regStato.setText("");

        SwingUtilities.invokeLater(() -> {
            if (registerScrollPane != null) {
                registerScrollPane.getVerticalScrollBar().setValue(0);
            }
        });
    }

}