package com.smartitems.ui;

import com.smartitems.dao.RepairDao;
import com.smartitems.dao.CustomerDao;
import com.smartitems.dao.ProductDao;
import com.smartitems.dao.EmployeeDao;
import com.smartitems.model.Repair;
import com.smartitems.model.Customer;
import com.smartitems.model.Product;
import com.smartitems.model.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class RepairsPanel extends JPanel {
    private final JTextField searchField = new JTextField();
    private final JButton searchButton = new JButton("Search");
    private final JButton addButton = new JButton("Add");
    private final JButton editButton = new JButton("Edit");
    private final JButton deleteButton = new JButton("Delete");
    private final JTable table = new JTable();
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"ID","Customer","Product","Issue","Status","Assigned To"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final RepairDao repairDao = new RepairDao();
    private final CustomerDao customerDao = new CustomerDao();
    private final ProductDao productDao = new ProductDao();
    private final EmployeeDao employeeDao = new EmployeeDao();

    public RepairsPanel() {
        setLayout(new BorderLayout());
        table.setModel(model);
        add(buildToolbar(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        try { 
            repairDao.createTableIfNotExists(); 
            customerDao.createTableIfNotExists();
            productDao.createTableIfNotExists();
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
            List<Repair> list = repairDao.search(q);
            for (Repair r : list) {
                model.addRow(new Object[]{
                    r.getId(), 
                    r.getCustomerName(), 
                    r.getProductName() != null ? r.getProductName() : "N/A", 
                    r.getIssue(), 
                    r.getStatus(), 
                    r.getAssignedToName() != null ? r.getAssignedToName() : "Unassigned"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdd() {
        RepairFormDialog dlg = new RepairFormDialog(SwingUtilities.getWindowAncestor(this));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        if (dlg.getResult() != null) {
            try {
                repairDao.insert(dlg.getResult());
                reload();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        // For edit, we'd need to fetch the full repair object from the database
        // This is a simplified version
        JOptionPane.showMessageDialog(this, "Edit functionality requires fetching full repair object", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Long id = (Long) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected repair?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            repairDao.delete(id);
            reload();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class RepairFormDialog extends JDialog {
        private final JComboBox<Customer> customerBox = new JComboBox<>();
        private final JComboBox<Product> productBox = new JComboBox<>();
        private final JTextArea issueArea = new JTextArea(4, 20);
        private final JComboBox<String> statusBox = new JComboBox<>(new String[]{"pending", "in progress", "completed", "cancelled"});
        private final JComboBox<Employee> assignedToBox = new JComboBox<>();
        private Repair result;
        private final CustomerDao customerDao = new CustomerDao();
        private final ProductDao productDao = new ProductDao();
        private final EmployeeDao employeeDao = new EmployeeDao();

        public RepairFormDialog(Window owner) {
            super(owner, "Repair", ModalityType.APPLICATION_MODAL);
            setSize(520, 400);
            setLayout(new BorderLayout());
            
            loadData();
            
            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6,6,6,6);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            int row = 0;
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Customer"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(customerBox, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Product (Optional)"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(productBox, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Issue"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(new JScrollPane(issueArea), gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Status"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(statusBox, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Assigned To (Optional)"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(assignedToBox, gbc);
            
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancel = new JButton("Cancel");
            JButton save = new JButton("Save");
            actions.add(cancel); actions.add(save);
            add(form, BorderLayout.CENTER);
            add(actions, BorderLayout.SOUTH);
            cancel.addActionListener(e -> dispose());
            save.addActionListener(e -> onSave());
        }

        private void loadData() {
            try {
                List<Customer> customers = customerDao.search("");
                for (Customer c : customers) {
                    customerBox.addItem(c);
                }
                
                List<Product> products = productDao.search("");
                for (Product p : products) {
                    productBox.addItem(p);
                }
                
                List<Employee> employees = employeeDao.search("");
                for (Employee e : employees) {
                    assignedToBox.addItem(e);
                }
            } catch (SQLException ex) {
                // Handle error
            }
        }

        private void onSave() {
            Customer selectedCustomer = (Customer) customerBox.getSelectedItem();
            if (selectedCustomer == null) {
                JOptionPane.showMessageDialog(this, "Please select a customer", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String issue = issueArea.getText().trim();
            if (issue.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Issue description is required", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Repair r = new Repair();
            r.setCustomerId(selectedCustomer.getId());
            
            Product selectedProduct = (Product) productBox.getSelectedItem();
            if (selectedProduct != null) {
                r.setProductId(selectedProduct.getId());
            }
            
            r.setIssue(issue);
            r.setStatus((String) statusBox.getSelectedItem());
            
            Employee selectedEmployee = (Employee) assignedToBox.getSelectedItem();
            if (selectedEmployee != null) {
                r.setAssignedTo(selectedEmployee.getId());
            }
            
            this.result = r;
            dispose();
        }

        public Repair getResult() { return result; }
    }
}



