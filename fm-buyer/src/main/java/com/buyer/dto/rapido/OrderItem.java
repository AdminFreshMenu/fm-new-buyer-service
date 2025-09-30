package com.buyer.dto.rapido;

import java.util.List;

public class OrderItem {
    private String itemId; //d
    private String name; //d
    private int quantity; //d
    private String description; //d
    private double unitPrice; //d
    private List<OrderTax> taxes; //d
    private List<OrderCharge> charges; //d
    private String instruction; //d
    private OrderVariant variants;
    private List<ItemAddon> addOns;


    public OrderItem() {
    }

    public OrderItem(String itemId, String name, int quantity, String description, double unitPrice, List<OrderTax> taxes, List<OrderCharge> charges, String instruction, OrderVariant variants, List<ItemAddon> addOns) {
        this.itemId = itemId;
        this.name = name;
        this.quantity = quantity;
        this.description = description;
        this.unitPrice = unitPrice;
        this.taxes = taxes;
        this.charges = charges;
        this.instruction = instruction;
        this.variants = variants;
        this.addOns = addOns;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public List<OrderTax> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<OrderTax> taxes) {
        this.taxes = taxes;
    }

    public List<OrderCharge> getCharges() {
        return charges;
    }

    public void setCharges(List<OrderCharge> charges) {
        this.charges = charges;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public OrderVariant getVariants() {
        return variants;
    }

    public void setVariants(OrderVariant variants) {
        this.variants = variants;
    }

    public List<ItemAddon> getAddOns() {
        return addOns;
    }

    public void setAddOns(List<ItemAddon> addOns) {
        this.addOns = addOns;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "itemId='" + itemId + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", description='" + description + '\'' +
                ", unitPrice=" + unitPrice +
                ", taxes=" + taxes +
                ", charges=" + charges +
                ", instruction='" + instruction + '\'' +
                ", variants=" + variants +
                ", addOns=" + addOns +
                '}';
    }
}
