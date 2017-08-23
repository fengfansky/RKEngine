package com.rokid.rkengine.parser;

import android.text.TextUtils;

import com.rokid.rkengine.bean.nlp.NLPBean;
import com.rokid.rkengine.scheduler.AppStarter;
import com.rokid.rkengine.utils.CloudAppCheckConfig;
import com.rokid.rkengine.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fanfeng on 2017/4/16.
 */

public class ParserProxy {

    private static final String INTENT_EXECUTE = "execute";
    private AppStarter appStarter;

    private static final String FORM_SCENE = "scene";

    private static final String FORM_CUT = "cut";

    public static ParserProxy getInstance() {

        return SingleHolder.instance;
    }

    public void startParse(final String nlpStr, String asr, String actionStr) {

        if (TextUtils.isEmpty(nlpStr) || TextUtils.isEmpty(actionStr)) {
            Logger.e("str empty error!! action:" + actionStr + " nlp: " + nlpStr + " asr: " + asr);
            return;
        }
        Logger.d("action  ---> " + actionStr);
        Logger.d("nlp -------> " + nlpStr);
        Logger.d("asr -------> " + asr);

        NLPParser nlpParser = new NLPParser(nlpStr);
        NLPBean nlpBean = nlpParser.execute();

        if (nlpBean == null) {
            Logger.d(" nlpBean is null , nlp invalid !");
            return;
        }

        nlpBean.setAction(actionStr);

        appStarter = new AppStarter();

        String appId = nlpBean.getAppId();

        if (nlpBean.isCloud()) {

            JSONObject actionObject = null;

            try {
                actionObject = new JSONObject(actionStr);
            } catch (JSONException e) {
                Logger.e(" JSONParseException : parse action error !");
                e.printStackTrace();
            }

            if (actionObject == null) {
                Logger.d("actionObject is null !");
                return;
            }

            JSONObject responseObj = null;

            try {
                responseObj = (JSONObject) actionObject.get("response");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (responseObj == null) {
                Logger.d(" responseObj is null !");
                return;
            }

            JSONObject actionObj = null;

            try {
                actionObj = (JSONObject) responseObj.get("action");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (actionObj == null) {
                Logger.d(" actionObj is null !");
                return;
            }

            String form = null;

            try {
                form = actionObj.getString("form");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (TextUtils.isEmpty(form)) {
                Logger.d("form is null !");
                return;
            }

            switch (form) {
                case FORM_SCENE:
                    appStarter.startCloudApp(CloudAppCheckConfig.CLOUD_SCENE_APP_PACKAGE_NAME, appId, INTENT_EXECUTE, nlpBean.toString());
                    break;
                case FORM_CUT:
                    appStarter.startCloudApp(CloudAppCheckConfig.CLOUD_CUT_APP_PACKAGE_NAME, appId, INTENT_EXECUTE, nlpBean.toString());
                    break;
                default:
                    Logger.d("unknow form :  " + form);
            }
        } else {
            appStarter.startNativeApp(appId, nlpStr);
        }
    }

    private static class SingleHolder {
        private static final ParserProxy instance = new ParserProxy();
    }

}
