package com.buyer.dto.rapido;

import java.util.List;

public class RapidoOrderCreate {
    private OrderInfo orderInfo;
    private Payment payment;
    private Customer customer;
    private List<OrderItem> items;

    public RapidoOrderCreate() {
    }

    public RapidoOrderCreate(OrderInfo orderInfo, Payment payment, Customer customer, List<OrderItem> items) {
        this.orderInfo = orderInfo;
        this.payment = payment;
        this.customer = customer;
        this.items = items;
    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "RapidoOrderCreate{" +
                "orderInfo=" + orderInfo +
                ", payment=" + payment +
                ", customer=" + customer +
                ", items=" + items +
                '}';
    }
}
