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

    private Stack<AppInfo> appStack = new Stack<AppInfo>();

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

        String sdomain = null;
        String cdomain = cloudAppId;
        if (appStack.size() == 1) {
            AppInfo cAppInfo = appStack.get(0);
            //only handle top is cloud
            if (!CloudAppCheckConfig.isCloudApp(cAppInfo.appId)) {
                return;
            }

            if (appId.equals(CloudAppCheckConfig.CLOUD_CUT_APP_PACKAGE_NAME) &&
                    cAppInfo.appId.equals(CloudAppCheckConfig.CLOUD_SCENE_APP_PACKAGE_NAME)) {
                sdomain = CloudAppCheckConfig.getCloudAppId(cAppInfo.appId);
            }
        } else if (appStack.size() == 2) {
            if (appId.equals(CloudAppCheckConfig.CLOUD_SCENE_APP_PACKAGE_NAME)) {
                sdomain = null;
            } else {
                AppInfo sAppInfo = appStack.get(1);
                sdomain = sAppInfo.appId;
                if (CloudAppCheckConfig.isCloudApp(sAppInfo.appId)) {
                    sdomain = CloudAppCheckConfig.getCloudAppId(sAppInfo.appId);
                }
            }
        }

        Logger.i("try push domain change with " + cdomain + ":" + sdomain);
        onDomainChanged(cdomain, sdomain);
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
            String shouldChangedDomainAppId = newApp.appId;
            if (CloudAppCheckConfig.isCloudApp(newApp.appId)) {
                shouldChangedDomainAppId = CloudAppCheckConfig.getCloudAppId(newApp.appId);
            }

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
                            String sdomain = sAppInfo.appId;
                            if (CloudAppCheckConfig.isCloudApp(sAppInfo.appId)) {
                                sdomain = CloudAppCheckConfig.getCloudAppId(sAppInfo.appId);
                            }
                            onDomainChanged(newCDomain, sdomain);
                        }
                    }
                }
                return;
            }

            String newCDomain = null;
            String newSDomain = null;

            if (CloudAppCheckConfig.isCloudApp(newApp.appId)) {
                newCDomain = CloudAppCheckConfig.getCloudAppId(newApp.appId);
            } else {
                newCDomain = newApp.appId;
            }

            if (appStack.size() == 1) {
                if (lastApp.type == AppInfo.TYPE_SCENE &&
                        newApp.type == AppInfo.TYPE_CUT) {
                    appStack.push(newApp);
                    if (CloudAppCheckConfig.isCloudApp(lastApp.appId)) {
                        newSDomain = CloudAppCheckConfig.getCloudAppId(lastApp.appId);
                    } else {
                        newSDomain = lastApp.appId;
                    }
                    onDomainChanged(newCDomain, newSDomain);
                } else {
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

                    if (CloudAppCheckConfig.isCloudApp(stackApp.appId)) {
                        newSDomain = CloudAppCheckConfig.getCloudAppId(stackApp.appId);
                    } else {
                        newSDomain = stackApp.appId;
                    }
                    onDomainChanged(newCDomain, newSDomain);
                }
            }
        }
        Logger.d("pushApp appStack size : " + appStack.size() + " top app is " + peekApp());
    }

    public synchronized AppInfo popApp(AppInfo appInfo) {
        if (appStack.empty() || appInfo == null || appStack.peek() == null) {
            Logger.d("appStack is empty or appInfo is null");
            return null;
        }

        if (TextUtils.isEmpty(appInfo.appId)) {
            Logger.d("appInfo appId is null !!!");
            return null;
        }

        if (!appInfo.appId.equals(appStack.peek().appId)) {
            Logger.d("newApp is not the same with lastApp, dot't pop app");
            return null;
        }

        AppInfo lastApp = appStack.peek();
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
        return lastApp;
    }

    public synchronized AppInfo peekApp() {
        if (appStack.empty())
            return null;
        return appStack.peek();
    }

    public synchronized AppInfo getLastApp() {
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
        String cdomain = null;
        String sdomain = null;

        if (appStack.empty()) {
            return lists;
        }

        if (appStack.size() == 1) {
            AppInfo capp = appStack.get(0);
            cdomain = capp.appId;
            if (CloudAppCheckConfig.isCloudApp(capp.appId)) {
                cdomain = CloudAppCheckConfig.getCloudAppId(capp.appId);
            }
        } else if (appStack.size() == 2) {
            AppInfo capp = appStack.get(0);
            AppInfo sapp = appStack.get(1);
            cdomain = capp.appId;
            sdomain = sapp.appId;
            if (CloudAppCheckConfig.isCloudApp(capp.appId)) {
                cdomain = CloudAppCheckConfig.getCloudAppId(capp.appId);
            }
            if (CloudAppCheckConfig.isCloudApp(sapp.appId)) {
                sdomain = CloudAppCheckConfig.getCloudAppId(sapp.appId);
            }
        }

        if (!TextUtils.isEmpty(cdomain)) {
            lists.add(cdomain);
        }

        if (!TextUtils.isEmpty(sdomain)) {
            lists.add(sdomain);
        }
        return lists;
    }

    private static class SingleHolder {
        private static final AppStack instance = new AppStack();
    }

}
