package data;

import models.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataManager {
    private static final String DB_FILE = "inventory.db";
    private static final String CONNECTION_URL = "jdbc:sqlite:" + DB_FILE;
    private List<User> users;
    private static DataManager instance;

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private DataManager() {
        users = new ArrayList<>();
        initializeUsers();
        try {
            Class.forName("org.sqlite.JDBC");
            initializeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(CONNECTION_URL);
    }

    private void initializeDatabase() throws SQLException {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS products (" +
                    "productId TEXT PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "quantity INTEGER DEFAULT 0," +
                    "price REAL DEFAULT 0.0," +
                    "threshold INTEGER DEFAULT 0," +
                    "imagePath TEXT DEFAULT 'none')");

            stmt.execute("CREATE TABLE IF NOT EXISTS customers (" +
                    "customerId TEXT PRIMARY KEY," +
                    "shopName TEXT NOT NULL," +
                    "ownerName TEXT," +
                    "phone TEXT," +
                    "address TEXT," +
                    "imagePath TEXT DEFAULT 'none')");

            stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                    "orderId TEXT PRIMARY KEY," +
                    "customerId TEXT," +
                    "items TEXT," +
                    "totalAmount REAL," +
                    "status TEXT," +
                    "createdDate TEXT," +
                    "estimatedDeliveryDate TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS enquiries (" +
                    "enquiryId TEXT PRIMARY KEY," +
                    "productId TEXT," +
                    "requestedQuantity INTEGER," +
                    "status TEXT," +
                    "createdDate TEXT," +
                    "estimatedCost REAL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS payments (" +
                    "paymentId TEXT PRIMARY KEY," +
                    "referenceId TEXT," +
                    "type TEXT," +
                    "amount REAL," +
                    "status TEXT," +
                    "createdDate TEXT," +
                    "paidDate TEXT)");

            try {
                stmt.execute("ALTER TABLE orders ADD COLUMN estimatedDeliveryDate TEXT");
            } catch (SQLException e) {}
            try {
                stmt.execute("ALTER TABLE products ADD COLUMN imagePath TEXT DEFAULT 'none'");
            } catch (SQLException e) {}
            try {
                stmt.execute("ALTER TABLE products RENAME COLUMN lowStockThreshold TO threshold");
            } catch (SQLException e) {}
            try {
                stmt.execute("ALTER TABLE customers ADD COLUMN imagePath TEXT DEFAULT 'none'");
            } catch (SQLException e) {}
        }
    }

    private void initializeUsers() {
        users.add(new User("owner", "owner123", "BUSINESS_OWNER", "Ahmed Khan"));
        users.add(new User("stock", "stock123", "STOCK_MANAGER", "Bilal Ahmed"));
        users.add(new User("delivery", "delivery123", "DELIVERY_PERSON", "Kamran Ali"));
        users.add(new User("supplier", "supplier123", "SUPPLIER", "Wholesale Corp"));
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                products.add(new Product(
                        rs.getString("productId"), rs.getString("name"),
                        rs.getInt("quantity"), rs.getDouble("price"), rs.getInt("threshold"), rs.getString("imagePath")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public Product getProductById(String productId) {
        String sql = "SELECT * FROM products WHERE productId = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Product(
                        rs.getString("productId"), rs.getString("name"),
                        rs.getInt("quantity"), rs.getDouble("price"), rs.getInt("threshold"), rs.getString("imagePath"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateProductQuantity(String productId, int newQuantity) {
        String sql = "UPDATE products SET quantity = ? WHERE productId = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newQuantity);
            pstmt.setString(2, productId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addProduct(Product p) {
        String sql = "INSERT OR REPLACE INTO products (productId, name, quantity, price, threshold, imagePath) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getProductId());
            pstmt.setString(2, p.getName());
            pstmt.setInt(3, p.getQuantity());
            pstmt.setDouble(4, p.getPrice());
            pstmt.setInt(5, p.getThreshold());
            pstmt.setString(6, p.getImagePath());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Product> getLowStockProducts() {
        List<Product> lowStock = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE quantity <= threshold";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lowStock.add(new Product(
                        rs.getString("productId"), rs.getString("name"),
                        rs.getInt("quantity"), rs.getDouble("price"), rs.getInt("threshold"), rs.getString("imagePath")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lowStock;
    }

    public String generateProductId() {
        try (Connection conn = connect(); Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM products")) {
            if (rs.next()) return "P" + String.format("%03d", rs.getInt(1) + 1);
        } catch (SQLException e) {}
        return "P" + System.currentTimeMillis();
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                customers.add(new Customer(rs.getString("customerId"), rs.getString("shopName"),
                        rs.getString("ownerName"), rs.getString("phone"), rs.getString("address"), rs.getString("imagePath")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public Customer getCustomerById(String customerId) {
        String sql = "SELECT * FROM customers WHERE customerId = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Customer(rs.getString("customerId"), rs.getString("shopName"),
                        rs.getString("ownerName"), rs.getString("phone"), rs.getString("address"), rs.getString("imagePath"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addCustomer(Customer c) {
        String sql = "INSERT OR REPLACE INTO customers (customerId, shopName, ownerName, phone, address, imagePath) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getCustomerId());
            pstmt.setString(2, c.getShopName());
            pstmt.setString(3, c.getOwnerName());
            pstmt.setString(4, c.getPhone());
            pstmt.setString(5, c.getAddress());
            pstmt.setString(6, c.getImagePath());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCustomer(Customer c) {
        addCustomer(c);
    }

    public void removeCustomer(String customerId) {
        String sql = "DELETE FROM customers WHERE customerId = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String generateCustomerId() {
        try (Connection conn = connect(); Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM customers")) {
            if (rs.next()) return "C" + String.format("%03d", rs.getInt(1) + 1);
        } catch (SQLException e) {}
        return "C" + System.currentTimeMillis();
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                orders.add(parseOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<Order> getOrdersByStatus(String status) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) orders.add(parseOrder(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<Order> getPendingOrdersForStockManager() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = ? OR status = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, Order.STATUS_PENDING);
            pstmt.setString(2, Order.STATUS_PREPARING);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) orders.add(parseOrder(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<Order> getOrdersForDelivery() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = ? OR status = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, Order.STATUS_PACKED);
            pstmt.setString(2, Order.STATUS_OUT_FOR_DELIVERY);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) orders.add(parseOrder(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public Order getOrderById(String orderId) {
        String sql = "SELECT * FROM orders WHERE orderId = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return parseOrder(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addOrder(Order o) {
        String sql = "INSERT OR REPLACE INTO orders (orderId, customerId, items, totalAmount, status, createdDate, estimatedDeliveryDate) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, o.getOrderId());
            pstmt.setString(2, o.getCustomerId());
            StringBuilder itemsStr = new StringBuilder();
            for (Map.Entry<String, Integer> entry : o.getItems().entrySet()) {
                if (itemsStr.length() > 0) itemsStr.append(",");
                itemsStr.append(entry.getKey()).append(":").append(entry.getValue());
            }
            pstmt.setString(3, itemsStr.toString());
            pstmt.setDouble(4, o.getTotalAmount());
            pstmt.setString(5, o.getStatus());
            pstmt.setString(6, o.getCreatedDate());
            pstmt.setString(7, o.getEstimatedDeliveryDate());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateOrderStatus(String orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE orderId = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, orderId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deductStockForOrder(Order order) {
        for (Map.Entry<String, Integer> entry : order.getItems().entrySet()) {
            Product product = getProductById(entry.getKey());
            if (product != null) {
                int newQuantity = product.getQuantity() - entry.getValue();
                updateProductQuantity(product.getProductId(), Math.max(0, newQuantity));
            }
        }
    }

    private Order parseOrder(ResultSet rs) throws SQLException {
        Map<String, Integer> items = new HashMap<>();
        String itemsStr = rs.getString("items");
        if (itemsStr != null && !itemsStr.isEmpty()) {
            for (String item : itemsStr.split(",")) {
                String[] kv = item.split(":");
                if (kv.length == 2) items.put(kv[0], Integer.parseInt(kv[1]));
            }
        }
        return new Order(rs.getString("orderId"), rs.getString("customerId"), items,
                rs.getDouble("totalAmount"), rs.getString("status"), rs.getString("createdDate"), rs.getString("estimatedDeliveryDate"));
    }

    public String generateOrderId() {
        try (Connection conn = connect(); Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM orders")) {
            if (rs.next()) return "ORD" + String.format("%04d", rs.getInt(1) + 1);
        } catch (SQLException e) {}
        return "ORD" + System.currentTimeMillis();
    }

    public List<SupplierEnquiry> getAllEnquiries() {
        List<SupplierEnquiry> list = new ArrayList<>();
        String sql = "SELECT * FROM enquiries";
        try (Connection conn = connect(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new SupplierEnquiry(rs.getString("enquiryId"), rs.getString("productId"), rs.getInt("requestedQuantity"),
                        rs.getString("status"), rs.getString("createdDate"), rs.getDouble("estimatedCost")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<SupplierEnquiry> getPendingEnquiries() {
        List<SupplierEnquiry> list = new ArrayList<>();
        String sql = "SELECT * FROM enquiries WHERE status != ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, SupplierEnquiry.STATUS_SUPPLIED);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new SupplierEnquiry(rs.getString("enquiryId"), rs.getString("productId"), rs.getInt("requestedQuantity"),
                        rs.getString("status"), rs.getString("createdDate"), rs.getDouble("estimatedCost")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addEnquiry(SupplierEnquiry e) {
        String sql = "INSERT OR REPLACE INTO enquiries (enquiryId, productId, requestedQuantity, status, createdDate, estimatedCost) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getEnquiryId());
            ps.setString(2, e.getProductId());
            ps.setInt(3, e.getRequestedQuantity());
            ps.setString(4, e.getStatus());
            ps.setString(5, e.getCreatedDate());
            ps.setDouble(6, e.getEstimatedCost());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public SupplierEnquiry getEnquiryById(String enquiryId) {
        String sql = "SELECT * FROM enquiries WHERE enquiryId = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, enquiryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new SupplierEnquiry(rs.getString("enquiryId"), rs.getString("productId"), rs.getInt("requestedQuantity"),
                        rs.getString("status"), rs.getString("createdDate"), rs.getDouble("estimatedCost"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<SupplierEnquiry> getEnquiriesForQualityCheck() {
        List<SupplierEnquiry> list = new ArrayList<>();
        String sql = "SELECT * FROM enquiries WHERE status = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, SupplierEnquiry.STATUS_SUPPLIED);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new SupplierEnquiry(rs.getString("enquiryId"), rs.getString("productId"), rs.getInt("requestedQuantity"),
                        rs.getString("status"), rs.getString("createdDate"), rs.getDouble("estimatedCost")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateEnquiryStatus(String id, String status) {
        SupplierEnquiry e = getEnquiryById(id);
        if (e != null) {
            String sqlUpd = "UPDATE enquiries SET status = ? WHERE enquiryId = ?";
            try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sqlUpd)) {
                ps.setString(1, status);
                ps.setString(2, id);
                ps.executeUpdate();
            } catch (SQLException ex) {}

            if (status.equals(SupplierEnquiry.STATUS_APPROVED)) {
                Product p = getProductById(e.getProductId());
                if (p != null) updateProductQuantity(p.getProductId(), p.getQuantity() + e.getRequestedQuantity());
                
                Payment payment = new Payment(generatePaymentId(), e.getEnquiryId(), 
                                             Payment.TYPE_SUPPLIER, e.getEstimatedCost());
                addPayment(payment);
            }
        }
    }

    public String generateEnquiryId() {
        try (Connection conn = connect(); Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM enquiries")) {
            if (rs.next()) return "ENQ" + String.format("%04d", rs.getInt(1) + 1);
        } catch (SQLException e) {}
        return "ENQ" + System.currentTimeMillis();
    }

    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments";
        try (Connection conn = connect(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Payment(rs.getString("paymentId"), rs.getString("referenceId"), rs.getString("type"),
                        rs.getDouble("amount"), rs.getString("status"), rs.getString("createdDate"),
                        rs.getString("paidDate")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addPayment(Payment p) {
        String sql = "INSERT OR REPLACE INTO payments (paymentId, referenceId, type, amount, status, createdDate, paidDate) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getPaymentId());
            ps.setString(2, p.getReferenceId());
            ps.setString(3, p.getType());
            ps.setDouble(4, p.getAmount());
            ps.setString(5, p.getStatus());
            ps.setString(6, p.getCreatedDate());
            ps.setString(7, p.getPaidDate());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updatePaymentStatus(String id, String status) {
        String sql = "UPDATE payments SET status = ?, paidDate = ? WHERE paymentId = ?";
        String paidDate = status.equals(Payment.STATUS_PAID) ? 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
            
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, paidDate);
            ps.setString(3, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String generatePaymentId() {
        try (Connection conn = connect(); Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM payments")) {
            if (rs.next()) return "PAY" + String.format("%04d", rs.getInt(1) + 1);
        } catch (SQLException e) {}
        return "PAY" + System.currentTimeMillis();
    }

    public User authenticateUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}
