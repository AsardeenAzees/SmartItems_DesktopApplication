package com.smartitems;

import com.formdev.flatlaf.FlatLightLaf;
import com.smartitems.ui.LoginFrame;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FlatLightLaf.setup();
            } catch (Exception ignored) {
            }
            LoginFrame frame = new LoginFrame();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}



