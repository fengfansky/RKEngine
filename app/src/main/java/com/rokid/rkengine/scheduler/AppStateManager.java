package com.rokid.rkengine.scheduler;

import android.os.RemoteException;
import android.text.TextUtils;

import com.rokid.rkengine.utils.Logger;

import rokid.rkengine.IAppStateCallback;
import rokid.rkengine.scheduler.AppException;
import rokid.rkengine.scheduler.AppInfo;

/**
 * Created by fanfeng on 2017/4/18.
 */

public class AppStateManager extends IAppStateCallback.Stub {

    private static final String TAG = "NativeAppClientCallback";

    private static final String CLOUD_APPID = "841f3558-f3d4-43f6-911a-6f80b62b352d";

    AppStack appStack = AppStack.getInstance();

    @Override
    public void onAppError(AppException appInfo) throws RemoteException {
        if (appInfo == null) {
            Logger.d("onAppError appInfo == null");
            return;
        }
        Logger.d(TAG, "exception with " + appInfo.errCode + " what " + appInfo.what);
    }

    @Override
    public void onAppInstalledSuccess(AppInfo appInfo) throws RemoteException {
        if (appInfo == null) {
            Logger.d("onAppInstalledSuccess appInfo == null");
            return;
        }
        Logger.d(TAG, "app " + appInfo.appId + " installed");
    }

    @Override
    public void onAppUninstalledSuccess(AppInfo appInfo) throws RemoteException {
        if (appInfo == null) {
            Logger.d("onAppUninstalledSuccess appInfo == null");
            return;
        }
        Logger.d(TAG, "app " + appInfo.appId + " uninstalled");
    }

    @Override
    public void onCreate(AppInfo appInfo) throws RemoteException {
        if (appInfo == null || TextUtils.isEmpty(appInfo.appId)) {
            Logger.d("onCreate appInfo is null");
            return;
        }
        Logger.d(TAG, "app " + appInfo.appId + " onCreate");
    }

    @Override
    public void onPause(AppInfo appInfo) throws RemoteException {
        if (appInfo == null || TextUtils.isEmpty(appInfo.appId)) {
            Logger.d("onPause appInfo is null");
            return;
        }
        Logger.d(TAG, "app " + appInfo.appId + " onPause");
    }

    @Override
    public void onResume(AppInfo appInfo) throws RemoteException {
        if (appInfo == null || TextUtils.isEmpty(appInfo.appId)) {
            Logger.d("onResume appInfo is null");
            return;
        }
        Logger.d(TAG, "onResume  " + appInfo.appId);
//        appStack.pushApp(appInfo);
    }

    @Override
    public void onStart(AppInfo appInfo) throws RemoteException {
        if (appInfo == null || TextUtils.isEmpty(appInfo.appId)) {
            Logger.d("onStart appInfo is null");
            return;
        }
        Logger.d(TAG, "onStart " + appInfo.appId);
        if (appInfo.ignoreFromCDomain) {
            Logger.d("ignoreFromCDomain don't push app");
            return;
        }
        //NOTICE: avoid push cloud appInfo twice!
        if (CLOUD_APPID.equals(appInfo.appId)) {
            Logger.d("CloudAppInfo has pushed into stack,don't need push more !");
            return;
        }

        appStack.pushApp(appInfo);
    }

    @Override
    public void onStop(AppInfo appInfo) throws RemoteException {
        if (appInfo == null || TextUtils.isEmpty(appInfo.appId)) {
            Logger.d("onStop appInfo is null or appInfo.appId is empty");
            return;
        }
        Logger.d(TAG, "onStop " + appInfo.appId);
        appStack.popApp(appInfo);
    }

}
