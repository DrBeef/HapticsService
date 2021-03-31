package com.drbeef.hapticservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class HapticService_bHaptics extends Service {

    private HapticServiceImpl_bHaptics bHapticsServiceImpl = null;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(bHaptics.TAG, "onBind");

        if (bHapticsServiceImpl == null) {
            bHapticsServiceImpl = new HapticServiceImpl_bHaptics(this);
        }

        return bHapticsServiceImpl;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(bHaptics.TAG, "onUnbind");

        //shut down
        bHapticsServiceImpl.shutdown();
        bHapticsServiceImpl = null;

        return super.onUnbind(intent);
    }
}
