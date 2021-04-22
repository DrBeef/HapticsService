package com.bhaptics.service;

public interface BhapticsService extends HapticsService {
    void registerHapticEvent(
            String application,
            String event, String feedbackFile);
    void registerReflectHapticEvent(
            String application,
            String event, String feedbackFile);


    void hapticEventDot(
            String application, String event, String position,
            byte[] dots, int duration);
    void hapticEventPath(
            String application, String event, String position,
            int arrSize,
            float[] x, float[] y, int[] intensities, int duration);

    void hapticEventRegisteredByTime(
            String application,
            String event,
            float time);

    void hapticEventRegistered(
            String application,
            String event,
            String asEvent,
            float intensity,
            float duration,
            float angle,
            float yHeight);

    void hapticStopAllEvent();

    boolean isRegistered(String application, String event);
    boolean isPlaying(String application, String event);
    boolean isAnythingPlaying();

    byte[] getStatus(String position);


}
