package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SupplierEnquiry {
    private String enquiryId;
    private String productId;
    private int requestedQuantity;
    private String status;
    private String createdDate;
    private double estimatedCost;

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_SUPPLIED = "SUPPLIED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_RETURNED = "RETURNED";

    public SupplierEnquiry() {}

    public SupplierEnquiry(String enquiryId, String productId, int requestedQuantity) {
        this.enquiryId = enquiryId;
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.status = STATUS_PENDING;
        this.createdDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.estimatedCost = 0;
    }

    public SupplierEnquiry(String enquiryId, String productId, int requestedQuantity,
                          String status, String createdDate, double estimatedCost) {
        this.enquiryId = enquiryId;
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.status = status;
        this.createdDate = createdDate;
        this.estimatedCost = estimatedCost;
    }

    public String getEnquiryId() {
        return enquiryId;
    }

    public void setEnquiryId(String enquiryId) {
        this.enquiryId = enquiryId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(int requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    @Override
    public String toString() {
        return "Enquiry #" + enquiryId + " - " + status;
    }
}
