package com.cb.softwares.doctorapp.volley;

public class Constants {
    public static final String BOOKING_CONFIRMED = "booking_confirmed";
    public static final String BOOKING_REQUEST = "booking_request";
    public static final String BOOKING_CANCELLED = "booking_cancelled";

    public static String getServerURL(String mobileNumber, String appointmentType, String dateAndTime) {
        return "http://bloodambulance.com/bloodapp/php/sendSMS.php?mobile_number=" + mobileNumber + "&appointment_type=" + appointmentType + "&date_time= " + dateAndTime + "";
    }
}
