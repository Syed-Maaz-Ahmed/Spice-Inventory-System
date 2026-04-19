package ui;

import data.DataManager;
import models.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DeliveryDashboard extends JFrame {
    
    private User currentUser;
    private DataManager dataManager;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JTable pendingDeliveriesTable;
    private JTable deliveryHistoryTable;
    private DefaultTableModel pendingDeliveriesModel;
    private DefaultTableModel deliveryHistoryModel;
    private JTextArea detailsArea;
    private JButton[] menuButtons;
    private int selectedMenuIndex = 0;

    public DeliveryDashboard(User user) {
        this.currentUser = user;
        this.dataManager = DataManager.getInstance();
        initializeUI();
        refreshData();
    }

    private void initializeUI() {
        setTitle("Delivery Dashboard - " + currentUser.getName());
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
        
        contentPanel.add(createPendingDeliveriesPanel(), "PENDING_DELIVERIES");
        contentPanel.add(createHistoryPanel(), "HISTORY");

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
        
        ImageIcon brandIcon = new ImageIcon("resources/logo_delivery.png");
        Image img = brandIcon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(img));
        leftPanel.add(iconLabel);
        
        JLabel titleLabel = new JLabel("Delivery Portal");
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
            {"Pending Deliveries", "PENDING_DELIVERIES", "DELIVERY"},
            {"Delivery History", "HISTORY", "HISTORY"}
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

    private JPanel createPendingDeliveriesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(UIStyles.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        ImageIcon icon = UIStyles.createIcon("DELIVERY", UIStyles.ORANGE_COLOR, 40);
        panel.add(UIStyles.createSectionHeader("Pending Deliveries", "Orders ready for delivery", icon), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setDividerSize(5);
        splitPane.setBackground(UIStyles.BACKGROUND_COLOR);
        splitPane.setBorder(null);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        
        String[] columns = {"Order ID", "Customer", "Items", "Est. Delivery", "Status"};
        pendingDeliveriesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pendingDeliveriesTable = new JTable(pendingDeliveriesModel);
        UIStyles.styleTable(pendingDeliveriesTable);
        
        JScrollPane leftScroll = UIStyles.createScrollPane(pendingDeliveriesTable);
        leftPanel.add(leftScroll, BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel);

        JPanel rightPanel = UIStyles.createSimpleCardPanel();
        rightPanel.setLayout(new BorderLayout(0, 15));
        
        JLabel detailsTitle = UIStyles.createLabel("Delivery Information", UIStyles.FONT_SUBHEADER, UIStyles.TEXT_PRIMARY);
        rightPanel.add(detailsTitle, BorderLayout.NORTH);
        
        detailsArea = UIStyles.createTextArea(15, 30);
        detailsArea.setEditable(false);
        detailsArea.setText("Select a delivery to view details...");
        rightPanel.add(UIStyles.createScrollPane(detailsArea), BorderLayout.CENTER);
        
        pendingDeliveriesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showDeliveryDetails();
            }
        });
        
        splitPane.setRightComponent(rightPanel);
        panel.add(splitPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        actionPanel.setBackground(UIStyles.BACKGROUND_COLOR);

        JButton outForDeliveryBtn = UIStyles.createButton("Out for Delivery", UIStyles.PRIMARY_COLOR);
        outForDeliveryBtn.addActionListener(e -> {
            int row = pendingDeliveriesTable.getSelectedRow();
            if (row < 0) {
                UIStyles.showMessage(this, "Please select an order", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String orderId = (String) pendingDeliveriesModel.getValueAt(row, 0);
            Order o = dataManager.getOrderById(orderId);
            if (o != null && o.getStatus().equals(Order.STATUS_PACKED)) {
                dataManager.updateOrderStatus(orderId, Order.STATUS_OUT_FOR_DELIVERY);
                refreshData();
                UIStyles.showMessage(this, "Order " + orderId + " is now Out for Delivery", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                UIStyles.showMessage(this, "Order must be PACKED first", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        actionPanel.add(outForDeliveryBtn);
        
        JButton deliveredBtn = UIStyles.createButton("Delivered", UIStyles.SUCCESS_COLOR);
        deliveredBtn.addActionListener(e -> {
            int row = pendingDeliveriesTable.getSelectedRow();
            if (row < 0) {
                UIStyles.showMessage(this, "Please select an order", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String orderId = (String) pendingDeliveriesModel.getValueAt(row, 0);
            String status = (String) pendingDeliveriesModel.getValueAt(row, 4);
            
            if (!status.equals(Order.STATUS_OUT_FOR_DELIVERY)) {
                UIStyles.showMessage(this, "Order must be 'OUT_FOR_DELIVERY' before marking delivered", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            int confirm = UIStyles.showConfirm(this, "Confirm delivery for Order " + orderId + "?", "Confirm");
            if (confirm == JOptionPane.YES_OPTION) {
                dataManager.updateOrderStatus(orderId, Order.STATUS_DELIVERED);
                UIStyles.showMessage(this, "Delivery successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
                detailsArea.setText("Select a delivery to view details...");
            }
        });
        actionPanel.add(deliveredBtn);
        
        JButton failedBtn = UIStyles.createButton("Failed", UIStyles.DANGER_COLOR);
        failedBtn.addActionListener(e -> {
            int row = pendingDeliveriesTable.getSelectedRow();
            if (row < 0) {
                UIStyles.showMessage(this, "Please select an order", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String orderId = (String) pendingDeliveriesModel.getValueAt(row, 0);
            int confirm = UIStyles.showConfirm(this, "Mark Order " + orderId + " as Failed?", "Confirm");
            if (confirm == JOptionPane.YES_OPTION) {
                dataManager.updateOrderStatus(orderId, Order.STATUS_FAILED);
                UIStyles.showMessage(this, "Delivery marked as failed.", "Info", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
                detailsArea.setText("Select a delivery to view details...");
            }
        });
        actionPanel.add(failedBtn);

        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showDeliveryDetails() {
        int row = pendingDeliveriesTable.getSelectedRow();
        if (row >= 0) {
            String orderId = (String) pendingDeliveriesModel.getValueAt(row, 0);
            Order order = dataManager.getOrderById(orderId);
            if (order != null) {
                Customer customer = dataManager.getCustomerById(order.getCustomerId());
                StringBuilder sb = new StringBuilder();
                sb.append("DELIVERY INFORMATION\n");
                sb.append("----------------------------------------\n");
                sb.append("Estimated Date: ").append(order.getEstimatedDeliveryDate()).append("\n");
                sb.append("----------------------------------------\n");
                sb.append("Order ID: ").append(order.getOrderId()).append("\n");
                sb.append("Status: ").append(order.getStatus()).append("\n\n");
                
                if (customer != null) {
                    sb.append("Shop Name: ").append(customer.getShopName()).append("\n");
                    sb.append("Owner: ").append(customer.getOwnerName()).append("\n");
                    sb.append("Phone: ").append(customer.getPhone()).append("\n");
                    sb.append("Address: \n").append(customer.getAddress()).append("\n");
                }
                
                detailsArea.setText(sb.toString());
            }
        }
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(UIStyles.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        ImageIcon icon = UIStyles.createIcon("HISTORY", UIStyles.TEXT_SECONDARY, 40);
        panel.add(UIStyles.createSectionHeader("Delivery History", "Past deliveries", icon), BorderLayout.NORTH);

        String[] columns = {"Order ID", "Customer", "Status", "Date"};
        deliveryHistoryModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        deliveryHistoryTable = new JTable(deliveryHistoryModel);
        UIStyles.styleTable(deliveryHistoryTable);
        
        JScrollPane scrollPane = UIStyles.createScrollPane(deliveryHistoryTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshData() {
        pendingDeliveriesModel.setRowCount(0);
        for (Order o : dataManager.getOrdersForDelivery()) {
            Customer c = dataManager.getCustomerById(o.getCustomerId());
            String customerName = c != null ? c.getShopName() : o.getCustomerId();
            pendingDeliveriesModel.addRow(new Object[]{
                o.getOrderId(), customerName, o.getItems().size() + " items", 
                o.getEstimatedDeliveryDate(), o.getStatus()
            });
        }

        deliveryHistoryModel.setRowCount(0);
        List<Order> allOrders = dataManager.getAllOrders();
        for (Order o : allOrders) {
            if (o.getStatus().equals(Order.STATUS_DELIVERED) || o.getStatus().equals(Order.STATUS_FAILED)) {
                Customer c = dataManager.getCustomerById(o.getCustomerId());
                String customerName = c != null ? c.getShopName() : o.getCustomerId();
                deliveryHistoryModel.addRow(new Object[]{
                    o.getOrderId(), customerName, o.getStatus(), o.getCreatedDate()
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
