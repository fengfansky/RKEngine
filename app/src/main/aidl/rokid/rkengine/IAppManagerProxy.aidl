// IAppManager.aidl
package rokid.rkengine;

import rokid.rkengine.scheduler.AppInfo;
import rokid.rkengine.IAppStateCallback;

// Declare any non-default types here with import statements
interface IAppManagerProxy {

     void setAppStateCallBack(IAppStateCallback callback);

     AppInfo queryAppInfoByID(String appId);

     void startApp(in AppInfo appInfo, String extra);

     void pauseApp(in AppInfo appInfo);

     void resumeApp(in AppInfo appInfo, String extra);

     void stopApp(in AppInfo appInfo);

     void stopAllApp();
}
