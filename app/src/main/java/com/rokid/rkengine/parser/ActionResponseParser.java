package com.rokid.rkengine.parser;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.rokid.rkengine.bean.action.ActionResponse;
import com.rokid.rkengine.utils.Logger;

/**
 * Created by fanfeng on 2017/4/16.
 */

public class ActionResponseParser extends BaseParser<String, ActionResponse> {

    ActionResponseParser(String response) {
        super(response);
    }

    @Override
    boolean checkIegality() {

        if (TextUtils.isEmpty(response)) {
            Logger.i("checkSpeechResponse: action string is invalid");
            return false;
        }

        return true;
    }

    @Override
    ActionResponse parse() {
        // parse Action result:
        ActionResponse cloudAppResponse = null;
        try {
            cloudAppResponse = new Gson().fromJson(response, ActionResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cloudAppResponse;
    }
}
