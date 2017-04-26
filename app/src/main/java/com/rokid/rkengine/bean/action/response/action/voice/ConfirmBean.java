package com.rokid.rkengine.bean.action.response.action.voice;

import android.text.TextUtils;

import com.rokid.rkengine.bean.action.BaseBean;

/**
 * Defines the Confirm content for confirm request
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/9
 */
public class ConfirmBean extends BaseBean {

    private String tts;
    private String confirmIntent;
    private String confirmSlot;
    private ConfirmAttributesBean confirmAttributes;

    public String getTts() {
        return tts;
    }

    public void setTts(String tts) {
        this.tts = tts;
    }

    public String getConfirmIntent() {
        return confirmIntent;
    }

    public void setConfirmIntent(String confirmIntent) {
        this.confirmIntent = confirmIntent;
    }

    public String getConfirmSlot() {
        return confirmSlot;
    }

    public void setConfirmSlot(String confirmSlot) {
        this.confirmSlot = confirmSlot;
    }

    public ConfirmAttributesBean getConfirmAttributes() {
        return confirmAttributes;
    }

    public void setConfirmAttributes(ConfirmAttributesBean confirmAttributes) {
        this.confirmAttributes = confirmAttributes;
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(tts);
    }

}
