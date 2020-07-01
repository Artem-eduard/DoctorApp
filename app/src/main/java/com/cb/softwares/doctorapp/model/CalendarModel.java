package com.cb.softwares.doctorapp.model;

public class CalendarModel {

    private String date, fullDate = "";
    private int imageId;
    private String imagePath;

    private  boolean isSelected;


    public CalendarModel(String date, int imageId) {
        this.date = date;
        this.imageId = imageId;
    }


    public CalendarModel(String date, String fullDate) {
        this.date = date;
        this.fullDate = fullDate;
    }

    public CalendarModel(String date, String fullDate, String imagePath) {
        this.date = date;
        this.fullDate = fullDate;
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public CalendarModel(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFullDate() {
        return fullDate;
    }

    public void setFullDate(String fullDate) {
        this.fullDate = fullDate;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
