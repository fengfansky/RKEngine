package com.rokid.rkengine.scheduler;

import android.os.RemoteException;

import com.rokid.rkengine.utils.Logger;

import java.util.Stack;

import rokid.rkengine.IRKAppEngineDomainChangeCallback;
import rokid.rkengine.scheduler.AppInfo;

/**
 * Created by fanfeng on 2017/4/18.
 */

public class AppStack {

    private static final String CLOUD_APPID = "841f3558-f3d4-43f6-911a-6f80b62b352d";

    private Stack<AppInfo> appStack = new Stack<>();

    private IRKAppEngineDomainChangeCallback onDomainChangedListener;

    private AppStack() {
    }

    public static AppStack getInstance() {
        return SingleHolder.instance;
    }

    public void setOnDomainChangedListener(IRKAppEngineDomainChangeCallback listener) {
        if (listener != null) {
            onDomainChangedListener = listener;
        }
    }

    /**
     * push native app
     *
     * @param newApp native app
     */
    public void pushApp(AppInfo newApp) {
        if (newApp == null) {
            return;
        }

        if (newApp.ignoreFromCDomain) {
            Logger.d("ignoreFromCDomain don't push app");
            return;
        }
        //NOTICE: avoid push cloud appInfo twice!
        if (CLOUD_APPID.equals(newApp.appId)) {
            Logger.d("CloudAppInfo has pushed into stack,don't need push more !");
            return;
        }
        if (appStack.empty()) {
            appStack.push(newApp);
            onDomainChanged(newApp.appId, null);
        } else {
            AppInfo lastApp = appStack.peek();
            Logger.d("appStack not empty lastType is " + lastApp.type + " newAppType is " + newApp.type);

            if (lastApp.appId.equals(newApp.appId)) {
                Logger.d("lastApp is the same with newApp");
                return;
            }
            if (AppInfo.TYPE_SCENE == lastApp.type && AppInfo.TYPE_CUT == newApp.type) {
                appStack.push(newApp);
            } else {
                appStack.pop();
                appStack.push(newApp);
            }
            onDomainChanged(newApp.appId, lastApp.appId);
        }
        Logger.d("pushApp appStack size : " + appStack.size() + " top app is " + peekApp());
    }


    /**
     * push cloudAppClient
     *
     * @param newApp cloud apps
     */
    public void pushChangeableApp(AppInfo newApp) {
        if (newApp == null) {
            return;
        }
        if (appStack.isEmpty()) {
            appStack.push(newApp);
            onDomainChanged(newApp.appId, null);
        } else {
            AppInfo lastApp = appStack.peek();
            Logger.d("appStack not empty , push CloudApp lastType is " + lastApp.type + " newAppType is " + newApp.type);
            if (lastApp.appId.equals(newApp.appId)) {
                lastApp.type = newApp.type;
                return;
            }

            if (AppInfo.TYPE_SCENE == lastApp.type && AppInfo.TYPE_CUT == newApp.type) {
                appStack.push(newApp);
            } else {
                appStack.pop();
                appStack.push(newApp);
            }
            onDomainChanged(newApp.appId, lastApp.appId);
        }
        Logger.d("pushApp appStack size : " + appStack.size() + " top app is " + peekApp());
    }

    public AppInfo popApp(AppInfo appInfo) {
        if (appStack.empty() || appInfo == null || appStack.peek() == null) {
            Logger.d("appStack is empty or appInfo is null");
            return null;
        }
        if (!appInfo.appId.equals(appStack.peek().appId)) {
            Logger.d("newApp is not the same with lastApp, dot't pop app");
            return null;
        }
        AppInfo lastApp = appStack.pop();
        Logger.d("popApp appStack size : " + appStack.size() + " top app is " + peekApp());
        if (onDomainChangedListener != null) {
            if (appStack.isEmpty()) {
                onDomainChanged(null, lastApp.appId);
            } else {
                AppInfo currApp = appStack.peek();
                onDomainChanged(currApp.appId, lastApp.appId);
            }
        }
        return lastApp;
    }

    public AppInfo peekApp() {
        if (appStack.empty())
            return null;
        return appStack.peek();
    }

    public AppInfo getLastApp() {
        if (appStack.isEmpty() || appStack.size() == 1) {
            Logger.d("getLastApp invalidate");
            return null;
        } else {
            AppInfo currentApp = appStack.pop();
            AppInfo lastApp = appStack.peek();
            onDomainChanged(currentApp.appId, lastApp.appId);
            return lastApp;
        }
    }

    private void onDomainChanged(String currentDomain, String lastDomain) {
        Logger.d("onDomainChanged current appId = " + currentDomain + " lastDomain = " + lastDomain);
        if (onDomainChangedListener != null) {
            try {
                onDomainChangedListener.onDomainChange(currentDomain, lastDomain);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public int queryAppInfo(AppInfo appInfo) {
        return appStack.search(appInfo);
    }

    public boolean isAppStackEmpty() {
        return appStack.isEmpty();
    }

    public int getAppNum() {
        return appStack.size();
    }

    public void clearAppStack() {
        Logger.d("clearAppStack");
        appStack.clear();
        onDomainChanged(null, null);
    }

    private static class SingleHolder {
        private static final AppStack instance = new AppStack();
    }

}
