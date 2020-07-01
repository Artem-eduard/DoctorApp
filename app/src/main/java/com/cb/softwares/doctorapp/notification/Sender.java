package com.cb.softwares.doctorapp.notification;

public class Sender {

    private Data data;
    public String to;
    public Notification notification;

    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }


    public Sender(Data data, String to, Notification notification) {
        this.data = data;
        this.to = to;
        this.notification = notification;

    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
