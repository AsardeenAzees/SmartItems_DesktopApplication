package com.smartitems.ui;

import com.smartitems.dao.MetricsDao;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class OverviewPanel extends JPanel {
    private final JLabel totalStockLabel = new JLabel("0");
    private final JLabel totalOrdersLabel = new JLabel("0");
    private final JLabel pendingOrdersLabel = new JLabel("0");
    private final JLabel completedOrdersLabel = new JLabel("0");
    private final JLabel totalEarningsLabel = new JLabel("$0.00");
    private final JLabel pendingAmountLabel = new JLabel("$0.00");
    private final JLabel totalCustomersLabel = new JLabel("0");
    private final JLabel ongoingRepairsLabel = new JLabel("0");
    private final JLabel totalSalariesDueLabel = new JLabel("$0.00");
    private final JLabel lowStockLabel = new JLabel("0");

    private final MetricsDao metricsDao = new MetricsDao();
    private final Timer autoRefreshTimer;

    public OverviewPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        
        // Main metrics panel
        JPanel metricsPanel = new JPanel(new GridLayout(3, 3, 12, 12));
        metricsPanel.add(createMetricCard("Total Stock", totalStockLabel, new Color(52, 152, 219)));
        metricsPanel.add(createMetricCard("Total Orders", totalOrdersLabel, new Color(46, 204, 113)));
        metricsPanel.add(createMetricCard("Pending Orders", pendingOrdersLabel, new Color(230, 126, 34)));
        metricsPanel.add(createMetricCard("Completed Orders", completedOrdersLabel, new Color(39, 174, 96)));
        metricsPanel.add(createMetricCard("Total Earnings", totalEarningsLabel, new Color(155, 89, 182)));
        metricsPanel.add(createMetricCard("Pending Amount", pendingAmountLabel, new Color(231, 76, 60)));
        metricsPanel.add(createMetricCard("Total Customers", totalCustomersLabel, new Color(52, 73, 94)));
        metricsPanel.add(createMetricCard("Ongoing Repairs", ongoingRepairsLabel, new Color(243, 156, 18)));
        metricsPanel.add(createMetricCard("Low Stock Items", lowStockLabel, new Color(192, 57, 43)));

        // Salaries panel
        JPanel salariesPanel = new JPanel(new BorderLayout());
        salariesPanel.setBorder(BorderFactory.createTitledBorder("Salary Information"));
        salariesPanel.add(createMetricCard("Total Salaries Due", totalSalariesDueLabel, new Color(142, 68, 173)), BorderLayout.CENTER);

        // Add refresh button
        JButton refreshButton = new JButton("Refresh Dashboard");
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadMetrics());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);

        add(metricsPanel, BorderLayout.CENTER);
        add(salariesPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.NORTH);

        // Setup auto-refresh timer (refresh every 30 seconds)
        autoRefreshTimer = new Timer(30000, e -> loadMetrics());
        autoRefreshTimer.start();

        SwingUtilities.invokeLater(this::loadMetrics);
    }

    private void loadMetrics() {
        try {
            totalStockLabel.setText(String.valueOf(metricsDao.getTotalStockQuantity()));
            totalOrdersLabel.setText(String.valueOf(metricsDao.getTotalOrders()));
            pendingOrdersLabel.setText(String.valueOf(metricsDao.getPendingOrders()));
            completedOrdersLabel.setText(String.valueOf(metricsDao.getCompletedOrders()));
            totalEarningsLabel.setText("$" + metricsDao.getTotalEarnings().toString());
            pendingAmountLabel.setText("$" + metricsDao.getPendingAmount().toString());
            totalCustomersLabel.setText(String.valueOf(metricsDao.getTotalCustomers()));
            ongoingRepairsLabel.setText(String.valueOf(metricsDao.getOngoingRepairs()));
            totalSalariesDueLabel.setText("$" + metricsDao.getTotalSalariesDue().toString());
            lowStockLabel.setText(String.valueOf(metricsDao.getLowStockProducts()));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JComponent createMetricCard(String title, JLabel value, Color color) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        card.setBackground(new Color(248, 249, 250));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        titleLabel.setForeground(color);

        value.setHorizontalAlignment(SwingConstants.CENTER);
        value.setFont(value.getFont().deriveFont(Font.BOLD, 24f));
        value.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        return card;
    }
}


