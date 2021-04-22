package com.bhaptics.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class BhapticsServiceClient implements ServiceConnection {
    public static final String TAG = "bhaptics_log_SClient";

    private boolean hasHapticService;
    private BhapticsService hapticsService = null;

    private Context context;

    public BhapticsServiceClient(Context context) {
        this.context = context;
    }

    public boolean bindService() {
        try {
            boolean serviceExist = context.bindService(
                    getServiceIntent(),
                    this,
                    Context.BIND_AUTO_CREATE
            );
            Log.w(TAG, "bindService: " + serviceExist );

            return serviceExist;
        } catch (Exception e) {
            Log.e(TAG, "bindService: " + e.getMessage(), e);
        }

        return false;
    }

    public void stopBinding() {
        if (context == null) {
            return;
        }

        Log.d(TAG, "stopBinding ");
        try {
            context.stopService(getServiceIntent());
        } catch (Exception e) {
            Log.e(TAG, "stopBinding: " + e.getMessage(), e);
        }
    }


    public boolean hasService() {
        return hasHapticService;
    }

    public BhapticsService getHapticsService() {
        return hapticsService;
    }

    private Intent getServiceIntent() {
        return new Intent(HapticsConstants.BHAPTICS_ACTION_FILTER)
                .setPackage(HapticsConstants.BHAPTICS_PACKAGE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        Log.e(TAG, "onServiceConnected");
        hapticsService = HapticsServiceClientProxy.asInterface(binder);
        hasHapticService = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.e(TAG, "onServiceDisconnected");
        stopBinding();

        hasHapticService = false;
        hapticsService = null;
    }
}
