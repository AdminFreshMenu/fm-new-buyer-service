package com.buyer.dto.swiggy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Variant implements Serializable {

    private final static long serialVersionUID = -3844511570120749985L;
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("price")
    private Long price;
    @JsonProperty("default")
    private Boolean _default;
    @JsonProperty("order")
    private Long order;
    @JsonProperty("in_stock")
    private Boolean inStock;
    @JsonProperty("is_veg")
    private Boolean isVeg;
    @JsonProperty("gst_details")
    private Object gstDetails;
    @JsonProperty("default_dependent_variant_id")
    private String defaultDependentVariantId;
    @JsonProperty("default_dependent_variant_group_id")
    private String defaultDependentVariantGroupId;

    /**
     * No args constructor for use in serialization
     */
    public Variant() {
    }

    /**
     * @param id
     * @param gstDetails
     * @param isVeg
     * @param price
     * @param order
     * @param _default
     * @param name
     * @param defaultDependentVariantId
     * @param inStock
     * @param defaultDependentVariantGroupId
     */
    public Variant(String id, String name, Long price, Boolean _default, Long order, Boolean inStock, Boolean isVeg, Object gstDetails, Object defaultDependentVariantId, Object defaultDependentVariantGroupId) {
        super();
        this.id = id;
        this.name = name;
        this.price = price;
        this._default = _default;
        this.order = order;
        this.inStock = inStock;
        this.isVeg = isVeg;
        this.gstDetails = gstDetails;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Boolean getDefault() {
        return _default;
    }

    public void setDefault(Boolean _default) {
        this._default = _default;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }

    public Boolean getIsVeg() {
        return isVeg;
    }

    public void setIsVeg(Boolean isVeg) {
        this.isVeg = isVeg;
    }

    public Object getGstDetails() {
        return gstDetails;
    }

    public void setGstDetails(Object gstDetails) {
        this.gstDetails = gstDetails;
    }

    public String getDefaultDependentVariantId() {
        return defaultDependentVariantId;
    }

    public void setDefaultDependentVariantId(String defaultDependentVariantId) {
        this.defaultDependentVariantId = defaultDependentVariantId;
    }

    public Object getDefaultDependentVariantGroupId() {
        return defaultDependentVariantGroupId;
    }

    public void setDefaultDependentVariantGroupId(String defaultDependentVariantGroupId) {
        this.defaultDependentVariantGroupId = defaultDependentVariantGroupId;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Variant{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", price=").append(price);
        sb.append(", _default=").append(_default);
        sb.append(", order=").append(order);
        sb.append(", inStock=").append(inStock);
        sb.append(", isVeg=").append(isVeg);
        sb.append(", gstDetails=").append(gstDetails);
        sb.append(", defaultDependentVariantId=").append(defaultDependentVariantId);
        sb.append(", defaultDependentVariantGroupId=").append(defaultDependentVariantGroupId);
        sb.append('}');
        return sb.toString();
    }
}
