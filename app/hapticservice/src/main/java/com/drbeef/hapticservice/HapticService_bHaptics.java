package com.drbeef.hapticservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class HapticService_bHaptics extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return new HapticServiceImpl_bHaptics(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {

        boolean result = super.onUnbind(intent);

        //Do this?!
        stopSelf();

        return result;
    }
}
