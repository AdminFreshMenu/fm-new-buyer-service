package com.buyer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_additional_details")
public class OrderAdditionalDetails extends AbstractMutableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column
    private OrderAdditionalData orderKey;

    @Column
    private String orderKeyValue;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public OrderAdditionalData getOrderKey() {
        return orderKey;
    }

    public void setOrderKey(OrderAdditionalData orderKey) {
        this.orderKey = orderKey;
    }

    public String getOrderKeyValue() {
        return orderKeyValue;
    }

    public void setOrderKeyValue(String orderKeyValue) {
        this.orderKeyValue = orderKeyValue;
    }
}