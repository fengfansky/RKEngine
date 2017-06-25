package com.rokid.rkengine.parser;

import android.text.TextUtils;
import com.rokid.rkengine.bean.CommonResponse;
import com.rokid.rkengine.bean.action.ActionResponse;
import com.rokid.rkengine.bean.action.response.action.ActionBean;
import com.rokid.rkengine.bean.nlp.NLPBean;
import com.rokid.rkengine.scheduler.AppStarter;
import com.rokid.rkengine.utils.CloudAppCheckConfig;
import com.rokid.rkengine.utils.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fanfeng on 2017/4/16.
 */

public class ParserProxy {

    private static final String KEY_COMMON_RESPONSE = "extra";

    private static final String INTENT_EXECUTE = "execute";

    public static ParserProxy getInstance() {

        return SingleHolder.instance;
    }

    public void startParse(final String nlpStr, String asr, String actionStr) {

        if (TextUtils.isEmpty(nlpStr) || TextUtils.isEmpty(asr) || TextUtils.isEmpty(actionStr)) {
            Logger.e("str empty error!! action:" + actionStr + " nlp: " + nlpStr + " asr: " + asr);
            return;
        }
        Logger.d("action  ---> " + actionStr);
        Logger.d("nlp -------> " + nlpStr);
        Logger.d("asr -------> " + asr);
        ActionResponseParser actionResponseParser = new ActionResponseParser(actionStr);
        ActionResponse actionResponse = actionResponseParser.execute();

        NLPParser nlpParser = new NLPParser(nlpStr);
        NLPBean nlp = nlpParser.execute();

        if (actionResponse == null) {
            Logger.d("actionResponse is null !");
            return;
        }

        if (nlp == null) {
            Logger.d("nlpBean is null !");
            return;
        }

        String cloudAppId = nlp.getAppId();

        if (TextUtils.isEmpty(cloudAppId)) {
            Logger.d(" appId is null !");
            return;
        }

        boolean isCloudApp = nlp.isCloud();
        AppStarter appStarter = new AppStarter();

        if (isCloudApp) {

            if (actionResponse == null) {
                Logger.d(" actionResponse is null !");
                return;
            }

            CommonResponse commonResponse = new CommonResponse();
            commonResponse.setAction(actionResponse);
            commonResponse.setNlp(nlp);
            commonResponse.setAsr(asr);

            final Map<String, String> slots = new HashMap<>();
            slots.put(KEY_COMMON_RESPONSE, commonResponse.toString());

            nlp.setSlots(slots);

            ActionBean action = actionResponse.getResponse().getAction();

            if (action == null) {
                Logger.d("action is null !");
                return;
            }

            if (TextUtils.isEmpty(action.getForm())) {
                Logger.d("form is null !");
                return;
            }

            String form = action.getForm();
            switch (form) {
                case ActionBean.FORM_SCENE:
                    appStarter.startCloudApp(CloudAppCheckConfig.CLOUD_SCENE_APP_PACKAGE_NAME, cloudAppId, INTENT_EXECUTE, slots);
                    break;
                case ActionBean.FORM_CUT:
                    appStarter.startCloudApp(CloudAppCheckConfig.CLOUD_CUT_APP_PACKAGE_NAME, cloudAppId, INTENT_EXECUTE, slots);
                    break;
                default:
                    Logger.d("unknow form :  " + form);
            }
        } else {
            String appId = actionResponse.getAppId();
            appStarter.startNativeApp(nlpStr, appId);
        }
    }

    private static class SingleHolder {
        private static final ParserProxy instance = new ParserProxy();
    }

}
