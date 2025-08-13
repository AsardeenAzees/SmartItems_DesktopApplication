package com.smartitems.ui;

import javax.swing.*;
import java.awt.*;

public class StyleUtils {
    
    // Color palette
    public static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    public static final Color WARNING_COLOR = new Color(230, 126, 34);
    public static final Color DANGER_COLOR = new Color(231, 76, 60);
    public static final Color INFO_COLOR = new Color(155, 89, 182);
    public static final Color DARK_COLOR = new Color(52, 73, 94);
    public static final Color LIGHT_BG = new Color(248, 249, 250);
    
    public static void applyModernStyle(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 12f));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
    }
    
    public static void applyTableStyle(JTable table) {
        table.setRowHeight(30);
        table.setFont(table.getFont().deriveFont(Font.PLAIN, 12f));
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(PRIMARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        
        // Style header
        table.getTableHeader().setBackground(DARK_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
    }
    
    public static void applyTextFieldStyle(JTextField textField) {
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        textField.setFont(textField.getFont().deriveFont(Font.PLAIN, 12f));
    }
    
    public static void applyPanelStyle(JPanel panel) {
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    }
    
    public static JLabel createStyledLabel(String text, Color color, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, fontSize));
        label.setForeground(color);
        return label;
    }
    
    public static JPanel createCardPanel(String title, Color borderColor) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 2),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        card.setBackground(Color.WHITE);
        
        if (title != null) {
            JLabel titleLabel = createStyledLabel(title, borderColor, 14);
            card.add(titleLabel, BorderLayout.NORTH);
        }
        
        return card;
    }
    
    public static void applySpinnerStyle(JSpinner spinner) {
        spinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        spinner.setFont(spinner.getFont().deriveFont(Font.PLAIN, 12f));
    }
    
    public static void applyComboBoxStyle(JComboBox<?> comboBox) {
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        comboBox.setFont(comboBox.getFont().deriveFont(Font.PLAIN, 12f));
    }
}






