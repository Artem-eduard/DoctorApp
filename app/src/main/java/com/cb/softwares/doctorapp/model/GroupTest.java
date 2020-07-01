package com.cb.softwares.doctorapp.model;

public class GroupTest {

    String name, type;

    public GroupTest(String name, String type) {
        this.name = name;
        this.type = type;
    }


    public GroupTest() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
