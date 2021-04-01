/*
   Copyright 2021 Simon Brown (DrBeef)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.drbeef.hapticservice;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.drbeef.hapticsservice.IHapticsService;

public class HapticServiceImpl_bHaptics extends IHapticsService.Stub {

    private Context context;

    private Object mutex = new Object();

    HapticServiceImpl_bHaptics(Context context)
    {
        this.context = context;
    }

    @Override
    public void hapticEvent(String a, String s, int i, int i1, int i2, float v, float v1) throws RemoteException {
        synchronized (mutex) {
            Log.d(bHaptics.TAG, "HapticEvent: Thread(" + Thread.currentThread().getId() + "), " + a + ", " + s + ", " + i + ", " + i1 + ", " + i2 + ", " + v + ", " + v1);
            bHaptics.playHaptic(a, s, i, i1, i2, v, v1);
        }
    }

    @Override
    public void hapticUpdateEvent(String a, String s, int i, float v) throws RemoteException {
        synchronized (mutex) {
            Log.d(bHaptics.TAG, "HapticUpdateEvent: Thread(" + Thread.currentThread().getId() + "), " + a + ", " + s + ", " + i + ", " + v);
            bHaptics.updateRepeatingHaptic(a, s, i, v);
        }
    }

    @Override
    public void hapticStopEvent(String a, String s) throws RemoteException {
        synchronized (mutex) {
            Log.d(bHaptics.TAG, "HapticStopEvent: Thread(" + Thread.currentThread().getId() + "), " + a + ", " + s);
            bHaptics.stopHaptic(a, s);
        }
    }

    @Override
    public void hapticFrameTick() throws RemoteException {
        synchronized (mutex) {
            Log.d(bHaptics.TAG, "HapticFrameTick: Thread(" + Thread.currentThread().getId() + ")");
            bHaptics.frameTick();
        }
    }

    @Override
    public void hapticEnable() throws RemoteException {
        synchronized (mutex) {
            Log.d(bHaptics.TAG, "HapticEnable: Thread(" + Thread.currentThread().getId() + ")");
            bHaptics.enable(context);
        }
    }

    @Override
    public void hapticDisable() throws RemoteException {
        synchronized (mutex) {
            Log.d(bHaptics.TAG, "HapticDisable: Thread(" + Thread.currentThread().getId() + ")");
            bHaptics.disable();
        }
    }

    public void shutdown()
    {
        bHaptics.destroy();
    }
}
