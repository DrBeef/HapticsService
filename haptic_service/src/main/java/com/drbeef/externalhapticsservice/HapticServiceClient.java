package com.drbeef.externalhapticsservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

import static com.drbeef.externalhapticsservice.StateConstants.STATE_DISCONNECTED;
import static com.drbeef.externalhapticsservice.StateConstants.STATE_NOT_INSTALLED;
import static com.drbeef.externalhapticsservice.StateConstants.STATE_NOT_READY;
import static com.drbeef.externalhapticsservice.StateConstants.STATE_OK;


public class HapticServiceClient implements ServiceConnection {
    public static final String TAG = LogUtils.makeLogTag(HapticServiceClient.class);

    private Context context;
    private List<HapticServiceCallback> callbacks;

    private boolean hasHapticService;
    private IHapticService hapticsService = null;

    private Intent serviceIntent = null;

    public HapticServiceClient(Context context, HapticServiceCallback callback, Intent serviceIntent) {
        callbacks = new ArrayList<>();
        callbacks.add(callback);
        this.context = context;
        this.serviceIntent = serviceIntent;

        boolean res = bindService();
        if (res) {
            notifyCallback(STATE_NOT_READY);
        } else {
            notifyCallback(STATE_NOT_INSTALLED);
        }
    }

    public boolean bindService() {
        return context.bindService(
                serviceIntent,
                this,
                Context.BIND_AUTO_CREATE
        );
    }

    public void stopBinding() {
        if (context == null) {
            return;
        }

        Log.d(TAG, "stopBinding ");
        try {
            context.stopService(serviceIntent);
        } catch (Exception e) {
            Log.e(TAG, "stopBinding: " + e.getMessage(), e);
        }
    }

    public boolean hasService() {
        return hasHapticService;
    }

    public IHapticService getHapticsService() {
        return hapticsService;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.i(TAG, "onServiceConnected " + name);
        hapticsService = IHapticService.Stub.asInterface(service);
        hasHapticService = true;
        notifyCallback(STATE_OK);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.i(TAG, "onServiceDisconnected" + name);
        stopBinding();

        hasHapticService = false;
        hapticsService = null;
        notifyCallback(STATE_DISCONNECTED);
    }
/*
    private Intent getServiceIntent() {
        return new Intent(HapticsConstants.HAPTICS_ACTION_FILTER)
                .setPackage(HapticsConstants.HAPTICS_PACKAGE);
    }
*/
    private void notifyCallback(int state) {
        for (HapticServiceCallback callback : callbacks) {
            callback.onBindChange(state, StateConstants.stateToDesc(state));
        }
    }

    public interface HapticServiceCallback {
        void onBindChange(int state, String description);
    }

}
