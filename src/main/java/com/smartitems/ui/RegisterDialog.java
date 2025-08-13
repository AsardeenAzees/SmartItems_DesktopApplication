package com.smartitems.ui;

import com.smartitems.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class RegisterDialog extends JDialog {
    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField mobileField = new JTextField();
    private final JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
    private final JPasswordField passwordField = new JPasswordField();
    private final JButton submitButton = new JButton("Create account");
    private final JButton cancelButton = new JButton("Cancel");
    private final AuthService authService = new AuthService();

    public RegisterDialog(Frame owner) {
        super(owner, "Register", true);
        setSize(520, 360);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Name"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; form.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Email"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; form.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Mobile"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; form.add(mobileField, gbc);

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Gender"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; form.add(genderBox, gbc);

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Password"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; form.add(passwordField, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(cancelButton);
        actions.add(submitButton);

        add(form, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        submitButton.addActionListener(e -> onSubmit());
        cancelButton.addActionListener(e -> dispose());
    }

    private void onSubmit() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String mobile = mobileField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        String password = new String(passwordField.getPassword());

        if (name.isEmpty() || email.isEmpty() || mobile.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            authService.register(name, email, mobile, password, gender);
            JOptionPane.showMessageDialog(this, "Registration successful. You can now login.");
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}



