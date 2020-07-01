package com.cb.softwares.doctorapp.util;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

/**
 * Created by Vijay on 24-10-2016.
 */

public class MyApplication extends Application {
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }



    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(checkInternet.ConnectivityReceiverListener listener) {
        checkInternet.connectivityReceiverListener = listener;
    }


}
