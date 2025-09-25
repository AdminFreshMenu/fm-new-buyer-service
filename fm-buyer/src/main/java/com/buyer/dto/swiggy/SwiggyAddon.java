package com.buyer.dto.swiggy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SwiggyAddon implements Serializable {

    private final static long serialVersionUID = 1L;

    @JsonProperty("id")
    private String id;

    @JsonProperty("sgst")
    private String sgst;

    @JsonProperty("sgst_percent")
    private String sgstPercent;

    @JsonProperty("cgst")
    private String cgst;


    @JsonProperty("cgst_percent")
    private String cgstPercent;

    @JsonProperty("igst")
    private String igst;

    @JsonProperty("igst_percent")
    private String igstPercent;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("price")
    private Float price;

    @JsonProperty("name")
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSgst() {
        return sgst;
    }

    public void setSgst(String sgst) {
        this.sgst = sgst;
    }

    public String getSgstPercent() {
        return sgstPercent;
    }

    public void setSgstPercent(String sgstPercent) {
        this.sgstPercent = sgstPercent;
    }

    public String getCgst() {
        return cgst;
    }

    public void setCgst(String cgst) {
        this.cgst = cgst;
    }

    public String getCgstPercent() {
        return cgstPercent;
    }

    public void setCgstPercent(String cgstPercent) {
        this.cgstPercent = cgstPercent;
    }

    public String getIgst() {
        return igst;
    }

    public void setIgst(String igst) {
        this.igst = igst;
    }

    public String getIgstPercent() {
        return igstPercent;
    }

    public void setIgstPercent(String igstPercent) {
        this.igstPercent = igstPercent;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
