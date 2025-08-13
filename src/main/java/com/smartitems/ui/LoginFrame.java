package com.smartitems.ui;

import com.smartitems.model.User;
import com.smartitems.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JButton loginButton = new JButton("Login");
    private final JButton registerButton = new JButton("Register");
    private final AuthService authService = new AuthService();

    public LoginFrame() {
        super("Smart Items - Login");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(420, 260);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Email"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; form.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Password"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; form.add(passwordField, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(registerButton);
        actions.add(loginButton);

        add(form, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> onLogin());
        registerButton.addActionListener(e -> onRegister());
    }

    private void onLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            User user = authService.login(email, password);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Login failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            DashboardFrame dashboard = new DashboardFrame(user);
            dashboard.setLocationRelativeTo(this);
            dashboard.setVisible(true);
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRegister() {
        RegisterDialog dialog = new RegisterDialog(this);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}



