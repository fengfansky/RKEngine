package com.rokid.rkengine.scheduler;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.rokid.rkengine.utils.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rokid.rkengine.IAppManagerProxy;
import rokid.rkengine.IRKAppEngineAppContextChangeCallback;
import rokid.rkengine.IRKAppEngineDomainChangeCallback;
import rokid.rkengine.scheduler.AppInfo;


/**
 * Created by fanfeng on 2017/4/17.
 */

public class AppManagerImp implements IAppManager {

    private IAppManagerProxy appManagerProxy;

    private AppStateManager appStateManager;

    private AppStack appStack = AppStack.getInstance();

    private Context context;

    private Map<String, String> nlpMaps = new ConcurrentHashMap<>();

    private static final String SERVICE_NAME = "com.rokid.runtime.openvoice.RKNativeAppClientService";
    private static final String PACKAGE_NAME = "com.rokid.runtime";


    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Logger.d("onServiceConnected ");
            appManagerProxy = IAppManagerProxy.Stub.asInterface(service);
            appStateManager = new AppStateManager();
            setAppStateCallBack(appStateManager);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private AppManagerImp() {
    }

    public static AppManagerImp getInstance() {

        return SingleHolder.instance;
    }

    @Override
    public void bindService(Context context) {
        this.context = context;
        Intent binderIntent = new Intent();
        binderIntent.setComponent(new ComponentName(PACKAGE_NAME, SERVICE_NAME));
        boolean isBind = context.bindService(binderIntent, conn, Context.BIND_AUTO_CREATE);
        Logger.d("isBind RemoteService " + isBind);
    }

    @Override
    public void unBindService() {
        if (context == null)
            return;
        context.unbindService(conn);
    }

    @Override
    public void setAppStateCallBack(AppStateManager callback) {
        if (appManagerProxy == null)
            return;
        Logger.d("appManager setAppStateCallBack");
        try {
            appManagerProxy.setAppStateCallBack(callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AppInfo queryAppInfoByID(String appId) {
        if (appManagerProxy == null)
            return null;
        Logger.d("appManager queryAppInfo appId " + appId);

        try {
            return appManagerProxy.queryAppInfoByID(appId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void startApp(AppInfo appInfo, String extra) {
        Logger.d("appManager startApp appInfo : " + appInfo.appId + " extra : " + extra);

        if (appManagerProxy == null)
            return;
        try {
            appManagerProxy.startApp(appInfo, extra);
            Logger.d("appManagerProxy.startApp");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pauseApp(AppInfo appInfo) {
        if (appManagerProxy == null)
            return;
        try {
            appManagerProxy.pauseApp(appInfo);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resumeApp(AppInfo appInfo, String extra) {
        if (appManagerProxy == null)
            return;
        try {
            appManagerProxy.resumeApp(appInfo, extra);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopApp(AppInfo appInfo) {
        if (appManagerProxy == null)
            return;
        try {
            appManagerProxy.stopApp(appInfo);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopAllApp() {
        if (appManagerProxy == null)
            return;
        try {
            appManagerProxy.stopAllApp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int getAppNum() {
        return appStack.getAppNum();
    }

    public AppInfo getTopApp() {
        return appStack.peekApp();
    }

    public AppInfo getLastApp() {
        return appStack.getLastApp();
    }

    public void clearAppStack() {
        appStack.clearAppStack();
    }

    public void storeNLP(String key, String nlp) {
        if (TextUtils.isEmpty(key)) {
            Logger.d("key is null !");
            return;
        }
        nlpMaps.put(key, nlp);
    }

    public String getNLP(String key) {
        return nlpMaps.get(key);
    }

    public void setOnDomainChangedListener(IRKAppEngineDomainChangeCallback listener) {
        appStack.setOnDomainChangedListener(listener);
    }

    private static class SingleHolder {
        private static final AppManagerImp instance = new AppManagerImp();
    }

    public void setOnAppContextChangeListener(IRKAppEngineAppContextChangeCallback callback) {
        if (appStateManager != null) {
            appStateManager.setOnAppContextChangeListener(callback);
        }
    }

}
