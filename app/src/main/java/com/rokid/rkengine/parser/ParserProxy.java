package com.rokid.rkengine.parser;

import android.text.TextUtils;

import com.rokid.rkengine.scheduler.AppManagerImp;
import com.rokid.rkengine.scheduler.AppStarter;
import com.rokid.rkengine.utils.CloudAppCheckConfig;
import com.rokid.rkengine.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import rokid.rkengine.scheduler.AppInfo;

/**
 * Created by fanfeng on 2017/4/16.
 */

public class ParserProxy {

	private AppStarter appStarter;

	private static final String FORM_SCENE = "scene";

	private static final String FORM_CUT = "cut";

	public static final String INTENT_EXECUTE = "ROKID.INTENT.EXECUTE";

	public static final String INTENT_EXIT = "ROKID.INTENT.EXIT";

	public static final String INTENT_CLEAR = "ROKID.INTENT.CLEAR";

	public static ParserProxy getInstance() {

		return SingleHolder.instance;
	}

	public void startParse( final String nlpStr, String asr, String actionStr ) {

		if (TextUtils.isEmpty(nlpStr) || TextUtils.isEmpty(actionStr)) {
			Logger.e("str empty error!! action:" + actionStr + " nlp: " + nlpStr + " asr: " + asr);
			return;
		}
		Logger.d("action  ---> " + actionStr);
		Logger.d("nlp -------> " + nlpStr);
		Logger.d("asr -------> " + asr);

		JSONObject nlpObject = null;
		try {
			nlpObject = new JSONObject(nlpStr);
		} catch (JSONException e) {
			Logger.e(" JSONParseException : parse nlp error !");
			e.printStackTrace();
		}

		if (nlpObject == null) {
			Logger.d("nlp is null!");
			return;
		}

		String appId = null;
		try {
			appId = (String) nlpObject.get("appId");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (TextUtils.isEmpty(appId)) {
			Logger.d(" appId is null !");
			return;
		}

		boolean cloud = false;

		try {
			cloud = (boolean) nlpObject.get("cloud");
		} catch (JSONException e) {
			e.printStackTrace();
		}

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

		String intent = null;
		try {
			intent = nlpObject.getString("intent");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (INTENT_EXIT.equals(intent)) {
			Logger.d(" exit app !");
			if (cloud) {
				switch (form) {
					case FORM_SCENE:
						appId = CloudAppCheckConfig.CLOUD_SCENE_APP_PACKAGE_NAME;
						break;
					case FORM_CUT:
						appId = CloudAppCheckConfig.CLOUD_CUT_APP_PACKAGE_NAME;
						break;
					default:
						Logger.d("unknow form :  " + form);
				}
			}
			AppInfo appInfo = AppManagerImp.getInstance().queryAppInfoByID(appId);
			AppManagerImp.getInstance().stopApp(appInfo);
			return;
		} else if (INTENT_CLEAR.equals(intent)) {
			Logger.d("exit all app !");
			AppManagerImp.getInstance().stopAllApp();
			return;
		} else {
			appStarter = new AppStarter();
			if (cloud) {
				switch (form) {
					case FORM_SCENE:
						appStarter.startCloudApp(CloudAppCheckConfig.CLOUD_SCENE_APP_PACKAGE_NAME, appId, actionStr);
						break;
					case FORM_CUT:
						appStarter.startCloudApp(CloudAppCheckConfig.CLOUD_CUT_APP_PACKAGE_NAME, appId, actionStr);
						break;
					default:
						Logger.d("unknow form :  " + form);
				}
			} else {
				appStarter.startNativeApp(appId, nlpStr);
			}
		}

	}

	private static class SingleHolder {
		private static final ParserProxy instance = new ParserProxy();
	}

}

