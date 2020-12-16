package com.giscen.gisredapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.Random;

public class RepartoService extends Service {

    private final IBinder mBinder = new RepartoBinder();
    private final Random mGenerator = new Random();

    public class RepartoBinder extends Binder {
        RepartoService getService() {
            return RepartoService.this;
        }
    }

    public RepartoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // Test service method
    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }
}
