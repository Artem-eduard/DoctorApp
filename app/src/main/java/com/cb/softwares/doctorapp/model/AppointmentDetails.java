package com.cb.softwares.doctorapp.model;

public class AppointmentDetails {


    private String id, name, date, age, gender, time, userid, mobile,hospitalName;
    private boolean isConfirmedByDoctor;

    public AppointmentDetails(String id, String name, String date, String age, String gender) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.age = age;
        this.gender = gender;
    }

    public AppointmentDetails() {
    }

    public AppointmentDetails(String id, String name, String date, String age, String gender, String time, String userid, boolean isConfirmedByDoctor, String mobile, String hospitalName) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.age = age;
        this.gender = gender;
        this.time = time;
        this.userid = userid;
        this.isConfirmedByDoctor = isConfirmedByDoctor;
        this.mobile = mobile;
        this.hospitalName = hospitalName;
    }


    public void setisConfirmedByDoctor(boolean isConfirmedByDoctor) {
        this.isConfirmedByDoctor = isConfirmedByDoctor;
    }

    public boolean getisConfirmedByDoctor() {
        return isConfirmedByDoctor;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTime() {
        return time;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }
}
