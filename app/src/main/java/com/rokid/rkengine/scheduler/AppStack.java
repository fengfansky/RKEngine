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

    public void pushApp(AppInfo newApp) {
        if (newApp == null) {
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
            if (AppInfo.TYPE_CUT == lastApp.type) {
                appStack.push(newApp);
                onDomainChanged(newApp.appId, null);
            } else if (AppInfo.TYPE_SCENE == lastApp.type) {
                if (AppInfo.TYPE_CUT == newApp.type) {
                    appStack.push(newApp);
                    onDomainChanged(newApp.appId, lastApp.appId);
                } else if (AppInfo.TYPE_SCENE == newApp.type) {
                    appStack.pop();
                    appStack.push(newApp);
                    onDomainChanged(newApp.appId, null);
                }
            }
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
            return appStack.peek();
        }
        AppInfo currentApp = appStack.pop();
        Logger.d("popApp appStack size : " + appStack.size() + " top app is " + peekApp());
        if (onDomainChangedListener != null) {
            AppInfo lastApp = appStack.peek();
            if (lastApp != null) {
                onDomainChanged(currentApp.appId, lastApp.appId);
            } else {
                onDomainChanged(currentApp.appId, null);
            }
        }
        return currentApp;
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
    }

    private static class SingleHolder {
        private static final AppStack instance = new AppStack();
    }

}
