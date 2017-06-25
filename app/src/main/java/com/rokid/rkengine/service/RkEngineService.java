package com.rokid.rkengine.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.rokid.rkengine.parser.ParserProxy;
import com.rokid.rkengine.scheduler.AppManagerImp;
import com.rokid.rkengine.utils.Logger;

import rokid.rkengine.IRKAppEngine;
import rokid.rkengine.IRKAppEngineDomainChangeCallback;
import rokid.rkengine.IRKAppEngineAppContextChangeCallback;
import rokid.rkengine.scheduler.AppInfo;


public class RkEngineService extends Service {

    private AppManagerImp appManager;

    public RkEngineService() {

    }

    @Override
    public void onCreate() {

        appManager = AppManagerImp.getInstance();
        appManager.bindService(this);
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            Logger.i("onStartCommand with invalid intent");
            return super.onStartCommand(intent, flags, startId);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }


    public IBinder binder = new IRKAppEngine.Stub() {

        @Override
        public void clearTaskStack() throws RemoteException {
            appManager.clearAppStack();
        }

        @Override
        public AppInfo getLastAppInfo() throws RemoteException {

            return appManager.getTopApp();
        }

        @Override
        public void launch(String nlp, String asr, String action) throws RemoteException {
            Logger.d("launch RKEngineService startParse nlp : " + nlp);
            Logger.d(" asr : " + asr);
            Logger.d("action : " + action);
            ParserProxy.getInstance().startParse(nlp, asr, action);
        }

        @Override
        public void setDeviceInfo(String deviceInfo) throws RemoteException {

        }

        @Override
        public void launchLast() throws RemoteException {

            AppInfo lastApp = appManager.getLastApp();
            if (lastApp == null) {
                Logger.d("lastApp no exits");
                return;
            }
            appManager.startApp(lastApp, appManager.getNLP(lastApp.appId));
        }

        @Override
        public void registerDomainChangeCallback(IRKAppEngineDomainChangeCallback onDomainChangedListener) throws RemoteException {
            if (onDomainChangedListener != null) {
                Logger.d("setOnDomainChangedListener");
                appManager.setOnDomainChangedListener(onDomainChangedListener);
            }
        }

        @Override
        public void registerAppContextChangeCallback(IRKAppEngineAppContextChangeCallback callback) throws RemoteException {
            if (callback != null) {
                Logger.d("setOnAppContextChangeListener");
                appManager.setOnAppContextChangeListener(callback);
            }
        } 
    };
}
