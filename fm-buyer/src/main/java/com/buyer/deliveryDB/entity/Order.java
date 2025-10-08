package com.buyer.deliveryDB.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "`order`")
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private Integer orderNumber;

    @Column
    private String latitude;

    @Column
    private Long deliveryPersonId;

    @Column
    private String longitude;

    @Column
    private Timestamp deliveredAt;

    @Column
    private Long userAddressId;

    @Column
    private Long orderInvoiceId;

    @Column(name = "status")
    private String status;

    @Column(name = "kitchen_id")
    private Integer kitchenId;

    @Column(name = "cooking_list_id")
    private Integer cookingListId;

    @Column(name = "created_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime updatedAt;

    @Column(name = "addl_note")
    private String addlNote;

    @Column(name = "current_location", columnDefinition = "TEXT")
    private String currentLocation;

    @Column(name = "source")
    private String source;

    @Column(name = "track_url")
    private String trackUrl;

    @Column(name = "invoice_number", unique = true)
    private Integer invoiceNumber;

    @Column(name = "delivery_channel")
    private String deliveryChannel;

    @Column(name = "expected_delivery_time")
    private Timestamp expectedDeliveryTime;

    @Column(name = "sub_zone_id")
    private Long subZoneId;

    @Column(name = "short_code")
    private String shortCode;

    @Column(name = "trip_id")
    private Long tripId;

    @Column(name = "trip_order_seq")
    private Long tripOrderSeq;

    @Column(name = "brand_id")
    private Integer brandId = 1;

    @Column(name = "search_key", length = 512)
    private String searchKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Long getUserAddressId() {
        return userAddressId;
    }

    public void setUserAddressId(Long userAddressId) {
        this.userAddressId = userAddressId;
    }

    public Long getOrderInvoiceId() {
        return orderInvoiceId;
    }

    public void setOrderInvoiceId(Long orderInvoiceId) {
        this.orderInvoiceId = orderInvoiceId;
    }

    public Long getDeliveryPersonId() {
        return deliveryPersonId;
    }

    public void setDeliveryPersonId(Long deliveryPersonId) {
        this.deliveryPersonId = deliveryPersonId;
    }

    public Timestamp getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Timestamp deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getKitchenId() {
        return kitchenId;
    }

    public void setKitchenId(Integer kitchenId) {
        this.kitchenId = kitchenId;
    }

    public Integer getCookingListId() {
        return cookingListId;
    }

    public void setCookingListId(Integer cookingListId) {
        this.cookingListId = cookingListId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAddlNote() {
        return addlNote;
    }

    public void setAddlNote(String addlNote) {
        this.addlNote = addlNote;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTrackUrl() {
        return trackUrl;
    }

    public void setTrackUrl(String trackUrl) {
        this.trackUrl = trackUrl;
    }

    public Integer getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(Integer invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getDeliveryChannel() {
        return deliveryChannel;
    }

    public void setDeliveryChannel(String deliveryChannel) {
        this.deliveryChannel = deliveryChannel;
    }

    public Timestamp getExpectedDeliveryTime() {
        return expectedDeliveryTime;
    }

    public void setExpectedDeliveryTime(Timestamp expectedDeliveryTime) {
        this.expectedDeliveryTime = expectedDeliveryTime;
    }

    public Long getSubZoneId() {
        return subZoneId;
    }

    public void setSubZoneId(Long subZoneId) {
        this.subZoneId = subZoneId;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Long getTripOrderSeq() {
        return tripOrderSeq;
    }

    public void setTripOrderSeq(Long tripOrderSeq) {
        this.tripOrderSeq = tripOrderSeq;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNumber=" + orderNumber +
                ", latitude='" + latitude + '\'' +
                ", deliveryPersonId=" + deliveryPersonId +
                ", longitude='" + longitude + '\'' +
                ", deliveredAt=" + deliveredAt +
                ", userAddressId=" + userAddressId +
                ", orderInvoiceId=" + orderInvoiceId +
                ", status='" + status + '\'' +
                ", kitchenId=" + kitchenId +
                ", cookingListId=" + cookingListId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", addlNote='" + addlNote + '\'' +
                ", currentLocation='" + currentLocation + '\'' +
                ", source='" + source + '\'' +
                ", trackUrl='" + trackUrl + '\'' +
                ", invoiceNumber=" + invoiceNumber +
                ", deliveryChannel='" + deliveryChannel + '\'' +
                ", expectedDeliveryTime=" + expectedDeliveryTime +
                ", subZoneId=" + subZoneId +
                ", shortCode='" + shortCode + '\'' +
                ", tripId=" + tripId +
                ", tripOrderSeq=" + tripOrderSeq +
                ", brandId=" + brandId +
                ", searchKey='" + searchKey + '\'' +
                '}';
    }
}