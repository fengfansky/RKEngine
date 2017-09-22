package com.rokid.rkengine.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.rokid.rkengine.confirm.ConfirmReporter;
import com.rokid.rkengine.confirm.ConfirmRequestConfig;
import com.rokid.rkengine.confirm.ReporterManager;
import com.rokid.rkengine.parser.ParserProxy;
import com.rokid.rkengine.scheduler.AppManagerImp;
import com.rokid.rkengine.scheduler.AppStack;
import com.rokid.rkengine.utils.Logger;

import org.json.JSONObject;

import java.util.List;

import rokid.os.RKEventBus;
import rokid.os.RKEventCallback;
import rokid.rkengine.IRKAppEngine;
import rokid.rkengine.IRKAppEngineAppContextChangeCallback;
import rokid.rkengine.IRKAppEngineDomainChangeCallback;
import rokid.rkengine.scheduler.AppInfo;
import rokid.services.util.RemoteServiceHelper;


public class RkEngineService extends Service {

    private AppManagerImp appManager;

    private static RkEngineService engineService;

    public static final String EXIT_SESSION = "EXIT_SESSION";

    public RKEventBus rkEventBus = RemoteServiceHelper.getService(RemoteServiceHelper.RK_EVENTBUS);

    public RkEngineService() {

    }

    public static RkEngineService getEngineService() {
        return engineService;
    }

    @Override
    public void onCreate() {

        engineService = this;
        appManager = AppManagerImp.getInstance();
        appManager.bindService(this);
        ConfirmRequestConfig.initDeviceInfo();
        rkEventBus.registerAction(EXIT_SESSION, new RKEventCallback() {
            @Override
            public void onReceive( String action, Bundle bundle ) {
                Logger.d(" rkEventBus onReceive action : " + action);
                switch (action) {
                    case EXIT_SESSION:
                        if (bundle == null) {
                            Logger.d("onReceive bundle is null !");
                            return;
                        }
                        String endSessionAppId = bundle.getString("appId");

                        AppStack.getInstance().exitSessionDomain(endSessionAppId);
                        break;
                    default:

                        break;
                }

            }
        });
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

        @Override
        public List<String> queryDomainState() throws RemoteException {
            return appManager.queryDomainState();
        }

        @Override
        public boolean startConfirm( String confirmIntent, String confirmSlot, List<String> confirmOptions ) {
            Logger.d("startConfirm confirmIntent : " + confirmIntent + " confirmSlot : " + confirmSlot + " confirmOptions : " + confirmOptions);
            String appId = "";
            AppInfo appInfo = AppStack.getInstance().peekApp();
            if (appInfo != null) {
                appId = appInfo.appId;
                Logger.d("appId is " + appId);
            }
            try {
                JSONObject intentJson = new JSONObject(confirmIntent);
                String intent = intentJson.getString("intent");
                JSONObject slotsJson = null;
                String attributes = "";
                if (intentJson.has("slots") && intentJson.getJSONObject("slots") != null) {
                    String slots = intentJson.getJSONObject("slots").toString();
                    if (!TextUtils.isEmpty(slots)) {
                        attributes = slots;
                    }
                }
                ReporterManager.getInstance().executeReporter(new ConfirmReporter(intent, confirmSlot, confirmOptions, appId, attributes));
            } catch (Exception e) {
                e.printStackTrace();
            }


            return true;
        }
    };

    public void openSiren( String type, boolean pickupEnable, int durationInMilliseconds ) {
        Logger.d(" process openSiren ");
        Intent intent = new Intent();
        ComponentName component = new ComponentName("com.rokid.activation", "com.rokid.activation.service.CoreService");
        intent.setComponent(component);
        intent.putExtra("FromType", type);
        intent.putExtra("InputAction", "confirmEvent");
        Bundle bundle = new Bundle();
        bundle.putBoolean("isConfirm", pickupEnable);//拾音打开或关闭
        bundle.putInt("durationInMilliseconds", durationInMilliseconds);//当enable=true时，在用户不说话的情况下，拾音打开持续时间
        intent.putExtra("intent", bundle);
        startService(intent);
    }

}
