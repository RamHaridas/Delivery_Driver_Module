package com.whitehorse.deliverydriver;

import com.google.firebase.firestore.GeoPoint;

public class OnlineDriver {

    String number;
    GeoPoint geoPoint;
    boolean isAssigned;

    public OnlineDriver(){}

    public String getNumber() {
        return number;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }
}
