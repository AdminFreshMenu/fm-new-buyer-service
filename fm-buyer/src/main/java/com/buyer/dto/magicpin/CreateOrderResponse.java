package com.buyer.dto.magicpin;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateOrderResponse {


    @JsonProperty("status")
    private Status status;
    @JsonProperty("message")
    private String message;
    @JsonProperty("httpStatus")
    private Integer httpStatus;
    @JsonProperty("thirdPartyOrderId")
    private String thirdPartyOrderId;

    public CreateOrderResponse() {
    }

    public CreateOrderResponse(Status status, String message, Integer httpStatus, String thirdPartyOrderId) {
        this.status = status;
        this.message = message;
        this.httpStatus = httpStatus;
        this.thirdPartyOrderId = thirdPartyOrderId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getThirdPartyOrderId() {
        return thirdPartyOrderId;
    }

    public void setThirdPartyOrderId(String thirdPartyOrderId) {
        this.thirdPartyOrderId = thirdPartyOrderId;
    }

    @Override
    public String toString() {
        return "CreateOrderResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", httpStatus=" + httpStatus +
                ", thirdPartyOrderId='" + thirdPartyOrderId + '\'' +
                '}';
    }
}
