package com.rokid.rkengine.parser;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.rokid.rkengine.bean.CommonResponse;
import com.rokid.rkengine.bean.action.ResponseBean;
import com.rokid.rkengine.bean.action.response.action.ActionBean;
import com.rokid.rkengine.bean.action.response.action.media.MediaBean;
import com.rokid.rkengine.bean.action.response.action.voice.VoiceBean;
import com.rokid.rkengine.bean.nlp.NLPBean;
import com.rokid.rkengine.scheduler.AppManagerImp;
import com.rokid.rkengine.bean.action.ActionResponse;
import com.rokid.rkengine.utils.CommonConfig;
import com.rokid.rkengine.utils.Logger;

import java.util.HashMap;
import java.util.Map;

import rokid.rkengine.scheduler.AppInfo;

/**
 * Created by fanfeng on 2017/4/16.
 */

public class ParserProxy {

    private static final String KEY_NLP = "nlp";
    private static final String KEY_COMMON_RESPONSE = "extra";

    private static final String CLOUD_APP_PACKAGE_NAME = "com.rokid.cloudappclient";

    private static final String CLOUD_APP_ACTIVITY_NAME = "com.rokid.cloudappclient.activity.CloudActivity";

    private static final String REMOTE_IRUNTIME_SERVICE = "rokid.os.IRuntimeService";

    private AppManagerImp appManager = AppManagerImp.getInstance();

    public static ParserProxy getInstance() {

        return SingleHolder.instance;
    }

    private static class SingleHolder {
        private static final ParserProxy instance = new ParserProxy();
    }

    public void startParse(Context context, final String nlpStr, String asr, String actionStr) {

        if (TextUtils.isEmpty(nlpStr) || TextUtils.isEmpty(asr) || TextUtils.isEmpty(actionStr)) {
            Logger.e("str empty error!! action:" + actionStr + " nlp: " + nlpStr + " asr: " + asr);
            return;
        }
        Logger.d("action:" + actionStr + " nlp: " + nlpStr + " asr: " + asr);

        ActionResponseParser actionResponseParser = new ActionResponseParser(actionStr);
        ActionResponse actionResponse = actionResponseParser.execute();

        NLPParser nlpParser = new NLPParser(nlpStr);
        NLPBean nlp = nlpParser.execute();

        boolean isCloudApp = nlp.isCloud();

        if (actionResponse == null) {
            Logger.d("actionResponse is null");
            return;
        }

        if (nlp == null) {
            Logger.d("nlpBean is null");
            return;
        }

        if (isCloudApp) {

            if (checkAction(actionResponse))
                return;

            CommonResponse commonResponse = new CommonResponse();
            commonResponse.setAction(actionResponse);
            commonResponse.setNlp(nlp);
            commonResponse.setAsr(asr);

            final Map<String, String> slots = new HashMap<>();
            slots.put(KEY_COMMON_RESPONSE, commonResponse.toString());
            nlp.setSlots(slots);
            Logger.d("extra: " + commonResponse.toString());

            String shot = actionResponse.getResponse().getShot();

            switch (shot) {
                case ResponseBean.SHOT_SCENE:
                    startCloudApp(context, CommonConfig.SCENE_DOMAIN, "execute", slots);
                    break;

                case ResponseBean.SHOT_CUT:
                    startCloudApp(context, CommonConfig.CUT_DOMAIN, "execute", slots);
                    break;
                default:
                    Logger.d("unknow shot:  " + shot);
            }
        } else {
            //TODO appManager.queryAppInfo(appId);
            final AppInfo appInfo = appManager.queryAppInfoByID(actionResponse.getAppId());
            appInfo.domain = nlp.getDomain();
            if (appInfo == null) {
                Logger.d("appInfo is null ");
                return;
            }
            Logger.d("aidl startApp");

            appManager.startApp(appInfo, nlpStr);
            appManager.storeNLP(appInfo.appId, nlpStr);
        }
    }

    private void startCloudApp(Context context, String domain, String intentType, Map<String, String> slots) {
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
        context.startActivity(cloudAppIntent);
    }

    private boolean checkAction(ActionResponse action) {
        if (action == null) {
            Logger.i("checkCloudAppResponse: action is null");
            return true;
        }

        Logger.d("action response : " + action.toString());

        if (!CommonConfig.PROTOCOL_VERSION.equals(action.getVersion())) {
            Logger.i("checkCloudAppAction: given protocol version: " + action.getVersion() + " is invalid");
            return true;
        }

        // check response
        if (action.getResponse() == null) {
            Logger.i("checkAction: response of action is null");
            return true;
        }

        // check response domain
        if (TextUtils.isEmpty(action.getResponse().getDomain())) {
            Logger.i("checkCloudAppAction: domain for response is invalid");
            return true;
        }

        // check response shot
        String shot = action.getResponse().getShot();

        if (TextUtils.isEmpty(shot)) {
            Logger.i("checkCloudAppAction: shot for response is invalid");
            return true;
        }

        if (!shot.equals(ResponseBean.SHOT_CUT)
                && !shot.equals(ResponseBean.SHOT_SCENE)) {
            Logger.i("checkCloudAppAction: ignore for unknown shot type: " + shot);
            return true;
        }

        /*  // TODO check response id
        if (TextUtils.isEmpty(action.getResponse().getRespId())) {
            Logger.i( "checkCloudAppAction: respId is invalid");
            return true;
        }*/

        // check response type
        String resType = action.getResponse().getResType();
        if (TextUtils.isEmpty(resType)) {
            Logger.i("checkCloudAppAction: resType is invalid");
            return true;
        }

        if (!resType.equals(ResponseBean.TYPE_INTENT)
                && !resType.equals(ResponseBean.TYPE_EVENT)) {
            Logger.i("checkCloudAppAction: ignore for unknown resType: " + resType);
            return true;
        }

        // check response action
        ActionBean responseAction = action.getResponse().getAction();
        if (responseAction == null) {
            Logger.i("checkCloudAppAction: response action is null");
            return true;
        }

        // check response action type
        String responseActionType = responseAction.getType();
        if (TextUtils.isEmpty(responseActionType)) {
            Logger.i("checkCloudAppAction: response action type is invalid");
            return true;
        }

        if (!responseActionType.equals(ActionBean.TYPE_NORMAL)
                && !responseActionType.equals(ActionBean.TYPE_EXIT)) {
            Logger.i("checkCloudAppAction: ignore unknown response action type: " + responseActionType);
            return true;
        }

        // check response action elements
        if (!checkActionElements(action.getResponse())) {
            Logger.i("checkCloudAppAction: elements are invalid");
            return true;
        }
        return false;
    }

    /**
     * Private method to check voice, media and display
     *
     * @param responseBean
     * @return
     */
    private boolean checkActionElements(ResponseBean responseBean) {
        ActionBean responseAction = responseBean.getAction();
        String responseActionType = responseAction.getType();

        MediaBean mediaBean = responseAction.getMedia();
        VoiceBean voiceBean = responseAction.getVoice();

        if (mediaBean == null
                && voiceBean == null
                && responseAction.getDisplay() == null) {
            if (!responseActionType.equals(ActionBean.TYPE_EXIT)) {
                Logger.i("checkCloudAppAction: media, voice and display cannot be null when response action type is not EXIT");
                return false;
            } else {
                return true;
            }
        } else {
            if (mediaBean != null && !mediaBean.isValid()) {
                Logger.i("media is invalid");
                return false;
            }

            if (voiceBean != null && !voiceBean.isValid()) {
                Logger.i("voice is invalid");
                return false;
            }
        }

        return true;
    }

}
