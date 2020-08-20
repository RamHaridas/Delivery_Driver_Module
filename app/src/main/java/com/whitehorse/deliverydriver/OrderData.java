package com.whitehorse.deliverydriver;

import com.google.firebase.firestore.GeoPoint;

public class OrderData {

    private String url1,url2,url3,url4;
    private String desc;
    private String weight;
    private String dimen;
    private GeoPoint pickup_location;
    private GeoPoint drop_location;
    private String date;
    private String time;
    private String rec_name;
    private String mobile_number;
    private String rec_address;
    private String driver;
    private String user;
    private boolean isAssigned;
    private boolean isCompleted;

    public OrderData(){}

    public boolean isCompleted() {
        return isCompleted;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public GeoPoint getPickup_location() {
        return pickup_location;
    }

    public GeoPoint getDrop_location() {
        return drop_location;
    }

    public String getUrl1() {
        return url1;
    }

    public String getUrl2() {
        return url2;
    }

    public String getUrl3() {
        return url3;
    }

    public String getUrl4() {
        return url4;
    }

    public String getDesc() {
        return desc;
    }

    public String getWeight() {
        return weight;
    }

    public String getDimen() {
        return dimen;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getRec_name() {
        return rec_name;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public String getRec_address() {
        return rec_address;
    }

    public String getDriver() {
        return driver;
    }

    public String getUser() {
        return user;
    }

    public void setUrl1(String url1) {
        this.url1 = url1;
    }

    public void setUrl2(String url2) {
        this.url2 = url2;
    }

    public void setUrl3(String url3) {
        this.url3 = url3;
    }

    public void setUrl4(String url4) {
        this.url4 = url4;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setDimen(String dimen) {
        this.dimen = dimen;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setRec_name(String rec_name) {
        this.rec_name = rec_name;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public void setRec_address(String rec_address) {
        this.rec_address = rec_address;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPickup_location(GeoPoint pickup_location) {
        this.pickup_location = pickup_location;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setDrop_location(GeoPoint drop_location) {
        this.drop_location = drop_location;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }
}
