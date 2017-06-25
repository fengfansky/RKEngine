package com.rokid.rkengine.scheduler;

import java.util.ArrayList;

import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.rokid.rkengine.utils.Logger;

import rokid.rkengine.IAppStateCallback;
import rokid.rkengine.scheduler.AppException;
import rokid.rkengine.scheduler.AppInfo;
import rokid.rkengine.IRKAppEngineAppContextChangeCallback;

/**
 * Created by fanfeng on 2017/4/18.
 */

public class AppStateManager extends IAppStateCallback.Stub {
    private static final String TAG = "NativeAppClientCallback";

    private ArrayList<IRKAppEngineAppContextChangeCallback> mContextChangeCallbacks = new ArrayList<IRKAppEngineAppContextChangeCallback>();

    AppStack appStack = AppStack.getInstance();

    @Override
    public void onAppError(AppException appInfo) throws RemoteException {
        if (appInfo == null) {
            Logger.d("onAppError appInfo == null");
            return;
        }
        Logger.d(TAG, "exception with " + appInfo.errCode + " what " + appInfo.what);

        for (IRKAppEngineAppContextChangeCallback _contextChangeCallback : mContextChangeCallbacks) {
            if (_contextChangeCallback != null) {
                _contextChangeCallback.onAppError(appInfo);
            }
        }
    }

    @Override
    public void onAppInstalledSuccess(AppInfo appInfo) throws RemoteException {
        if (appInfo == null) {
            Logger.d("onAppInstalledSuccess appInfo == null");
            return;
        }
        Logger.d(TAG, "app " + appInfo.appId + " installed");
        for (IRKAppEngineAppContextChangeCallback _contextChangeCallback : mContextChangeCallbacks) {
            if (_contextChangeCallback != null) {
                _contextChangeCallback.onAppInstalledSuccess(appInfo);
            }
        }
    }

    @Override
    public void onAppUninstalledSuccess(AppInfo appInfo) throws RemoteException {
        if (appInfo == null) {
            Logger.d("onAppUninstalledSuccess appInfo == null");
            return;
        }
        Logger.d(TAG, "app " + appInfo.appId + " uninstalled");
        for (IRKAppEngineAppContextChangeCallback _contextChangeCallback : mContextChangeCallbacks) {
            if (_contextChangeCallback != null) {
                _contextChangeCallback.onAppUninstalledSuccess(appInfo);
            }
        }
    }

    @Override
    public void onCreate(AppInfo appInfo) throws RemoteException {
        if (appInfo == null || TextUtils.isEmpty(appInfo.appId)) {
            Logger.d("onCreate appInfo is null");
            return;
        }
        Logger.d(TAG, "app " + appInfo.appId + " onCreate");
        for (IRKAppEngineAppContextChangeCallback _contextChangeCallback : mContextChangeCallbacks) {
            if (_contextChangeCallback != null) {
                _contextChangeCallback.onCreate(appInfo);
            }
        }
    }

    @Override
    public void onPause(AppInfo appInfo) throws RemoteException {
        if (appInfo == null || TextUtils.isEmpty(appInfo.appId)) {
            Logger.d("onPause appInfo is null");
            return;
        }

        Logger.d(TAG, "app " + appInfo.appId + " onPause");
        appStack.popApp(appInfo);
        for (IRKAppEngineAppContextChangeCallback _contextChangeCallback : mContextChangeCallbacks) {
            if (_contextChangeCallback != null) {
                _contextChangeCallback.onPause(appInfo);
            }
        }
    }

    @Override
    public void onResume(AppInfo appInfo) throws RemoteException {
        if (appInfo == null || TextUtils.isEmpty(appInfo.appId)) {
            Logger.d("onResume appInfo is null");
            return;
        }

        Logger.d(TAG, "onResume  " + appInfo.appId);
        appStack.pushApp(appInfo);
        for (IRKAppEngineAppContextChangeCallback _contextChangeCallback : mContextChangeCallbacks) {
            if (_contextChangeCallback != null) {
                _contextChangeCallback.onResume(appInfo);
            }
        }
        
    }

    @Override
    public void onStart(AppInfo appInfo) throws RemoteException {
        if (appInfo == null || TextUtils.isEmpty(appInfo.appId)) {
            Logger.d("onStart appInfo is null");
            return;
        }

        Logger.d(TAG, "onStart " + appInfo.appId);


    }

    @Override
    public void onStop(AppInfo appInfo) throws RemoteException {
        if (appInfo == null || TextUtils.isEmpty(appInfo.appId)) {
            Logger.d("onStop appInfo is null or appInfo.appId is empty");
            return;
        }
        Logger.d(TAG, "onStop " + appInfo.appId);
        for (IRKAppEngineAppContextChangeCallback _contextChangeCallback : mContextChangeCallbacks) {
            if (_contextChangeCallback != null) {
                _contextChangeCallback.onStop(appInfo);
            }
        }
    }

    public void setOnAppContextChangeListener(final IRKAppEngineAppContextChangeCallback callback) {
        Logger.d(TAG, "setOnAppStateCallbackDeathListenr in AppStateManager");
        try {
            callback.asBinder().linkToDeath(new DeathRecipient() {
                @Override
                public void binderDied() {
                    Logger.i("callback " + callback.toString() + " is dead...");
                    mContextChangeCallbacks.remove(callback);
                }
            }, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.i("add callback " + callback.toString());
        mContextChangeCallbacks.add(callback);
    }
}
