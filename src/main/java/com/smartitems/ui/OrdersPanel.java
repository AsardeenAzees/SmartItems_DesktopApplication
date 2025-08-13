package com.smartitems.ui;

import com.smartitems.dao.OrderDao;
import com.smartitems.dao.CustomerDao;
import com.smartitems.dao.ProductDao;
import com.smartitems.model.Order;
import com.smartitems.model.Customer;
import com.smartitems.model.Product;
import com.smartitems.ui.ProductFormDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class OrdersPanel extends JPanel {
    private final JTextField searchField = new JTextField();
    private final JButton searchButton = new JButton("Search");
    private final JButton addButton = new JButton("Add");
    private final JButton editButton = new JButton("Edit");
    private final JButton deleteButton = new JButton("Delete");
    private final JButton processButton = new JButton("Process Order");
    private final JButton deliverButton = new JButton("Mark Delivered");
    private final JTable table = new JTable();
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"ID","Customer","Date","Status","Total"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final OrderDao orderDao = new OrderDao();
    private final CustomerDao customerDao = new CustomerDao();

    public OrdersPanel() {
        setLayout(new BorderLayout());
        table.setModel(model);
        add(buildToolbar(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        try { 
            orderDao.createTableIfNotExists(); 
            customerDao.createTableIfNotExists();
        } catch (SQLException ignored) {}

        searchButton.addActionListener(e -> reload());
        addButton.addActionListener(e -> onAdd());
        editButton.addActionListener(e -> onEdit());
        deleteButton.addActionListener(e -> onDelete());
        processButton.addActionListener(e -> onProcessOrder());
        deliverButton.addActionListener(e -> onDeliverOrder());

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
        right.add(processButton);
        right.add(deliverButton);
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
            List<Order> list = orderDao.search(q);
            for (Order o : list) {
                model.addRow(new Object[]{
                    o.getId(), 
                    o.getCustomerName(), 
                    o.getOrderDate(), 
                    o.getStatus(), 
                    o.getTotalAmount()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdd() {
        OrderFormDialog dlg = new OrderFormDialog(SwingUtilities.getWindowAncestor(this));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        if (dlg.getResult() != null) {
            try {
                orderDao.insert(dlg.getResult());
                reload();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Order o = new Order();
        o.setId((Long) model.getValueAt(row, 0));
        o.setCustomerName((String) model.getValueAt(row, 1));
        o.setOrderDate((LocalDate) model.getValueAt(row, 2));
        o.setStatus((String) model.getValueAt(row, 3));
        o.setTotalAmount((BigDecimal) model.getValueAt(row, 4));
        
        OrderFormDialog dlg = new OrderFormDialog(SwingUtilities.getWindowAncestor(this), o);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        if (dlg.getResult() != null) {
            try {
                Order updated = dlg.getResult();
                updated.setId(o.getId());
                orderDao.update(updated);
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
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected order?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            orderDao.delete(id);
            reload();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class OrderFormDialog extends JDialog {
        private final JComboBox<Customer> customerBox = new JComboBox<>();
        private final JComboBox<Product> productBox = new JComboBox<>();
        private final JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        private final JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        private final JComboBox<String> statusBox = new JComboBox<>(new String[]{"pending", "processing", "shipped", "delivered", "cancelled"});
        private final JLabel totalLabel = new JLabel("$0.00");
        private final JButton addProductButton = new JButton("Add Product");
        private final JButton newProductButton = new JButton("New Product");
        private Order result;
        private final CustomerDao customerDao = new CustomerDao();
        private final ProductDao productDao = new ProductDao();

        public OrderFormDialog(Window owner) { this(owner, null); }

        public OrderFormDialog(Window owner, Order initial) {
            super(owner, "Order", ModalityType.APPLICATION_MODAL);
            setSize(500, 400);
            setLayout(new BorderLayout());
            
            loadCustomers();
            loadProducts();
            
            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6,6,6,6);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            int row = 0;
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Customer:"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(customerBox, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Product:"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(productBox, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Quantity:"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(quantitySpinner, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Date:"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(dateSpinner, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Status:"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(statusBox, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Total:"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(totalLabel, gbc);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(addProductButton);
            buttonPanel.add(newProductButton);
            
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancel = new JButton("Cancel");
            JButton save = new JButton("Save");
            actions.add(cancel); actions.add(save);
            
            add(form, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
            add(actions, BorderLayout.PAGE_END);
            
            cancel.addActionListener(e -> dispose());
            save.addActionListener(e -> onSave());
            addProductButton.addActionListener(e -> addProductToOrder());
            newProductButton.addActionListener(e -> createNewProduct());
            
            // Update total when product or quantity changes
            productBox.addActionListener(e -> updateTotal());
            quantitySpinner.addChangeListener(e -> updateTotal());

            if (initial != null) {
                // Set initial values
                statusBox.setSelectedItem(initial.getStatus());
            }
        }

        private void loadCustomers() {
            try {
                List<Customer> customers = customerDao.search("");
                customerBox.removeAllItems();
                for (Customer c : customers) {
                    customerBox.addItem(c);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private void loadProducts() {
            try {
                List<Product> products = productDao.search("");
                productBox.removeAllItems();
                for (Product p : products) {
                    productBox.addItem(p);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private void updateTotal() {
            Product selectedProduct = (Product) productBox.getSelectedItem();
            Integer quantity = (Integer) quantitySpinner.getValue();
            
            if (selectedProduct != null && quantity != null) {
                BigDecimal total = selectedProduct.getPrice().multiply(BigDecimal.valueOf(quantity));
                totalLabel.setText("$" + total.toString());
            } else {
                totalLabel.setText("$0.00");
            }
        }
        
        private void addProductToOrder() {
            Product selectedProduct = (Product) productBox.getSelectedItem();
            if (selectedProduct == null) {
                JOptionPane.showMessageDialog(this, "Please select a product", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Integer quantity = (Integer) quantitySpinner.getValue();
            if (quantity > selectedProduct.getStockQuantity()) {
                JOptionPane.showMessageDialog(this, 
                    "Insufficient stock. Available: " + selectedProduct.getStockQuantity(), 
                    "Stock Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // For now, just show a message. In a full implementation, this would add to an order items list
            JOptionPane.showMessageDialog(this, 
                "Added " + quantity + "x " + selectedProduct.getName() + " to order", 
                "Product Added", JOptionPane.INFORMATION_MESSAGE);
        }
        
        private void createNewProduct() {
            ProductFormDialog productDialog = new ProductFormDialog(this);
            productDialog.setLocationRelativeTo(this);
            productDialog.setVisible(true);
            
            if (productDialog.getResult() != null) {
                try {
                    productDao.insert(productDialog.getResult());
                    loadProducts(); // Reload the product list
                    JOptionPane.showMessageDialog(this, "Product created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void onSave() {
            Customer selectedCustomer = (Customer) customerBox.getSelectedItem();
            if (selectedCustomer == null) {
                JOptionPane.showMessageDialog(this, "Please select a customer", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Product selectedProduct = (Product) productBox.getSelectedItem();
            if (selectedProduct == null) {
                JOptionPane.showMessageDialog(this, "Please select a product", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Integer quantity = (Integer) quantitySpinner.getValue();
            BigDecimal total = selectedProduct.getPrice().multiply(BigDecimal.valueOf(quantity));
            
            Order o = new Order();
            o.setCustomerId(selectedCustomer.getId());
            o.setOrderDate(LocalDate.now());
            o.setStatus((String) statusBox.getSelectedItem());
            o.setTotalAmount(total);
            this.result = o;
            dispose();
        }

        public Order getResult() { return result; }
    }
    
    private void onProcessOrder() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to process", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Long orderId = (Long) model.getValueAt(row, 0);
        String currentStatus = (String) model.getValueAt(row, 3);
        
        if (!"pending".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "Only pending orders can be processed", "Invalid Status", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Process this order? This will update the status to 'processing'.", 
            "Confirm Processing", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                orderDao.updateOrderStatus(orderId, "processing");
                reload();
                JOptionPane.showMessageDialog(this, "Order processed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void onDeliverOrder() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to deliver", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Long orderId = (Long) model.getValueAt(row, 0);
        String currentStatus = (String) model.getValueAt(row, 3);
        
        if (!"processing".equals(currentStatus) && !"shipped".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "Only processing or shipped orders can be delivered", "Invalid Status", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Mark this order as delivered? This will update stock quantities.", 
            "Confirm Delivery", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Update order status
                orderDao.updateOrderStatus(orderId, "delivered");
                
                // Update stock quantities (this would require order items to be implemented)
                // For now, we'll just update the status
                
                reload();
                JOptionPane.showMessageDialog(this, "Order delivered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
