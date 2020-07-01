package com.cb.softwares.doctorapp.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AIzaSyDUP7jkmB6cc91m34KjWijEzLYgU2aIj-g"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
