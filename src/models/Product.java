package models;

public class Product {
    private String productId;
    private String name;
    private int quantity;
    private double price;
    private int threshold;
    private String imagePath;

    public Product() {}

    public Product(String productId, String name, int quantity, double price, int threshold) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.threshold = threshold;
        this.imagePath = "none";
    }

    public Product(String productId, String name, int quantity, double price, int threshold, String imagePath) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.threshold = threshold;
        this.imagePath = imagePath;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isLowStock() {
        return quantity <= threshold;
    }

    @Override
    public String toString() {
        return name + " (Qty: " + quantity + ")";
    }
}
