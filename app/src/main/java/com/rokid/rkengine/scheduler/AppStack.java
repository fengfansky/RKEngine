package com.rokid.rkengine.scheduler;

import android.os.RemoteException;
import android.text.TextUtils;

import com.rokid.rkengine.utils.CloudAppCheckConfig;
import com.rokid.rkengine.utils.Logger;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
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

    public synchronized void tryPushApp(String cloudAppId, String appId) {
        if (!appId.equals(CloudAppCheckConfig.CLOUD_CUT_APP_PACKAGE_NAME) &&
                !appId.equals(CloudAppCheckConfig.CLOUD_SCENE_APP_PACKAGE_NAME)) {
            return;
        }

        if (appStack.isEmpty()) {
            Logger.i("try push with empty stack");
            return;
        }

        AppInfo appInfo = appStack.peek();
        if (TextUtils.isEmpty(appInfo.appId)) {
            Logger.i("top is null");
            appStack.pop();
            return;
        }

        String sDomain = null;
        String cDomain = cloudAppId;
        if (appStack.size() == 1) {
            AppInfo cAppInfo = appStack.get(0);
            //only handle top is cloud
            if (!CloudAppCheckConfig.isCloudApp(cAppInfo.appId)) {
                return;
            }

            if (appId.equals(CloudAppCheckConfig.CLOUD_CUT_APP_PACKAGE_NAME) &&
                    cAppInfo.appId.equals(CloudAppCheckConfig.CLOUD_SCENE_APP_PACKAGE_NAME)) {
                sDomain = CloudAppCheckConfig.getCloudAppId(cAppInfo.appId);
            }
        } else if (appStack.size() == 2) {
            if (appId.equals(CloudAppCheckConfig.CLOUD_SCENE_APP_PACKAGE_NAME)) {
                sDomain = null;
            } else {
                AppInfo sAppInfo = appStack.get(1);
                /*sDomain = sAppInfo.appId;
                if (CloudAppCheckConfig.isCloudApp(sAppInfo.appId)) {
                    sDomain = CloudAppCheckConfig.getCloudAppId(sAppInfo.appId);
                }*/
                sDomain = CloudAppCheckConfig.getFinalAppId(sAppInfo.appId);
            }
        }

        Logger.i("try push domain change with " + cDomain + ":" + sDomain);
        onDomainChanged(cDomain, sDomain);
        Logger.d("pushApp appStack size : " + appStack.size() + " top app is " + peekApp());

    }

    /**
     * push native app
     *
     * @param newApp native app
     */
    public synchronized void pushApp(AppInfo newApp) {
        if (newApp == null) {
            return;
        }

        if (newApp.ignoreFromCDomain) {
            Logger.d("ignoreFromCDomain don't push app");
            return;
        }

        Logger.d("newAppType is " + newApp.type + " newApp appId is " + newApp.appId);
        if (appStack.empty()) {
            appStack.push(newApp);
            String shouldChangedDomainAppId = CloudAppCheckConfig.getFinalAppId(newApp.appId);

            if (!TextUtils.isEmpty(shouldChangedDomainAppId)) {
                onDomainChanged(shouldChangedDomainAppId, null);
            }
        } else {
            AppInfo lastApp = appStack.peek();
            Logger.d("appStack not empty lastType is " + lastApp.type + " lastApp appId is " + lastApp.appId + " ,  newAppType is " + newApp.type + " newApp appId is " + newApp.appId);
            if (TextUtils.isEmpty(lastApp.appId)) {
                Logger.d("lastApp appId is null !!!");
                //remove it since it is null!
                appStack.pop();
                //push again
                pushApp(newApp);
                return;
            }

            if (lastApp.appId.equals(newApp.appId)) {
                Logger.d("lastApp is the same with newApp");
                //maybe cloud
                if (CloudAppCheckConfig.isCloudApp(newApp.appId)
                        && CloudAppCheckConfig.isCloudApp(lastApp.appId)) {
                    String newCDomain = CloudAppCheckConfig.getCloudAppId(newApp.appId);
                    if (!TextUtils.isEmpty(newCDomain)) {
                        //SS/CC
                        if (appStack.size() == 1) {
                            onDomainChanged(newCDomain, null);
                            //CS
                        } else if (appStack.size() == 2) {
                            if (newApp.type != AppInfo.TYPE_CUT ||
                                    lastApp.type != AppInfo.TYPE_CUT) {
                                Logger.e("wtf 1!");
                            }
                            AppInfo sAppInfo = appStack.get(1);
                            /*String sDomain = sAppInfo.appId;
                            if (CloudAppCheckConfig.isCloudApp(sAppInfo.appId)) {
                                sDomain = CloudAppCheckConfig.getCloudAppId(sAppInfo.appId);
                            }*/
                            String sDomain = CloudAppCheckConfig.getFinalAppId(sAppInfo.appId);
                            onDomainChanged(newCDomain, sDomain);
                        }
                    }
                }
                return;
            }

            String newCDomain = null;
            String newSDomain = null;

            /*if (CloudAppCheckConfig.isCloudApp(newApp.appId)) {
                newCDomain = CloudAppCheckConfig.getCloudAppId(newApp.appId);
            } else {
                newCDomain = newApp.appId;
            }*/

            newCDomain = CloudAppCheckConfig.getFinalAppId(newApp.appId);

            if (appStack.size() == 1) {
                if (lastApp.type == AppInfo.TYPE_SCENE &&
                        newApp.type == AppInfo.TYPE_CUT) {
                    appStack.push(newApp);
                    /*if (CloudAppCheckConfig.isCloudApp(lastApp.appId)) {
                        newSDomain = CloudAppCheckConfig.getCloudAppId(lastApp.appId);
                    } else {
                        newSDomain = lastApp.appId;
                    }*/
                    newSDomain = CloudAppCheckConfig.getFinalAppId(lastApp.appId);
                    onDomainChanged(newCDomain, newSDomain);
                } else {
                    appStack.pop();
                    appStack.push(newApp);
                    onDomainChanged(newCDomain, null);
                }
            } else if (appStack.size() == 2) {
                AppInfo stackApp = appStack.get(1);
                if (newApp.type == AppInfo.TYPE_SCENE) {
                    appStack.clear();
                    appStack.push(newApp);
                    onDomainChanged(newCDomain, null);
                } else {
                    appStack.pop();
                    appStack.push(newApp);

                    /*if (CloudAppCheckConfig.isCloudApp(stackApp.appId)) {
                        newSDomain = CloudAppCheckConfig.getCloudAppId(stackApp.appId);
                    } else {
                        newSDomain = stackApp.appId;
                    }*/
                    newSDomain = CloudAppCheckConfig.getFinalAppId(stackApp.appId);
                    onDomainChanged(newCDomain, newSDomain);
                }
            }
        }
        Logger.d("pushApp appStack size : " + appStack.size() + " top app is " + peekApp());
    }

    public synchronized boolean popApp( AppInfo appInfo ) {
        if (appStack.empty() || appInfo == null || appStack.peek() == null) {
            Logger.d("appStack is empty or appInfo is null");
            return false;
        }

        if (TextUtils.isEmpty(appInfo.appId)) {
            Logger.d("appInfo appId is null !!!");
            return false;
        }

        AppInfo topApp = appStack.peek();

        if (CloudAppCheckConfig.getFinalAppId(appInfo.appId).equals(CloudAppCheckConfig.getFinalAppId(topApp.appId))) {
            Logger.d("target app is the same with topApp, so appStack pop app");
            appStack.pop();
        }

        Logger.d("popApp appStack size : " + appStack.size() + " top app is " + peekApp());

        return true;
    }

    public synchronized AppInfo peekApp() {
        if (appStack.empty())
            return null;
        return appStack.peek();
    }

    public synchronized boolean exitSessionDomain( String endSessionAppId ) {
        if (appStack.empty() || TextUtils.isEmpty(endSessionAppId)) {
            Logger.d("appStack is empty or endSessionAppId is null");
            return false;
        }

        AppInfo topAppInfo = appStack.peek();

        Logger.d("topAppInfo appId : " + topAppInfo.appId + " endSessionAppId : " + endSessionAppId);
        //Notice: appId could be cloud app.
        if (CloudAppCheckConfig.getFinalAppId(endSessionAppId).equals(CloudAppCheckConfig.getFinalAppId(topAppInfo.appId))) {
            Logger.d("endSessionId is the same with topAppId, so appStack pop app");
            appStack.pop();
        }

        Logger.d("exitSessionDomain appStack size : " + appStack.size() + " top app is " + peekApp());

        if (appStack.size() == 2) {
            onDomainChanged(CloudAppCheckConfig.getFinalAppId(appStack.get(1).appId), CloudAppCheckConfig.getFinalAppId(appStack.get(0).appId));
        } else if (appStack.size() == 1) {
            onDomainChanged(CloudAppCheckConfig.getFinalAppId(appStack.get(0).appId), null);
        } else {
            onDomainChanged(null, null);
        }

        /*
        if (lastApp.type == AppInfo.TYPE_CUT) {
        	appStack.pop();
        	if (!appStack.empty()) {
                AppInfo stackApp = appStack.peek();
                if (stackApp != null && !TextUtils.isEmpty(stackApp.appId)) {
                	String cdomain = null;
                	if (CloudAppCheckConfig.isCloudApp(stackApp.appId)) {
                		cdomain = CloudAppCheckConfig.getCloudAppId(stackApp.appId);
                	} else {
                		cdomain = stackApp.appId;
                	}
                	onDomainChanged(cdomain, null);
                }
            }
        }
        */

        return true;

    }

    public synchronized AppInfo getLastApp() {
        if (appStack.isEmpty() || appStack.size() == 1) {
            Logger.d("getLastApp invalidate");
            return null;
        } else {
            AppInfo currentApp = appStack.pop();
            AppInfo lastApp = appStack.peek();
            onDomainChanged(CloudAppCheckConfig.getFinalAppId(currentApp.appId), CloudAppCheckConfig.getFinalAppId(lastApp.appId));
            return lastApp;
        }
    }

    private void onDomainChanged(String cdomain, String sdomain) {
        Logger.d("onDomainChanged current appId = " + cdomain + " lastDomain = " + sdomain);
        if (onDomainChangedListener != null) {
            try {
                onDomainChangedListener.onDomainChange(cdomain, sdomain);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized int queryAppInfo(AppInfo appInfo) {
        return appStack.search(appInfo);
    }

    public synchronized boolean isAppStackEmpty() {
        return appStack.isEmpty();
    }

    public synchronized boolean isAppExitInStack( String appId ) {
        if (appStack.isEmpty() || TextUtils.isEmpty(appId))
            return false;
        for (AppInfo stackAppInfo : appStack) {
            if (appId.equals(stackAppInfo.appId)) {
                return true;
            }
        }

        return false;
    }

    public synchronized Stack<AppInfo> getAppStack() {
        return appStack;
    }

    public synchronized int getAppNum() {
        return appStack.size();
    }

    public synchronized void clearAppStack() {
        Logger.d("clearAppStack");
        appStack.clear();
        onDomainChanged(null, null);
    }

    public synchronized List<String> queryDomainState() {
        ArrayList<String> lists = new ArrayList<String>();
        String cDomain = null;
        String sDomain = null;

        if (appStack.empty()) {
            return lists;
        }

        if (appStack.size() == 1) {
            AppInfo cAppInfo = appStack.get(0);
            /*cDomain = cAppInfo.appId;
            if (CloudAppCheckConfig.isCloudApp(cAppInfo.appId)) {
                cDomain = CloudAppCheckConfig.getCloudAppId(cAppInfo.appId);
            }*/
            cDomain = CloudAppCheckConfig.getFinalAppId(cAppInfo.appId);
        } else if (appStack.size() == 2) {
            AppInfo cAppInfo = appStack.get(0);
            AppInfo sAppInfo = appStack.get(1);
            /*cDomain = cAppInfo.appId;
            if (CloudAppCheckConfig.isCloudApp(cAppInfo.appId)) {
                cDomain = CloudAppCheckConfig.getCloudAppId(cAppInfo.appId);
            }*/
            cDomain = CloudAppCheckConfig.getFinalAppId(cAppInfo.appId);
            /*sDomain = sapp.appId;
            if (CloudAppCheckConfig.isCloudApp(sapp.appId)) {
                sDomain = CloudAppCheckConfig.getCloudAppId(sapp.appId);
            }*/
            sDomain = CloudAppCheckConfig.getFinalAppId(sAppInfo.appId);
        }

        if (!TextUtils.isEmpty(cDomain)) {
            lists.add(cDomain);
        }

        if (!TextUtils.isEmpty(sDomain)) {
            lists.add(sDomain);
        }
        return lists;
    }

    private static class SingleHolder {
        private static final AppStack instance = new AppStack();
    }

}
