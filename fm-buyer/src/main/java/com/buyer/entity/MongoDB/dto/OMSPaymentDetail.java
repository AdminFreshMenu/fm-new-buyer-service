package com.buyer.entity.MongoDB.dto;

import com.buyer.entity.PaymentEnum.PaymentMethod;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "orders")
@EqualsAndHashCode(exclude = "orders")public class OMSPaymentDetail {
    private String amount;

    private String paymentGateway;

    private String paymentMode;

    private String transactionId;

    private String pgTransactionId;

    private PaymentMethod paymentMethod;
}
