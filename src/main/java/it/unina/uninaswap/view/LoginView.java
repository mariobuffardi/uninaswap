package it.unina.uninaswap.view;

import com.formdev.flatlaf.FlatClientProperties;
import it.unina.uninaswap.util.UITheme;
import it.unina.uninaswap.model.enums.SessoStudente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class LoginView extends JFrame {

    // Login
    private JTextField emailLoginField;
    private JPasswordField passwordLoginField;
    private JButton loginButton;
    private JLabel linkToRegister;

    // SignIn
    private JTextField regNomeField;
    private JTextField regCognomeField;
    private JTextField regMatricolaField;
    private JTextField regEmailField;
    private JPasswordField regPasswordField;
    private JTextField regViaField;
    private JTextField regCivicoField;
    private JTextField regCapField;
    private JTextField regCittaField;
    private JTextField regStatoField;
    private JComboBox<String> regSessoCombo;
    private JCheckBox checkSpedizione;
    private JCheckBox checkIncontro;

    private JButton registerButton;
    private JLabel linkToLogin;

    // Layout
    private Image backgroundImage;
    private ImageIcon logoIcon;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public final static String CARD_LOGIN = "LOGIN";
    public final static String CARD_REGISTER = "REGISTER";

    // Dimensioni Box
    private static final int BOX_WIDTH = 350;
    private static final int BOX_HEIGHT = 440;


    public LoginView() {
        setTitle("UniNaSwap - Accesso");
        setSize(1000, 700);
        setMinimumSize(new Dimension(900, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loadResources();

        BackgroundPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // Header
        JPanel pageHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 20));
        pageHeader.setOpaque(false);

        if (logoIcon != null) {
            JLabel lblLogo = new JLabel(logoIcon);
            pageHeader.add(lblLogo);
        }
        mainPanel.add(pageHeader, BorderLayout.NORTH);

        // Centro
        JPanel centerArea = new JPanel(new GridBagLayout());
        centerArea.setOpaque(false);

        // Posizione Box
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(60, 0, 0, 0);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        cardPanel.add(createLoginContent(), CARD_LOGIN);
        cardPanel.add(createRegisterContent(), CARD_REGISTER);

        RoundedGlassPanel glassBox = new RoundedGlassPanel(cardPanel);
        glassBox.setPreferredSize(new Dimension(BOX_WIDTH, BOX_HEIGHT));

        centerArea.add(glassBox, gbc);
        mainPanel.add(centerArea, BorderLayout.CENTER);
    }

    private void loadResources() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/images/altro/LoginPage.png"));
            BufferedImage originalLogo = ImageIO.read(getClass().getResource("/images/altro/logo_unina.png"));
            int targetHeight = 70;
            int targetWidth = (int) ((double) originalLogo.getWidth() / originalLogo.getHeight() * targetHeight);
            logoIcon = new ImageIcon(originalLogo.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            System.err.println("Errore caricamento risorse: " + e.getMessage());
        }
    }

    private JPanel createLoginContent() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(25, 35, 25, 35));

        p.add(createTitleLabel("Bentornato"));
        p.add(Box.createVerticalStrut(5));
        p.add(createSubtitleLabel("Accedi al tuo account"));
        p.add(Box.createVerticalStrut(25));

        p.add(createFieldLabel("Email Istituzionale"));
        emailLoginField = createStyledTextField("nome.cognome@studenti.unina.it");
        p.add(emailLoginField);
        p.add(Box.createVerticalStrut(12));

        p.add(createFieldLabel("Password"));
        passwordLoginField = createStyledPasswordField();
        p.add(passwordLoginField);

        p.add(Box.createVerticalGlue());

        loginButton = createStyledButton("ACCEDI");
        p.add(loginButton);
        p.add(Box.createVerticalStrut(12));

        linkToRegister = createLink("Non sei registrato? ", "Crea Account");
        p.add(linkToRegister);

        return p;
    }

    private JPanel createRegisterContent() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(15, 25, 10, 25));

        p.add(createTitleLabel("Registrazione"));
        p.add(Box.createVerticalStrut(5));

        JPanel row1 = new JPanel(new GridLayout(1, 2, 8, 0));
        row1.setOpaque(false);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        row1.add(createMiniFieldPanel("Nome", regNomeField = createStyledTextField("")));
        row1.add(createMiniFieldPanel("Cognome", regCognomeField = createStyledTextField("")));
        p.add(row1);

        p.add(Box.createVerticalStrut(2));
        p.add(createFieldLabel("Matricola"));
        regMatricolaField = createStyledTextField("");
        p.add(regMatricolaField);

        p.add(Box.createVerticalStrut(2));
        p.add(createFieldLabel("Email"));
        regEmailField = createStyledTextField("");
        p.add(regEmailField);

        p.add(Box.createVerticalStrut(2));
        JPanel row2 = new JPanel(new GridLayout(1, 2, 8, 0));
        row2.setOpaque(false);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        row2.add(createMiniFieldPanel("Password", regPasswordField = createStyledPasswordField()));
        regSessoCombo = new JComboBox<>(new String[]{"M", "F", "Altro"});
        regSessoCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        row2.add(createMiniFieldPanel("Sesso", regSessoCombo));
        p.add(row2);

        p.add(Box.createVerticalStrut(2));
        JPanel rowVia = new JPanel(new BorderLayout(8, 0));
        rowVia.setOpaque(false);
        rowVia.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        rowVia.add(createMiniFieldPanel("Via", regViaField = createStyledTextField("")), BorderLayout.CENTER);
        rowVia.add(createMiniFieldPanel("Civico", regCivicoField = createStyledTextField("")), BorderLayout.EAST);
        p.add(rowVia);

        p.add(Box.createVerticalStrut(2));
        JPanel rowDetails = new JPanel(new GridLayout(1, 3, 5, 0));
        rowDetails.setOpaque(false);
        rowDetails.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        rowDetails.add(createMiniFieldPanel("CAP", regCapField = createStyledTextField("")));
        rowDetails.add(createMiniFieldPanel("Città", regCittaField = createStyledTextField("")));
        rowDetails.add(createMiniFieldPanel("Stato", regStatoField = createStyledTextField("")));
        p.add(rowDetails);

        p.add(Box.createVerticalStrut(5));
        JPanel prefs = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        prefs.setOpaque(false);
        checkSpedizione = new JCheckBox("Spedizione");
        checkIncontro = new JCheckBox("Incontro");
        checkSpedizione.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        checkIncontro.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        checkSpedizione.setOpaque(false); checkIncontro.setOpaque(false);
        prefs.add(checkSpedizione); prefs.add(checkIncontro);
        p.add(prefs);

        p.add(Box.createVerticalGlue());

        registerButton = createStyledButton("REGISTRATI");
        p.add(registerButton);

        p.add(Box.createVerticalStrut(5));
        linkToLogin = createLink("Hai già un account? ", "Accedi");
        p.add(linkToLogin);

        return p;
    }

    private JPanel createMiniFieldPanel(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 9));
        l.setForeground(UITheme.PRIMARY);
        p.add(l);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        p.add(field);
        return p;
    }

    public String getEmail() { return emailLoginField.getText(); }
    public String getPassword() { return new String(passwordLoginField.getPassword()); }
    public void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Errore", JOptionPane.ERROR_MESSAGE); }
    public void showSuccess(String msg) { JOptionPane.showMessageDialog(this, msg, "Successo", JOptionPane.INFORMATION_MESSAGE); }
    public void addLoginListener(ActionListener l) { loginButton.addActionListener(l); }
    public void addRegisterListener(ActionListener l) { registerButton.addActionListener(l); }

    public void addSwitchToRegisterListener(ActionListener l) {
        linkToRegister.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { l.actionPerformed(null); }
        });
    }
    public void addBackToLoginListener(ActionListener l) {
        linkToLogin.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { l.actionPerformed(null); }
        });
    }

    public void showRegisterCard() { cardLayout.show(cardPanel, CARD_REGISTER); }
    public void showLoginCard() { cardLayout.show(cardPanel, CARD_LOGIN); }
    public void switchToLoginCardPrefillEmail(String email) { emailLoginField.setText(email); showLoginCard(); }

    public void clearRegisterFields() {
        regNomeField.setText(""); regCognomeField.setText(""); regMatricolaField.setText("");
        regEmailField.setText(""); regPasswordField.setText(""); regViaField.setText("");
        regCivicoField.setText(""); regCapField.setText(""); regCittaField.setText("");
        regStatoField.setText(""); checkSpedizione.setSelected(false); checkIncontro.setSelected(false);
    }

    public String getRegNome() { return regNomeField.getText(); }
    public String getRegCognome() { return regCognomeField.getText(); }
    public String getRegMatricola() { return regMatricolaField.getText(); }
    public String getRegEmail() { return regEmailField.getText(); }
    public String getRegPassword() { return new String(regPasswordField.getPassword()); }
    public SessoStudente getRegSesso() {
        String s = (String) regSessoCombo.getSelectedItem();
        return "M".equals(s) ? SessoStudente.M : "F".equals(s) ? SessoStudente.F : SessoStudente.Altro;
    }
    public String getRegVia() { return regViaField.getText(); }
    public String getRegCivico() { return regCivicoField.getText(); }
    public String getRegCap() { return regCapField.getText(); }
    public String getRegCitta() { return regCittaField.getText(); }
    public String getRegStato() { return regStatoField.getText(); }
    public boolean isRegPreferisceSpedizione() { return checkSpedizione.isSelected(); }
    public boolean isRegPreferisceIncontroInUni() { return checkIncontro.isSelected(); }

    private JLabel createTitleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 18));
        l.setForeground(UITheme.PRIMARY);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private JLabel createSubtitleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(UITheme.TEXT_SECONDARY);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(UITheme.PRIMARY);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private JTextField createStyledTextField(String ph) {
        JTextField t = new JTextField();
        t.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, ph);
        t.putClientProperty(FlatClientProperties.STYLE, "arc: 12; margin: 2,8,2,8;");
        t.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
        return t;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField t = new JPasswordField();
        t.putClientProperty(FlatClientProperties.STYLE, "arc: 12; margin: 2,8,2,8; showRevealButton: true");
        t.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
        return t;
    }

    private JButton createStyledButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(UITheme.PRIMARY);
        b.setForeground(Color.WHITE);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.putClientProperty(FlatClientProperties.STYLE, "arc: 15; border: 0,0,0,0;");
        b.setMaximumSize(new Dimension(160, 28));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        return b;
    }

    private JLabel createLink(String normal, String link) {
        JLabel l = new JLabel("<html>" + normal + "<font color='#1B415D'><b>" + link + "</b></font></html>");
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        return l;
    }

    private class BackgroundPanel extends JPanel {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            }
        }
    }

    private class RoundedGlassPanel extends JPanel {
        public RoundedGlassPanel(JComponent content) {
            setLayout(new BorderLayout());
            setOpaque(false);
            add(content, BorderLayout.CENTER);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 240));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            g2.setColor(new Color(200, 200, 200, 100));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 40, 40);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}