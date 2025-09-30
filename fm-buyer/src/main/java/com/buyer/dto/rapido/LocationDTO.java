package com.buyer.dto.rapido;

public class LocationDTO {
    private Double latitude;
    private Double longitude;
    private String address;
    private String pinCode;
    private String instructions;

    public LocationDTO() {
    }

    public LocationDTO(double latitude, double longitude, String address, String pinCode, String instructions) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.pinCode = pinCode;
        this.instructions = instructions;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    @Override
    public String toString() {
        return "LocationDTO{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                ", pinCode='" + pinCode + '\'' +
                ", instructions='" + instructions + '\'' +
                '}';
    }
}
