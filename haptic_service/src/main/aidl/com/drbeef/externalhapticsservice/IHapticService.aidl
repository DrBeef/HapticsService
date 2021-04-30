package com.drbeef.externalhapticsservice;

interface IHapticService {
    void hapticEvent(
            String application,
            String event,
            int position,
            int flags,
            int intensity,
            float angle,
            float yHeight);

    void hapticUpdateEvent(
            String application,
            String event,
            int intensity, float angle);

    void hapticStopEvent(String application, String event);

    void hapticFrameTick();

    void hapticEnable();

    void hapticDisable();
}