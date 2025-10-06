package com.buyer.dto.magicpin;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MerchantData {

    @JsonProperty("integration_partner_name")
    private String integrationPartnerName;
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("client_id2")
    private String clientId2;
    @JsonProperty("client_id3")
    private String clientId3;

    public MerchantData() {
    }

    public MerchantData(String integrationPartnerName, String clientId, String clientId2, String clientId3) {
        this.integrationPartnerName = integrationPartnerName;
        this.clientId = clientId;
        this.clientId2 = clientId2;
        this.clientId3 = clientId3;
    }

    public String getIntegrationPartnerName() {
        return integrationPartnerName;
    }

    public void setIntegrationPartnerName(String integrationPartnerName) {
        this.integrationPartnerName = integrationPartnerName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId2() {
        return clientId2;
    }

    public void setClientId2(String clientId2) {
        this.clientId2 = clientId2;
    }

    public String getClientId3() {
        return clientId3;
    }

    public void setClientId3(String clientId3) {
        this.clientId3 = clientId3;
    }


    @Override
    public String toString() {
        return "MerchantData{" +
                "integrationPartnerName='" + integrationPartnerName + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientId2='" + clientId2 + '\'' +
                ", clientId3='" + clientId3 + '\'' +
                '}';
    }
}
