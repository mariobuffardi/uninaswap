package it.unina.uninaswap.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import it.unina.uninaswap.util.UITheme;

public class AnnunciMenuPanel extends JPanel {

	private JButton homeButton;
	private JButton profileButton;
	private JButton reportButton;
	private JButton notificationButton;
	private JButton addButton;
	private JButton settingsButton;
	private JButton logoutButton;

	public AnnunciMenuPanel() {
		setPreferredSize(new Dimension(200, 0));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(UITheme.PRIMARY);
		setBorder(new EmptyBorder(16, 12, 16, 12));

		// Titolo Menu (centered)
		JLabel menuLabel = new JLabel("Menu");
		menuLabel.setForeground(Color.WHITE);
		menuLabel.setFont(menuLabel.getFont().deriveFont(22f).deriveFont(java.awt.Font.BOLD));
		menuLabel.setAlignmentX(CENTER_ALIGNMENT);
		add(menuLabel);

		add(Box.createVerticalStrut(16));

		setVisible(false);

		// Bottoni menu con icone
		homeButton = createMenuButton("/images/menuIcons/home-button.png", "Home");
		add(homeButton);
		add(Box.createVerticalStrut(8));

		profileButton = createMenuButton("/images/menuIcons/profile.png", "Profilo");
		add(profileButton);
		add(Box.createVerticalStrut(8));

		reportButton = createMenuButton("/images/menuIcons/report.png", "Report");
		add(reportButton);
		add(Box.createVerticalStrut(8));

		notificationButton = createMenuButton("/images/menuIcons/notification.png", "Notifiche");
		add(notificationButton);
		add(Box.createVerticalStrut(8));

		addButton = createMenuButton("/images/menuIcons/add.png", "Pubblica");
		add(addButton);
		add(Box.createVerticalStrut(8));

		settingsButton = createMenuButton("/images/menuIcons/profileSettings.png", "Impostazioni");
		add(settingsButton);
		add(Box.createVerticalStrut(8));

		logoutButton = createMenuButton("/images/menuIcons/logout.png", "Logout");
		add(logoutButton);

		add(Box.createVerticalGlue());
	}


	private JButton createMenuButton(String iconPath, String text) {
		JButton button = new JButton();


		try {
			ImageIcon rawIcon = new ImageIcon(getClass().getResource(iconPath));
			Image scaledIcon = rawIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			button.setIcon(new ImageIcon(scaledIcon));
		} catch (Exception e) {
			System.err.println("Icona non trovata: " + iconPath);
			button.setText(text); // Fallback to text
		}


		button.setAlignmentX(CENTER_ALIGNMENT);
		button.setMaximumSize(new Dimension(170, 70));
		button.setPreferredSize(new Dimension(170, 70));
		button.setBackground(UITheme.PRIMARY);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(true);
		button.setOpaque(true);

		// Hover effect
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(UITheme.PRIMARY_DARK);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(UITheme.PRIMARY);
			}
		});

		return button;
	}

	// Getter per il controller
	public JButton getHomeButton() {
		return homeButton;
	}

	public JButton getProfileButton() {
		return profileButton;
	}

	public JButton getReportButton() {
		return reportButton;
	}

	public JButton getNotificationButton() {
		return notificationButton;
	}

	public JButton getAddButton() {
		return addButton;
	}

	public JButton getSettingsButton() {
		return settingsButton;
	}

	public JButton getLogoutButton() {
		return logoutButton;
	}
}