package com.bhaptics.service;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

public class HapticsServiceClientProxy implements BhapticsService {
    public static final String TAG = "bhaptics_Proxy";
    private IBinder mRemote;

    HapticsServiceClientProxy(IBinder remote) {
        this.mRemote = remote;
    }

    public IBinder asBinder() {
        return this.mRemote;
    }

    public static BhapticsService asInterface(IBinder binder) {
        if (binder == null) {
            return null;
        } else {
            IInterface iInterface = binder.queryLocalInterface(HapticsConstants.BHAPTICS_PACKAGE);

            if (iInterface != null && iInterface instanceof HapticsService) {
                return (BhapticsService)iInterface;
            }

            return new HapticsServiceClientProxy(binder);
        }
    }


    @Override
    public void hapticEvent(String application, String event, int position, int flags, int intensity, float angle, float yHeight) {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();

        try {
            data.writeInterfaceToken(HapticsConstants.BHAPTICS_PACKAGE);
            data.writeString(application);
            data.writeString(event);
            data.writeInt(position);
            data.writeInt(flags);
            data.writeInt(intensity);
            data.writeFloat(angle);
            data.writeFloat(yHeight);
            boolean status = this.mRemote.transact(HapticsConstants.TRANSACTION_hapticEvent, data, reply, 0);
        } catch (RemoteException e) {
            Log.e(TAG, "hapticEvent: ", e);
        } finally {
            reply.recycle();
            data.recycle();
        }

    }

    @Override
    public void hapticUpdateEvent(String application, String event, int intensity, float angle) {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();

        try {
            _data.writeInterfaceToken(HapticsConstants.BHAPTICS_PACKAGE);
            _data.writeString(application);
            _data.writeString(event);
            _data.writeInt(intensity);
            _data.writeFloat(angle);
            boolean status = this.mRemote.transact(HapticsConstants.TRANSACTION_hapticUpdateEvent, _data, _reply, 0);
            _reply.readException();
        } catch (RemoteException e) {
            Log.e(TAG, "hapticEvent: ", e);
        } finally {
            _reply.recycle();
            _data.recycle();
        }

    }

    @Override
    public void hapticStopEvent(String application, String event) {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();

        try {
            data.writeInterfaceToken(HapticsConstants.BHAPTICS_PACKAGE);
            data.writeString(application);
            data.writeString(event);
            boolean status = this.mRemote.transact(HapticsConstants.TRANSACTION_hapticStopEvent, data, reply, 0);
        } catch (RemoteException e) {
            Log.e(TAG, "hapticEvent: ", e);
        } finally {
            reply.recycle();
            data.recycle();
        }

    }

    @Override
    public void hapticFrameTick() {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();

        try {
            data.writeInterfaceToken(HapticsConstants.BHAPTICS_PACKAGE);
            boolean status = this.mRemote.transact(HapticsConstants.TRANSACTION_hapticFrameTick, data, reply, 0);

        } catch (RemoteException e) {
            Log.e(TAG, "hapticEvent: ", e);
        } finally {
            reply.recycle();
            data.recycle();
        }

    }

    @Override
    public void hapticEnable() {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();

        try {
            data.writeInterfaceToken(HapticsConstants.BHAPTICS_PACKAGE);
            boolean status = this.mRemote.transact(HapticsConstants.TRANSACTION_hapticEnable, data, reply, 0);

            reply.readException();
        } catch (RemoteException e) {
            Log.e(TAG, "hapticEvent: ", e);
        } finally {
            reply.recycle();
            data.recycle();
        }

    }

