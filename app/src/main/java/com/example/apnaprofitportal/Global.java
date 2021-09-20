package com.example.apnaprofitportal;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Global {
    public static String urls;

    public static String name, desc, price, sideeffects, dosage, usage, safetyinfo, manufacturer, habit, alternatives, type, drugs;
    public static String posi;
    public static String status;


    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return false;

                    }
                }
            }
        }

        return true;


    }

}
