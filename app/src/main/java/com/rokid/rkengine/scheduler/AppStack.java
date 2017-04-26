package com.rokid.rkengine.scheduler;

import com.rokid.rkengine.utils.Logger;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import rokid.rkengine.scheduler.AppInfo;

/**
 * Created by fanfeng on 2017/4/18.
 */

public class AppStack {

    private Stack<AppInfo> appStack = new Stack<>();

    private AppStack() {
    }

    public static AppStack getInstance() {
        return SingleHolder.instance;
    }

    private static class SingleHolder {
        private static final AppStack instance = new AppStack();
    }

    public void pushApp(AppInfo newApp) {
        if (newApp == null) {
            return;
        }
        if (appStack.empty()) {
            appStack.push(newApp);
        } else {
            AppInfo lastApp = appStack.peek();
            Logger.d("appStack not empty lastType is " + lastApp.type + " newAppType is " + newApp.type);
            if (lastApp == null) {
                Logger.d("lastApp is null");
                return;
            }
            if (lastApp.appId == newApp.appId) {
                Logger.d("lastApp is the same with newApp");
                return;
            }
            if (AppInfo.TYPE_CUT == lastApp.type) {
                appStack.pop();
                appStack.push(newApp);
            } else if (AppInfo.TYPE_SCENE == lastApp.type) {
                if (AppInfo.TYPE_CUT == newApp.type) {
                    appStack.push(newApp);
                } else if (AppInfo.TYPE_SCENE == newApp.type) {
                    appStack.pop();
                    appStack.push(newApp);
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
        return currentApp;
    }

    public AppInfo peekApp() {
        if (appStack.empty())
            return null;
        return appStack.peek();
    }

    //TODO getLastApp
    //stack max size is 2.
    public AppInfo getLastApp() {
        if (appStack.isEmpty()) {
            Logger.d("getLastApp stack empty");
            return null;
        } else if (appStack.size() == 1) {
            Logger.d("getLastApp lastApp not exits ,return top app");
            return appStack.peek();
        } else {
            appStack.pop();
            AppInfo lastApp = appStack.peek();
            return lastApp;
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

}
