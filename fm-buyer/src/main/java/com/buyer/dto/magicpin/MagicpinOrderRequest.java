package com.buyer.dto.magicpin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MagicpinOrderRequest {

    private static final long serialVersionUID = 1L;
    @JsonProperty("orderId")
    private Integer orderId;
    @JsonProperty("shipmentId")
    private Integer shipmentId;
    @JsonProperty("createdAt")
    private String createdAt;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("phoneNo")
    private String phoneNo;
    @JsonProperty("amount")
    private Double amount;
    @JsonProperty("tax")
    private Double tax;
    @JsonProperty("note")
    private String note;
    @JsonProperty("orderType")
    private OrderType orderType;
    @JsonProperty("deliveryFee")
    private Double deliveryFee;
    @JsonProperty("deliveryInitiator")
    private final String deliveryInitiator = "MAGICPIN";
    @JsonProperty("items")
    private List<Item> items;
    @JsonProperty("contactLessDelivery")
    private Boolean contactLessDelivery;
    @JsonProperty("paymentMode")
    private PaymentMode paymentMode;
    @JsonProperty("merchantFundedDiscount")
    private Double merchantFundedDiscount;
    @JsonProperty("orderLevelCharges")
    private Charge orderLevelCharges;
    @JsonProperty("additionalCharges")
    private List<AdditionalCharge> additionalCharges;
    @JsonProperty("riderOTP")
    private String riderOTP;
    @JsonProperty("shippingAddress")
    private Address shipingAddress;
    @JsonProperty("billingAddress")
    private Address billingAddress;
    @JsonProperty("orderTaxes")
    private List<Tax> orederTaxes;
    @JsonProperty("merchantData")
    private MerchantData merchantData;

    public MagicpinOrderRequest() {
    }

    public MagicpinOrderRequest(Integer orderId, Integer shipmentId, String createdAt, String userName, String phoneNo, Double amount, Double tax, String note, OrderType orderType, Double deliveryFee, List<Item> items, Boolean contactLessDelivery, PaymentMode paymentMode, Double merchantFundedDiscount, Charge orderLevelCharges, String riderOTP, Address shipingAddress, Address billingAddress, List<Tax> orederTaxes, MerchantData merchantData) {
        this.orderId = orderId;
        this.shipmentId = shipmentId;
        this.createdAt = createdAt;
        this.userName = userName;
        this.phoneNo = phoneNo;
        this.amount = amount;
        this.tax = tax;
        this.note = note;
        this.orderType = orderType;
        this.deliveryFee = deliveryFee;
        this.items = items;
        this.contactLessDelivery = contactLessDelivery;
        this.paymentMode = paymentMode;
        this.merchantFundedDiscount = merchantFundedDiscount;
        this.orderLevelCharges = orderLevelCharges;
        this.riderOTP = riderOTP;
        this.shipingAddress = shipingAddress;
        this.billingAddress = billingAddress;
        this.orederTaxes = orederTaxes;
        this.merchantData = merchantData;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public List<AdditionalCharge> getAdditionalCharges() {
        return additionalCharges;
    }

    public void setAdditionalCharges(List<AdditionalCharge> additionalCharges) {
        this.additionalCharges = additionalCharges;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Integer shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getDeliveryInitiator() {
        return deliveryInitiator;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Boolean getContactLessDelivery() {
        return contactLessDelivery;
    }

    public void setContactLessDelivery(Boolean contactLessDelivery) {
        this.contactLessDelivery = contactLessDelivery;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Double getMerchantFundedDiscount() {
        return merchantFundedDiscount;
    }

    public void setMerchantFundedDiscount(Double merchantFundedDiscount) {
        this.merchantFundedDiscount = merchantFundedDiscount;
    }

    public Charge getOrderLevelCharges() {
        return orderLevelCharges;
    }

    public void setOrderLevelCharges(Charge orderLevelCharges) {
        this.orderLevelCharges = orderLevelCharges;
    }

    public String getRiderOTP() {
        return riderOTP;
    }

    public void setRiderOTP(String riderOTP) {
        this.riderOTP = riderOTP;
    }

    public Address getShipingAddress() {
        return shipingAddress;
    }

    public void setShipingAddress(Address shipingAddress) {
        this.shipingAddress = shipingAddress;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public List<Tax> getOrederTaxes() {
        return orederTaxes;
    }

    public void setOrederTaxes(List<Tax> orederTaxes) {
        this.orederTaxes = orederTaxes;
    }

    public MerchantData getMerchantData() {
        return merchantData;
    }

    public void setMerchantData(MerchantData merchantData) {
        this.merchantData = merchantData;
    }

    @Override
    public String toString() {
        return "MagicpinOrderRequest{" +
                "orderId=" + orderId +
                ", shipmentId=" + shipmentId +
                ", createdAt='" + createdAt + '\'' +
                ", userName='" + userName + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", amount=" + amount +
                ", tax=" + tax +
                ", note='" + note + '\'' +
                ", orderType=" + orderType +
                ", deliveryFee=" + deliveryFee +
                ", deliveryInitiator='" + deliveryInitiator + '\'' +
                ", items=" + items +
                ", contactLessDelivery=" + contactLessDelivery +
                ", paymentMode=" + paymentMode +
                ", merchantFundedDiscount=" + merchantFundedDiscount +
                ", orderLevelCharges=" + orderLevelCharges +
                ", additionalCharges=" + additionalCharges +
                ", riderOTP='" + riderOTP + '\'' +
                ", shipingAddress=" + shipingAddress +
                ", billingAddress=" + billingAddress +
                ", orederTaxes=" + orederTaxes +
                ", merchantData=" + merchantData +
                '}';
    }
}
