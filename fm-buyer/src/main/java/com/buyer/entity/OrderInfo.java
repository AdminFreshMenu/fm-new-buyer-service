package com.buyer.entity;

import com.buyer.dto.TaxConvertor;
import com.buyer.dto.TaxDTO;
import com.buyer.entity.OrderEnum.Channel;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_info")
@DynamicUpdate
@DynamicInsert
public class OrderInfo extends AbstractMutableEntity {

    /**
     *
     */

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String externalOrderId;

    @Embedded
    private OrderUserInfo user;

    @ManyToOne
    @JoinColumn(name = "shipping_address")
    private OrderAddress shippingAddress;
    
    @ManyToOne
    @JoinColumn(name = "billing_address")
    private OrderAddress billingAddress;

    @Column
    private Float totalAmount;

    @Column
    private Float finalAmount;

    @Column
    private Float offerAmount;

    @Column
    private BigDecimal cartLevelDiscount;

    @Column
    private Float shippingCharges;
    
    @Column
    private Long offerId;

    @Column
    private String offerCode;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String orderData;

    @Column
    private Float vat;

    @Column
    private Float serviceTax;


    @Column
    private BigDecimal cgst;

    @Column
    private BigDecimal sgst;


    @Column
    private BigDecimal promoBalance=BigDecimal.ZERO;

    @Column
    private BigDecimal bankOffer=BigDecimal.ZERO;


    @Column
    private Integer amountToBeCollected;

    @Column(columnDefinition = "DATETIME(0)")
    private LocalDateTime deliveryTime;

    @Column
    private Integer status;

    @Column
    private Integer denominationRequired;

    @Enumerated(EnumType.STRING)
    @Column
    private Channel channel;

    @Column
    private Long kitchenId;

    @Column
    private BigDecimal cgstRate=null;

    @Column
    private BigDecimal sgstRate=null;

    @Column
    private String orderStatusShortUrl;

    @Column
    private String deliveryTimeInMinutes;

    @Column
    private BigDecimal packagingCharges = BigDecimal.ZERO;

    @Column
    private Integer brandId;

    @Column
    private BigDecimal productDiscount = BigDecimal.ZERO;

    @Column
    @Convert(converter = TaxConvertor.class)
    private TaxDTO taxDTO;

    public Float getShippingCharges() {
		return shippingCharges;
	}

	public void setShippingCharges(Float shippingCharges) {
		this.shippingCharges = shippingCharges;
	}

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Float getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Float finalAmount) {
        this.finalAmount = finalAmount;
    }

    public Float getVat() {
        return vat;
    }

    public void setVat(Float vat) {
        this.vat = vat;
    }

    public Float getServiceTax() {
        return serviceTax;
    }

    public void setServiceTax(Float serviceTax) {
        this.serviceTax = serviceTax;
    }

    public Integer getAmountToBeCollected() {
        return amountToBeCollected;
    }

    public void setAmountToBeCollected(Integer amountToBeCollected) {
        this.amountToBeCollected = amountToBeCollected;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public OrderUserInfo getUser() {
        return user;
    }

    public void setUser(OrderUserInfo user) {
        this.user = user;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public OrderAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(OrderAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public OrderAddress getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(OrderAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getExternalOrderId() {
        return externalOrderId;
    }

    public void setExternalOrderId(String externalOrderId) {
        this.externalOrderId = externalOrderId;
    }

    public Float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public Float getOfferAmount() {
        return offerAmount;
    }

    public void setOfferAmount(Float offerAmount) {
        this.offerAmount = offerAmount;
    }

    public String getOfferCode() {
        return offerCode;
    }

    public void setOfferCode(String offerCode) {
        this.offerCode = offerCode;
    }

    public String getOrderData() {
        return orderData;
    }

    public void setOrderData(String orderData) {
        this.orderData = orderData;
    }

    public Integer getDenominationRequired() {
        return denominationRequired;
    }

    public void setDenominationRequired(Integer denominationRequired) {
        this.denominationRequired = denominationRequired;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Long getKitchenId() {
        return kitchenId;
    }

    public void setKitchenId(Long kitchenId) {
        this.kitchenId = kitchenId;
    }

    public BigDecimal getCgst() {
        return cgst;
    }

    public void setCgst(BigDecimal cgst) {
        this.cgst = cgst;
    }

    public BigDecimal getSgst() {
        return sgst;
    }

    public void setSgst(BigDecimal sgst) {
        this.sgst = sgst;
    }

    public BigDecimal getCartLevelDiscount() {
        return cartLevelDiscount;
    }

    public void setCartLevelDiscount(BigDecimal cartLevelDiscount) {
        this.cartLevelDiscount = cartLevelDiscount;
    }

    public BigDecimal getPromoBalance() {
        return promoBalance;
    }

    public void setPromoBalance(BigDecimal promoBalance) {
        this.promoBalance = promoBalance;
    }

    public BigDecimal getCgstRate() {
        return cgstRate;
    }

    public void setCgstRate(BigDecimal cgstRate) {
        this.cgstRate = cgstRate;
    }

    public BigDecimal getSgstRate() {
        return sgstRate;
    }

    public void setSgstRate(BigDecimal sgstRate) {
        this.sgstRate = sgstRate;
    }

    public BigDecimal getBankOffer() {
        return bankOffer;
    }

    public void setBankOffer(BigDecimal bankOffer) {
        this.bankOffer = bankOffer;
    }

    public String getOrderStatusShortUrl() {
        return orderStatusShortUrl;
    }

    public void setOrderStatusShortUrl(String orderStatusShortUrl) {
        this.orderStatusShortUrl = orderStatusShortUrl;
    }

    public String getDeliveryTimeInMinutes() {
        return deliveryTimeInMinutes;
    }

    public void setDeliveryTimeInMinutes(String deliveryTimeInMinutes) {
        this.deliveryTimeInMinutes = deliveryTimeInMinutes;
    }
    public BigDecimal getPackagingCharges() {
        return packagingCharges;
    }

    public void setPackagingCharges(BigDecimal packagingCharges) {
        this.packagingCharges = packagingCharges;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public BigDecimal getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(BigDecimal productDiscount) {
        this.productDiscount = productDiscount;
    }

    public TaxDTO getTaxDTO() {
        return taxDTO;
    }

    public void setTaxDTO(TaxDTO taxDTO) {
        this.taxDTO = taxDTO;
    }
}