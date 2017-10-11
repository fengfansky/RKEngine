package com.rokid.rkengine.confirm;

import com.android.okhttp.Response;
import com.rokid.rkengine.service.RkEngineService;
import com.rokid.rkengine.utils.Logger;

import java.io.IOException;
import java.util.List;

/**
 * Created by fanfeng on 2017/9/15.
 */

public class ConfirmReporter implements Runnable {

    private String confirmIntent;
    private String confirmSlot;
    private List<String> confirmOptions;
    private String appId;
    private String attributes;

    private static final String SIREN_TYPE_CONFIRM = "CONFIRM";
    private static final int SIREN_TIME = 6000;

    public ConfirmReporter( String confirmIntent, String confirmSlot, List<String> confirmOptions, String appId, String attributes ) {
        this.confirmIntent = confirmIntent;
        this.confirmSlot = confirmSlot;
        this.confirmOptions = confirmOptions;
        this.appId = appId;
        this.attributes = attributes;
    }

    @Override
    public void run() {

        /*String appId = null;
        AppInfo appInfo = AppStack.getInstance().peekApp();
        if (appInfo != null) {
            appId = appInfo.appId;
            Logger.d(" appId : " + appInfo.appId);
        }*/
        Confirm.SetConfirmRequest confirmRequest = Confirm.SetConfirmRequest.newBuilder().setAppId(appId).setConfirmIntent(confirmIntent)
                .setConfirmSlot(confirmSlot).setAttributes(attributes).build();
        for (int i = 0; i < confirmOptions.size(); i++) {
            confirmRequest.toBuilder().setConfirmOptions(i, confirmOptions.get(i)).build();
        }

        Logger.i("request body is " + confirmRequest.toString());
        Response response;
        Confirm.SetConfirmResponse confirmResponse;
        try {
            ConfirmRequestConfig.initDeviceInfo();
            Logger.d("url is " + ConfirmRequestConfig.getUrl());
            response = HttpClientWrapper.getInstance().sendRequest(ConfirmRequestConfig.getUrl(), confirmRequest);

            if (response != null && response.body() != null) {
                String respString = response.body().string();
                Logger.i(" confirm respString is : " + respString);
                confirmResponse = Confirm.SetConfirmResponse.parseFrom(respString.getBytes());
                if (confirmResponse != null && confirmResponse.getSuccess()) {
                    RkEngineService.getEngineService().openSiren(SIREN_TYPE_CONFIRM, true, SIREN_TIME);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            HttpClientWrapper.getInstance().close();
        }

    }
}
