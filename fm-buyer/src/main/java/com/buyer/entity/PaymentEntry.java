package com.buyer.entity;

import com.buyer.dto.MapToJsonConverter;
import com.buyer.dto.PaymentUserInfo;
import com.buyer.entity.PaymentEnum.PaymentFor;
import com.buyer.entity.PaymentEnum.PaymentGateway;
import com.buyer.entity.PaymentEnum.PaymentMethod;
import com.buyer.entity.PaymentEnum.PaymentMode;
import com.buyer.entity.PaymentEnum.PaymentStatus;
import com.buyer.entity.PaymentEnum.RefundForm;
import com.buyer.entity.PaymentEnum.TransactionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.joda.time.DateTime;

import java.util.Map;

@Entity
@DynamicUpdate
@DynamicInsert
public class PaymentEntry extends AbstractMutableEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private PaymentUserInfo user;

    @Column
    private Long orderId;

    @Column
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private PaymentMode paymentMode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private PaymentFor paymentFor = PaymentFor.ORDER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private RefundForm refundForm;

    @Column()
    private DateTime refundedAt;

    @Column
    private String amount;

    @Column
    private Double refundedAmount;


    @Column
    private String pgTransactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private TransactionStatus transactionStatus;

    @Column
    private String transactionMsg;

    @Column
    private String pgTxRefNo;

    @Column
    private String pgTxResponse;

    @Column
    private String refundResponse;

    @Enumerated(EnumType.STRING)
    private PaymentGateway paymentGateway;

    private Long cartId;

    private String paymentLink;

    private Integer brandId;


    @Column(columnDefinition = "LONGTEXT")
    @Convert(converter = MapToJsonConverter.class)
    private Map<String, String> data;

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String getPgTxResponse() {
        return pgTxResponse;
    }

    public void setPgTxResponse(String pgTxResponse) {
        this.pgTxResponse = pgTxResponse;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPgTransactionId() {
        return pgTransactionId;
    }

    public void setPgTransactionId(String pgTransactionId) {
        this.pgTransactionId = pgTransactionId;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionMsg() {
        return transactionMsg;
    }

    public void setTransactionMsg(String transactionMsg) {
        this.transactionMsg = transactionMsg;
    }

    public String getPgTxRefNo() {
        return pgTxRefNo;
    }

    public void setPgTxRefNo(String pgTxRefNo) {
        this.pgTxRefNo = pgTxRefNo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public PaymentGateway getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public PaymentUserInfo getUser() {
        return user;
    }

    public void setUser(PaymentUserInfo user) {
        this.user = user;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public String getRefundResponse() {
        return refundResponse;
    }

    public void setRefundResponse(String refundResponse) {
        this.refundResponse = refundResponse;
    }

    public Double getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(Double refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public RefundForm getRefundForm() {
        return refundForm;
    }

    public void setRefundForm(RefundForm refundForm) {
        this.refundForm = refundForm;
    }

    public DateTime getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(DateTime refundedAt) {
        this.refundedAt = refundedAt;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentLink() {
        return paymentLink;
    }

    public void setPaymentLink(String paymentLink) {
        this.paymentLink = paymentLink;
    }

    public PaymentFor getPaymentFor() {
        return paymentFor;
    }

    public void setPaymentFor(PaymentFor paymentFor) {
        this.paymentFor = paymentFor;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

}
