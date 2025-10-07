package com.buyer.dto.bitsila;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Response {

    private static final long serialVersionUID = 1L;

    private Integer code;
    private String status;
    private String message;
    private List<String> errors;
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("request_id")
    private String requestId;

    public Response() {}

    public Response(String message, String orderId, String status, boolean success) {
        this.message = message;
        this.orderId = orderId;
        this.status = status;
    }

    public Response(Response response) {
        this.code = response.getCode();
        this.status = response.getStatus();
        this.message = response.getMessage();
        this.errors = response.getErrors();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
