package com.cb.softwares.doctorapp.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.cb.softwares.doctorapp.util.TimestampConverter;

import java.util.Date;


@Entity(tableName = "UserForChatDetails")
public class UserForChatDetails {
    @PrimaryKey
    @NonNull
    private String id;
    private String name, token, lastMsg;

    @TypeConverters({TimestampConverter.class})
    private Date time;
    private int UnreadCount;


    public UserForChatDetails(@NonNull String id, String name, String token, String lastMsg, int UnreadCount, Date time) {
        this.id = id;
        this.name = name;
        this.token = token;
        this.lastMsg = lastMsg;
        this.UnreadCount = UnreadCount;
        this.time = time;

    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public int getUnreadCount() {
        return UnreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        UnreadCount = unreadCount;
    }
}
