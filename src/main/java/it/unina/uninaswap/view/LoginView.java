package it.unina.uninaswap.view;

import com.formdev.flatlaf.FlatClientProperties;
import it.unina.uninaswap.model.enums.SessoStudente;
import it.unina.uninaswap.util.UITheme;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class LoginView extends JFrame {

    // Login
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    // Registrazione
    private JTextField txtRegNome, txtRegCognome, txtRegMatricola, txtRegEmail;
    private JPasswordField txtRegPassword;
    private JComboBox<SessoStudente> comboSesso;
    private JTextField txtRegVia, txtRegCivico, txtRegCap, txtRegCitta, txtRegStato;
    private JCheckBox checkSpedizione, checkIncontro;
    private JButton btnRegister;

    // Overlay
    private JButton btnOverlayToRegister;
    private JButton btnOverlayToLogin;

    private Image backgroundImage;
    private JLayeredPane authBox;
    private JPanel pnlLogin;
    private JPanel pnlRegister;
    private JPanel pnlOverlay;

    private Timer animationTimer;
    private final int BOX_WIDTH = 800;
    private final int BOX_HEIGHT = 480;
    private final int SIDE_WIDTH = BOX_WIDTH / 2;
    private final int ARC_SIZE = 40;
    private boolean isLoginVisible = true;

    public LoginView() {
        setTitle("UniNaSwap - Accesso");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        loadResources();

        // Struttura generale
        BackgroundPanel mainBackground = new BackgroundPanel();
        mainBackground.setLayout(new GridBagLayout());
        setContentPane(mainBackground);

        authBox = new RoundedLayeredPane(ARC_SIZE);
        authBox.setPreferredSize(new Dimension(BOX_WIDTH, BOX_HEIGHT));
        authBox.setLayout(null);

        initLoginPanel();
        initRegisterPanel();
        initOverlayPanel();

        authBox.add(pnlLogin, JLayeredPane.DEFAULT_LAYER);
        authBox.add(pnlRegister, JLayeredPane.DEFAULT_LAYER);
        authBox.add(pnlOverlay, JLayeredPane.PALETTE_LAYER);

        mainBackground.add(authBox);
    }

    private void loadResources() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/images/altro/LoginPage.png"));
        } catch (Exception e) {
            System.err.println("Impossibile caricare sfondo: " + e.getMessage());
        }
    }

    // Login
    private void initLoginPanel() {
        pnlLogin = new JPanel();
        pnlLogin.setLayout(null);
        pnlLogin.setBounds(0, 0, SIDE_WIDTH, BOX_HEIGHT);
        pnlLogin.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Accedi", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(UITheme.PRIMARY);
        lblTitle.setBounds(40, 60, 320, 40);
        pnlLogin.add(lblTitle);

        JLabel lblSub = new JLabel("Usa il tuo account istituzionale", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(UITheme.TEXT_SECONDARY);
        lblSub.setBounds(40, 100, 320, 20);
        pnlLogin.add(lblSub);

        txtEmail = createStyledTextField("Email Istituzionale");
        txtEmail.setBounds(40, 160, 320, 40);
        pnlLogin.add(txtEmail);

        txtPassword = createStyledPasswordField("Password");
        txtPassword.setBounds(40, 215, 320, 40);
        pnlLogin.add(txtPassword);

        btnLogin = createStyledButton("ACCEDI");
        btnLogin.setBounds(40, 300, 320, 40);
        pnlLogin.add(btnLogin);
    }

    // Registrazione
    private void initRegisterPanel() {
        pnlRegister = new JPanel();
        pnlRegister.setLayout(new BorderLayout());
        pnlRegister.setBounds(SIDE_WIDTH, 0, SIDE_WIDTH, BOX_HEIGHT);
        pnlRegister.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Crea Account", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(UITheme.PRIMARY);
        lblTitle.setBorder(new EmptyBorder(15, 0, 10, 0));
        pnlRegister.add(lblTitle, BorderLayout.NORTH);

        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(Color.WHITE);
        formContent.setBorder(new EmptyBorder(5, 35, 5, 35));

        txtRegNome = addField(formContent, "Nome");
        txtRegCognome = addField(formContent, "Cognome");
        txtRegMatricola = addField(formContent, "Matricola");
        txtRegEmail = addField(formContent, "Email");

        txtRegPassword = createStyledPasswordField("Password");
        formContent.add(txtRegPassword);
        formContent.add(Box.createVerticalStrut(5));

        comboSesso = new JComboBox<>(SessoStudente.values());
        comboSesso.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: #FFFFFF; borderColor: #E0E0E0");
        comboSesso.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        comboSesso.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Sesso"));
        formContent.add(comboSesso);
        formContent.add(Box.createVerticalStrut(5));

        txtRegVia = addField(formContent, "Via");

        JPanel rowCivCap = new JPanel(new GridLayout(1, 2, 10, 0));
        rowCivCap.setBackground(Color.WHITE);
        rowCivCap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtRegCivico = createStyledTextField("Civico");
        txtRegCap = createStyledTextField("CAP");
        rowCivCap.add(txtRegCivico);
        rowCivCap.add(txtRegCap);
        formContent.add(rowCivCap);
        formContent.add(Box.createVerticalStrut(5));

        txtRegCitta = addField(formContent, "Città");
        txtRegStato = addField(formContent, "Stato");

        checkSpedizione = new JCheckBox("Spedizione");
        checkIncontro = new JCheckBox("Incontro Uni");
        checkSpedizione.setBackground(Color.WHITE);
        checkIncontro.setBackground(Color.WHITE);

        JLabel lblPreferenza = new JLabel("Preferenza Spedizione");
        lblPreferenza.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblPreferenza.setForeground(UITheme.TEXT_SECONDARY);
        lblPreferenza.setAlignmentX(Component.LEFT_ALIGNMENT);
        formContent.add(Box.createVerticalStrut(5));
        formContent.add(lblPreferenza);

        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkPanel.setBackground(Color.WHITE);
        checkPanel.add(checkSpedizione);
        checkPanel.add(Box.createHorizontalStrut(10));
        checkPanel.add(checkIncontro);
        formContent.add(Box.createVerticalStrut(5));
        formContent.add(checkPanel);

        JScrollPane scrollPane = new JScrollPane(formContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        pnlRegister.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 15, 0));

        btnRegister = createStyledButton("REGISTRATI");
        btnRegister.setPreferredSize(new Dimension(280, 40));
        bottomPanel.add(btnRegister);
        pnlRegister.add(bottomPanel, BorderLayout.SOUTH);
    }

    private JTextField addField(JPanel p, String placeholder) {
        JTextField tf = createStyledTextField(placeholder);
        p.add(tf);
        p.add(Box.createVerticalStrut(5));
        return tf;
    }

    // Overlay
    private void initOverlayPanel() {
        pnlOverlay = new JPanel();
        pnlOverlay.setLayout(new CardLayout());
        pnlOverlay.setBounds(SIDE_WIDTH, 0, SIDE_WIDTH, BOX_HEIGHT);
        pnlOverlay.setBackground(UITheme.PRIMARY);

        JPanel cardToReg = createOverlayContent("Ciao Studente!",
                "Non hai un account? Registrati ora.", "VAI ALLA REGISTRAZIONE");
        btnOverlayToRegister = (JButton) cardToReg.getClientProperty("btnAction");

        JPanel cardToLog = createOverlayContent("Bentornato!",
                "Hai già un account? Accedi.", "TORNA AL LOGIN");
        btnOverlayToLogin = (JButton) cardToLog.getClientProperty("btnAction");

        pnlOverlay.add(cardToReg, "TO_REGISTER");
        pnlOverlay.add(cardToLog, "TO_LOGIN");
    }

    private JPanel createOverlayContent(String title, String desc, String btnText) {
        JPanel p = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.PRIMARY, getWidth(), getHeight(), UITheme.PRIMARY_DARK);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 30, 10, 30);

        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblT.setForeground(Color.WHITE);
        p.add(lblT, gbc);

        JTextArea txtDesc = new JTextArea(desc);
        txtDesc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtDesc.setForeground(new Color(230, 230, 230));
        txtDesc.setOpaque(false);
        txtDesc.setEditable(false);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setPreferredSize(new Dimension(280, 50));
        p.add(txtDesc, gbc);

        JButton btn = new JButton(btnText);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        btn.setForeground(UITheme.PRIMARY);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE,
                "arc: 50; " +
                        "borderWidth: 1; " +
                        "borderColor: " + toHex(UITheme.PRIMARY) + "; " +
                        "background: #FFFFFF; " +
                        "focusedBackground: null; " +
                        "hoverBackground: " + toHex(UITheme.PRIMARY) + "22; " +
                        "pressedBackground: " + toHex(UITheme.PRIMARY) + "44; " +
                        "foreground: " + toHex(UITheme.PRIMARY) + "; " +
                        "hoverForeground: #FFFFFF; " +
                        "pressedForeground: #FFFFFF;");
        btn.setPreferredSize(new Dimension(200, 40));

        p.add(btn, gbc);
        p.putClientProperty("btnAction", btn);
        return p;
    }

    // Stile componenti
    private JTextField createStyledTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        tf.putClientProperty(FlatClientProperties.STYLE,
                "arc: 20; " +
                        "margin: 5,10,5,10; " +
                        "borderWidth: 1; " +
                        "borderColor: #E0E0E0; " +
                        "focusedBorderColor: " + toHex(UITheme.PRIMARY));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return tf;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField tf = new JPasswordField();
        tf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        tf.putClientProperty(FlatClientProperties.STYLE,
                "arc: 20; " +
                        "margin: 5,10,5,10; " +
                        "borderWidth: 1; " +
                        "borderColor: #E0E0E0; " +
                        "showRevealButton: true; " +
                        "focusedBorderColor: " + toHex(UITheme.PRIMARY));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return tf;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(UITheme.PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE,
                "arc: 50; " +
                        "borderWidth: 0; " +
                        "hoverBackground: " + toHex(UITheme.PRIMARY_DARK));
        return btn;
    }

    private String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    // Animazione slider
    public void showRegisterCard() {
        if (!isLoginVisible) return;
        startAnimation(true);
    }

    public void showLoginCard() {
        if (isLoginVisible) return;
        startAnimation(false);
    }

    private void startAnimation(boolean goToRegister) {
        if (animationTimer != null && animationTimer.isRunning()) return;

        int targetX = goToRegister ? 0 : SIDE_WIDTH;
        int step = 30;

        CardLayout cl = (CardLayout) pnlOverlay.getLayout();
        cl.show(pnlOverlay, goToRegister ? "TO_LOGIN" : "TO_REGISTER");

        animationTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentX = pnlOverlay.getX();
                if (currentX == targetX) {
                    animationTimer.stop();
                    isLoginVisible = !goToRegister;
                    return;
                }

                int direction = (targetX > currentX) ? 1 : -1;
                int nextX = currentX + (step * direction);

                if ((direction == 1 && nextX > targetX) || (direction == -1 && nextX < targetX)) {
                    nextX = targetX;
                }

                pnlOverlay.setLocation(nextX, 0);
                repaint();
            }
        });
        animationTimer.start();
    }

    // Box
    private class RoundedLayeredPane extends JLayeredPane {
        private int arc;

        public RoundedLayeredPane(int arc) {
            this.arc = arc;
        }

        @Override
        protected void paintChildren(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D.Float round = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc);
            g2.setClip(round);
            super.paintChildren(g2);
            g2.dispose();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
        }
    }

    // Getter e listener
    public void addLoginListener(ActionListener al) {
        btnLogin.addActionListener(al);
    }

    public void addRegisterListener(ActionListener al) {
        btnRegister.addActionListener(al);
    }

    public void addSwitchToRegisterListener(ActionListener al) {
        btnOverlayToRegister.addActionListener(al);
    }

    public void addBackToLoginListener(ActionListener al) {
        btnOverlayToLogin.addActionListener(al);
    }

    public String getEmail() {
        return txtEmail.getText();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    public String getRegNome() {
        return txtRegNome.getText();
    }

    public String getRegCognome() {
        return txtRegCognome.getText();
    }

    public String getRegMatricola() {
        return txtRegMatricola.getText();
    }

    public String getRegEmail() {
        return txtRegEmail.getText();
    }

    public String getRegPassword() {
        return new String(txtRegPassword.getPassword());
    }

    public SessoStudente getRegSesso() {
        return (SessoStudente) comboSesso.getSelectedItem();
    }

    public String getRegVia() {
        return txtRegVia.getText();
    }

    public String getRegCivico() {
        return txtRegCivico.getText();
    }

    public String getRegCap() {
        return txtRegCap.getText();
    }

    public String getRegCitta() {
        return txtRegCitta.getText();
    }

    public String getRegStato() {
        return txtRegStato.getText();
    }

    public boolean isRegPreferisceSpedizione() {
        return checkSpedizione.isSelected();
    }

    public boolean isRegPreferisceIncontroInUni() {
        return checkIncontro.isSelected();
    }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Errore", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Successo", JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearRegisterFields() {
        txtRegNome.setText("");
        txtRegCognome.setText("");
        txtRegMatricola.setText("");
        txtRegEmail.setText("");
        txtRegPassword.setText("");
        txtRegVia.setText("");
        txtRegCivico.setText("");
        txtRegCap.setText("");
        txtRegCitta.setText("");
        txtRegStato.setText("");
        checkSpedizione.setSelected(false);
        checkIncontro.setSelected(false);
    }

    public void switchToLoginCardPrefillEmail(String email) {
        txtEmail.setText(email);
        showLoginCard();
    }

    // Sfondo
    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(UITheme.BACKGROUND_LIGHT);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
}
