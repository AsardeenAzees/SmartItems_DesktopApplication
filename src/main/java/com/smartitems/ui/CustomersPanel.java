package com.smartitems.ui;

import com.smartitems.dao.CustomerDao;
import com.smartitems.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CustomersPanel extends JPanel {
    private final JTextField searchField = new JTextField();
    private final JButton searchButton = new JButton("Search");
    private final JButton addButton = new JButton("Add");
    private final JButton editButton = new JButton("Edit");
    private final JButton deleteButton = new JButton("Delete");
    private final JTable table = new JTable();
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"ID","Name","Email","Phone","Address"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final CustomerDao dao = new CustomerDao();

    public CustomersPanel() {
        setLayout(new BorderLayout());
        table.setModel(model);
        add(buildToolbar(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        try { dao.createTableIfNotExists(); } catch (SQLException ignored) {}
        searchButton.addActionListener(e -> reload());
        addButton.addActionListener(e -> onAdd());
        editButton.addActionListener(e -> onEdit());
        deleteButton.addActionListener(e -> onDelete());
        reload();
    }

    private JComponent buildToolbar() {
        JPanel p = new JPanel(new BorderLayout(6,6));
        JPanel left = new JPanel(new BorderLayout(6,6));
        left.add(searchField, BorderLayout.CENTER);
        left.add(searchButton, BorderLayout.EAST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.add(addButton);
        right.add(editButton);
        right.add(deleteButton);
        p.add(left, BorderLayout.CENTER);
        p.add(right, BorderLayout.EAST);
        p.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        return p;
    }

    private void reload() {
        model.setRowCount(0);
        String q = searchField.getText().trim();
        try {
            List<Customer> list = dao.search(q);
            for (Customer c : list) {
                model.addRow(new Object[]{c.getId(), c.getName(), c.getEmail(), c.getPhone(), c.getAddress()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdd() {
        CustomerFormDialog dlg = new CustomerFormDialog(SwingUtilities.getWindowAncestor(this));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        if (dlg.getResult() != null) {
            try {
                dao.insert(dlg.getResult());
                reload();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Customer c = new Customer();
        c.setId((Long) model.getValueAt(row, 0));
        c.setName((String) model.getValueAt(row, 1));
        c.setEmail((String) model.getValueAt(row, 2));
        c.setPhone((String) model.getValueAt(row, 3));
        c.setAddress((String) model.getValueAt(row, 4));
        CustomerFormDialog dlg = new CustomerFormDialog(SwingUtilities.getWindowAncestor(this), c);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        if (dlg.getResult() != null) {
            try {
                Customer updated = dlg.getResult();
                updated.setId(c.getId());
                dao.update(updated);
                reload();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Long id = (Long) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected customer?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            dao.delete(id);
            reload();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class CustomerFormDialog extends JDialog {
        private final JTextField nameField = new JTextField();
        private final JTextField emailField = new JTextField();
        private final JTextField phoneField = new JTextField();
        private final JTextField addressField = new JTextField();
        private Customer result;

        public CustomerFormDialog(Window owner) { this(owner, null); }
        public CustomerFormDialog(Window owner, Customer initial) {
            super(owner, "Customer", ModalityType.APPLICATION_MODAL);
            setSize(480, 280);
            setLayout(new BorderLayout());
            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6,6,6,6);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            int row = 0;
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Name"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(nameField, gbc);
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Email"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(emailField, gbc);
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Phone"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(phoneField, gbc);
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Address"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(addressField, gbc);
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancel = new JButton("Cancel");
            JButton save = new JButton("Save");
            actions.add(cancel); actions.add(save);
            add(form, BorderLayout.CENTER);
            add(actions, BorderLayout.SOUTH);
            cancel.addActionListener(e -> dispose());
            save.addActionListener(e -> onSave());

            if (initial != null) {
                nameField.setText(initial.getName());
                emailField.setText(initial.getEmail());
                phoneField.setText(initial.getPhone());
                addressField.setText(initial.getAddress());
            }
        }

        private void onSave() {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name is required", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Customer c = new Customer();
            c.setName(name);
            c.setEmail(emailField.getText().trim());
            c.setPhone(phoneField.getText().trim());
            c.setAddress(addressField.getText().trim());
            this.result = c;
            dispose();
        }

        public Customer getResult() { return result; }
    }
}



