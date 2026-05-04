package ui;

import data.DataManager;
import models.*;
import java.io.File;
import java.nio.file.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StockManagerDashboard extends JFrame {
    
    private User currentUser;
    private DataManager dataManager;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JTable pendingOrdersTable;
    private JTable inventoryTable;
    private JTable qcTable;
    private DefaultTableModel pendingOrdersModel;
    private DefaultTableModel inventoryModel;
    private DefaultTableModel qcModel;
    private JTextArea detailsArea;
    private JButton[] menuButtons;
    private int selectedMenuIndex = 0;

    public StockManagerDashboard(User user) {
        this.currentUser = user;
        this.dataManager = DataManager.getInstance();
        initializeUI();
        refreshData();
    }

    private void initializeUI() {
        setTitle("Stock Manager Portal - " + currentUser.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1100, 700));

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(UIStyles.BACKGROUND_COLOR);

        mainPanel.add(createHeader(), BorderLayout.NORTH);

        JPanel contentWrapper = new JPanel(new BorderLayout(0, 0));
        contentWrapper.setBackground(UIStyles.BACKGROUND_COLOR);
        contentWrapper.add(createSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIStyles.BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        contentPanel.add(createPendingOrdersPanel(), "PENDING_ORDERS");
        contentPanel.add(createInventoryPanel(), "INVENTORY");
        contentPanel.add(createQualityCheckPanel(), "QUALITY_CHECK");
        contentPanel.add(createAddProductPanel(), "ADD_PRODUCT");

        contentWrapper.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(contentWrapper, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyles.PRIMARY_COLOR);
        header.setPreferredSize(new Dimension(0, 70));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        leftPanel.setOpaque(false);
        
        ImageIcon brandIcon = new ImageIcon("resources/logo_stock.png");
        Image img = brandIcon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(img));
        leftPanel.add(iconLabel);
        
        JLabel titleLabel = new JLabel("Stock Manager Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        leftPanel.add(titleLabel);
        
        header.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(0, 0, 0, 20));
        
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getName() + "  ");
        userLabel.setFont(UIStyles.FONT_BODY);
        userLabel.setForeground(Color.WHITE);
        rightPanel.add(userLabel);
        
        rightPanel.add(Box.createHorizontalStrut(15));
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(UIStyles.FONT_BUTTON);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(UIStyles.DANGER_COLOR); 
        logoutBtn.setFocusPainted(false);
        logoutBtn.setOpaque(true);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        logoutBtn.addActionListener(e -> logout());
        rightPanel.add(logoutBtn);
        
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIStyles.SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        Object[][] menuItems = {
            {"Pending Orders", "PENDING_ORDERS", "PENDING"},
            {"Inventory", "INVENTORY", "INVENTORY"},
            {"Quality Check", "QUALITY_CHECK", "🛡️"},
            {"Add Product", "ADD_PRODUCT", "ADD"}
        };

        menuButtons = new JButton[menuItems.length];
        
        for (int i = 0; i < menuItems.length; i++) {
            final int index = i;
            String text = (String) menuItems[i][0];
            String action = (String) menuItems[i][1];
            String symbol = (String) menuItems[i][2];
            
            menuButtons[i] = createMenuButton(text, action, symbol, index);
            menuButtons[i].setToolTipText("Open " + text + " panel");
            sidebar.add(menuButtons[i]);
            sidebar.add(Box.createVerticalStrut(5));
        }
        
        updateMenuSelection(0);
        sidebar.add(Box.createVerticalGlue());
        
        JButton refreshBtn = createMenuButton("Refresh", "REFRESH", "🔄", -1);
        refreshBtn.setToolTipText("Sync data with server");
        sidebar.add(refreshBtn);

        sidebar.add(Box.createVerticalStrut(15));
        
        JPanel statusFooter = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        statusFooter.setOpaque(false);
        statusFooter.setMaximumSize(new Dimension(220, 30));
        
        JLabel dot = new JLabel("●");
        dot.setForeground(UIStyles.SUCCESS_COLOR);
        dot.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel statusText = new JLabel("System: Active | v1.0.5");
        statusText.setFont(UIStyles.FONT_SMALL);
        statusText.setForeground(UIStyles.TEXT_SECONDARY);
        
        statusFooter.add(dot);
        statusFooter.add(statusText);
        sidebar.add(statusFooter);

        return sidebar;
    }

    private JButton createMenuButton(String text, String action, String symbol, int index) {
        JButton btn = new JButton(text);
        btn.setIcon(UIStyles.createIcon(symbol, new Color(0,0,0,0), 24));
        btn.setIconTextGap(15);
        btn.setFont(UIStyles.FONT_MENU);
        btn.setForeground(UIStyles.TEXT_SECONDARY);
        btn.setBackground(UIStyles.SIDEBAR_COLOR);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setPreferredSize(new Dimension(220, 45));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        final Color normalBg = UIStyles.SIDEBAR_COLOR;
        final Color hoverBg = UIStyles.BACKGROUND_COLOR;
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (selectedMenuIndex != index) btn.setBackground(hoverBg);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (selectedMenuIndex != index) btn.setBackground(normalBg);
            }
        });
        
        btn.addActionListener(e -> {
            if (action.equals("REFRESH")) {
                refreshData();
                UIStyles.showMessage(this, "Data refreshed!", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                if (index >= 0) {
                    updateMenuSelection(index);
                    refreshData();
                }
                cardLayout.show(contentPanel, action);
            }
        });
        
        return btn;
    }

    private void updateMenuSelection(int index) {
        selectedMenuIndex = index;
        for (int i = 0; i < menuButtons.length; i++) {
            if (i == index) {
                menuButtons[i].setBackground(UIStyles.PRIMARY_LIGHT);
                menuButtons[i].setForeground(UIStyles.PRIMARY_COLOR);
                menuButtons[i].setFont(UIStyles.FONT_SUBHEADER);
            } else {
                menuButtons[i].setBackground(UIStyles.SIDEBAR_COLOR);
                menuButtons[i].setForeground(UIStyles.TEXT_SECONDARY);
                menuButtons[i].setFont(UIStyles.FONT_MENU);
            }
        }
    }

    private JPanel createPendingOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(UIStyles.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        ImageIcon icon = UIStyles.createIcon("PENDING", UIStyles.PURPLE_COLOR, 40);
        panel.add(UIStyles.createSectionHeader("Pending Processing", "Orders matching availability to be packed", icon), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setDividerSize(5);
        splitPane.setBackground(UIStyles.BACKGROUND_COLOR);
        splitPane.setBorder(null);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        
        String[] columns = {"Order ID", "Customer", "Items", "Total (Rs.)", "Status"};
        pendingOrdersModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        pendingOrdersTable = new JTable(pendingOrdersModel);
        UIStyles.styleTable(pendingOrdersTable);
        
        leftPanel.add(UIStyles.createScrollPane(pendingOrdersTable), BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel);

        JPanel rightPanel = UIStyles.createSimpleCardPanel();
        rightPanel.setLayout(new BorderLayout(0, 15));
        
        JLabel detailsTitle = UIStyles.createLabel("Order Details", UIStyles.FONT_SUBHEADER, UIStyles.TEXT_PRIMARY);
        rightPanel.add(detailsTitle, BorderLayout.NORTH);
        
        detailsArea = UIStyles.createTextArea(15, 30);
        detailsArea.setEditable(false);
        detailsArea.setText("Select an order to view details...");
        rightPanel.add(UIStyles.createScrollPane(detailsArea), BorderLayout.CENTER);
        
        pendingOrdersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showOrderDetails();
        });
        
        splitPane.setRightComponent(rightPanel);
        panel.add(splitPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        actionPanel.setBackground(UIStyles.BACKGROUND_COLOR);

        JButton startPreparingBtn = UIStyles.createButton("Start Preparing", UIStyles.PRIMARY_COLOR);
        startPreparingBtn.addActionListener(e -> updateOrderStatus(Order.STATUS_PREPARING));
        actionPanel.add(startPreparingBtn);

        JButton markPackedBtn = UIStyles.createButton("Mark as Packed", UIStyles.SUCCESS_COLOR);
        markPackedBtn.addActionListener(e -> packOrder());
        actionPanel.add(markPackedBtn);

        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showOrderDetails() {
        int row = pendingOrdersTable.getSelectedRow();
        if (row >= 0) {
            String orderId = (String) pendingOrdersModel.getValueAt(row, 0);
            Order order = dataManager.getOrderById(orderId);
            if (order != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("ORDER ID: ").append(order.getOrderId()).append("\n");
                sb.append("--------------------------------\n");
                for (String itemId : order.getItems().keySet()) {
                    Product p = dataManager.getProductById(itemId);
                    String pName = p != null ? p.getName() : itemId;
                    sb.append(pName).append(": x").append(order.getItems().get(itemId)).append("\n");
                }
                detailsArea.setText(sb.toString());
            }
        }
    }

    private void updateOrderStatus(String newStatus) {
        int row = pendingOrdersTable.getSelectedRow();
        if (row < 0) return;
        String orderId = (String) pendingOrdersModel.getValueAt(row, 0);
        dataManager.updateOrderStatus(orderId, newStatus);
        refreshData();
    }

    private void packOrder() {
        int row = pendingOrdersTable.getSelectedRow();
        if (row < 0) return;
        String orderId = (String) pendingOrdersModel.getValueAt(row, 0);
        String status = (String) pendingOrdersModel.getValueAt(row, 4);
        
        if (!status.equals(Order.STATUS_PREPARING)) {
            UIStyles.showMessage(this, "Order must be packing first", "Info", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Order o = dataManager.getOrderById(orderId);
        dataManager.deductStockForOrder(o);
        dataManager.updateOrderStatus(orderId, Order.STATUS_PACKED);
        refreshData();
    }

    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(UIStyles.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        ImageIcon icon = UIStyles.createIcon("INVENTORY", UIStyles.PURPLE_COLOR, 40);
        panel.add(UIStyles.createSectionHeader("Inventory Management", "Manage product stock levels", icon), BorderLayout.NORTH);

        String[] columns = {"Img", "Product ID", "Name", "Stock", "Price (Rs.)", "Threshold", "Status"};
        inventoryModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return ImageIcon.class;
                return String.class;
            }
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        inventoryTable = new JTable(inventoryModel);
        UIStyles.styleTable(inventoryTable);
        inventoryTable.setRowHeight(50);
        inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        inventoryTable.getColumnModel().getColumn(0).setMaxWidth(50);
        panel.add(UIStyles.createScrollPane(inventoryTable), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        actionPanel.setBackground(UIStyles.BACKGROUND_COLOR);

        JButton editBtn = UIStyles.createButton("Edit Details (Image)", UIStyles.TEAL_COLOR);
        editBtn.addActionListener(e -> {
            int row = inventoryTable.getSelectedRow();
            if (row < 0) {
                UIStyles.showMessage(this, "Please select a product to edit", "Info", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String id = (String) inventoryModel.getValueAt(row, 1);
            showEditProductDialog(id);
        });
        actionPanel.add(editBtn);
        
        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void showEditProductDialog(String productId) {
        Product p = dataManager.getProductById(productId);
        if (p == null) return;

        JDialog dialog = new JDialog(this, "Edit Product: " + p.getName(), true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        formPanel.setOpaque(false);
        formPanel.add(new JLabel("Product: " + p.getName() + " (" + productId + ")"));
        
        JLabel imgLabel = new JLabel("Current: " + (p.getImagePath().equals("none") ? "No Image" : "Image Set"));
        JButton changeImgBtn = UIStyles.createButton("Change Image", UIStyles.TEAL_COLOR);
        final String[] newImgPath = {p.getImagePath()};
        
        changeImgBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                try {
                    File f = fc.getSelectedFile();
                    String newName = "img_" + System.currentTimeMillis() + "_" + f.getName();
                    Path dest = Paths.get("images", newName);
                    if (!Files.exists(Paths.get("images"))) Files.createDirectory(Paths.get("images"));
                    Files.copy(f.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
                    newImgPath[0] = dest.toString();
                    imgLabel.setText("Selected: " + f.getName());
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });

        formPanel.add(imgLabel);
        formPanel.add(changeImgBtn);

        JButton saveBtn = UIStyles.createButton("Save Changes", UIStyles.SUCCESS_COLOR);
        saveBtn.addActionListener(e -> {
            p.setImagePath(newImgPath[0]);
            dataManager.addProduct(p);
            UIStyles.showMessage(this, "Image updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
            dialog.dispose();
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(saveBtn, BorderLayout.SOUTH);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private JPanel createQualityCheckPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(UIStyles.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        ImageIcon icon = UIStyles.createIcon("CHECK", UIStyles.PRIMARY_COLOR, 40);
        panel.add(UIStyles.createSectionHeader("Quality Control", "Inspect supplied items before adding to stock", icon), BorderLayout.NORTH);

        String[] columns = {"Enquiry ID", "Product", "Quantity", "Date Supplied", "Cost (Rs.)"};
        qcModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        qcTable = new JTable(qcModel);
        UIStyles.styleTable(qcTable);
        panel.add(UIStyles.createScrollPane(qcTable), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        actionPanel.setBackground(UIStyles.BACKGROUND_COLOR);

        JButton approveBtn = UIStyles.createButton("Approve QC", UIStyles.SUCCESS_COLOR);
        approveBtn.addActionListener(e -> processQualityCheck(SupplierEnquiry.STATUS_APPROVED));
        actionPanel.add(approveBtn);

        JButton returnBtn = UIStyles.createButton("Return to Supplier", UIStyles.DANGER_COLOR);
        returnBtn.addActionListener(e -> processQualityCheck(SupplierEnquiry.STATUS_RETURNED));
        actionPanel.add(returnBtn);

        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void processQualityCheck(String status) {
        int row = qcTable.getSelectedRow();
        if (row < 0) {
            UIStyles.showMessage(this, "Please select an item to inspect", "Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) qcModel.getValueAt(row, 0);
        String msg = status.equals(SupplierEnquiry.STATUS_APPROVED) ? 
            "Approve quality and add to inventory?" : "Reject quality and return to supplier?";
        
        int confirm = UIStyles.showConfirm(this, msg, "Quality Inspection");
        if (confirm == JOptionPane.YES_OPTION) {
            dataManager.updateEnquiryStatus(id, status);
            UIStyles.showMessage(this, "Item mark as " + status, "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        }
    }

    private JPanel createAddProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(UIStyles.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        ImageIcon icon = UIStyles.createIcon("ADD", UIStyles.SUCCESS_COLOR, 40);
        panel.add(UIStyles.createSectionHeader("Add New Product", "Introduce a new item to inventory", icon), BorderLayout.NORTH);

        JPanel formCard = UIStyles.createSimpleCardPanel();
        formCard.setLayout(new GridLayout(7, 2, 10, 20));
        
        formCard.add(UIStyles.createLabel("Product Name:", UIStyles.FONT_BODY, UIStyles.TEXT_PRIMARY));
        JTextField nameField = UIStyles.createTextField();
        formCard.add(nameField);

        formCard.add(UIStyles.createLabel("Quantity:", UIStyles.FONT_BODY, UIStyles.TEXT_PRIMARY));
        JSpinner qtySp = UIStyles.createSpinner(0, 1000, 50);
        formCard.add(qtySp);

        formCard.add(UIStyles.createLabel("Price:", UIStyles.FONT_BODY, UIStyles.TEXT_PRIMARY));
        JTextField priceField = UIStyles.createTextField();
        formCard.add(priceField);
        
        formCard.add(UIStyles.createLabel("Threshold:", UIStyles.FONT_BODY, UIStyles.TEXT_PRIMARY));
        JSpinner thSp = UIStyles.createSpinner(0, 100, 10);
        formCard.add(thSp);

        formCard.add(UIStyles.createLabel("Image:", UIStyles.FONT_BODY, UIStyles.TEXT_PRIMARY));
        JPanel imgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        imgPanel.setOpaque(false);
        
        JButton imgBtn = UIStyles.createButton("Choose...", UIStyles.TEAL_COLOR);
        imgBtn.setPreferredSize(new Dimension(100, 30));
        JLabel imgPathLabel = new JLabel("None");
        final String[] selectedPath = {"none"};
        
        imgBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try {
                    String newName = "img_" + System.currentTimeMillis() + "_" + f.getName();
                    Path dest = Paths.get("images", newName);
                    if (!Files.exists(Paths.get("images"))) Files.createDirectory(Paths.get("images"));
                    Files.copy(f.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
                    selectedPath[0] = dest.toString();
                    imgPathLabel.setText(f.getName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    UIStyles.showMessage(panel, "Error saving image", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        imgPanel.add(imgBtn);
        imgPanel.add(Box.createHorizontalStrut(10));
        imgPanel.add(imgPathLabel);
        formCard.add(imgPanel);

        JButton addBtn = UIStyles.createButton("Add Product", UIStyles.SUCCESS_COLOR);
        addBtn.addActionListener(e -> {
             try {
                 Product p = new Product(dataManager.generateProductId(), nameField.getText(), 
                    (Integer)qtySp.getValue(), 
                    Double.parseDouble(priceField.getText()), (Integer)thSp.getValue(), selectedPath[0]);
                 dataManager.addProduct(p);
                 refreshData();
                 UIStyles.showMessage(this, "Added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                 nameField.setText("");
                 priceField.setText("");
                 imgPathLabel.setText("None");
                 selectedPath[0] = "none";
             } catch (Exception ex) {
                 UIStyles.showMessage(this, "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
             }
        });
        formCard.add(addBtn);

        JPanel center = new JPanel(new FlowLayout());
        center.setOpaque(false);
        center.add(formCard);
        panel.add(center, BorderLayout.CENTER);

        return panel;
    }

    private void refreshData() {
        pendingOrdersModel.setRowCount(0);
        for (Order o : dataManager.getPendingOrdersForStockManager()) {
            pendingOrdersModel.addRow(new Object[]{o.getOrderId(), o.getCustomerId(), o.getItems().size(), o.getTotalAmount(), o.getStatus()});
        }
        
        inventoryModel.setRowCount(0);
        for (Product p : dataManager.getAllProducts()) {
            ImageIcon img = null;
            if (!p.getImagePath().equals("none") && new File(p.getImagePath()).exists()) {
                 img = new ImageIcon(new ImageIcon(p.getImagePath()).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
            }
            inventoryModel.addRow(new Object[]{img, p.getProductId(), p.getName(), p.getQuantity(), p.getPrice(), p.getThreshold(), p.isLowStock() ? "LOW" : "OK"});
        }

        if (qcModel != null) {
            qcModel.setRowCount(0);
            for (SupplierEnquiry e : dataManager.getEnquiriesForQualityCheck()) {
                Product p = dataManager.getProductById(e.getProductId());
                String pName = p != null ? p.getName() : e.getProductId();
                qcModel.addRow(new Object[]{
                    e.getEnquiryId(), pName, e.getRequestedQuantity(), 
                    e.getCreatedDate(), String.format("%,.2f", e.getEstimatedCost())
                });
            }
        }
    }

    private void logout() {
        int confirm = UIStyles.showConfirm(this, "Are you sure you want to logout?", "Confirm Logout");
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
