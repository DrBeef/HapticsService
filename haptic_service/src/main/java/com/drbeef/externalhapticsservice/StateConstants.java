package com.drbeef.externalhapticsservice;

public class StateConstants {
    public static final int STATE_DISCONNECTED = 2;
    public static final int STATE_NOT_INSTALLED = 3;
    public static final int STATE_NOT_READY = 1;
    public static final int STATE_OK = 0;

    public static String stateToDesc(int state) {
        switch (state) {
            case STATE_OK:
                return "OK";
            case STATE_NOT_READY:
                return "Not Ready";
            case STATE_NOT_INSTALLED:
                return "Not Installed";
            case STATE_DISCONNECTED:
                return "Disconnected";
        }
        return "Unknown";
    };
}
