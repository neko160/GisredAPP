package com.giscen.gisredapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

public class CheckNetwork {

    private Context mContext;

  /*  public CheckNetwork(Context context) {
        this.mContext = context;
    }
*/
    public CheckNetwork(Context context) {
        this.mContext = context;
    }


    /**
     * Checking for all possible internet providers
     * **/
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        }else {
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {

                            return true;
                        }
                    }
                }
            }
        }
        //Toast.makeText(mContext,mContext.getString(R.string.please_connect_to_internet),Toast.LENGTH_SHORT).show();
        return false;
    }


}
