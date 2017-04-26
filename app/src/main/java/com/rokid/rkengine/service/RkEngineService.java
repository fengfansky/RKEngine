package com.rokid.rkengine.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.rokid.rkengine.parser.ParserProxy;
import com.rokid.rkengine.scheduler.AppManagerImp;
import com.rokid.rkengine.scheduler.AppStack;
import com.rokid.rkengine.utils.Logger;

import rokid.rkengine.IRKAppEngine;
import rokid.rkengine.scheduler.AppInfo;


public class RkEngineService extends Service {

    private AppManagerImp appManager;

    public RkEngineService() {

    }

    @Override
    public void onCreate() {

        appManager = AppManagerImp.getInstance();
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


    private IBinder binder = new IRKAppEngine.Stub() {

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
            Logger.d("launch RKEngineService startParse nlp : " + nlp.toString() + " asr : " + asr.toString() + "action : " + action.toString());
            ParserProxy.getInstance().startParse(RkEngineService.this
                    , nlp, asr, action);
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

    };

}
