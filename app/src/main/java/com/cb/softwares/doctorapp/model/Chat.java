package com.cb.softwares.doctorapp.model;

public class Chat {

    private String sender, receiver, message, time, key, type;
    private boolean isseen;

    private String senderLocalPath, receiverLocalPath;
    private String isDownloaded;
    private String isProgressing, isDownloading;

    private long id;

    private boolean isSelected = false;

    public Chat(String sender, String receiver, String message, boolean isssen, String time, long id, String key, String type) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isssen;
        this.time = time;
        this.id = id;
        this.key = key;
        this.type = type;

    }

    public Chat(String sender, String receiver, String message, String time, String key, String type, boolean isseen, String senderLocalPath, String isDownloaded, String isProgressing, long id, String isDownloading, String receiverLocalPath) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.time = time;
        this.key = key;
        this.type = type;
        this.isseen = isseen;
        this.senderLocalPath = senderLocalPath;
        this.isDownloaded = isDownloaded;
        this.isProgressing = isProgressing;
        this.id = id;
        this.isDownloading = isDownloading;
        this.receiverLocalPath = receiverLocalPath;
    }

    public String getSenderLocalPath() {
        return senderLocalPath;
    }

    public void setSenderLocalPath(String senderLocalPath) {
        this.senderLocalPath = senderLocalPath;
    }

    public String getReceiverLocalPath() {
        return receiverLocalPath;
    }

    public void setReceiverLocalPath(String receiverLocalPath) {
        this.receiverLocalPath = receiverLocalPath;
    }

    public String getIsDownloading() {
        return isDownloading;
    }

    public void setIsDownloading(String isDownloading) {
        this.isDownloading = isDownloading;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getIsDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(String isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public String getIsProgressing() {
        return isProgressing;
    }

    public void setIsProgressing(String isProgressing) {
        this.isProgressing = isProgressing;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
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


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
