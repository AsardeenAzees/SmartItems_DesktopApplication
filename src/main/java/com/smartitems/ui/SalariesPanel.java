package com.smartitems.ui;

import com.smartitems.dao.SalaryDao;
import com.smartitems.dao.EmployeeDao;
import com.smartitems.model.Salary;
import com.smartitems.model.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class SalariesPanel extends JPanel {
    private final JTextField searchField = new JTextField();
    private final JButton searchButton = new JButton("Search");
    private final JButton addButton = new JButton("Add");
    private final JButton editButton = new JButton("Edit");
    private final JButton deleteButton = new JButton("Delete");
    private final JTable table = new JTable();
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"ID","Employee","Salary","Bonus","Deductions","Payment Date"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final SalaryDao salaryDao = new SalaryDao();
    private final EmployeeDao employeeDao = new EmployeeDao();

    public SalariesPanel() {
        setLayout(new BorderLayout());
        table.setModel(model);
        add(buildToolbar(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        try { 
            salaryDao.createTableIfNotExists(); 
            employeeDao.createTableIfNotExists();
        } catch (SQLException ignored) {}

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
            List<Salary> list = salaryDao.search(q);
            for (Salary s : list) {
                model.addRow(new Object[]{
                    s.getId(), 
                    s.getEmployeeName(), 
                    s.getSalaryAmount(), 
                    s.getBonus(), 
                    s.getDeductions(), 
                    s.getPaymentDate()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdd() {
        SalaryFormDialog dlg = new SalaryFormDialog(SwingUtilities.getWindowAncestor(this));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        if (dlg.getResult() != null) {
            try {
                salaryDao.insert(dlg.getResult());
                reload();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        // For edit, we'd need to fetch the full salary object from the database
        // This is a simplified version
        JOptionPane.showMessageDialog(this, "Edit functionality requires fetching full salary object", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Long id = (Long) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected salary record?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            salaryDao.delete(id);
            reload();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class SalaryFormDialog extends JDialog {
        private final JComboBox<Employee> employeeBox = new JComboBox<>();
        private final JSpinner salarySpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 1_000_000.0, 100.0));
        private final JSpinner bonusSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 1_000_000.0, 100.0));
        private final JSpinner deductionsSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 1_000_000.0, 100.0));
        private final JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        private Salary result;
        private final EmployeeDao employeeDao = new EmployeeDao();

        public SalaryFormDialog(Window owner) {
            super(owner, "Salary", ModalityType.APPLICATION_MODAL);
            setSize(480, 320);
            setLayout(new BorderLayout());
            
            loadEmployees();
            
            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6,6,6,6);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            int row = 0;
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Employee"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(employeeBox, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Salary Amount"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(salarySpinner, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Bonus"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(bonusSpinner, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Deductions"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(deductionsSpinner, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Payment Date"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(dateSpinner, gbc);
            
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancel = new JButton("Cancel");
            JButton save = new JButton("Save");
            actions.add(cancel); actions.add(save);
            add(form, BorderLayout.CENTER);
            add(actions, BorderLayout.SOUTH);
            cancel.addActionListener(e -> dispose());
            save.addActionListener(e -> onSave());
        }

        private void loadEmployees() {
            try {
                List<Employee> employees = employeeDao.search("");
                for (Employee e : employees) {
                    employeeBox.addItem(e);
                }
            } catch (SQLException ex) {
                // Handle error
            }
        }

        private void onSave() {
            Employee selectedEmployee = (Employee) employeeBox.getSelectedItem();
            if (selectedEmployee == null) {
                JOptionPane.showMessageDialog(this, "Please select an employee", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double salaryVal = ((Number) salarySpinner.getValue()).doubleValue();
            double bonusVal = ((Number) bonusSpinner.getValue()).doubleValue();
            double deductionsVal = ((Number) deductionsSpinner.getValue()).doubleValue();
            
            Salary s = new Salary();
            s.setEmployeeId(selectedEmployee.getId());
            s.setSalaryAmount(BigDecimal.valueOf(salaryVal));
            s.setBonus(BigDecimal.valueOf(bonusVal));
            s.setDeductions(BigDecimal.valueOf(deductionsVal));
            s.setPaymentDate(LocalDate.now());
            this.result = s;
            dispose();
        }

        public Salary getResult() { return result; }
    }
}