    @Override
    public void hapticDisable() {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();

        try {
            data.writeInterfaceToken(HapticsConstants.BHAPTICS_PACKAGE);
            // disable
            boolean status = this.mRemote.transact(HapticsConstants.TRANSACTION_hapticDisable, data, reply, 0);

            reply.readException();
        } catch (RemoteException e) {
            Log.e(TAG, "hapticEvent: ", e);
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    @Override
    public void registerHapticEvent(String application, String event, String feedbackFile) {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();

        try {
            data.writeInterfaceToken(HapticsConstants.BHAPTICS_PACKAGE);
            data.writeString(application);
            data.writeString(event);
            data.writeString(feedbackFile);
            boolean status = this.mRemote.transact(HapticsConstants.TRANSACTION_BHAPTICS_registerHapticEvent, data, reply, 0);

            reply.readException();
        } catch (RemoteException e) {
            Log.e(TAG, "hapticEvent: ", e);
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    @Override
    public void registerReflectHapticEvent(String application, String event, String feedbackFile)  {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();

        try {
            data.writeInterfaceToken(HapticsConstants.BHAPTICS_PACKAGE);
            data.writeString(application);
            data.writeString(event);
            data.writeString(feedbackFile);
            boolean status = this.mRemote.transact(HapticsConstants.TRANSACTION_BHAPTICS_registerReflectHapticEvent, data, reply, 0);

            reply.readException();
        }  catch (RemoteException e) {
            Log.e(TAG, "registerReflectHapticEvent: ", e);
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    @Override
    public void hapticEventDot(
            String application, String event, String position,
            byte[] dots, int duration) {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();

        try {
            data.writeInterfaceToken(HapticsConstants.BHAPTICS_PACKAGE);
            data.writeString(application);
            data.writeString(event);
            data.writeString(position);
            data.writeByteArray(dots);
            data.writeInt(duration);
            boolean status = this.mRemote.transact(HapticsConstants.TRANSACTION_BHAPTICS_hapticEventDot, data, reply, 0);

            reply.readException();
        }  catch (RemoteException e) {
            Log.e(TAG, "hapticEventDot: ", e);
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    @Override
    public void hapticEventPath(
            String application, String event, String position,
            int size,
            float[] x, float[] y, int[] intensities, int duration) {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();

        try {
            data.writeInterfaceToken(HapticsConstants.BHAPTICS_PACKAGE);
            data.writeString(application);
            data.writeString(event);
            data.writeString(position);
            data.writeInt(size);
            data.writeFloatArray(x);
            data.writeFloatArray(y);
            data.writeIntArray(intensities);
            data.writeInt(duration);
            boolean status = this.mRemote.transact(HapticsConstants.TRANSACTION_BHAPTICS_hapticEventPath, data, reply, 0);

            reply.readException();
        }  catch (RemoteException e) {
            Log.e(TAG, "hapticEventPath: ", e);
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    @Override
    public void hapticEventRegisteredByTime(String application, String event, float time) {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(HapticsConstants.BHAPTICS_PACKAGE);
            data.writeString(application);
            data.writeString(event);
            data.writeFloat(time);
            boolean status = this.mRemote.transact(HapticsConstants.TRANSACTION_BHAPTICS_hapticEventRegisteredByTime, data, reply, 0);

            reply.readException();
        }  catch (RemoteException e) {
            Log.e(TAG, "hapticEventRegisteredByTime: ", e);
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    @Override
    public void hapticEventRegistered(
            String application, String event, String asEvent,
            float intensity, float duration, float angle, float yHeight) {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(HapticsConstants.BHAPTICS_PACKAGE);
            data.writeString(application);
            data.writeString(event);
            data.writeString(asEvent);
            data.writeFloat(intensity);
            data.writeFloat(duration);
            data.writeFloat(angle);
            data.writeFloat(yHeight);
            boolean status = this.mRemote.transact(HapticsConstants.TRANSACTION_BHAPTICS_hapticEventRegistered, data, reply, 0);

            reply.readException();
        }  catch (RemoteException e) {
            Log.e(TAG, "hapticEventRegistered: ", e);
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    @Override
    public void hapticStopAllEvent() {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(HapticsConstants.BHAPTICS_PACKAGE);
            boolean status = this.mRemote.transact(HapticsConstants.TRANSACTION_BHAPTICS_hapticStopAllEvent, data, reply, 0);

            reply.readException();
        }  catch (RemoteException e) {
            Log.e(TAG, "hapticStopAllEvent: " + e.getMessage(), e);
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    @Override
    public boolean isRegistered(String application, String event) {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(HapticsConstants.BHAPTICS_PACKAGE);
            data.writeString(application);
            data.writeString(event);
            boolean status = this.mRemote.transact(HapticsConstants.TRANSACTION_BHAPTICS_isRegistered, data, reply, 0);

            boolean res = reply.readBoolean();
            reply.readException();
            return res;
        }  catch (RemoteException e) {
            Log.e(TAG, "isRegistered: " + e.getMessage(), e);
        } finally {
            reply.recycle();
            data.recycle();
        }
        return false;
    }

    @Override
    public boolean isPlaying(String application, String event) {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(HapticsConstants.BHAPTICS_PACKAGE);
            data.writeString(application);
            data.writeString(event);
            boolean status = this.mRemote.transact(HapticsConstants.TRANSACTION_BHAPTICS_isPlaying, data, reply, 0);

            boolean res = reply.readBoolean();
            reply.readException();
            return res;
        }  catch (RemoteException e) {
            Log.e(TAG, "isPlaying: " + e.getMessage(), e);
        } finally {
            reply.recycle();
            data.recycle();
        }
        return false;
    }

    @Override
    public boolean isAnythingPlaying() {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(HapticsConstants.BHAPTICS_PACKAGE);
            boolean status = this.mRemote.transact(HapticsConstants.TRANSACTION_BHAPTICS_isAnythingPlaying, data, reply, 0);

            boolean res = reply.readBoolean();
            reply.readException();
            return res;
        }  catch (RemoteException e) {
            Log.e(TAG, "hapticStopAllEvent: " + e.getMessage(), e);
        } finally {
            reply.recycle();
            data.recycle();
        }
        return false;
    }

    @Override
    public byte[] getStatus(String position) {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(HapticsConstants.BHAPTICS_PACKAGE);
            data.writeString(position);
            boolean status = this.mRemote.transact(HapticsConstants.TRANSACTION_BHAPTICS_getStatus, data, reply, 0);

            byte[] bytes = new byte[20];
            reply.readByteArray(bytes);
            reply.readException();
            return bytes;
        }  catch (RemoteException e) {
            Log.e(TAG, "getStatus: " + e.getMessage(), e);
        } finally {
            reply.recycle();
            data.recycle();
        }
        return new byte[20];
    }

}