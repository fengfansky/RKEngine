// IAppStateCallback.aidl
package rokid.rkengine;

import rokid.rkengine.scheduler.AppInfo;
import rokid.rkengine.scheduler.AppException;
interface IAppStateCallback {

    void onAppInstalledSuccess(in AppInfo appInfo);

    void onAppUninstalledSuccess(in AppInfo appInfo);

    void onCreate(in AppInfo appInfo);

    void onStart(in AppInfo appInfo);

    void onPause(in AppInfo appInfo);

    void onResume(in AppInfo appInfo);

    void onStop(in AppInfo appInfo);

    void onAppError(in AppException exception);

}

