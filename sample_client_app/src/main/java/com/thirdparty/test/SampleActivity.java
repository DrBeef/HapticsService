package com.thirdparty.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drbeef.externalhapticsservice.HapticServiceClient;
import com.drbeef.externalhapticsservice.IHapticService;

public class SampleActivity extends AppCompatActivity {
    public static final String TAG = "bhaptics_activity";

    private LinearLayout layoutParent;
    private TextView statusText;
    private Button testButton;

    private HapticServiceClient hapticClient;


    private String hapticServiceStatus = "";

    private void updateText() {
        runOnUiThread(() -> {
            if (statusText != null) {
                statusText.setText(
                        hapticServiceStatus
                );
            }
        });
    }

    private View.OnClickListener clickListener = v -> {
        if (v.getId() == R.id.testButton) {
            handTest();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusText = findViewById(R.id.textStatus);
        layoutParent = findViewById(R.id.layoutParent);

        hapticClient = new HapticServiceClient(this, (state, desc) ->  {
            if (state == 0) {
                layoutParent.setVisibility(View.VISIBLE);
            } else {
                layoutParent.setVisibility(View.GONE);
            }
            hapticServiceStatus = "HapticService: " +  desc;
            updateText();
        });
        testButton = findViewById(R.id.testButton);
        testButton.setOnClickListener(clickListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        IHapticService service = getService();

        if (service == null) {
            return;
        }
        Log.w(TAG, "hapticDisable: ");
        try {
            service.hapticDisable();
        } catch (RemoteException e) {
            Log.e(TAG, "onPause: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        IHapticService service = getService();

        if (service == null) {
            Log.w(TAG, "try binding again. ");
            hapticClient.bindService();
            return;
        }
        Log.w(TAG, "hapticEnable: ");
        try {
            service.hapticEnable();
        } catch (RemoteException e) {
            Log.e(TAG, "onResume: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "stopBinding: ");
        hapticClient.stopBinding();
    }

    private IHapticService getService() {

        IHapticService service = hapticClient.getHapticsService();

        if (!hapticClient.hasService() || service == null) {
            return null;
        }

        return service;
    }

    private void handTest() {
        IHapticService service = getService();

        if (service == null) {
            return;
        }

        try {
            service.hapticEvent("app", "evt", 0, 0, 100, 0, 0);
            service.hapticStopEvent("app", "evt");
            service.hapticUpdateEvent("app", "evt", 100, 0);
            service.hapticFrameTick();

        } catch (Exception e) {
            Log.e(TAG, "handTest: " + e.getMessage(), e);
        }
    }
}