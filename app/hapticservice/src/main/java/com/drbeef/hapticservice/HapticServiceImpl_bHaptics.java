package com.drbeef.hapticservice;

import android.content.Context;
import android.os.RemoteException;

import com.drbeef.hapticsservice.IHapticsService;

public class HapticServiceImpl_bHaptics extends IHapticsService.Stub {

    private Context context;

    HapticServiceImpl_bHaptics(Context context)
    {
        this.context = context;
    }

    @Override
    public void hapticEvent(String a, String s, int i, int i1, int i2, float v, float v1) throws RemoteException {
        bHaptics.playHaptic(a, s, i, i1, i2, v, v1);
    }

    @Override
    public void hapticUpdateEvent(String a, String s, int i, float v) throws RemoteException {
        bHaptics.updateRepeatingHaptic(a, s, i, v);
    }

    @Override
    public void hapticStopEvent(String a, String s) throws RemoteException {
        bHaptics.stopHaptic(a, s);
    }

    @Override
    public void hapticFrameTick() throws RemoteException {
        bHaptics.frameTick();
    }

    @Override
    public void hapticEnable() throws RemoteException {
        bHaptics.enable(context);
    }

    @Override
    public void hapticDisable() throws RemoteException {
        bHaptics.disable();
    }
}
