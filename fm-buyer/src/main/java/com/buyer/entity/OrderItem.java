package com.buyer.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
public class OrderItem extends AbstractMutableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private Long productId;

    @Column(name = "related_product_id")
    private Long parentOrderItemId;

    @Column
    private Integer quantity;

    @Column
    private Integer sellingPrice;

    @Column
    private Integer tsp;

    @Column
    private BigDecimal cDisc;

    @Column
    private Long orderId;

    @Column
    private Integer mrp;

    @Column
    private BigDecimal discountAmount;

    @Column
    private BigDecimal cashbackAmount;

    @Column
    @Enumerated(EnumType.STRING)
    private OrderItemType orderItemType;

    @Column
    private BigDecimal packagingPrice;

    public Integer getTsp() {
        return tsp;
    }

    public void setTsp(Integer tsp) {
        this.tsp = tsp;
    }

    public BigDecimal getcDisc() {
        return cDisc;
    }

    public void setcDisc(BigDecimal cDisc) {
        this.cDisc = cDisc;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Integer sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getMrp() {
        return mrp;
    }

    public void setMrp(Integer mrp) {
        this.mrp = mrp;
    }

    public Long getParentOrderItemId() {
        return parentOrderItemId;
    }

    public void setParentOrderItemId(Long parentOrderItemId) {
        this.parentOrderItemId = parentOrderItemId;
    }

    public OrderItemType getOrderItemType() {
        return orderItemType;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getCashbackAmount() {
        return cashbackAmount;
    }

    public void setCashbackAmount(BigDecimal cashbackAmount) {
        this.cashbackAmount = cashbackAmount;
    }

    public void setOrderItemType(OrderItemType orderItemType) {
        this.orderItemType = orderItemType;
    }

    public BigDecimal getPackagingPrice() {
        return packagingPrice;
    }

    public void setPackagingPrice(BigDecimal packagingPrice) {
        this.packagingPrice = packagingPrice;
    }
}