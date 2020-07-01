package com.cb.softwares.doctorapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TagCreation extends BaseObservable implements Parcelable {

    private String hospitalName, hospitalAddress;
    private String startTime, endTime;

    private String formatDate = "";

    private String id;

    private String dates;


    public TagCreation() {
    }

    public TagCreation(String id, String hospitalName, String hospitalAddress, String startTime, String endTime, String dates) {
        this.hospitalName = hospitalName;
        this.hospitalAddress = hospitalAddress;
        this.startTime = startTime;
        this.endTime = endTime;
        this.id = id;
        this.dates = dates;
        this.formatDate = getAllDates();
    }

    public TagCreation(Parcel in) {
    }


    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getHospitalAddress() {
        return hospitalAddress;
    }

    public void setHospitalAddress(String hospitalAddress) {
        this.hospitalAddress = hospitalAddress;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    public void onHospitalNameTextChanged(CharSequence text) {
        this.hospitalName = text.toString();
    }

    public void onHospitalAddressChanged(CharSequence text) {
        this.hospitalAddress = text.toString();
    }


    public void onStartTimeChanged(CharSequence text) {
        this.startTime = text.toString();
    }

    public void onEddTimeChanged(CharSequence text) {
        this.endTime = text.toString();
    }


    public String getAllDates() {


        String myDates = "";
        try {
            JSONArray array = new JSONArray(dates);
            for (int i = array.length() - 1; i >= 0; i--) {

                JSONObject object = array.getJSONObject(i);
                myDates += "  " + object.getString("dates");
            }


        } catch (JSONException e) {
        }


        return myDates.trim();
    }


    public String getFormatDate() {
        return formatDate;
    }

    public void setFormatDate(String formatDate) {
        this.formatDate = formatDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TagCreation createFromParcel(Parcel in) {
            return new TagCreation(in);
        }

        public TagCreation[] newArray(int size) {
            return new TagCreation[size];
        }
    };
    
    
}
