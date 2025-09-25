package com.buyer.dto.swiggy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

/**
 * Swiggy Order Request DTO
 * Represents the incoming order request from Swiggy
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SwiggyOrderRequest implements Serializable {
    private final static long serialVersionUID = 1L;

    @JsonProperty("is_thirty_mof")
    private Boolean thirtyMintsFlag = Boolean.FALSE;

    @JsonProperty("cart_gst")
    private Float cartGST;

    @JsonProperty("instructions")
    private String instructions;

    //@JsonProperty("item_total_price")
    @JsonIgnore
    private Float itemTotalPrice;

    @JsonProperty("order_packing_charges")
    private Float orderPackingCharges = 0.0f;

    @JsonProperty("cart_igst_percent")
    private Float cartIgstPercent;

    @JsonProperty("cart_sgst")
    private Float cartSgst;

    @JsonProperty("cart_sgst_percent")
    private Float cartSgstPercent;

    @JsonProperty("callback_url")
    private String callbackUrl;

    @JsonProperty("cart_cgst_percent")
    private Float cartCgstPercent;

    @JsonProperty("cart_igst")
    private Float cartIgst;

    @JsonProperty("order_edit")
    private Boolean orderEdit;

    @JsonProperty("delivery_type")
    private String deliveryType;

    @JsonProperty("cart_gst_percent")
    private Float cartGstPercent;

    @JsonProperty("customer_city")
    private String customerCity;

    @JsonProperty("customer_address")
    private String customerAddress;

    @JsonProperty("restaurant_gross_bill")
    private Float restaurantGrossBill;

    @JsonProperty("order_date_time")
    private String orderDateTime;

    @JsonProperty("customer_phone")
    private String customerPhone;

    @JsonProperty("cart_cgst")
    private Float cartCgst;

    @JsonProperty("restaurant_service_charges")
    private Float restaurantServiceCharges;

    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("restaurant_discount")
    private Float restaurantDiscount;

    @JsonProperty("outlet_id")
    private String outletId;

    @JsonProperty("order_edit_reason")
    private String orderEditReason;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("customer_area")
    private String customerArea;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("items")
    List<SwiggyItems> items;

    @JsonProperty("order_type")
    private String orderType;

    @JsonProperty("reward_type")
    private String rewardType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> tags;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String parent_order_id;

    @JsonProperty("cutlery_opted_in")
    private Boolean cutleryOptedIn;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Float getCartGST() {
        return cartGST;
    }

    public void setCartGST(Float cartGST) {
        this.cartGST = cartGST;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Float getItemTotalPrice() {
        return itemTotalPrice;
    }

    public void setItemTotalPrice(Float itemTotalPrice) {
        this.itemTotalPrice = itemTotalPrice;
    }

    public Float getOrderPackingCharges() {
        return orderPackingCharges;
    }

    public void setOrderPackingCharges(Float orderPackingCharges) {
        this.orderPackingCharges = orderPackingCharges;
    }

    public Float getCartIgstPercent() {
        return cartIgstPercent;
    }

    public void setCartIgstPercent(Float cartIgstPercent) {
        this.cartIgstPercent = cartIgstPercent;
    }

    public Float getCartSgst() {
        return cartSgst;
    }

    public void setCartSgst(Float cartSgst) {
        this.cartSgst = cartSgst;
    }

    public Float getCartSgstPercent() {
        return cartSgstPercent;
    }

    public void setCartSgstPercent(Float cartSgstPercent) {
        this.cartSgstPercent = cartSgstPercent;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public Float getCartCgstPercent() {
        return cartCgstPercent;
    }

    public void setCartCgstPercent(Float cartCgstPercent) {
        this.cartCgstPercent = cartCgstPercent;
    }

    public Float getCartIgst() {
        return cartIgst;
    }

    public void setCartIgst(Float cartIgst) {
        this.cartIgst = cartIgst;
    }

    public Boolean getOrderEdit() {
        return orderEdit;
    }

    public void setOrderEdit(Boolean orderEdit) {
        this.orderEdit = orderEdit;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public Float getCartGstPercent() {
        return cartGstPercent;
    }

    public void setCartGstPercent(Float cartGstPercent) {
        this.cartGstPercent = cartGstPercent;
    }

    public String getCustomerCity() {
        return customerCity;
    }

    public void setCustomerCity(String customerCity) {
        this.customerCity = customerCity;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public Float getRestaurantGrossBill() {
        return restaurantGrossBill;
    }

    public void setRestaurantGrossBill(Float restaurantGrossBill) {
        this.restaurantGrossBill = restaurantGrossBill;
    }

    public String getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(String orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Float getCartCgst() {
        return cartCgst;
    }

    public void setCartCgst(Float cartCgst) {
        this.cartCgst = cartCgst;
    }

    public Float getRestaurantServiceCharges() {
        return restaurantServiceCharges;
    }

    public void setRestaurantServiceCharges(Float restaurantServiceCharges) {
        this.restaurantServiceCharges = restaurantServiceCharges;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Float getRestaurantDiscount() {
        return restaurantDiscount;
    }

    public void setRestaurantDiscount(Float restaurantDiscount) {
        this.restaurantDiscount = restaurantDiscount;
    }

    public String getOutletId() {
        return outletId;
    }

    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }

    public String getOrderEditReason() {
        return orderEditReason;
    }

    public void setOrderEditReason(String orderEditReason) {
        this.orderEditReason = orderEditReason;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerArea() {
        return customerArea;
    }

    public void setCustomerArea(String customerArea) {
        this.customerArea = customerArea;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<SwiggyItems> getItems() {
        return items;
    }

    public void setItems(List<SwiggyItems> items) {
        this.items = items;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Boolean getThirtyMintsFlag() {
        return thirtyMintsFlag;
    }

    public void setThirtyMintsFlag(Boolean thirtyMintsFlag) {
        this.thirtyMintsFlag = thirtyMintsFlag;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getParent_order_id() {
        return parent_order_id;
    }

    public void setParent_order_id(String parent_order_id) {
        this.parent_order_id = parent_order_id;
    }

    public Boolean getCutleryOptedIn() {
        return cutleryOptedIn;
    }

    public void setCutleryOptedIn(Boolean cutleryOptedIn) {
        this.cutleryOptedIn = cutleryOptedIn;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SwiggyOrderRequest{");
        sb.append("thirtyMintsFlag=").append(thirtyMintsFlag);
        sb.append(", cartGST=").append(cartGST);
        sb.append(", instructions='").append(instructions).append('\'');
        sb.append(", itemTotalPrice=").append(itemTotalPrice);
        sb.append(", orderPackingCharges=").append(orderPackingCharges);
        sb.append(", cartIgstPercent=").append(cartIgstPercent);
        sb.append(", cartSgst=").append(cartSgst);
        sb.append(", cartSgstPercent=").append(cartSgstPercent);
        sb.append(", callbackUrl='").append(callbackUrl).append('\'');
        sb.append(", cartCgstPercent=").append(cartCgstPercent);
        sb.append(", cartIgst=").append(cartIgst);
        sb.append(", orderEdit=").append(orderEdit);
        sb.append(", deliveryType='").append(deliveryType).append('\'');
        sb.append(", cartGstPercent=").append(cartGstPercent);
        sb.append(", customerCity='").append(customerCity).append('\'');
        sb.append(", customerAddress='").append(customerAddress).append('\'');
        sb.append(", restaurantGrossBill=").append(restaurantGrossBill);
        sb.append(", orderDateTime='").append(orderDateTime).append('\'');
        sb.append(", customerPhone='").append(customerPhone).append('\'');
        sb.append(", cartCgst=").append(cartCgst);
        sb.append(", restaurantServiceCharges=").append(restaurantServiceCharges);
        sb.append(", paymentType='").append(paymentType).append('\'');
        sb.append(", restaurantDiscount=").append(restaurantDiscount);
        sb.append(", outletId='").append(outletId).append('\'');
        sb.append(", orderEditReason='").append(orderEditReason).append('\'');
        sb.append(", customerName='").append(customerName).append('\'');
        sb.append(", customerArea='").append(customerArea).append('\'');
        sb.append(", orderId=").append(orderId);
        sb.append(", items=").append(items);
        sb.append(", orderType='").append(orderType).append('\'');
        sb.append(", rewardType='").append(rewardType).append('\'');
        sb.append('}');
        return sb.toString();
    }
}