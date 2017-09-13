package com.rokid.rkengine.scheduler;

import android.text.TextUtils;

import com.rokid.rkengine.utils.CloudAppCheckConfig;
import com.rokid.rkengine.utils.Logger;

import rokid.rkengine.scheduler.AppInfo;

/**
 * Created by fanfeng on 2017/4/27.
 */

public class AppStarter {

    private AppManagerImp appManager = AppManagerImp.getInstance();

    public void startCloudApp(String appId, String cloudAppId, String extra) {

        Logger.d(" startCloudApp appId:" + appId + " cloudAppId:" + cloudAppId + " extra: " + extra);

        startNativeApp(appId, extra);
        CloudAppCheckConfig.storeCloudAppId(appId, cloudAppId);
    }

    public void startNativeApp(String appId, String nlpStr) {
        if (TextUtils.isEmpty(appId)) {
            Logger.d("native appId is null!");
            return;
        }
        final AppInfo appInfo = appManager.queryAppInfoByID(appId);
        if (appInfo == null) {
            Logger.d("native appInfo is null!");
            return;
        }
        appManager.startApp(appInfo, nlpStr);
        appManager.storeNLP(appInfo.appId, nlpStr);
    }

}
