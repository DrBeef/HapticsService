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

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override protected void onCreate( Bundle icicle )
    {
        super.onCreate( icicle );

        //Literally just ask for permission for this application
        bHaptics.requestPermissions(this);
    }

    /** Handles the user accepting the permission. */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {

        if (requestCode == bHaptics.BHAPTICS_PERMISSION_REQUEST) {
            //Quit for now
            finish();
            System.exit(0);
        }
    }
}
