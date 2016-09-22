package com.example.messagedemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by king on 2016/9/22.
 */
public class SmsSendService extends Service {
    public SmsSendService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

}
