package com.smartitems.ui;

import com.smartitems.dao.ProductDao;
import com.smartitems.model.Product;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProductsPanel extends JPanel {
    private final JTextField searchField = new JTextField();
    private final JButton searchButton = new JButton("Search");
    private final JButton addButton = new JButton("Add Product");
    private final JComboBox<String> categoryFilter = new JComboBox<>();
    private final JComboBox<String> brandFilter = new JComboBox<>();
    private final JPanel productsContainer = new JPanel();
    private final ProductDao productDao = new ProductDao();

    public ProductsPanel() {
        setLayout(new BorderLayout());
        
        // Apply modern styling
        StyleUtils.applyModernStyle(addButton, StyleUtils.SUCCESS_COLOR);
        StyleUtils.applyModernStyle(searchButton, StyleUtils.INFO_COLOR);
        StyleUtils.applyTextFieldStyle(searchField);
        StyleUtils.applyComboBoxStyle(categoryFilter);
        StyleUtils.applyComboBoxStyle(brandFilter);
        StyleUtils.applyPanelStyle(this);
        
        // Setup products container with box layout for better scrolling
        productsContainer.setLayout(new BoxLayout(productsContainer, BoxLayout.Y_AXIS));
        productsContainer.setBackground(StyleUtils.LIGHT_BG);
        
        // Create a scroll pane with proper scrolling behavior
        JScrollPane scrollPane = new JScrollPane(productsContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBlockIncrement(100);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        add(buildToolbar(), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        try { 
            productDao.createTableIfNotExists(); 
            setupFilters();
        } catch (SQLException ignored) {}

        searchButton.addActionListener(e -> reload());
        addButton.addActionListener(e -> onAdd());
        categoryFilter.addActionListener(e -> reload());
        brandFilter.addActionListener(e -> reload());

        reload();
    }

    private void setupFilters() {
        // Add category options
        categoryFilter.addItem("All Categories");
        categoryFilter.addItem("Smartphone");
        categoryFilter.addItem("Tablet");
        categoryFilter.addItem("Laptop");
        categoryFilter.addItem("Desktop");
        categoryFilter.addItem("Hardware Components");
        categoryFilter.addItem("Smartwatch");
        categoryFilter.addItem("Accessories");
        categoryFilter.addItem("Gaming Console");
        categoryFilter.addItem("Wearable Technology");
        categoryFilter.addItem("Networking Equipment");
        categoryFilter.addItem("Home Appliances");
        
        // Add brand options
        brandFilter.addItem("All Brands");
        brandFilter.addItem("Apple");
        brandFilter.addItem("Samsung");
        brandFilter.addItem("Redmi");
        brandFilter.addItem("Realme");
        brandFilter.addItem("Huawei");
        brandFilter.addItem("Xiaomi");
        brandFilter.addItem("Lenovo");
        brandFilter.addItem("ASUS");
        brandFilter.addItem("Dell");
        brandFilter.addItem("Sony");
        brandFilter.addItem("Microsoft");
        brandFilter.addItem("Motorola");
        brandFilter.addItem("LG");
        brandFilter.addItem("OnePlus");
        brandFilter.addItem("NVIDIA");
        brandFilter.addItem("Intel");
        brandFilter.addItem("TP-Link");
        brandFilter.addItem("Netgear");
        brandFilter.addItem("Meta");
        brandFilter.addItem("Generic");
    }

    private JComponent buildToolbar() {
        JPanel p = new JPanel(new BorderLayout(6,6));
        
        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(6,6));
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(categoryFilter);
        filterPanel.add(new JLabel("Brand:"));
        filterPanel.add(brandFilter);
        
        // Left panel with search and filters
        JPanel leftPanel = new JPanel(new BorderLayout(6,6));
        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(filterPanel, BorderLayout.CENTER);
        
        // Right panel with add button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(addButton);
        
        p.add(leftPanel, BorderLayout.CENTER);
        p.add(rightPanel, BorderLayout.EAST);
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        return p;
    }

    private void reload() {
        productsContainer.removeAll();
        
        String searchQuery = searchField.getText().trim();
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        String selectedBrand = (String) brandFilter.getSelectedItem();
        
        try {
            List<Product> allProducts = productDao.search(searchQuery);
            List<Product> filteredProducts = allProducts.stream()
                .filter(p -> "All Categories".equals(selectedCategory) || selectedCategory.equals(p.getCategory()))
                .filter(p -> "All Brands".equals(selectedBrand) || selectedBrand.equals(p.getBrand()))
                .toList();
            
            // Create rows of products (3 per row)
            for (int i = 0; i < filteredProducts.size(); i += 3) {
                JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
                rowPanel.setBackground(StyleUtils.LIGHT_BG);
                rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                // Add products for this row
                for (int j = 0; j < 3 && (i + j) < filteredProducts.size(); j++) {
                    rowPanel.add(createProductCard(filteredProducts.get(i + j)));
                }
                
                // Add the row to the container
                productsContainer.add(rowPanel);
                
                // Add some spacing between rows
                if (i + 3 < filteredProducts.size()) {
                    productsContainer.add(Box.createVerticalStrut(16));
                }
            }
            
            // Add flexible space at the bottom for better scrolling
            productsContainer.add(Box.createVerticalGlue());
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        productsContainer.revalidate();
        productsContainer.repaint();
    }

    private JPanel createProductCard(Product product) {
        JPanel card = StyleUtils.createCardPanel(null, StyleUtils.PRIMARY_COLOR);
        card.setPreferredSize(new Dimension(300, 400));
        
        // Product image
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(280, 200));
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
            try {
                java.io.File file = new java.io.File(product.getImagePath());
                if (file.exists()) {
                    java.awt.Image image = javax.imageio.ImageIO.read(file);
                    if (image != null) {
                        java.awt.Image scaledImage = image.getScaledInstance(280, 200, java.awt.Image.SCALE_SMOOTH);
                        imageLabel.setIcon(new ImageIcon(scaledImage));
                    }
                }
            } catch (Exception e) {
                imageLabel.setText("Image not found");
            }
        } else {
            imageLabel.setText("No Image");
        }
        
        // Product info
        JLabel nameLabel = StyleUtils.createStyledLabel(product.getName(), StyleUtils.DARK_COLOR, 16);
        JLabel brandLabel = StyleUtils.createStyledLabel(product.getBrand(), StyleUtils.INFO_COLOR, 12);
        JLabel categoryLabel = StyleUtils.createStyledLabel(product.getCategory(), StyleUtils.WARNING_COLOR, 12);
        JLabel priceLabel = StyleUtils.createStyledLabel("$" + product.getPrice().toString(), StyleUtils.SUCCESS_COLOR, 18);
        JLabel stockLabel = StyleUtils.createStyledLabel("Stock: " + product.getStockQuantity(), 
            product.getStockQuantity() > 10 ? StyleUtils.SUCCESS_COLOR : StyleUtils.DANGER_COLOR, 12);
        
        // Description (truncated)
        String description = product.getDescription();
        if (description != null && description.length() > 60) {
            description = description.substring(0, 57) + "...";
        }
        JLabel descLabel = new JLabel(description != null ? description : "");
        descLabel.setFont(descLabel.getFont().deriveFont(Font.PLAIN, 11f));
        descLabel.setForeground(new Color(100, 100, 100));
        
        // Buttons
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        StyleUtils.applyModernStyle(editButton, StyleUtils.PRIMARY_COLOR);
        StyleUtils.applyModernStyle(deleteButton, StyleUtils.DANGER_COLOR);
        
        editButton.addActionListener(e -> onEdit(product));
        deleteButton.addActionListener(e -> onDelete(product));
        
        // Layout
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(nameLabel);
        infoPanel.add(brandLabel);
        infoPanel.add(categoryLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(stockLabel);
        infoPanel.add(descLabel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        card.add(imageLabel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        return card;
    }

    private void onAdd() {
        ProductFormDialog dlg = new ProductFormDialog(SwingUtilities.getWindowAncestor(this));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        if (dlg.getResult() != null) {
            try {
                productDao.insert(dlg.getResult());
                reload();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEdit(Product product) {
        ProductFormDialog dlg = new ProductFormDialog(SwingUtilities.getWindowAncestor(this), product);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        if (dlg.getResult() != null) {
            try {
                Product updated = dlg.getResult();
                updated.setId(product.getId());
                productDao.update(updated);
                reload();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDelete(Product product) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Delete product '" + product.getName() + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            productDao.delete(product.getId());
            reload();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Enhanced form dialog with image support
    private static class ProductFormDialog extends JDialog {
        private final JTextField nameField = new JTextField();
        private final JComboBox<String> categoryCombo = new JComboBox<>();
        private final JComboBox<String> brandCombo = new JComboBox<>();
        private final JSpinner priceSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 1_000_000.0, 1.0));
        private final JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1_000_000, 1));
        private final JTextArea descArea = new JTextArea(4, 20);
        private final JTextField imagePathField = new JTextField();
        private final JButton browseButton = new JButton("Browse");
        private final JLabel imagePreviewLabel = new JLabel("No image selected");
        private Product result;

        public ProductFormDialog(Window owner) { this(owner, null); }

        public ProductFormDialog(Window owner, Product initial) {
            super(owner, "Product", ModalityType.APPLICATION_MODAL);
            setSize(600, 600);
            setLayout(new BorderLayout());
            
            setupComboBoxes();
            
            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6,6,6,6);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            
            int row = 0;
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Name:"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(nameField, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Category:"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(categoryCombo, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Brand:"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(brandCombo, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Price:"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(priceSpinner, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Stock:"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(stockSpinner, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Image Path:"), gbc);
            JPanel imagePanel = new JPanel(new BorderLayout(5, 0));
            imagePanel.add(imagePathField, BorderLayout.CENTER);
            imagePanel.add(browseButton, BorderLayout.EAST);
            gbc.gridx=1; gbc.gridy=row++; form.add(imagePanel, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Image Preview:"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(imagePreviewLabel, gbc);
            
            gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Description:"), gbc);
            gbc.gridx=1; gbc.gridy=row++; form.add(new JScrollPane(descArea), gbc);
            
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancel = new JButton("Cancel");
            JButton save = new JButton("Save");
            actions.add(cancel); actions.add(save);
            
            add(form, BorderLayout.CENTER);
            add(actions, BorderLayout.SOUTH);
            
            cancel.addActionListener(e -> dispose());
            save.addActionListener(e -> onSave());
            browseButton.addActionListener(e -> browseImage());
            
            // Set up image preview
            imagePathField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void changedUpdate(javax.swing.event.DocumentEvent e) { updateImagePreview(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { updateImagePreview(); }
                public void insertUpdate(javax.swing.event.DocumentEvent e) { updateImagePreview(); }
            });

            if (initial != null) {
                nameField.setText(initial.getName());
                categoryCombo.setSelectedItem(initial.getCategory());
                brandCombo.setSelectedItem(initial.getBrand());
                priceSpinner.setValue(initial.getPrice().doubleValue());
                stockSpinner.setValue(initial.getStockQuantity());
                descArea.setText(initial.getDescription());
                imagePathField.setText(initial.getImagePath());
                updateImagePreview();
            }
        }

        private void setupComboBoxes() {
            // Add category options
            categoryCombo.addItem("Smartphone");
            categoryCombo.addItem("Tablet");
            categoryCombo.addItem("Laptop");
            categoryCombo.addItem("Desktop");
            categoryCombo.addItem("Hardware Components");
            categoryCombo.addItem("Smartwatch");
            categoryCombo.addItem("Accessories");
            categoryCombo.addItem("Gaming Console");
            categoryCombo.addItem("Wearable Technology");
            categoryCombo.addItem("Networking Equipment");
            categoryCombo.addItem("Home Appliances");
            
            // Add brand options
            brandCombo.addItem("Apple");
            brandCombo.addItem("Samsung");
            brandCombo.addItem("Redmi");
            brandCombo.addItem("Realme");
            brandCombo.addItem("Huawei");
            brandCombo.addItem("Xiaomi");
            brandCombo.addItem("Lenovo");
            brandCombo.addItem("ASUS");
            brandCombo.addItem("Dell");
            brandCombo.addItem("Sony");
            brandCombo.addItem("Microsoft");
            brandCombo.addItem("Motorola");
            brandCombo.addItem("LG");
            brandCombo.addItem("OnePlus");
            brandCombo.addItem("NVIDIA");
            brandCombo.addItem("Intel");
            brandCombo.addItem("TP-Link");
            brandCombo.addItem("Netgear");
            brandCombo.addItem("Meta");
            brandCombo.addItem("Generic");
            
            // Apply styling
            StyleUtils.applyComboBoxStyle(categoryCombo);
            StyleUtils.applyComboBoxStyle(brandCombo);
        }
        
        private void onSave() {
            String name = nameField.getText().trim();
            String category = (String) categoryCombo.getSelectedItem();
            String brand = (String) brandCombo.getSelectedItem();
            double priceVal = ((Number) priceSpinner.getValue()).doubleValue();
            int stock = (Integer) stockSpinner.getValue();
            String desc = descArea.getText();
            String imagePath = imagePathField.getText().trim();
            
            if (name.isEmpty() || category == null || brand == null) {
                JOptionPane.showMessageDialog(this, "Name, Category, and Brand are required", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Product p = new Product();
            p.setName(name);
            p.setCategory(category);
            p.setBrand(brand);
            p.setPrice(BigDecimal.valueOf(priceVal));
            p.setStockQuantity(stock);
            p.setDescription(desc);
            p.setImagePath(imagePath);
            this.result = p;
            dispose();
        }
        
        private void browseImage() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image files", "jpg", "jpeg", "png", "gif", "bmp"));
            
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String selectedFile = fileChooser.getSelectedFile().getAbsolutePath();
                imagePathField.setText(selectedFile);
                updateImagePreview();
            }
        }
        
        private void updateImagePreview() {
            String imagePath = imagePathField.getText().trim();
            if (imagePath.isEmpty()) {
                imagePreviewLabel.setText("No image selected");
                imagePreviewLabel.setIcon(null);
                return;
            }
            
            try {
                java.io.File file = new java.io.File(imagePath);
                if (file.exists()) {
                    java.awt.Image image = javax.imageio.ImageIO.read(file);
                    if (image != null) {
                        // Scale image to fit preview
                        int maxSize = 100;
                        int width = image.getWidth(null);
                        int height = image.getHeight(null);
                        
                        if (width > height) {
                            height = (int) ((double) height * maxSize / width);
                            width = maxSize;
                        } else {
                            width = (int) ((double) width * maxSize / height);
                            height = maxSize;
                        }
                        
                        java.awt.Image scaledImage = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
                        imagePreviewLabel.setIcon(new ImageIcon(scaledImage));
                        imagePreviewLabel.setText("");
                    } else {
                        imagePreviewLabel.setText("Invalid image file");
                        imagePreviewLabel.setIcon(null);
                    }
                } else {
                    imagePreviewLabel.setText("File not found");
                    imagePreviewLabel.setIcon(null);
                }
            } catch (Exception e) {
                imagePreviewLabel.setText("Error loading image");
                imagePreviewLabel.setIcon(null);
            }
        }

        public Product getResult() { return result; }
    }
}



