package ui;

import data.DataManager;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import models.User;

public class LoginFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private DataManager dataManager;
    private int pX, pY;

    public LoginFrame() {
        this.dataManager = DataManager.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setUndecorated(true);
        setBackground(new Color(0,0,0,0));
        
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                GradientPaint gp = new GradientPaint(0, 0, UIStyles.PRIMARY, 400, 600, UIStyles.PRIMARY_HOVER);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, 400, getHeight(), 20, 20);
                
                g2.fillRect(380, 0, 20, getHeight()); 
                
                g2.dispose();
            }
        };
        mainPanel.setLayout(null);
        
        JButton closeBtn = new JButton("×");
        closeBtn.setBounds(860, 10, 30, 30);
        closeBtn.setFont(new Font("Arial", Font.BOLD, 20));
        closeBtn.setForeground(UIStyles.TEXT_GRAY);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> System.exit(0));
        mainPanel.add(closeBtn);

        mainPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                pX = me.getX();
                pY = me.getY();
            }
        });
        
        mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
                setLocation(getLocation().x + me.getX() - pX, getLocation().y + me.getY() - pY);
            }
        });

        JLabel logoLabel = new JLabel();
        try {
            ImageIcon logoIcon = new ImageIcon("resources/main_logo.png");
            if (logoIcon.getIconWidth() > 0) {
                Image img = logoIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(img));
            } else {
                logoLabel.setText("IMS");
                logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 64));
                logoLabel.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            logoLabel.setText("IMS");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 64));
            logoLabel.setForeground(Color.WHITE);
        }
        logoLabel.setBounds(50, 100, 300, 180);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(logoLabel);
        
        JLabel brandSub = new JLabel("Spice Inventory System");
        brandSub.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brandSub.setForeground(new Color(255, 255, 255, 220));
        brandSub.setBounds(50, 290, 300, 30);
        brandSub.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(brandSub);
        
        JLabel welcomeLbl = new JLabel("Welcome Back");
        welcomeLbl.setFont(UIStyles.FONT_HERO);
        welcomeLbl.setForeground(UIStyles.TEXT_DARK);
        welcomeLbl.setBounds(480, 100, 350, 40);
        mainPanel.add(welcomeLbl);
        
        JLabel subLbl = new JLabel("Please sign in to continue");
        subLbl.setFont(UIStyles.FONT_BODY);
        subLbl.setForeground(UIStyles.TEXT_GRAY);
        subLbl.setBounds(480, 145, 350, 20);
        mainPanel.add(subLbl);
        
        JLabel uLbl = new JLabel("Username");
        uLbl.setFont(UIStyles.FONT_BODY_BOLD);
        uLbl.setForeground(UIStyles.TEXT_DARK);
        uLbl.setBounds(480, 200, 350, 20);
        mainPanel.add(uLbl);
        
        usernameField = UIStyles.createTextField();
        usernameField.setBounds(480, 225, 350, 45);
        mainPanel.add(usernameField);
        
        JLabel pLbl = new JLabel("Password");
        pLbl.setFont(UIStyles.FONT_BODY_BOLD);
        pLbl.setForeground(UIStyles.TEXT_DARK);
        pLbl.setBounds(480, 290, 350, 20);
        mainPanel.add(pLbl);
        
        passwordField = UIStyles.createPasswordField();
        passwordField.setBounds(480, 315, 350, 45);
        passwordField.addActionListener(e -> performLogin());
        mainPanel.add(passwordField);

        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.setBounds(480, 365, 150, 25);
        showPassword.setFont(UIStyles.FONT_SMALL);
        showPassword.setForeground(UIStyles.TEXT_GRAY);
        showPassword.setOpaque(false);
        showPassword.setFocusPainted(false);
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        mainPanel.add(showPassword);
        
        JButton loginBtn = UIStyles.createButton("Sign In", UIStyles.PRIMARY);
        loginBtn.setBounds(480, 400, 350, 50);
        loginBtn.addActionListener(e -> performLogin());
        mainPanel.add(loginBtn);
        
        setContentPane(mainPanel);
    }

    private void performLogin() {
        String u = usernameField.getText().trim();
        String p = new String(passwordField.getPassword());
        
        if (u.isEmpty() || p.isEmpty()) {
            UIStyles.showMessage(this, "Enter username and password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        User user = dataManager.authenticateUser(u, p);
        if (user != null) {
            dispose();
            openDashboard(user);
        } else {
            UIStyles.showMessage(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDashboard(User user) {
        JFrame frame = null;
        switch(user.getRole()) {
            case "BUSINESS_OWNER": frame = new BusinessOwnerDashboard(user); break;
            case "STOCK_MANAGER": frame = new StockManagerDashboard(user); break;
            case "DELIVERY_PERSON": frame = new DeliveryDashboard(user); break;
            case "SUPPLIER": frame = new SupplierDashboard(user); break;
        }
        if (frame != null) frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
