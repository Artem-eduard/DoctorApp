package com.cb.softwares.doctorapp.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {

    private static String PREF_NAME = "docApp";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Context context;

    private String LOGIN_TYPE = "loginType";
    private String CURRENT_DATE = "currentDate";
    private String LASTMSGID = "lastMSGId";

    private String START_TIME = "startTime";
    private String END_TIME = "endTime";
    private String BREAK = "break";
    private String FCMKEY = "fcmKey";
    private String ISFCMKEYUPDATED = "isFCMKeyUpdated";
    private String DOCTORNAME = "name";
    private String DOCTORID = "doctorId";
    private String DOCTORUSERID = "doctorUserID";
    private String DOCTORTOKEN = "doctorToken";


    private String ISCOMPLETELY_SETUPED = "isCompletelySetuped";

    public SharedPreferenceManager(Context context) {
        this.context = context;

        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
    }


    public void setDoctorName(String name) {
        editor.putString(DOCTORNAME, name);
        editor.apply();
    }

    public void setDoctorID(String id) {
        editor.putString(DOCTORID, id);
        editor.apply();
    }

    public void setDoctorUserID(Long userid) {
        editor.putLong(DOCTORUSERID, userid);
        editor.apply();
    }

    public void setDoctorToken(String token) {
        editor.putString(DOCTORTOKEN, token);
        editor.apply();
    }

    public String getDoctorName() {
        return sharedPreferences.getString(DOCTORNAME, "");
    }

    public String getDoctorId() {
        return sharedPreferences.getString(DOCTORID, "");
    }

    public Long getDOCTORUSERID() {
        return sharedPreferences.getLong(DOCTORUSERID, 0);
    }

    public String getDoctorToken() {
        return sharedPreferences.getString(DOCTORTOKEN, "");
    }


    public void setFCMKEY(String key) {

        editor.putString(FCMKEY, key);
        editor.apply();
    }

    public void setISFCMKEYUPDATED(boolean val) {
        editor.putBoolean(ISFCMKEYUPDATED, val);
        editor.apply();
    }

    public void setISCOMPLETELY_SETUPED(boolean val) {
        editor.putBoolean(ISCOMPLETELY_SETUPED, val);
        editor.apply();
    }


    public boolean isCompletelySetuped() {
        return sharedPreferences.getBoolean(ISCOMPLETELY_SETUPED, false);
    }


    public String getFCMKey() {
        return sharedPreferences.getString(FCMKEY, "");
    }

    public boolean isFCMKeyUpdated() {
        return sharedPreferences.getBoolean(ISFCMKEYUPDATED, false);
    }


    public void setUserType(String type) {
        editor.putString(LOGIN_TYPE, type);
        editor.apply();

    }
    public void setCurrentDate(String currentDate) {
        editor.putString(CURRENT_DATE, currentDate);
        editor.apply();

    }
    public void setStartTime(String time) {
        editor.putString(START_TIME, time);
        editor.apply();
    }

    public void setEndTime(String time) {
        editor.putString(END_TIME, time);
        editor.apply();
    }

    public void setBreakTime(String time) {
        editor.putString(BREAK, time);
        editor.apply();
    }

    public  String getUserType() {
        return sharedPreferences.getString(LOGIN_TYPE, "empty");
    }

    public String getCurrentDate() {
        return sharedPreferences.getString(CURRENT_DATE, "");
    }

    public String getSTART_TIME() {
        return sharedPreferences.getString(START_TIME, "09:00 AM");
    }

    public String getEND_TIME() {
        return sharedPreferences.getString(END_TIME, "09:00 PM");
    }

    public String getBREAK_TIME() {
        return sharedPreferences.getString(BREAK, "02:03");
    }
}
