package com.rokid.rkengine.scheduler;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.rokid.rkengine.bean.nlp.NLPBean;
import com.rokid.rkengine.utils.CloudAppCheckConfig;
import com.rokid.rkengine.utils.Logger;

import java.util.Map;

import rokid.rkengine.scheduler.AppInfo;

/**
 * Created by fanfeng on 2017/4/27.
 */

public class AppStarter {

    private AppManagerImp appManager = AppManagerImp.getInstance();

    public void startCloudApp(String appId, String cloudAppId, String intentType, Map<String, String> slots) {

        Logger.d(" startCloudApp appId:" + appId + " cloudAppId:" + cloudAppId + " intentType:" + intentType + " slots:" + slots.toString());
        NLPBean nlpBean = new NLPBean();
        nlpBean.setAppId(cloudAppId);
        nlpBean.setIntent(intentType);
        nlpBean.setCloud(true);
        nlpBean.setSlots(slots);
        String nlpStr = new Gson().toJson(nlpBean);

        startNativeApp(nlpStr, appId);
        CloudAppCheckConfig.storeCloudAppId(appId, cloudAppId);
    }


    public void startNativeApp(String nlpStr, String appId) {
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
