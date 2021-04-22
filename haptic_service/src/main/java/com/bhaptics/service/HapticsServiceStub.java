package com.bhaptics.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

public abstract class HapticsServiceStub extends Binder implements BhapticsService {

    public HapticsServiceStub() {
        this.attachInterface(this, HapticsConstants.BHAPTICS_PACKAGE);
    }

    public IBinder asBinder() {
        return this;
    }

    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        String packageName = HapticsConstants.BHAPTICS_PACKAGE;
        String application;
        String event;
        int arg2;
        switch(code) {
            case HapticsConstants.TRANSACTION_hapticEvent:
                data.enforceInterface(packageName);
                application = data.readString();
                event = data.readString();
                arg2 = data.readInt();
                int flag = data.readInt();
                int intensity = data.readInt();
                float xAngle = data.readFloat();
                float yHeight = data.readFloat();
                this.hapticEvent(application, event, arg2, flag, intensity, xAngle, yHeight);
                reply.writeNoException();
                return true;
            case HapticsConstants.TRANSACTION_hapticUpdateEvent:
                data.enforceInterface(packageName);
                application = data.readString();
                event = data.readString();
                arg2 = data.readInt();
                float angleX = data.readFloat();
                this.hapticUpdateEvent(application, event, arg2, angleX);
                reply.writeNoException();
                return true;
            case HapticsConstants.TRANSACTION_hapticStopEvent:
                data.enforceInterface(packageName);
                application = data.readString();
                event = data.readString();
                this.hapticStopEvent(application, event);
                reply.writeNoException();
                return true;
            case HapticsConstants.TRANSACTION_hapticFrameTick:
                data.enforceInterface(packageName);
                this.hapticFrameTick();
                reply.writeNoException();
                return true;
            case HapticsConstants.TRANSACTION_hapticEnable:
                data.enforceInterface(packageName);
                this.hapticEnable();
                reply.writeNoException();
                return true;
            case HapticsConstants.TRANSACTION_hapticDisable:
                data.enforceInterface(packageName);
                this.hapticDisable();
                reply.writeNoException();
                return true;
            case HapticsConstants.TRANSACTION_BHAPTICS_registerHapticEvent:
            case HapticsConstants.TRANSACTION_BHAPTICS_registerReflectHapticEvent:
                data.enforceInterface(packageName);
                application = data.readString();
                event = data.readString();
                String tactFileString = data.readString();

                if (code == HapticsConstants.TRANSACTION_BHAPTICS_registerReflectHapticEvent) {
                    this.registerReflectHapticEvent(application, event, tactFileString);
                } else {
                    this.registerHapticEvent(application, event, tactFileString);
                }
                reply.writeNoException();
                return true;
            case HapticsConstants.TRANSACTION_BHAPTICS_hapticEventDot:
                data.enforceInterface(packageName);
                application = data.readString();
                event = data.readString();
                String pos = data.readString();
                byte[] bytes = new byte[20];
                data.readByteArray(bytes);

                this.hapticEventDot(application, event, pos, bytes, data.readInt());
                reply.writeNoException();
                return true;
            case HapticsConstants.TRANSACTION_BHAPTICS_hapticEventPath:
                data.enforceInterface(packageName);
                application = data.readString();
                event = data.readString();
                String position  = data.readString();
                int size = data.readInt();
                float[] x = new float[size];
                float[] y = new float[size];
                int[] intensities = new int[size];
                this.hapticEventPath(application, event, position, size, x, y, intensities, data.readInt());
                reply.writeNoException();
                return true;
            case HapticsConstants.TRANSACTION_BHAPTICS_hapticEventRegisteredByTime:
                data.enforceInterface(packageName);
                application = data.readString();
                event = data.readString();
                this.hapticEventRegisteredByTime(application, event, data.readFloat());
                reply.writeNoException();
                return true;
            case HapticsConstants.TRANSACTION_BHAPTICS_hapticEventRegistered:
                data.enforceInterface(packageName);
                application = data.readString();
                event = data.readString();
                this.hapticEventRegistered(application, event, data.readString(),
                        data.readFloat(),  data.readFloat(),  data.readFloat(),  data.readFloat());
                reply.writeNoException();
                return true;
            case HapticsConstants.TRANSACTION_BHAPTICS_hapticStopAllEvent:
                data.enforceInterface(packageName);
                this.hapticStopAllEvent();
                reply.writeNoException();
                return true;
            case HapticsConstants.TRANSACTION_BHAPTICS_isRegistered:
                data.enforceInterface(packageName);
                application = data.readString();
                event = data.readString();
                reply.writeBoolean(this.isRegistered(application, event));
                reply.writeNoException();
                return true;
            case HapticsConstants.TRANSACTION_BHAPTICS_isPlaying:
                data.enforceInterface(packageName);
                application = data.readString();
                event = data.readString();
                reply.writeBoolean(this.isPlaying(application, event));
                reply.writeNoException();
                return true;
            case HapticsConstants.TRANSACTION_BHAPTICS_isAnythingPlaying:
                data.enforceInterface(packageName);
                reply.writeBoolean(this.isAnythingPlaying());
                reply.writeNoException();
                return true;
            case HapticsConstants.TRANSACTION_BHAPTICS_getStatus:
                data.enforceInterface(packageName);
                String posForStatus = data.readString();
                reply.writeByteArray(this.getStatus(posForStatus));
                reply.writeNoException();
                return true;
            case 1598968902:
                reply.writeString(packageName);
                return true;
            default:
                return super.onTransact(code, data, reply, flags);
        }
    }

}