package com.buyer.dto.rapido;

public class OrderTax {

    private String title;
    private double value;
    private double percentage;

    public OrderTax() {
    }

    public OrderTax(String title, double value, double percentage) {
        this.title = title;
        this.value = value;
        this.percentage = percentage;
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

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "OrderTax{" +
                "title='" + title + '\'' +
                ", value=" + value +
                ", percentage=" + percentage +
                '}';
    }
}
