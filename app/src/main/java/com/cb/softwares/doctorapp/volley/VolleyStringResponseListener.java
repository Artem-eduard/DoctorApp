package com.cb.softwares.doctorapp.volley;

import com.android.volley.VolleyError;

public interface VolleyStringResponseListener {
    void onResponse(String response);

    void onError(VolleyError volleyError);
}
