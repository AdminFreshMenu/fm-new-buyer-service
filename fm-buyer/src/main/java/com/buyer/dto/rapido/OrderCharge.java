package com.buyer.dto.rapido;

public class OrderCharge {
    private String title;
    private double value;


    public OrderCharge() {
    }

    public OrderCharge(String title, double value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "OrderCharge{" +
                "title='" + title + '\'' +
                ", value=" + value +
                '}';
    }
}
