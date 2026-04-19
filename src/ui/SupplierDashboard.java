package ui;

import data.DataManager;
import models.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SupplierDashboard extends JFrame {
    
    private User currentUser;
    private DataManager dataManager;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JTable enquiriesTable;
    private JTable historyTable;
    private DefaultTableModel enquiriesModel;
    private DefaultTableModel historyModel;
    private JTextArea detailsArea;
    private JTextField costField;
    private JButton[] menuButtons;
    private int selectedMenuIndex = 0;

    public SupplierDashboard(User user) {
        this.currentUser = user;
        this.dataManager = DataManager.getInstance();
        initializeUI();
        refreshData();
    }

    private void initializeUI() {
        setTitle("Supplier Portal - " + currentUser.getName());
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
        
        contentPanel.add(createEnquiriesPanel(), "ENQUIRIES");
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
        
        ImageIcon brandIcon = new ImageIcon("resources/logo_supplier.png");
        Image img = brandIcon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(img));
        leftPanel.add(iconLabel);
        
        JLabel titleLabel = new JLabel("Supplier Portal");
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
            {"Pending Enquiries", "ENQUIRIES", "ENQUIRIES"},
            {"Supply History", "HISTORY", "HISTORY"}
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
        
        JButton refreshBtn = createMenuButton("Refresh", "REFRESH", "🔄", -1);
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

    private JPanel createEnquiriesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(UIStyles.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        ImageIcon icon = UIStyles.createIcon("ENQUIRIES", UIStyles.TEAL_COLOR, 40);
        panel.add(UIStyles.createSectionHeader("Pending Enquiries", "Respond to stock requests", icon), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        
        String[] columns = {"Enquiry ID", "Product", "Quantity", "Est. Cost", "Status"};
        enquiriesModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        enquiriesTable = new JTable(enquiriesModel);
        UIStyles.styleTable(enquiriesTable);
        leftPanel.add(UIStyles.createScrollPane(enquiriesTable), BorderLayout.CENTER);
        
        enquiriesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showEnquiryDetails();
        });
        
        splitPane.setLeftComponent(leftPanel);

        JPanel rightPanel = UIStyles.createSimpleCardPanel();
        rightPanel.setLayout(new BorderLayout(0, 15));
        
        JLabel detailsTitle = UIStyles.createLabel("Processing", UIStyles.FONT_SUBHEADER, UIStyles.TEXT_PRIMARY);
        rightPanel.add(detailsTitle, BorderLayout.NORTH);
        
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        
        detailsArea = UIStyles.createTextArea(6, 25);
        detailsArea.setEditable(false);
        detailsArea.setText("Select an enquiry from the table...");
        
        gbc.gridy = 0;
        gbc.weighty = 0.6;
        gbc.fill = GridBagConstraints.BOTH;
        form.add(UIStyles.createScrollPane(detailsArea), gbc);
        
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 5, 0);
        form.add(UIStyles.createLabel("Set Estimated Cost (Rs.):", UIStyles.FONT_BODY, UIStyles.TEXT_PRIMARY), gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        costField = UIStyles.createTextField();
        form.add(costField, gbc);
        
        gbc.gridy = 3;
        JButton setCostBtn = UIStyles.createButton("Update Estimate", UIStyles.PRIMARY_COLOR);
        setCostBtn.addActionListener(e -> updateEstimate());
        form.add(setCostBtn, gbc);
        
        rightPanel.add(form, BorderLayout.CENTER);
        splitPane.setRightComponent(rightPanel);
        panel.add(splitPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        actionPanel.setBackground(UIStyles.BACKGROUND_COLOR);

        JButton processBtn = UIStyles.createButton("Mark Processing", UIStyles.WARNING_COLOR);
        processBtn.addActionListener(e -> updateStatus(SupplierEnquiry.STATUS_PROCESSING));
        actionPanel.add(processBtn);

        JButton supplyBtn = UIStyles.createButton("Complete Supply", UIStyles.SUCCESS_COLOR);
        supplyBtn.addActionListener(e -> supplyOrder());
        actionPanel.add(supplyBtn);

        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showEnquiryDetails() {
        int row = enquiriesTable.getSelectedRow();
        if (row >= 0) {
            String id = (String) enquiriesModel.getValueAt(row, 0);
            SupplierEnquiry e = dataManager.getEnquiryById(id);
            if (e != null) {
                Product p = dataManager.getProductById(e.getProductId());
                String pName = (p != null) ? p.getName() : e.getProductId();
                
                StringBuilder sb = new StringBuilder();
                sb.append("Enquiry: ").append(e.getEnquiryId()).append("\n");
                sb.append("Product: ").append(pName).append("\n");
                sb.append("Quantity: ").append(e.getRequestedQuantity()).append("\n");
                sb.append("Status: ").append(e.getStatus()).append("\n");
                sb.append("Created: ").append(e.getCreatedDate());
                
                detailsArea.setText(sb.toString());
                costField.setText(String.valueOf(e.getEstimatedCost()));
            }
        } else {
            detailsArea.setText("Select an enquiry from the table...");
            costField.setText("");
        }
    }
    
    private void updateEstimate() {
        int row = enquiriesTable.getSelectedRow();
        if (row < 0) {
            UIStyles.showMessage(this, "Please select an enquiry first", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String id = (String) enquiriesModel.getValueAt(row, 0);
        try {
            double cost = Double.parseDouble(costField.getText());
            SupplierEnquiry e = dataManager.getEnquiryById(id);
            if (e != null) {
                e.setEstimatedCost(cost);
                dataManager.addEnquiry(e);
                refreshData();
                showEnquiryDetails();
                UIStyles.showMessage(this, "Estimated cost updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            UIStyles.showMessage(this, "Please enter a valid numeric cost", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStatus(String status) {
        int row = enquiriesTable.getSelectedRow();
        if (row < 0) {
            UIStyles.showMessage(this, "Please select an enquiry first", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String id = (String) enquiriesModel.getValueAt(row, 0);
        dataManager.updateEnquiryStatus(id, status);
        refreshData();
        showEnquiryDetails();
    }
    
    private void supplyOrder() {
        int row = enquiriesTable.getSelectedRow();
        if (row < 0) return;
        
        String id = (String) enquiriesModel.getValueAt(row, 0);
        SupplierEnquiry e = dataManager.getEnquiryById(id);
        
        if (e != null && e.getEstimatedCost() <= 0) {
            int confirm = UIStyles.showConfirm(this, 
                "The estimated cost is currently 0. Do you want to supply it anyway?", "Confirm Supply");
            if (confirm != JOptionPane.YES_OPTION) return;
        }
        
        updateStatus(SupplierEnquiry.STATUS_SUPPLIED);
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(UIStyles.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        ImageIcon icon = UIStyles.createIcon("HISTORY", UIStyles.TEXT_SECONDARY, 40);
        panel.add(UIStyles.createSectionHeader("History", "Past supplies", icon), BorderLayout.NORTH);

        String[] columns = {"ID", "Product", "Qty", "Cost", "Status"};
        historyModel = new DefaultTableModel(columns, 0);
        historyTable = new JTable(historyModel);
        UIStyles.styleTable(historyTable);
        panel.add(UIStyles.createScrollPane(historyTable), BorderLayout.CENTER);

        return panel;
    }

    private void refreshData() {
        enquiriesModel.setRowCount(0);
        for (SupplierEnquiry e : dataManager.getPendingEnquiries()) {
             Product p = dataManager.getProductById(e.getProductId());
             String pName = (p != null) ? p.getName() : e.getProductId();
             enquiriesModel.addRow(new Object[]{
                 e.getEnquiryId(), pName, e.getRequestedQuantity(), 
                 String.format("%.2f", e.getEstimatedCost()), e.getStatus()
             });
        }
        historyModel.setRowCount(0);
        for (SupplierEnquiry e : dataManager.getAllEnquiries()) {
            if (e.getStatus().equals(SupplierEnquiry.STATUS_SUPPLIED) || 
                e.getStatus().equals(SupplierEnquiry.STATUS_APPROVED)) {
                 Product p = dataManager.getProductById(e.getProductId());
                 String pName = (p != null) ? p.getName() : e.getProductId();
                 historyModel.addRow(new Object[]{
                     e.getEnquiryId(), pName, e.getRequestedQuantity(), 
                     String.format("%.2f", e.getEstimatedCost()), e.getStatus()
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
