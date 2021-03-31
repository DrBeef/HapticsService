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

        if (requestCode == 2) {
            //Quit for now
            finish();
            System.exit(0);
        }
    }
}
