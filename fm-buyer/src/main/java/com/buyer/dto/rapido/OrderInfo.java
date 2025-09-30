package com.buyer.dto.rapido;

public class OrderInfo {
    private String orderId;
    private String restId;
    private String type;
    private String instruction;
    private String status;
    private String paymentMode;
    private double subTotal;
    private double total;
    private double totalTaxes;
    private String createdAt;
    private double deliveryCharge;
    private double totalPackingCharge;
    private String deliveryMode;
    private boolean collectCash;
    private double amountToBeCollected;

    public OrderInfo() {
    }

    public OrderInfo(String orderId, String restId, String type, String instruction, String status, String paymentMode, double subTotal, double total, double totalTaxes, String createdAt, double deliveryCharge, double packingCharge, String deliveryMode, boolean collectCash, double amountToBeCollected) {
        this.orderId = orderId;
        this.restId = restId;
        this.type = type;
        this.instruction = instruction;
        this.status = status;
        this.paymentMode = paymentMode;
        this.subTotal = subTotal;
        this.total = total;
        this.totalTaxes = totalTaxes;
        this.createdAt = createdAt;
        this.deliveryCharge = deliveryCharge;
        this.totalPackingCharge = packingCharge;
        this.deliveryMode = deliveryMode;
        this.collectCash = collectCash;
        this.amountToBeCollected = amountToBeCollected;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRestId() {
        return restId;
    }

    public void setRestId(String restId) {
        this.restId = restId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getTotalTaxes() {
        return totalTaxes;
    }

    public void setTotalTaxes(double totalTaxes) {
        this.totalTaxes = totalTaxes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public double getTotalPackingCharge() {
        return totalPackingCharge;
    }

    public void setTotalPackingCharge(double totalPackingCharge) {
        this.totalPackingCharge = totalPackingCharge;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public boolean isCollectCash() {
        return collectCash;
    }

    public void setCollectCash(boolean collectCash) {
        this.collectCash = collectCash;
    }

    public double getAmountToBeCollected() {
        return amountToBeCollected;
    }

    public void setAmountToBeCollected(double amountToBeCollected) {
        this.amountToBeCollected = amountToBeCollected;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "orderId='" + orderId + '\'' +
                ", restId='" + restId + '\'' +
                ", type='" + type + '\'' +
                ", instruction='" + instruction + '\'' +
                ", status='" + status + '\'' +
                ", paymentMode='" + paymentMode + '\'' +
                ", subTotal=" + subTotal +
                ", total=" + total +
                ", totalTaxes=" + totalTaxes +
                ", createdAt='" + createdAt + '\'' +
                ", deliveryCharge=" + deliveryCharge +
                ", packingCharge=" + totalPackingCharge +
                ", deliveryMode='" + deliveryMode + '\'' +
                ", collectCash=" + collectCash +
                ", amountToBeCollected=" + amountToBeCollected +
                '}';
    }
}
