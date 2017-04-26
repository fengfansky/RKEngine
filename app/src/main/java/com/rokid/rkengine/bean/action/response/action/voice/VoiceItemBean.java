package com.rokid.rkengine.bean.action.response.action.voice;

import android.text.TextUtils;

import com.rokid.rkengine.bean.action.BaseBean;

/**
 * Voice's Item indicates the voice interaction content detail including TTS and Confirm.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/9
 */
public class VoiceItemBean extends BaseBean {

    private String tts;
    private ConfirmBean confirm;

    public String getTts() {
        return tts;
    }

    public void setTts(String tts) {
        this.tts = tts;
    }

    public ConfirmBean getConfirm() {
        return confirm;
    }

    public void setConfirm(ConfirmBean confirm) {
        this.confirm = confirm;
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(tts) || isConfirmValid();
    }

    public boolean isConfirmValid() {
        return null != confirm && confirm.isValid();
    }

}
