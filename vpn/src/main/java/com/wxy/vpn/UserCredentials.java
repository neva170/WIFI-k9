package com.wxy.vpn;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bers on 20.03.17.
 */

public final class UserCredentials {
    @SerializedName("full_name")
    String fullName;
    String country;
    String state;
    String city;
    String address1, address2;
    @SerializedName("mailcode")
    String zipCode;


    public UserCredentials(String fullName, String country, String state, String city, String address1, String address2, String zipCode) {
        this.fullName = fullName;
        this.country = country;
        this.state = state;
        this.city = city;
        this.address1 = address1;
        this.address2 = address2;
        this.zipCode = zipCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
