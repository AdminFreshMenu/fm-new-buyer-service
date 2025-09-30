package com.buyer.dto.rapido;

public class Payment {

    private String mode;
    private String status;
    private double amountPaid;
    private double amountBalance;

    public Payment() {
    }

    public Payment(String mode, String status, double amountPaid, double amountBalance) {
        this.mode = mode;
        this.status = status;
        this.amountPaid = amountPaid;
        this.amountBalance = amountBalance;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public double getAmountBalance() {
        return amountBalance;
    }

    public void setAmountBalance(double amountBalance) {
        this.amountBalance = amountBalance;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "mode='" + mode + '\'' +
                ", status='" + status + '\'' +
                ", amountPaid=" + amountPaid +
                ", amountBalance=" + amountBalance +
                '}';
    }
}
