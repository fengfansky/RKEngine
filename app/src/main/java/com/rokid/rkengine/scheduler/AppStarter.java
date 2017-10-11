package com.rokid.rkengine.scheduler;

import android.text.TextUtils;

import com.rokid.rkengine.utils.Logger;

import rokid.rkengine.scheduler.AppInfo;

/**
 *  * Created by fanfeng on 2017/4/27.
 *   */

public class AppStarter {

	private AppManagerImp appManager = AppManagerImp.getInstance();

	/**
	 *
	 * @param appId  cloudScene : CloudAppCheckConfig.CLOUD_SCENE_APP_PACKAGE_NAME
	 *               cloudCut  :  CloudAppCheckConfig.CLOUD_CUT_APP_PACKAGE_NAME
	 * @param actionStr action info
	 */
	public void startCloudApp( String appId, String actionStr ) {

		Logger.d(" startCloudApp appId:" + appId + " extra: " + actionStr);

		if (TextUtils.isEmpty(appId)) {
			Logger.d(" appId is null!");
			return;
		}
		final AppInfo appInfo = appManager.queryAppInfoByID(appId);
		if (appInfo == null) {
			Logger.d(" appInfo is null!");
			return;
		}

		appManager.startApp(appInfo, actionStr);
		appManager.storeNLP(appInfo.appId, actionStr);

		AppStack.getInstance().pushApp(appInfo);
	}

	public void startCloudService( String appId, String actionStr ) {
		Logger.d(" startCloudService appId:" + appId + " extra: " + actionStr);

		if (TextUtils.isEmpty(appId)) {
			Logger.d(" appId is null!");
			return;
		}
		final AppInfo appInfo = appManager.queryAppInfoByID(appId);
		if (appInfo == null) {
			Logger.d(" appInfo is null!");
			return;
		}

		appManager.startApp(appInfo, actionStr);
		//	appManager.storeNLP(appInfo.appId, actionStr);
	}

	public void startNativeApp( String appId, String nlpStr ) {

		Logger.d(" startNativeApp appId:" + appId + " extra: " + nlpStr);

		if (TextUtils.isEmpty(appId)) {
			Logger.d("native appId is null!");
			return;
		}
		final AppInfo appInfo = appManager.queryAppInfoByID(appId);
		if (appInfo == null) {
			Logger.d("native appInfo is null!");
			return;
		}

		Logger.d("app type : " + appInfo.type);

		appManager.startApp(appInfo, nlpStr);
		appManager.storeNLP(appInfo.appId, nlpStr);
		if (AppInfo.TYPE_SERVICE != appInfo.type) {
			AppStack.getInstance().pushApp(appInfo);
		}
	}

}

