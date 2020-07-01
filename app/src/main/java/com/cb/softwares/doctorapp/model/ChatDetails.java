package com.cb.softwares.doctorapp.model;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.cb.softwares.doctorapp.util.TimestampConverter;

import java.util.Date;

@Entity(tableName = "ChatDetails")
public class ChatDetails {


    private String sender, receiver, message;
    private boolean isseen;


    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int autoId;

    @ColumnInfo(name = "time")
    @TypeConverters({TimestampConverter.class})
    private Date time;

    public ChatDetails(String sender, String receiver, String message, boolean isseen, Date time, long id) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
        this.time = time;
        this.id = id;
    }

    public int getAutoId() {
        return autoId;
    }

    public void setAutoId(int autoId) {
        this.autoId = autoId;
    }

    private long id;


    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
