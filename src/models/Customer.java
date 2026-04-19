package models;

public class Customer {
    private String customerId;
    private String shopName;
    private String ownerName;
    private String phone;
    private String address;
    private String imagePath;

    public Customer() {}

    public Customer(String customerId, String shopName, String ownerName, String phone, String address) {
        this.customerId = customerId;
        this.shopName = shopName;
        this.ownerName = ownerName;
        this.phone = phone;
        this.address = address;
        this.imagePath = "none";
    }

    public Customer(String customerId, String shopName, String ownerName, String phone, String address, String imagePath) {
        this.customerId = customerId;
        this.shopName = shopName;
        this.ownerName = ownerName;
        this.phone = phone;
        this.address = address;
        this.imagePath = imagePath;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return shopName + " - " + ownerName;
    }
}
