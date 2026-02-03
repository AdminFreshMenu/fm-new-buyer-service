package com.buyer.entity.MongoDB.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopifyAddress {
    private String name;
    private String first_name;
    private String last_name;
    private String phone;
    private String zip;
    private String label;
    private String address1;
    private String province;
    private String city;
    private String latitude;
    private String longitude;
    private String oldLatitude;
    private String oldLongitude;
    private String landmark;
    private Long cityId;
    private String latForSubzone;
    private String lonForSubzone;
    private Long stateId;
}
