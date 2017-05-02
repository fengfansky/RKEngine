package com.rokid.rkengine.scheduler;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.rokid.rkengine.bean.action.ActionResponse;
import com.rokid.rkengine.bean.action.ResponseBean;
import com.rokid.rkengine.bean.nlp.NLPBean;
import com.rokid.rkengine.utils.Logger;

import java.util.Map;

import rokid.rkengine.scheduler.AppInfo;

/**
 * Created by fanfeng on 2017/4/27.
 */

public class AppStarter {

    private static final String KEY_NLP = "nlp";
    private static final String CLOUD_APP_PACKAGE_NAME = "com.rokid.cloudappclient";
    private static final String CLOUD_APP_ACTIVITY_NAME = "com.rokid.cloudappclient.activity.CloudActivity";
    private AppManagerImp appManager = AppManagerImp.getInstance();

    public void startCloudApp(Context context, String domain, String intentType, Map<String, String> slots) {
        if (context == null)
            return;
        NLPBean nlpBean = new NLPBean();
        nlpBean.setDomain(domain);
        nlpBean.setIntent(intentType);
        nlpBean.setSlots(slots);
        String nlpStr = new Gson().toJson(nlpBean);

        Intent cloudAppIntent = new Intent();
        ComponentName component = new ComponentName(CLOUD_APP_PACKAGE_NAME, CLOUD_APP_ACTIVITY_NAME);
        cloudAppIntent.setComponent(component);
        cloudAppIntent.setAction(Intent.ACTION_MAIN);
        cloudAppIntent.putExtra(KEY_NLP, nlpStr);
        cloudAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Logger.d("intent startCloudApp");
        context.startActivity(cloudAppIntent);
    }

    public void startNativeApp(String nlpStr, ActionResponse actionResponse) {
        final AppInfo appInfo = appManager.queryAppInfoByID(actionResponse.getAppId());
        if (appInfo == null) {
            Logger.d("appInfo is null ");
            return;
        }

        String shot = actionResponse.getResponse().getShot();
        Logger.d("aidl startNativeApp " + shot);
        switch (shot) {
            case ResponseBean.SHOT_SCENE:
                appInfo.type = AppInfo.TYPE_SCENE;
                break;

            case ResponseBean.SHOT_CUT:
                appInfo.type = AppInfo.TYPE_CUT;
                break;

            case ResponseBean.SHOT_SERVICE:
                appInfo.type = AppInfo.TYPE_SERVICE;
                break;

            default:
                appInfo.type = AppInfo.TYPE_ANY;
        }

        appManager.startApp(appInfo, nlpStr);
        appManager.storeNLP(appInfo.appId, nlpStr);
    }

}
