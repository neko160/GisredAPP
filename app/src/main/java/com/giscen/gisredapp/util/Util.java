package com.giscen.gisredapp.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import com.giscen.gisredapp.BuildConfig;

public class Util {
    public static final int REQUEST_READ_PHONE_STATE = 1001;
    public static final int REQUEST_ACCESS_FINE_LOCATION = 1002;
    public static final int REQUEST_CAMERA = 1003;
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 1004;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1005;



    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    public String formatCapitalize(String str) {
        String formatStr = str.replace("_", " ");
        String[] splitStr = formatStr.split(" ");
        String resp = "";

        if (splitStr.length > 0) {
            for (String s : splitStr) {
                String sTemp = s.toUpperCase();
                s = s.trim().replaceFirst("" + s.charAt(0), "" + sTemp.charAt(0));
                resp += s + " ";
            }
        }
        return resp.trim();
    }

    public static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
    public static String getImei(Context c) {
        TelephonyManager telephonyManager = (TelephonyManager) c.getSystemService( Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return telephonyManager.getDeviceId();
        }
        return telephonyManager.getDeviceId();
    }

    public static String getVersionPackage() {
        return String.format(" v%sc%s", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
    }

    public static void logout(Activity activity){
//      SharedPreferences preferencesl = getActivity().getSharedPreferences("GisRedPrefs", Context.MODE_PRIVATE);
        //SharedPreferences preferences = acgetSharedPreferences( "GisRedPrefs",Context.MODE_PRIVATE );
        SharedPreferences preferences = activity.getSharedPreferences( "GISREDPrefe", Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("username");
        editor.remove("password");
        editor.apply();
    }
}
