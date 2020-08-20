package com.whitehorse.deliverydriver.ui.gallery;

public class ListDataOP {
    private String date;
    private String time;
    private String cost;
    public ListDataOP(String date, String time, String cost) {
        this.date = date;
        this.time=time;
        this.cost=cost;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getCost() {
        return cost;
    }
    public void setCost(String cost) {
        this.cost = cost;
    }
}