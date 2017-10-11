package com.rokid.rkengine.confirm;

import android.text.TextUtils;

import com.rokid.rkengine.md5.MD5Utils;
import com.rokid.rkengine.utils.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by fanfeng on 2017/5/15.
 */

public class ConfirmRequestConfig {

    private static final String BASE_HTTP = "https://";

    private static final String DEFAULT_HOST = "apigwrest.open.rokid.com";

    private static final String SEND_EVENT_PATH = "/v1/skill/dispatch/setConfirm";

    private static String mHost;

    private static final String PARAM_KEY_KEY = "key";
    private static final String PARAM_KEY_DEVICE_TYPE_ID = "device_type_id";
    private static final String PARAM_KEY_DEVICE_ID = "device_id";
    private static final String PARAM_KEY_SERVICE = "service";
    private static final String PARAM_VALUE_SERVICE = "rest";
    private static final String PARAM_KEY_VERSION = "version";
    private static final String PARAM_KEY_TIME = "time";
    private static final String PARAM_KEY_SIGN = "sign";
    private static final String PARAM_KEY_SECRET = "secret";

    private static final String PARAM_KEY_ACCOUNTID = "accountId";

    private static final String KEY_HOST = "event_req_host";

    private static Map<String, String> params;

    private static void putUnEmptyParam( String key, String value ) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            Logger.d("param invalidate ! key " + key + " value : " + value);
            return;
        }
        params.put(key, value);
    }

    public static Map<String, String> initDeviceInfo() {
        Map<String, String> deviceMap = null;
        rokid.os.IRuntimeService runtime = rokid.os.IRuntimeService.Stub.asInterface(android.os.ServiceManager.getService("runtime_java"));
        try {
            deviceMap = runtime.getPlatformAccountInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (deviceMap == null || deviceMap.isEmpty()) {
            Logger.d(" deviceMap is null ");
            return null;
        }

        Logger.d(" deviceMap is " + deviceMap.toString());

        mHost = deviceMap.get(KEY_HOST);

        params = new LinkedHashMap<>();
        putUnEmptyParam(PARAM_KEY_KEY, deviceMap.get(PARAM_KEY_KEY));
        putUnEmptyParam(PARAM_KEY_DEVICE_TYPE_ID, deviceMap.get(PARAM_KEY_DEVICE_TYPE_ID));
        putUnEmptyParam(PARAM_KEY_DEVICE_ID, deviceMap.get(PARAM_KEY_DEVICE_ID));

        putUnEmptyParam(PARAM_KEY_SERVICE, PARAM_VALUE_SERVICE);
        putUnEmptyParam(PARAM_KEY_VERSION, deviceMap.get("api_version"));
        putUnEmptyParam(PARAM_KEY_TIME, String.valueOf(System.currentTimeMillis()));
        putUnEmptyParam(PARAM_KEY_SIGN, MD5Utils.generateMD5(params, deviceMap.get(PARAM_KEY_SECRET)));
        Logger.d(" params : " + params.toString());
        return params;
    }

    public static String getUrl() {

        if (mHost == null || mHost.isEmpty()) {
            mHost = DEFAULT_HOST;
        }

        return BASE_HTTP + mHost + SEND_EVENT_PATH;
    }

    public static String getAuthorization() {

        //Logger.d("param invalidate !!!");
        //initDeviceInfo();
        //return null;

        String authorization = params.toString()
                .replace("{", "").replace("}", "").replace(",", ";").replace(" ", "");
        Logger.i("authorization is " + authorization);
        return authorization;
    }

}
