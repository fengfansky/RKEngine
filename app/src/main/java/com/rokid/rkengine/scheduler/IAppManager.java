package com.rokid.rkengine.scheduler;

import android.content.Context;

import rokid.rkengine.scheduler.AppInfo;

/**
 * Created by fanfeng on 2017/4/18.
 */

public interface IAppManager {
    void bindService( Context context );

    void unBindService();

    void setAppStateCallBack( AppStateManager callback );

    AppInfo queryAppInfoByID( String appId );

    void startApp( AppInfo appInfo, String extra );

    void pauseApp( AppInfo appInfo );

    void resumeApp( AppInfo appInfo, String extra );

    void stopApp( AppInfo appInfo );

    void stopAllApp();
}
