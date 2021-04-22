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
