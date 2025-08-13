package com.smartitems.ui;

import com.smartitems.model.User;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private final User currentUser;
    private JTabbedPane tabs; // Add this field

    public DashboardFrame(User currentUser) {
        super("Smart Items - Dashboard");
        this.currentUser = currentUser;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JComponent buildSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0,1,8,8));
        panel.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        panel.setBackground(new Color(52, 73, 94));

        JButton dashboardBtn = createSidebarButton("Dashboard", new Color(52, 152, 219));
        JButton productsBtn = createSidebarButton("Products", new Color(46, 204, 113));
        JButton customersBtn = createSidebarButton("Customers", new Color(155, 89, 182));
        JButton ordersBtn = createSidebarButton("Orders", new Color(230, 126, 34));
        JButton repairsBtn = createSidebarButton("Repairs", new Color(231, 76, 60));
        JButton employeesBtn = createSidebarButton("Employees", new Color(52, 73, 94));
        JButton salariesBtn = createSidebarButton("Salaries", new Color(142, 68, 173));

        // Add action listeners for navigation
        dashboardBtn.addActionListener(e -> selectTab(0));
        productsBtn.addActionListener(e -> selectTab(1));
        customersBtn.addActionListener(e -> selectTab(2));
        ordersBtn.addActionListener(e -> selectTab(3));
        repairsBtn.addActionListener(e -> selectTab(4));
        employeesBtn.addActionListener(e -> selectTab(5));
        salariesBtn.addActionListener(e -> selectTab(6));

        panel.add(dashboardBtn);
        panel.add(productsBtn);
        panel.add(customersBtn);
        panel.add(ordersBtn);
        panel.add(repairsBtn);
        panel.add(employeesBtn);
        panel.add(salariesBtn);

        return new JScrollPane(panel);
    }
    
    private JButton createSidebarButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private JComponent buildContent() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        
        JLabel titleLabel = new JLabel("Smart Items Management System");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24f));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getName());
        userLabel.setFont(userLabel.getFont().deriveFont(Font.PLAIN, 14f));
        userLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.EAST);
        
        // Tabbed content
        tabs = new JTabbedPane(); // Assign to field
        tabs.setFont(tabs.getFont().deriveFont(Font.BOLD, 14f));
        tabs.addTab("Overview", new OverviewPanel());
        tabs.addTab("Products", new ProductsPanel());
        tabs.addTab("Customers", new CustomersPanel());
        tabs.addTab("Orders", new OrdersPanel());
        tabs.addTab("Repairs", new RepairsPanel());
        tabs.addTab("Employees", new EmployeesPanel());
        tabs.addTab("Salaries", new SalariesPanel());
        
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(tabs, BorderLayout.CENTER);
        
        return contentPanel;
    }

    // Add method to select tabs
    private void selectTab(int index) {
        if (tabs != null && index >= 0 && index < tabs.getTabCount()) {
            tabs.setSelectedIndex(index);
        }
    }
}


