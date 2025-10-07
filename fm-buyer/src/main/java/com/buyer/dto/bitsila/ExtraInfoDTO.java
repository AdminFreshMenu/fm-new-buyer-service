package com.buyer.dto.bitsila;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExtraInfoDTO implements Serializable {

    @JsonProperty("no_of_persons")
    private Integer noOfPersons;

    @JsonProperty("table_no")
    private String tableNo;

    @JsonProperty("flash_order")
    private Boolean flashOrder;

    // Getters and Setters


    public Integer getNoOfPersons() {
        return noOfPersons;
    }

    public void setNoOfPersons(Integer noOfPersons) {
        this.noOfPersons = noOfPersons;
    }

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public Boolean getFlashOrder() {
        return flashOrder;
    }

    public void setFlashOrder(Boolean flashOrder) {
        this.flashOrder = flashOrder;
    }
}