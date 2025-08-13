package com.smartitems.ui;

import com.smartitems.model.Product;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Reusable dialog for creating/editing a Product.
 * Returns the created/edited Product via getResult().
 */
public class ProductFormDialog extends JDialog {
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
		JPanel imagePanel = new JPanel(new BorderLayout(5,0));
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
		actions.add(cancel);
		actions.add(save);

		add(form, BorderLayout.CENTER);
		add(actions, BorderLayout.SOUTH);

		cancel.addActionListener(e -> dispose());
		save.addActionListener(e -> onSave());
		browseButton.addActionListener(e -> browseImage());

		imagePathField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			public void changedUpdate(javax.swing.event.DocumentEvent e) { updateImagePreview(); }
			public void removeUpdate(javax.swing.event.DocumentEvent e) { updateImagePreview(); }
			public void insertUpdate(javax.swing.event.DocumentEvent e) { updateImagePreview(); }
		});

		if (initial != null) {
			nameField.setText(initial.getName());
			categoryCombo.setSelectedItem(initial.getCategory());
			brandCombo.setSelectedItem(initial.getBrand());
			if (initial.getPrice() != null) priceSpinner.setValue(initial.getPrice().doubleValue());
			stockSpinner.setValue(initial.getStockQuantity());
			descArea.setText(initial.getDescription());
			imagePathField.setText(initial.getImagePath());
			updateImagePreview();
		}
	}

	private void setupComboBoxes() {
		// Categories
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

		// Brands
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
					int width = 280;
					int height = 200;
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





