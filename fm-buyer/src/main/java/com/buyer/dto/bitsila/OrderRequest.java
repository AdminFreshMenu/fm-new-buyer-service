package com.buyer.dto.bitsila;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRequest implements Serializable {

    @JsonProperty("customer")
    private CustomerDTO customer;

    @JsonProperty("order")
    private OrderDetailsDTO order;

    @JsonProperty("order_items")
    private List<OrderItemDTO> orderItems;

    @JsonProperty("offers")
    private List<OfferDTO> offers;

    @JsonProperty("payment")
    private PaymentDTO payment;

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public OrderDetailsDTO getOrder() {
        return order;
    }

    public void setOrder(OrderDetailsDTO order) {
        this.order = order;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public List<OfferDTO> getOffers() {
        return offers;
    }

    public void setOffers(List<OfferDTO> offers) {
        this.offers = offers;
    }

    public PaymentDTO getPayment() {
        return payment;
    }

    public void setPayment(PaymentDTO payment) {
        this.payment = payment;
    }
}
