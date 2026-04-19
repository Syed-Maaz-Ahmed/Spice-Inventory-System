package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Payment {
    private String paymentId;
    private String referenceId;
    private String type;
    private double amount;
    private String status;
    private String createdDate;
    private String paidDate;

    public static final String TYPE_CUSTOMER = "CUSTOMER";
    public static final String TYPE_SUPPLIER = "SUPPLIER";

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PAID = "PAID";
    public static final String STATUS_CANCELLED = "CANCELLED";

    public Payment() {}

    public Payment(String paymentId, String referenceId, String type, double amount) {
        this.paymentId = paymentId;
        this.referenceId = referenceId;
        this.type = type;
        this.amount = amount;
        this.status = STATUS_PENDING;
        this.createdDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.paidDate = "";
    }

    public Payment(String paymentId, String referenceId, String type, double amount,
                  String status, String createdDate, String paidDate) {
        this.paymentId = paymentId;
        this.referenceId = referenceId;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.createdDate = createdDate;
        this.paidDate = paidDate;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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

    public String getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(String paidDate) {
        this.paidDate = paidDate;
    }

    public void markAsPaid() {
        this.status = STATUS_PAID;
        this.paidDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        return "Payment #" + paymentId + " - Rs. " + amount + " (" + status + ")";
    }
}
