# 📦 Inventory Management System

A comprehensive, beginner-friendly inventory management system built with **Plain Java** and **Java Swing** for the GUI. This system is designed for wholesale-to-retail business operations.

## 🎯 System Overview

This application eliminates paper-based record keeping by digitizing the entire order-to-delivery workflow.

### 👥 System Actors

| Actor | Username | Password | Role |
|-------|----------|----------|------|
| **Business Owner** | `owner` | `owner123` | Middle-man who takes orders, monitors low stock, sends enquiries to suppliers, manages payments |
| **Stock Manager** | `stock` | `stock123` | Manages inventory, prepares and packs orders |
| **Delivery Person** | `delivery` | `delivery123` | Delivers orders, updates delivery status |
| **Supplier** | `supplier` | `supplier123` | Receives enquiries, supplies stock |

## 🔄 Workflow

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  Business Owner │────▶│  Stock Manager  │────▶│ Delivery Person │────▶│    Customer     │
│   Creates Order │     │  Prepares/Packs │     │    Delivers     │     │   Receives      │
└─────────────────┘     └─────────────────┘     └─────────────────┘     └─────────────────┘
         │
         │ (Low Stock Alert)
         ▼
┌─────────────────┐
│    Supplier     │
│ Supplies Stock  │
└─────────────────┘
```

### Order Status Flow:
`PENDING` → `PREPARING` → `PACKED` → `OUT_FOR_DELIVERY` → `DELIVERED` / `DELIVERY_FAILED`

### Enquiry Status Flow:
`PENDING` → `PROCESSING` → `SUPPLIED` / `REJECTED`

## 💾 Data Storage

This system uses **text files** for data storage - a beginner-friendly approach that requires no database setup!

Data is stored in the `data/` folder:
- `products.txt` - Product inventory
- `customers.txt` - Customer/retailer information
- `orders.txt` - All orders
- `enquiries.txt` - Supplier enquiries
- `payments.txt` - Payment records

### How to Run

1. **Double-click `run.bat`** (Windows)
   This will automatically compile the latest changes and start the application.

2. **Manual Run** (Terminal):
   ```cmd
   mkdir bin
   javac -d bin -sourcepath src src\models\*.java src\data\*.java src\ui\*.java
   java -cp bin ui.LoginFrame
   ```

## 📋 Features by Actor

### 🏢 Business Owner
- ✅ Create new orders for customers
- ✅ View all orders and their status
- ✅ See low stock alerts
- ✅ Send enquiries to supplier for restocking
- ✅ Manage customer information
- ✅ Track payments (customer & supplier)
- ✅ Mark payments as Paid/Cancelled

### 📦 Stock Manager
- ✅ View pending orders to prepare
- ✅ See order details with items
- ✅ Mark orders as "Preparing"
- ✅ Mark orders as "Packed" (stock is automatically deducted)
- ✅ Manage inventory stock levels
- ✅ Add new products to inventory
- ✅ Update stock quantities

### 🚚 Delivery Person
- ✅ View packed orders ready for delivery
- ✅ See customer contact info and address
- ✅ Mark orders as "Out for Delivery"
- ✅ Mark orders as "Delivered"
- ✅ Mark orders as "Delivery Failed" (with reason)
- ✅ View delivery history with statistics

### 🏭 Supplier
- ✅ Receive stock enquiries from business owner
- ✅ View product details for each enquiry
- ✅ Set estimated cost for supply
- ✅ Mark enquiries as "Processing"
- ✅ Mark enquiries as "Supplied" (stock auto-added to inventory)
- ✅ Reject enquiries with reason
- ✅ View supply history with statistics

## 🎨 UI Features

- Modern, professional design with color-coded dashboards
- Each actor has a unique color theme for easy identification
- Status badges with color indicators
- Real-time data refresh
- Responsive sidebar navigation
- Clean tables with sorting support
- Form validation and error handling
- Confirmation dialogs for important actions

### Color Themes:
- 🔵 **Business Owner**: Blue (#2980B9)
- 🟣 **Stock Manager**: Purple (#8E44AD)
- 🟠 **Delivery Person**: Orange (#E67E22)
- 🟢 **Supplier**: Teal (#16A085)

## 📁 Project Structure

```
finhyl/
├── src/
│   ├── models/
│   │   ├── User.java          # User model with roles
│   │   ├── Product.java       # Product/inventory item model
│   │   ├── Customer.java      # Customer/retailer model
│   │   ├── Order.java         # Order model with status tracking
│   │   ├── SupplierEnquiry.java # Stock enquiry model
│   │   └── Payment.java       # Payment tracking model
│   ├── data/
│   │   └── DataManager.java   # Data layer - file operations
│   └── ui/
│       ├── UIStyles.java      # Styling utilities
│       ├── LoginFrame.java    # Login screen
│       ├── BusinessOwnerDashboard.java
│       ├── StockManagerDashboard.java
│       ├── DeliveryDashboard.java
│       └── SupplierDashboard.java
├── data/                      # Data storage folder (auto-created)
├── resources/                 # Application assets (logos, icons)
├── run.bat                   # Unified Compile & Run script
└── README.md                 # Documentation
```

## 💡 Tips for Beginners

1. **Start with sample data**: The system initializes with sample products and customers on first run.

2. **Test the workflow**: 
   - Login as `owner` → Create an order
   - Login as `stock` → Prepare and pack the order
   - Login as `delivery` → Deliver the order
   - Login as `owner` → Check the payment record

3. **Low stock testing**:
   - Login as `stock` → Reduce a product's quantity below its threshold
   - Login as `owner` → See the alert and send an enquiry
   - Login as `supplier` → Process and supply the stock

4. **Data persistence**: All data is saved automatically to text files. Close and reopen the app - your data will still be there!

## 🤝 Contributing

This is a beginner-friendly project. Feel free to:
- Add new features
- Improve the UI
- Add more validation
- Implement reports/analytics

## 📝 License

This project is open source and available for educational purposes.

---
Made with ❤️ using Plain Java and Swing
