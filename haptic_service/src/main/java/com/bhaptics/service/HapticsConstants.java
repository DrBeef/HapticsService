package com.bhaptics.service;

public class HapticsConstants {
    public static final String BHAPTICS_PACKAGE = "com.bhaptics.player";
    public static final String BHAPTICS_ACTION_FILTER = "com.bhaptics.player.BhapticsHapticService";

    public static final int TRANSACTION_hapticEvent = 1;
    public static final int TRANSACTION_hapticUpdateEvent = 2;
    public static final int TRANSACTION_hapticStopEvent = 3;
    public static final int TRANSACTION_hapticFrameTick = 4;
    public static final int TRANSACTION_hapticEnable = 5;
    public static final int TRANSACTION_hapticDisable = 6;

    public static final int TRANSACTION_BHAPTICS_registerHapticEvent = 100;
    public static final int TRANSACTION_BHAPTICS_registerReflectHapticEvent = 101;
    public static final int TRANSACTION_BHAPTICS_hapticEventDot = 102;
    public static final int TRANSACTION_BHAPTICS_hapticEventPath = 103;

    public static final int TRANSACTION_BHAPTICS_hapticEventRegistered = 105;
    public static final int TRANSACTION_BHAPTICS_hapticEventRegisteredByTime = 106;
    public static final int TRANSACTION_BHAPTICS_hapticStopAllEvent = 107;
    public static final int TRANSACTION_BHAPTICS_isRegistered = 108;
    public static final int TRANSACTION_BHAPTICS_isPlaying = 109;
    public static final int TRANSACTION_BHAPTICS_isAnythingPlaying = 110;
    public static final int TRANSACTION_BHAPTICS_getStatus = 112;
}
