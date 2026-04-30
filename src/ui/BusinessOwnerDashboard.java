package ui;

import data.DataManager;
import models.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BusinessOwnerDashboard extends JFrame {
    private User currentUser;
    private DataManager dataManager;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JTable ordersTable;
    private JTable lowStockTable;
    private JTable enquiriesTable;
    private JTable paymentsTable;
    private JTable customersTable;
    private JTable manageProductsTable;
    private DefaultTableModel ordersTableModel;
    private DefaultTableModel lowStockTableModel;
    private DefaultTableModel enquiriesTableModel;
    private DefaultTableModel paymentsTableModel;
    private DefaultTableModel customersTableModel;
    private DefaultTableModel manageProductsTableModel;
    private JLabel alertLabel;
    private JButton[] menuButtons;
    private int selectedMenuIndex = 0;
    private JComboBox<String> customerCombo;
    private DefaultTableModel newOrderProductModel;
    private JLabel newOrderTotalLabel;

    private JLabel totalRevenueLabel;
    private JLabel pendingOrdersLabel;
    private JLabel lowStockAlertsLabel;

    public BusinessOwnerDashboard(User user) {
        this.currentUser = user;
        this.dataManager = DataManager.getInstance();
        initializeUI();
        refreshData();
    }

    private void initializeUI() {
        setTitle("Business Owner Dashboard - " + currentUser.getName());
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
        
        contentPanel.add(createOrdersPanel(), "ORDERS");
        contentPanel.add(createNewOrderPanel(), "NEW_ORDER");
        contentPanel.add(createLowStockPanel(), "LOW_STOCK");
        contentPanel.add(createEnquiriesPanel(), "ENQUIRIES");
        contentPanel.add(createPaymentsPanel(), "PAYMENTS");
        contentPanel.add(createCustomersPanel(), "CUSTOMERS");
        contentPanel.add(createManageProductsPanel(), "MANAGE_PRODUCTS");

        JPanel mainContentWrapper = new JPanel(new BorderLayout(0, 0));
        mainContentWrapper.setBackground(UIStyles.BACKGROUND_COLOR);
        mainContentWrapper.add(createOverviewPanel(), BorderLayout.NORTH);
        mainContentWrapper.add(contentPanel, BorderLayout.CENTER);

        contentWrapper.add(mainContentWrapper, BorderLayout.CENTER);
        mainPanel.add(contentWrapper, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyles.PRIMARY_COLOR);
        header.setPreferredSize(new Dimension(0, 70));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        leftPanel.setOpaque(false);
        
        ImageIcon brandIcon = new ImageIcon("resources/logo_business.png");
        Image img = brandIcon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(img));
        leftPanel.add(iconLabel);
        
        JLabel titleLabel = new JLabel("Business Owner Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        leftPanel.add(titleLabel);
        
        header.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(0, 0, 0, 20));
        
        alertLabel = new JLabel();
        alertLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        alertLabel.setForeground(UIStyles.WARNING_COLOR);
        rightPanel.add(alertLabel);
        
        rightPanel.add(Box.createHorizontalStrut(15));
        
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
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
            {"All Orders", "ORDERS", "ORDERS"},
            {"New Order", "NEW_ORDER", "NEW_ORDER"},
            {"Low Stock", "LOW_STOCK", "LOW_STOCK"},
            {"Enquiries", "ENQUIRIES", "ENQUIRIES"},
            {"Payments", "PAYMENTS", "PAYMENTS"},
            {"Customers", "CUSTOMERS", "CUSTOMERS"},
            {"Manage Products", "MANAGE_PRODUCTS", "INVENTORY"}
        };

        menuButtons = new JButton[menuItems.length];
        
        for (int i = 0; i < menuItems.length; i++) {
            final int index = i;
            String text = (String) menuItems[i][0];
            String action = (String) menuItems[i][1];
            String symbol = (String) menuItems[i][2];
            
            menuButtons[i] = createMenuButton(text, action, symbol, index);
            sidebar.add(menuButtons[i]);
            sidebar.add(Box.createVerticalStrut(5));
        }
        
        updateMenuSelection(0);
        sidebar.add(Box.createVerticalGlue());
        
        JButton refreshBtn = createMenuButton("Refresh", "REFRESH", "REFRESH", -1);
        sidebar.add(refreshBtn);

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
                if (selectedMenuIndex != index) {
                    btn.setBackground(hoverBg);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (selectedMenuIndex != index) {
                    btn.setBackground(normalBg);
                }
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

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(UIStyles.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 0, 25));

        totalRevenueLabel = new JLabel("Rs. 0.00");
        panel.add(createStatCard("Total Revenue (Paid)", totalRevenueLabel, UIStyles.SUCCESS_COLOR));

        pendingOrdersLabel = new JLabel("0");
        panel.add(createStatCard("Pending Orders", pendingOrdersLabel, UIStyles.WARNING_COLOR));

        lowStockAlertsLabel = new JLabel("0");
        panel.add(createStatCard("Low Stock Alerts", lowStockAlertsLabel, UIStyles.DANGER_COLOR));

        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = UIStyles.createSimpleCardPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(3, 0, 0, 0, accentColor),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyles.FONT_BODY);
        titleLabel.setForeground(UIStyles.TEXT_SECONDARY);
        card.add(titleLabel, BorderLayout.NORTH);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(UIStyles.TEXT_PRIMARY);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(UIStyles.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        ImageIcon icon = UIStyles.createIcon("ORDERS", UIStyles.PRIMARY_COLOR, 40);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(UIStyles.createSectionHeader("All Orders", "Manage and view all customer orders", icon), BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);
        
        JButton exportBtn = UIStyles.createButton("Export", UIStyles.TEAL_COLOR);
        exportBtn.setPreferredSize(new Dimension(100, 35));
        exportBtn.addActionListener(e -> exportTableToCSV(ordersTable, "All_Orders.csv"));
        searchPanel.add(exportBtn);

        JTextField searchField = UIStyles.createTextField();
        searchField.setPreferredSize(new Dimension(200, 35));
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        topPanel.add(searchPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Order ID", "Customer", "Items", "Total (Rs.)", "Status", "Date"};
        ordersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        ordersTable = new JTable(ordersTableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    if (column == 4) { // Status column
                        try {
                            String status = (String) getValueAt(row, column);
                            if ("PENDING".equalsIgnoreCase(status) || "PREPARING".equalsIgnoreCase(status)) {
                                c.setForeground(new Color(255, 140, 0)); // Orange
                            } else if ("DELIVERED".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)) {
                                c.setForeground(new Color(0, 150, 0)); // Green
                            } else if ("CANCELLED".equalsIgnoreCase(status)) {
                                c.setForeground(Color.RED);
                            } else {
                                c.setForeground(UIStyles.TEXT_PRIMARY);
                            }
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } catch (Exception e) {
                            c.setForeground(UIStyles.TEXT_PRIMARY);
                        }
                    } else {
                        c.setForeground(UIStyles.TEXT_PRIMARY);
                    }
                }
                return c;
            }
        };
        UIStyles.styleTable(ordersTable);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(ordersTableModel);
        ordersTable.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
        
        ordersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = ordersTable.getSelectedRow();
                    if (row >= 0) {
                        String orderId = (String) ordersTableModel.getValueAt(row, 0);
                        Order order = dataManager.getOrderById(orderId);
                        if (order != null) {
                            showOrderDetailsDialog(order);
                        }
                    }
                }
            }
        });
        
        JScrollPane scrollPane = UIStyles.createScrollPane(ordersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createNewOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(UIStyles.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        ImageIcon icon = UIStyles.createIcon("NEW_ORDER", UIStyles.SUCCESS_COLOR, 40);
        panel.add(UIStyles.createSectionHeader("Create New Order", "Process a new customer order (Backorders enabled)", icon), BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(20, 0));
        mainContent.setBackground(UIStyles.BACKGROUND_COLOR);

        JPanel formCard = UIStyles.createSimpleCardPanel();
        formCard.setLayout(new BorderLayout(0, 15));
        formCard.setPreferredSize(new Dimension(320, 0));

        JLabel formTitle = UIStyles.createLabel("Order Details", UIStyles.FONT_SUBHEADER, UIStyles.TEXT_PRIMARY);
        formCard.add(formTitle, BorderLayout.NORTH);

        JPanel formFields = new JPanel();
        formFields.setBackground(Color.WHITE);
        formFields.setLayout(new BoxLayout(formFields, BoxLayout.Y_AXIS));

        JLabel custLabel = UIStyles.createLabel("Select Customer", UIStyles.FONT_BODY, UIStyles.TEXT_SECONDARY);
        custLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formFields.add(custLabel);
        formFields.add(Box.createVerticalStrut(8));
        
        customerCombo = UIStyles.createComboBox();
        customerCombo.setMaximumSize(new Dimension(290, 40));
        customerCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formFields.add(customerCombo);
        formFields.add(Box.createVerticalStrut(25));

        JLabel totalTitle = UIStyles.createLabel("Order Total", UIStyles.FONT_BODY, UIStyles.TEXT_SECONDARY);
        totalTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formFields.add(totalTitle);
        formFields.add(Box.createVerticalStrut(5));
        
        newOrderTotalLabel = new JLabel("Rs. 0.00");
        newOrderTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        newOrderTotalLabel.setForeground(UIStyles.PRIMARY_COLOR);
        newOrderTotalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formFields.add(newOrderTotalLabel);
        formFields.add(Box.createVerticalStrut(20));

        formCard.add(formFields, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1, 1));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton submitBtn = UIStyles.createButton("Place Order", UIStyles.SUCCESS_COLOR);
        btnPanel.add(submitBtn);
        formCard.add(btnPanel, BorderLayout.SOUTH);

        mainContent.add(formCard, BorderLayout.WEST);

        JPanel productsCard = UIStyles.createSimpleCardPanel();
        productsCard.setLayout(new BorderLayout(0, 15));

        JLabel prodTitle = UIStyles.createLabel("Select Products", UIStyles.FONT_SUBHEADER, UIStyles.TEXT_PRIMARY);
        productsCard.add(prodTitle, BorderLayout.NORTH);

        String[] prodColumns = {"Select", "Image", "Product ID", "Product Name", "Price (Rs.)", "Quantity"};
        newOrderProductModel = new DefaultTableModel(prodColumns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Boolean.class;
                if (column == 1) return ImageIcon.class;
                if (column == 5) return Integer.class;
                if (column == 4) return Double.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 5;
            }
        };
        
        JTable productTable = new JTable(newOrderProductModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    try {
                        Boolean selected = (Boolean) getValueAt(row, 0);
                        if (selected != null && selected) {
                            String productId = (String) getValueAt(row, 2);
                            int qty = (Integer) getValueAt(row, 5);
                            Product p = dataManager.getProductById(productId);
                            if (p != null && qty > p.getQuantity()) {
                                c.setBackground(new Color(255, 235, 238)); // Light Red (Backorder Warning)
                            } else {
                                c.setBackground(new Color(238, 242, 255)); // Light Blue (Selected)
                            }
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                    } catch (Exception e) {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        };
        UIStyles.styleTable(productTable);
        productTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        productTable.getColumnModel().getColumn(0).setMaxWidth(60);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        productTable.getColumnModel().getColumn(1).setMaxWidth(60);
        productTable.setRowHeight(50);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(250);
        productTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        productTable.getColumnModel().getColumn(5).setMaxWidth(100);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        productTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        productTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        ((DefaultTableCellRenderer)productTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        
        JScrollPane productScroll = UIStyles.createScrollPane(productTable);
        productsCard.add(productScroll, BorderLayout.CENTER);

        newOrderProductModel.addTableModelListener(e -> {
            double total = 0;
            for (int i = 0; i < newOrderProductModel.getRowCount(); i++) {
                Boolean selected = (Boolean) newOrderProductModel.getValueAt(i, 0);
                if (selected != null && selected) {
                    double price = (Double) newOrderProductModel.getValueAt(i, 4);
                    int qty = (Integer) newOrderProductModel.getValueAt(i, 5);
                    total += price * qty;
                }
            }
            newOrderTotalLabel.setText("Rs. " + String.format("%,.2f", total));
        });

        mainContent.add(productsCard, BorderLayout.CENTER);

        submitBtn.addActionListener(e -> {
            if (customerCombo.getSelectedItem() == null) {
                UIStyles.showMessage(this, "Please select a customer", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String customerId = customerCombo.getSelectedItem().toString().split(" - ")[0];
            Order order = new Order(dataManager.generateOrderId(), customerId);
            double total = 0;
            boolean needsBackorder = false;
            boolean hasItems = false;

            for (int i = 0; i < newOrderProductModel.getRowCount(); i++) {
                Boolean selected = (Boolean) newOrderProductModel.getValueAt(i, 0);
                if (selected != null && selected) {
                    String productId = (String) newOrderProductModel.getValueAt(i, 2);
                    int qty = (Integer) newOrderProductModel.getValueAt(i, 5);
                    
                    if (qty > 999) {
                        UIStyles.showMessage(this, "Quantity cannot exceed 3 digits (Max: 999)", "Limit Reached", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (qty <= 0) {
                        UIStyles.showMessage(this, "Quantity must be at least 1", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double price = (Double) newOrderProductModel.getValueAt(i, 4);
                    
                    Product p = dataManager.getProductById(productId);
                    if (p != null && qty > p.getQuantity()) {
                        needsBackorder = true;
                    }

                    order.addItem(productId, qty);
                    total += price * qty;
                    hasItems = true;
                }
            }

            if (!hasItems) {
                UIStyles.showMessage(this, "Please select at least one product", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int deliveryDays = needsBackorder ? 7 : 2;
            String estDate = LocalDate.now().plusDays(deliveryDays).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            order.setEstimatedDeliveryDate(estDate);

            int confirmOrder = UIStyles.showConfirm(this, 
                "Place order for " + customerCombo.getSelectedItem().toString() + "?\nTotal Amount: Rs. " + 
                String.format("%,.2f", total), "Confirm Order");
            
            if (confirmOrder != JOptionPane.YES_OPTION) return;

            order.setTotalAmount(total);
            dataManager.addOrder(order);
            
            Payment payment = new Payment(dataManager.generatePaymentId(), order.getOrderId(), 
                                         Payment.TYPE_CUSTOMER, total);
            dataManager.addPayment(payment);

            UIStyles.showMessage(this, "Order " + order.getOrderId() + " created successfully!\nTotal: Rs. " + 
                                String.format("%,.2f", total), "Success", JOptionPane.INFORMATION_MESSAGE);
            
            for (int i = 0; i < newOrderProductModel.getRowCount(); i++) {
                newOrderProductModel.setValueAt(false, i, 0);
                newOrderProductModel.setValueAt(1, i, 5);
            }
            newOrderTotalLabel.setText("Rs. 0.00");
            refreshData();
        });

        panel.add(mainContent, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createLowStockPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(UIStyles.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        ImageIcon icon = UIStyles.createIcon("LOW_STOCK", UIStyles.WARNING_COLOR, 40);
        panel.add(UIStyles.createSectionHeader("Procurement Planning", "Stock needed to fulfill orders + buffer", icon), BorderLayout.NORTH);

        String[] columns = {"Product ID", "Product Name", "Physical Stock", "Committed", "Shortage", "To Order"};
        lowStockTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        lowStockTable = new JTable(lowStockTableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    try {
                        Object val = getValueAt(row, 4); // Shortage column
                        int shortage = (val instanceof Integer) ? (Integer) val : Integer.parseInt(val.toString());
                        
                        if (shortage > 50) {
                            c.setBackground(new Color(255, 235, 238)); // Light Red (Critical)
                        } else if (shortage > 0) {
                            c.setBackground(new Color(255, 248, 225)); // Light Yellow (Warning)
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                    } catch (Exception e) {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        };
        UIStyles.styleTable(lowStockTable);
        
        JScrollPane scrollPane = UIStyles.createScrollPane(lowStockTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        actionPanel.setBackground(UIStyles.BACKGROUND_COLOR);
        
        JButton sendEnquiryBtn = UIStyles.createButton("Send Enquiry", UIStyles.PRIMARY_COLOR);
        sendEnquiryBtn.setPreferredSize(new Dimension(160, 40));
        sendEnquiryBtn.addActionListener(e -> {
            int row = lowStockTable.getSelectedRow();
            if (row < 0) {
                UIStyles.showMessage(this, "Please select a product first", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String productId = (String) lowStockTableModel.getValueAt(row, 0);
            String productName = (String) lowStockTableModel.getValueAt(row, 1);
            
            String quantityStr = JOptionPane.showInputDialog(this, 
                "Enter quantity to order for:\n" + productName, "Send Enquiry", JOptionPane.QUESTION_MESSAGE);
            
            if (quantityStr != null && !quantityStr.isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityStr);
                    SupplierEnquiry enquiry = new SupplierEnquiry(
                        dataManager.generateEnquiryId(), productId, quantity
                    );
                    dataManager.addEnquiry(enquiry);
                    UIStyles.showMessage(this, "Enquiry sent to supplier successfully!", 
                                       "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } catch (NumberFormatException ex) {
                    UIStyles.showMessage(this, "Please enter a valid number", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        actionPanel.add(sendEnquiryBtn);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    private JPanel createEnquiriesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(UIStyles.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        ImageIcon icon = UIStyles.createIcon("ENQUIRIES", UIStyles.TEAL_COLOR, 40);
        panel.add(UIStyles.createSectionHeader("Supplier Enquiries", "Track status of stock requests", icon), BorderLayout.NORTH);

        String[] columns = {"Enquiry ID", "Product", "Quantity", "Status", "Date", "Est. Cost"};
        enquiriesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        enquiriesTable = new JTable(enquiriesTableModel);
        UIStyles.styleTable(enquiriesTable);
        
        JScrollPane scrollPane = UIStyles.createScrollPane(enquiriesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel createPaymentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(UIStyles.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        ImageIcon icon = UIStyles.createIcon("PAYMENTS", UIStyles.BLUE_COLOR, 40);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(UIStyles.createSectionHeader("Payment Records", "Monitor incoming and outgoing payments", icon), BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);
        JTextField searchField = UIStyles.createTextField();
        searchField.setPreferredSize(new Dimension(200, 35));
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        topPanel.add(searchPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Payment ID", "Reference", "Type", "Amount (Rs.)", "Status", "Created", "Paid Date"};
        paymentsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        paymentsTable = new JTable(paymentsTableModel);
        UIStyles.styleTable(paymentsTable);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(paymentsTableModel);
        paymentsTable.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
        
        JScrollPane scrollPane = UIStyles.createScrollPane(paymentsTable);
        
        paymentsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = paymentsTable.getSelectedRow();
                    if (row >= 0) {
                        String paymentId = (String) paymentsTableModel.getValueAt(row, 0);
                        String status = (String) paymentsTableModel.getValueAt(row, 4);
                        if (status.equals("PENDING")) {
                            int confirm = UIStyles.showConfirm(BusinessOwnerDashboard.this, "Mark payment " + paymentId + " as paid?", "Confirm");
                            if (confirm == JOptionPane.YES_OPTION) {
                                dataManager.updatePaymentStatus(paymentId, Payment.STATUS_PAID);
                                UIStyles.showMessage(BusinessOwnerDashboard.this, "Payment marked as paid!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                refreshData();
                            }
                        }
                    }
                }
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        actionPanel.setBackground(UIStyles.BACKGROUND_COLOR);
        
        JButton markPaidBtn = UIStyles.createButton("Mark as Paid", UIStyles.SUCCESS_COLOR);
        markPaidBtn.addActionListener(e -> {
            int row = paymentsTable.getSelectedRow();
            if (row < 0) {
                UIStyles.showMessage(this, "Please select a payment first", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String paymentId = (String) paymentsTableModel.getValueAt(row, 0);
            String status = (String) paymentsTableModel.getValueAt(row, 4);
            
            if (status.equals("PAID")) {
                UIStyles.showMessage(this, "This payment is already marked as paid", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            int confirm = UIStyles.showConfirm(this, "Mark payment " + paymentId + " as paid?", "Confirm");
            if (confirm == JOptionPane.YES_OPTION) {
                dataManager.updatePaymentStatus(paymentId, Payment.STATUS_PAID);
                UIStyles.showMessage(this, "Payment marked as paid!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            }
        });
        actionPanel.add(markPaidBtn);
        
        JButton cancelBtn = UIStyles.createButton("Cancel", UIStyles.DANGER_COLOR);
        cancelBtn.addActionListener(e -> {
            int row = paymentsTable.getSelectedRow();
            if (row < 0) {
                UIStyles.showMessage(this, "Please select a payment first", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String paymentId = (String) paymentsTableModel.getValueAt(row, 0);
            String status = (String) paymentsTableModel.getValueAt(row, 4);
            
            if (!status.equals("PENDING")) {
                UIStyles.showMessage(this, "Only pending payments can be cancelled", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            int confirm = UIStyles.showConfirm(this, "Cancel payment " + paymentId + "?", "Confirm");
            if (confirm == JOptionPane.YES_OPTION) {
                dataManager.updatePaymentStatus(paymentId, Payment.STATUS_CANCELLED);
                refreshData();
            }
        });
        actionPanel.add(cancelBtn);
        
        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel createCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(UIStyles.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        ImageIcon icon = UIStyles.createIcon("CUSTOMERS", UIStyles.PURPLE_COLOR, 40);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(UIStyles.createSectionHeader("Customer Management", "View and manage verified customers", icon), BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);
        JTextField searchField = UIStyles.createTextField();
        searchField.setPreferredSize(new Dimension(200, 35));
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        topPanel.add(searchPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Img", "ID", "Shop Name", "Owner", "Phone", "Address"};
        customersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return ImageIcon.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        customersTable = new JTable(customersTableModel);
        UIStyles.styleTable(customersTable);
        customersTable.setRowHeight(50);
        customersTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        customersTable.getColumnModel().getColumn(0).setMaxWidth(50);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(customersTableModel);
        customersTable.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
        
        JScrollPane scrollPane = UIStyles.createScrollPane(customersTable);
        
        customersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = customersTable.getSelectedRow();
                    if (row >= 0) {
                        String id = (String) customersTableModel.getValueAt(row, 1);
                        Customer c = dataManager.getCustomerById(id);
                        if (c != null) showEditCustomerDialog(c);
                    }
                }
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel southContainer = new JPanel();
        southContainer.setLayout(new BoxLayout(southContainer, BoxLayout.Y_AXIS));
        southContainer.setOpaque(false);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JButton editBtn = UIStyles.createButton("Edit Info", UIStyles.TEAL_COLOR);
        editBtn.setPreferredSize(new Dimension(120, 35));
        editBtn.addActionListener(e -> {
            int row = customersTable.getSelectedRow();
            if (row < 0) {
                UIStyles.showMessage(this, "Please select a customer to edit", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String id = (String) customersTableModel.getValueAt(row, 1);
            Customer c = dataManager.getCustomerById(id);
            if (c != null) showEditCustomerDialog(c);
        });

        JButton deleteBtn = UIStyles.createButton("Remove", UIStyles.DANGER_COLOR);
        deleteBtn.setPreferredSize(new Dimension(120, 35));
        deleteBtn.addActionListener(e -> {
            int row = customersTable.getSelectedRow();
            if (row < 0) {
                UIStyles.showMessage(this, "Please select a customer to remove", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String id = (String) customersTableModel.getValueAt(row, 1);
            String name = (String) customersTableModel.getValueAt(row, 2);
            int confirm = UIStyles.showConfirm(this, "Are you sure you want to remove '" + name + "'?", "Remove Customer");
            if (confirm == JOptionPane.YES_OPTION) {
                dataManager.removeCustomer(id);
                refreshData();
                UIStyles.showMessage(this, "Customer removed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        southContainer.add(actionPanel);

        JPanel formPanel = UIStyles.createSimpleCardPanel();
        formPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        formPanel.setPreferredSize(new Dimension(0, 90));

        formPanel.add(new JLabel("Shop:"));
        JTextField shopField = UIStyles.createTextField();
        shopField.setPreferredSize(new Dimension(150, 35));
        formPanel.add(shopField);

        formPanel.add(new JLabel("Owner:"));
        JTextField ownerField = UIStyles.createTextField();
        ownerField.setPreferredSize(new Dimension(120, 35));
        formPanel.add(ownerField);
        
        formPanel.add(new JLabel("Phone:"));
        JTextField phoneField = UIStyles.createTextField();
        phoneField.setPreferredSize(new Dimension(120, 35));
        formPanel.add(phoneField);
        
        formPanel.add(new JLabel("Address:"));
        JTextField addressField = UIStyles.createTextField();
        addressField.setPreferredSize(new Dimension(200, 35));
        formPanel.add(addressField);

        JButton imgBtn = UIStyles.createButton("Img...", UIStyles.TEAL_COLOR);
        imgBtn.setPreferredSize(new Dimension(80, 35));
        final String[] selectedPath = {"none"};
        imgBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                try {
                    File f = fc.getSelectedFile();
                    String newName = "cust_" + System.currentTimeMillis() + "_" + f.getName();
                    Path dest = Paths.get("images", newName);
                    if (!Files.exists(Paths.get("images"))) Files.createDirectory(Paths.get("images"));
                    Files.copy(f.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
                    selectedPath[0] = dest.toString();
                    imgBtn.setText("Set!");
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });
        formPanel.add(imgBtn);

        JButton addBtn = UIStyles.createButton("Add New", UIStyles.SUCCESS_COLOR);
        addBtn.setPreferredSize(new Dimension(120, 35));
        addBtn.addActionListener(e -> {
            String shop = shopField.getText().trim();
            String owner = ownerField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();

            if (shop.isEmpty() || owner.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                UIStyles.showMessage(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!validateCustomerInput(owner, phone)) {
                return;
            }
            
            Customer customer = new Customer(
                dataManager.generateCustomerId(),
                capitalizeWords(shop),
                capitalizeWords(owner),
                phone,
                address,
                selectedPath[0]
            );
            dataManager.addCustomer(customer);
            refreshData();
            
            shopField.setText("");
            ownerField.setText("");
            phoneField.setText("");
            addressField.setText("");
            imgBtn.setText("Img...");
            selectedPath[0] = "none";
            
            UIStyles.showMessage(this, "Customer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        formPanel.add(addBtn);
        southContainer.add(formPanel);

        panel.add(southContainer, BorderLayout.SOUTH);
        return panel;
    }

    private void showEditCustomerDialog(Customer c) {
        JDialog d = new JDialog(this, "Edit Customer Info", true);
        d.setSize(450, 400);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());

        JPanel p = new JPanel(new GridLayout(5, 2, 10, 15));
        p.setBorder(new EmptyBorder(25, 25, 25, 25));
        p.setBackground(Color.WHITE);

        JTextField shopField = UIStyles.createTextField();
        shopField.setText(c.getShopName());
        JTextField ownerField = UIStyles.createTextField();
        ownerField.setText(c.getOwnerName());
        JTextField phoneField = UIStyles.createTextField();
        phoneField.setText(c.getPhone());
        JTextField addressField = UIStyles.createTextField();
        addressField.setText(c.getAddress());
        
        JLabel imgLabel = new JLabel("Current: " + (c.getImagePath().equals("none") ? "No Image" : "Image Set"));
        JButton changeImgBtn = UIStyles.createButton("Change Image", UIStyles.TEAL_COLOR);
        final String[] newImgPath = {c.getImagePath()};
        
        changeImgBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(d) == JFileChooser.APPROVE_OPTION) {
                try {
                    File f = fc.getSelectedFile();
                    String newName = "cust_" + System.currentTimeMillis() + "_" + f.getName();
                    Path dest = Paths.get("images", newName);
                    if (!Files.exists(Paths.get("images"))) Files.createDirectory(Paths.get("images"));
                    Files.copy(f.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
                    newImgPath[0] = dest.toString();
                    imgLabel.setText("Selected: " + f.getName());
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });

        p.add(new JLabel("Shop Name:")); p.add(shopField);
        p.add(new JLabel("Owner Name:")); p.add(ownerField);
        p.add(new JLabel("Phone:")); p.add(phoneField);
        p.add(new JLabel("Address:")); p.add(addressField);
        p.add(imgLabel); p.add(changeImgBtn);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(UIStyles.BACKGROUND_COLOR);
        
        JButton saveBtn = UIStyles.createButton("Save Changes", UIStyles.PRIMARY_COLOR);
        saveBtn.addActionListener(data -> {
            String owner = ownerField.getText().trim();
            String phone = phoneField.getText().trim();

            if (!validateCustomerInput(owner, phone)) {
                return;
            }

            c.setShopName(capitalizeWords(shopField.getText()));
            c.setOwnerName(capitalizeWords(owner));
            c.setPhone(phone);
            c.setAddress(addressField.getText());
            c.setImagePath(newImgPath[0]);
            dataManager.updateCustomer(c);
            
            refreshData();
            d.dispose();
            UIStyles.showMessage(this, "Customer updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        
        btnPanel.add(saveBtn);
        d.add(p, BorderLayout.CENTER);
        d.add(btnPanel, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private void refreshData() {
        ordersTableModel.setRowCount(0);
        for (Order o : dataManager.getAllOrders()) {
            Customer c = dataManager.getCustomerById(o.getCustomerId());
            String customerName = c != null ? c.getShopName() : o.getCustomerId();
            ordersTableModel.addRow(new Object[]{
                o.getOrderId(), customerName, o.getItems().size() + " items",
                String.format("%,.2f", o.getTotalAmount()), o.getStatus(), o.getCreatedDate()
            });
        }

        lowStockTableModel.setRowCount(0);
        List<Product> allProducts = dataManager.getAllProducts();
        List<Order> unfulfilledOrders = new ArrayList<>();
        int pendingOrdersCount = 0;
        for (Order o : dataManager.getAllOrders()) {
            if (o.getStatus().equals(Order.STATUS_PENDING) || o.getStatus().equals(Order.STATUS_PREPARING)) {
                unfulfilledOrders.add(o);
                pendingOrdersCount++;
            }
        }

        if (pendingOrdersLabel != null) {
            pendingOrdersLabel.setText(String.valueOf(pendingOrdersCount));
        }

        int alerts = 0;
        for (Product p : allProducts) {
            int physical = p.getQuantity();
            int committed = 0;
            for (Order o : unfulfilledOrders) {
                committed += o.getItems().getOrDefault(p.getProductId(), 0);
            }
            int shortage = Math.max(0, committed - physical);
            int toOrder = Math.max(0, committed + p.getThreshold() - physical);
            if (toOrder > 0 || physical <= p.getThreshold()) {
                lowStockTableModel.addRow(new Object[]{
                    p.getProductId(), p.getName(), physical, committed, shortage, toOrder
                });
                if (toOrder > 0) alerts++;
            }
        }
        
        if (alerts > 0) {
            alertLabel.setText("[!] " + alerts + " items need procurement");
        } else {
            alertLabel.setText("");
        }
        
        if (lowStockAlertsLabel != null) {
            lowStockAlertsLabel.setText(String.valueOf(alerts));
        }

        enquiriesTableModel.setRowCount(0);
        for (SupplierEnquiry e : dataManager.getAllEnquiries()) {
            Product p = dataManager.getProductById(e.getProductId());
            String productName = p != null ? p.getName() : e.getProductId();
            enquiriesTableModel.addRow(new Object[]{
                e.getEnquiryId(), productName, e.getRequestedQuantity(),
                e.getStatus(), e.getCreatedDate(), String.format("%,.2f", e.getEstimatedCost())
            });
        }

        paymentsTableModel.setRowCount(0);
        double totalRevenue = 0;
        for (Payment p : dataManager.getAllPayments()) {
            paymentsTableModel.addRow(new Object[]{
                p.getPaymentId(), p.getReferenceId(), p.getType(),
                String.format("%,.2f", p.getAmount()), p.getStatus(), 
                p.getCreatedDate(), p.getPaidDate()
            });
            if (Payment.STATUS_PAID.equals(p.getStatus())) {
                totalRevenue += p.getAmount();
            }
        }
        if (totalRevenueLabel != null) {
            totalRevenueLabel.setText("Rs. " + String.format("%,.2f", totalRevenue));
        }

        if (customersTableModel != null) {
            customersTableModel.setRowCount(0);
            for (Customer c : dataManager.getAllCustomers()) {
                ImageIcon img = null;
                if (!c.getImagePath().equals("none") && new File(c.getImagePath()).exists()) {
                     img = new ImageIcon(new ImageIcon(c.getImagePath()).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
                }
                customersTableModel.addRow(new Object[]{
                    img, c.getCustomerId(), c.getShopName(), 
                    c.getOwnerName(), c.getPhone(), c.getAddress()
                });
            }
        }

        if (customerCombo != null) {
            Object selected = customerCombo.getSelectedItem();
            customerCombo.removeAllItems();
            for (Customer c : dataManager.getAllCustomers()) {
                customerCombo.addItem(c.getCustomerId() + " - " + c.getShopName());
            }
            if (selected != null) customerCombo.setSelectedItem(selected);
        }

        if (manageProductsTableModel != null) {
            manageProductsTableModel.setRowCount(0);
            for (Product p : dataManager.getAllProducts()) {
                manageProductsTableModel.addRow(new Object[]{
                    p.getProductId(), p.getName(),
                    p.getQuantity(), String.format("%,.2f", p.getPrice()),
                    p.getThreshold()
                });
            }
        }

        if (newOrderProductModel != null) {
            newOrderProductModel.setRowCount(0);
            for (Product p : dataManager.getAllProducts()) {
                ImageIcon img = null;
                if (!p.getImagePath().equals("none") && new File(p.getImagePath()).exists()) {
                     img = new ImageIcon(new ImageIcon(p.getImagePath()).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
                }
                newOrderProductModel.addRow(new Object[]{false, img, p.getProductId(), p.getName(), p.getPrice(), 1});
            }
        }
    }

    private JPanel createManageProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(UIStyles.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        ImageIcon icon = UIStyles.createIcon("INVENTORY", UIStyles.PURPLE_COLOR, 40);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(UIStyles.createSectionHeader("Manage Product Pricing", "Control selling prices and stock thresholds", icon), BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);
        JTextField searchField = UIStyles.createTextField();
        searchField.setPreferredSize(new Dimension(200, 35));
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        topPanel.add(searchPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Product ID", "Name", "Current Stock", "Selling Price", "Threshold"};
        manageProductsTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        manageProductsTable = new JTable(manageProductsTableModel);
        UIStyles.styleTable(manageProductsTable);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(manageProductsTableModel);
        manageProductsTable.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
        
        manageProductsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = manageProductsTable.getSelectedRow();
                    if (row >= 0) {
                        String id = (String) manageProductsTableModel.getValueAt(row, 0);
                        showEditProductDialog(id);
                    }
                }
            }
        });
        
        panel.add(UIStyles.createScrollPane(manageProductsTable), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        actionPanel.setBackground(UIStyles.BACKGROUND_COLOR);

        JButton editBtn = UIStyles.createButton("Edit Pricing/Threshold", UIStyles.PRIMARY_COLOR);
        editBtn.addActionListener(e -> {
            int row = manageProductsTable.getSelectedRow();
            if (row < 0) {
                UIStyles.showMessage(this, "Please select a product to edit", "Info", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String id = (String) manageProductsTableModel.getValueAt(row, 0);
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
        dialog.setSize(400, 550);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel formPanel = new JPanel(new GridLayout(8, 1, 10, 10));
        formPanel.setOpaque(false);
        formPanel.add(new JLabel("Product: " + p.getName() + " (" + productId + ")"));
        
        formPanel.add(new JLabel("Selling Price (Rs.):"));
        JTextField priceField = UIStyles.createTextField();
        priceField.setText(String.valueOf(p.getPrice()));
        formPanel.add(priceField);

        formPanel.add(new JLabel("Stock Alert Threshold:"));
        JTextField thresholdField = UIStyles.createTextField();
        thresholdField.setText(String.valueOf(p.getThreshold()));
        formPanel.add(thresholdField);

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

        JButton saveBtn = UIStyles.createButton("Update Product", UIStyles.SUCCESS_COLOR);
        saveBtn.addActionListener(e -> {
            try {
                double newPrice = Double.parseDouble(priceField.getText());
                int newThreshold = Integer.parseInt(thresholdField.getText());
                p.setPrice(newPrice);
                p.setThreshold(newThreshold);
                p.setImagePath(newImgPath[0]);
                dataManager.addProduct(p);
                UIStyles.showMessage(this, "Product updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
                dialog.dispose();
            } catch (Exception ex) {
                UIStyles.showMessage(this, "Please enter valid numeric values", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(saveBtn, BorderLayout.SOUTH);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showOrderDetailsDialog(Order order) {
        JDialog dialog = new JDialog(this, "Order Details - " + order.getOrderId(), true);
        dialog.setSize(650, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(UIStyles.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel infoPanel = UIStyles.createSimpleCardPanel();
        infoPanel.setLayout(new GridLayout(2, 2, 10, 10));
        
        Customer customer = dataManager.getCustomerById(order.getCustomerId());
        String customerName = customer != null ? customer.getShopName() : order.getCustomerId();

        infoPanel.add(UIStyles.createLabel("Order ID:", UIStyles.FONT_BODY_BOLD, UIStyles.TEXT_SECONDARY));
        infoPanel.add(UIStyles.createLabel(order.getOrderId(), UIStyles.FONT_BODY, UIStyles.TEXT_PRIMARY));
        infoPanel.add(UIStyles.createLabel("Customer:", UIStyles.FONT_BODY_BOLD, UIStyles.TEXT_SECONDARY));
        infoPanel.add(UIStyles.createLabel(customerName, UIStyles.FONT_BODY, UIStyles.TEXT_PRIMARY));

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        String[] columns = {"Product Name", "Quantity", "Unit Price", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        for (Map.Entry<String, Integer> entry : order.getItems().entrySet()) {
            Product p = dataManager.getProductById(entry.getKey());
            String name = p != null ? p.getName() : entry.getKey();
            double price = p != null ? p.getPrice() : 0.0;
            model.addRow(new Object[]{
                name, entry.getValue(), 
                "Rs. " + String.format("%,.2f", price),
                "Rs. " + String.format("%,.2f", price * entry.getValue())
            });
        }

        JTable table = new JTable(model);
        UIStyles.styleTable(table);
        mainPanel.add(UIStyles.createScrollPane(table), BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(15, 0, 0, 0));

        JLabel totalLabel = new JLabel("Grand Total: Rs. " + String.format("%,.2f", order.getTotalAmount()));
        totalLabel.setFont(UIStyles.FONT_TITLE);
        totalLabel.setForeground(UIStyles.PRIMARY_COLOR);
        footer.add(totalLabel, BorderLayout.WEST);

        JButton billBtn = UIStyles.createButton("Generate Receipt", UIStyles.SUCCESS_COLOR);
        billBtn.setPreferredSize(new Dimension(220, 45));
        billBtn.setIcon(UIStyles.createIcon("ORDERS", Color.WHITE, 18));
        billBtn.setIconTextGap(10);
        billBtn.addActionListener(e -> showReceiptDialog(order));
        footer.add(billBtn, BorderLayout.EAST);

        mainPanel.add(footer, BorderLayout.SOUTH);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showReceiptDialog(Order order) {
        JDialog dialog = new JDialog(this, "Official Receipt", true);
        dialog.setSize(500, 700);
        dialog.setLocationRelativeTo(this);

        JPanel paper = new JPanel(new BorderLayout(0, 20));
        paper.setBackground(Color.WHITE);
        paper.setBorder(new CompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(40, 40, 40, 40)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel brand = new JLabel("BUSINESS PORTAL");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 24));
        brand.setForeground(UIStyles.PRIMARY_COLOR);
        header.add(brand, BorderLayout.NORTH);
        
        JLabel type = new JLabel("OFFICIAL RECEIPT");
        type.setFont(UIStyles.FONT_SMALL);
        type.setForeground(UIStyles.TEXT_SECONDARY);
        header.add(type, BorderLayout.SOUTH);
        paper.add(header, BorderLayout.NORTH);

        Customer c = dataManager.getCustomerById(order.getCustomerId());
        StringBuilder sb = new StringBuilder();
        sb.append("------------------------------------------------------------------\n");
        sb.append("RECEIPT TO:\n");
        sb.append(c != null ? c.getShopName() : "Walk-in Customer").append("\n");
        sb.append("Owner: ").append(c != null ? c.getOwnerName() : "N/A").append("\n");
        sb.append("Phone: ").append(c != null ? c.getPhone() : "N/A").append("\n");
        sb.append("Address: ").append(c != null ? c.getAddress() : "N/A").append("\n");
        sb.append("------------------------------------------------------------------\n");
        sb.append("Receipt No: ").append(order.getOrderId().replace("ORD", "REC")).append("\n");
        sb.append("Order ID: ").append(order.getOrderId()).append("\n");
        sb.append("Date: ").append(order.getCreatedDate()).append("\n");
        sb.append("Est. Delivery: ").append(order.getEstimatedDeliveryDate()).append("\n");
        sb.append("Status: ").append(order.getStatus()).append("\n");
        sb.append("------------------------------------------------------------------\n\n");
        sb.append(String.format("%-30s %-10s %-15s\n", "Item", "Qty", "Price"));
        sb.append("------------------------------------------------------------------\n");
        
        for (Map.Entry<String, Integer> entry : order.getItems().entrySet()) {
            Product p = dataManager.getProductById(entry.getKey());
            String name = p != null ? p.getName() : entry.getKey();
            if (name.length() > 28) name = name.substring(0, 25) + "...";
            sb.append(String.format("%-30s %-10d Rs. %-15.2f\n", name, entry.getValue(), p != null ? p.getPrice() : 0.0));
        }
        
        sb.append("\n------------------------------------------------------------------\n");
        sb.append(String.format("GRAND TOTAL: Rs. %,.2f\n", order.getTotalAmount()));
        sb.append("------------------------------------------------------------------\n\n");
        sb.append("Thank you for your business!");

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setEditable(false);
        paper.add(new JScrollPane(area), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton saveBtn = UIStyles.createButton("Save as Text", UIStyles.PRIMARY_COLOR);
        saveBtn.addActionListener(e -> {
            saveReceiptToFile(order, sb.toString());
            dialog.dispose();
        });
        actions.add(saveBtn);
        paper.add(actions, BorderLayout.SOUTH);

        dialog.add(paper);
        dialog.setVisible(true);
    }

    private void saveReceiptToFile(Order order, String content) {
        try {
            Path path = Paths.get("receipts");
            if (!Files.exists(path)) Files.createDirectories(path);
            String filename = "receipts/Receipt_" + order.getOrderId() + ".txt";
            try (PrintWriter out = new PrintWriter(filename)) {
                out.println(content);
            }
            UIStyles.showMessage(this, "Receipt saved to: " + filename, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            UIStyles.showMessage(this, "Failed to save receipt: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        int confirm = UIStyles.showConfirm(this, "Are you sure you want to logout?", "Confirm Logout");
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private boolean validateCustomerInput(String ownerName, String phone) {
        if (!ownerName.matches("[a-zA-Z\\s]+")) {
            UIStyles.showMessage(this, "Owner name should only contain letters and spaces", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!phone.matches("^(03\\d{9}|021\\d{8})$")) {
            UIStyles.showMessage(this, "Phone must be a valid 11-digit mobile (e.g. 03xx) or 11-digit landline (e.g. 021xx)", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) return str;
        String[] words = str.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }

    private void exportTableToCSV(JTable table, String fileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(fileName));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(file)) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                int columnCount = model.getColumnCount();
                
                // Write Header
                for (int i = 0; i < columnCount; i++) {
                    pw.print(model.getColumnName(i) + (i == columnCount - 1 ? "" : ","));
                }
                pw.println();

                // Write Rows
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < columnCount; j++) {
                        Object val = model.getValueAt(i, j);
                        String strVal = (val == null) ? "" : val.toString().replace(",", " ");
                        pw.print(strVal + (j == columnCount - 1 ? "" : ","));
                    }
                    pw.println();
                }
                UIStyles.showMessage(this, "Data exported successfully to " + file.getName(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                UIStyles.showMessage(this, "Failed to export data: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
