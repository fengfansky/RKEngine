package com.rokid.rkengine.utils;

import android.text.TextUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fengfan on 6/13/17.
 * <p>
 * CloudAppClient应用Id与CloudAppId的键值绑定
 */
public class CloudAppCheckConfig {

    public static final String CLOUD_SCENE_APP_PACKAGE_NAME = "com.rokid.cloudsceneclient";
    public static final String CLOUD_CUT_APP_PACKAGE_NAME = "com.rokid.cloudcutclient";

    private static Map<String, String> cloudAppIdMaps = new ConcurrentHashMap<>();

    public static boolean isCloudApp(String appId) {
        return CLOUD_CUT_APP_PACKAGE_NAME.equals(appId) || CLOUD_SCENE_APP_PACKAGE_NAME.equals(appId);
    }

    /**
     * 存储cloudAppId并与应用配置文件的appId相绑定
     *
     * @param appId      RokidManifest 设置的appId
     * @param cloudAppId cloud
     */
    public static void storeCloudAppId(String appId, String cloudAppId) {
        Logger.d("store appMap cloudAppId : " + cloudAppId + " appId : " + appId);
        if (TextUtils.isEmpty(appId)) {
            Logger.d("key is null !");
            return;
        }
        if (TextUtils.isEmpty(cloudAppId)) {
            Logger.d("value is null !");
            return;
        }
        cloudAppIdMaps.put(appId, cloudAppId);
    }

    public static void removeCloudAppId(String cloudAppId) {
        if (TextUtils.isEmpty(cloudAppId)) {
            Logger.d("cloudAppId is null !");
            return;
        }
        if (cloudAppId.equals(cloudAppIdMaps.get(CLOUD_SCENE_APP_PACKAGE_NAME))) {
            cloudAppIdMaps.remove(CLOUD_SCENE_APP_PACKAGE_NAME);
        } else if (cloudAppId.equals(cloudAppIdMaps.get(CLOUD_CUT_APP_PACKAGE_NAME))) {
            cloudAppIdMaps.remove(CLOUD_CUT_APP_PACKAGE_NAME);
        }
    }

    /**
     * appId -> cloud appId
     * @param appId
     * @return
     */
    public static String getCloudAppId(String appId) {
        return cloudAppIdMaps.get(appId);
    }


    public static String getFinalAppId(String appId) {
        if (isCloudApp(appId)) {
            Logger.d(" appId : " + appId + " get CloudAppId :" + getCloudAppId(appId));
            return getCloudAppId(appId);
        }
        return appId;
    }

}

