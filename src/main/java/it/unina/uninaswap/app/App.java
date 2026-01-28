package it.unina.uninaswap.app;

import javax.swing.SwingUtilities;

import it.unina.uninaswap.controller.LoginController;
import it.unina.uninaswap.util.UITheme;
import it.unina.uninaswap.view.LoginView;

public class App {
    public static void main(String[] args) {
        UITheme.setup();

        SwingUtilities.invokeLater(() -> {
                                           
            LoginView view = new LoginView();
            new LoginController(view); 
            view.setVisible(true);
        });
    }
}