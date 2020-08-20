package com.whitehorse.deliverydriver.ui.home;

public class ListDataAO {
    private String pick_up;
    private String time;
    private String status;
    public ListDataAO(String pick_up, String time, String status) {
        this.pick_up = pick_up;
        this.time=time;
        this.status=status;
    }
    public String getPickup() {
        return pick_up;
    }
    public void setPickup(String pick_up) {
        this.pick_up = pick_up;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
