package com.example.zoidonarapp;

public class modelHistory {

    public String remarks, datee, volume, status;

    public modelHistory() {

    }

    public modelHistory(String remarks, String datee, String volume, String status) {
        this.remarks = remarks;
        this.datee = datee;
        this.volume = volume;
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getDatee() {
        return datee;
    }

    public String getVolume() {
        return volume;
    }

    public String getStatus() {
        return status;
    }
}
