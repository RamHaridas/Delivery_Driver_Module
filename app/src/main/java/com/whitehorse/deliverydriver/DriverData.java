package com.whitehorse.deliverydriver;

public class DriverData {

    String name;
    String email;
    String address;
    String phone;
    String number_plate;
    String profile_url;
    String password;
    String vehicle_type;

    public DriverData(){}

    public String getVehicle_type() {
        return vehicle_type;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getNumber_plate() {
        return number_plate;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setNumber_plate(String number_plate) {
        this.number_plate = number_plate;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }
}
