package com.smartitems.ui;

import com.smartitems.dao.EmployeeDao;
import com.smartitems.model.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class EmployeesPanel extends JPanel {
    private final JTextField searchField = new JTextField();
    private final JButton searchButton = new JButton("Search");
    private final JButton addButton = new JButton("Add");
    private final JButton editButton = new JButton("Edit");
    private final JButton deleteButton = new JButton("Delete");
    private final JTable table = new JTable();
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"ID","Name","Role","Salary","Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final EmployeeDao dao = new EmployeeDao();

    public EmployeesPanel() {
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
            List<Employee> list = dao.search(q);
            for (Employee emp : list) {
                model.addRow(new Object[]{
                    emp.getId(), 
                    emp.getName(), 
                    emp.getRole(), 
                    emp.getSalary(), 
                    emp.getStatus()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdd() {
        EmployeeFormDialog dlg = new EmployeeFormDialog(SwingUtilities.getWindowAncestor(this));
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
        Employee emp = new Employee();
        emp.setId((Long) model.getValueAt(row, 0));
        emp.setName((String) model.getValueAt(row, 1));
        emp.setRole((String) model.getValueAt(row, 2));
        emp.setSalary((BigDecimal) model.getValueAt(row, 3));
        emp.setStatus((String) model.getValueAt(row, 4));
        
        EmployeeFormDialog dlg = new EmployeeFormDialog(SwingUtilities.getWindowAncestor(this), emp);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        if (dlg.getResult() != null) {
            try {
                Employee updated = dlg.getResult();
                updated.setId(emp.getId());
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
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected employee?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            dao.delete(id);
            reload();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class EmployeeFormDialog extends JDialog {
        private final JTextField nameField = new JTextField();
        private final JTextField roleField = new JTextField();
        private final JSpinner salarySpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 1_000_000.0, 100.0));
        private final JComboBox<String> statusBox = new JComboBox<>(new String[]{"active", "inactive"});
        private Employee result;

        public EmployeeFormDialog(Window owner) { this(owner, null); }
        public EmployeeFormDialog(Window owner, Employee initial) {
            super(owner, "Employee", ModalityType.APPLICATION_MODAL);
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
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Role"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(roleField, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Salary"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(salarySpinner, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Status"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(statusBox, gbc);
            
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
                roleField.setText(initial.getRole());
                if (initial.getSalary() != null) {
                    salarySpinner.setValue(initial.getSalary().doubleValue());
                }
                statusBox.setSelectedItem(initial.getStatus());
            }
        }

        private void onSave() {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name is required", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double salaryVal = ((Number) salarySpinner.getValue()).doubleValue();
            
            Employee emp = new Employee();
            emp.setName(name);
            emp.setRole(roleField.getText().trim());
            emp.setSalary(BigDecimal.valueOf(salaryVal));
            emp.setStatus((String) statusBox.getSelectedItem());
            this.result = emp;
            dispose();
        }

        public Employee getResult() { return result; }
    }
}



