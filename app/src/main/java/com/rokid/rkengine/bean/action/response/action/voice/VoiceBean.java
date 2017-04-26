package com.rokid.rkengine.bean.action.response.action.voice;

import android.text.TextUtils;

import com.rokid.rkengine.bean.action.BaseBean;


/**
 * Defines the voice interaction of CloudApps, including TTS and Confirmation.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/9
 */
public class VoiceBean extends BaseBean {

    /**
     * Current voice interaction will be appended to the end of the execution queue.
     * And the voice interaction will not be executed until all of the previous voice interactions finish execution.
     */
    public static final String BEHAVIOUR_APPEND = "APPEND";
    /**
     * current voice interaction will be executed immediately.
     * Currently executing voice interaction will be shut down and all of the previous voice interactions in queue will be cleared.
     */
    public static final String BEHAVIOUR_REPLACE_ALL = "REPLACE_ALL";
    /**
     * Current voice interaction will be appended to the end of the execution queue.
     * And currently executing voice interaction will be shut down immediately.
     */
    public static final String BEHAVIOUR_REPLACE_APPEND = "REPLACE_APPEND";
    /**
     * Currently executing voice interaction will be shut down immediately.
     * And meanwhile, the execution queue will be cleared.
     * In addition, current voice interaction in response will NOT be executed and duraton and item will NOT be in effect as well.
     */
    public static final String BEHAVIOUR_CLEAR = "CLEAR";

    private boolean needEventCallback;
    private String behaviour;
    private VoiceItemBean item;

    public boolean isNeedEventCallback() {
        return needEventCallback;
    }

    public void setNeedEventCallback(boolean needEventCallback) {
        this.needEventCallback = needEventCallback;
    }

    public String getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(String behaviour) {
        this.behaviour = behaviour;
    }

    public VoiceItemBean getItem() {
        return item;
    }

    public void setItem(VoiceItemBean item) {
        this.item = item;
    }

    public boolean isValid() {
        return isBehaviourValid() && isItemValid();
    }

    public boolean isBehaviourValid() {
        return !TextUtils.isEmpty(behaviour)
                && (BEHAVIOUR_APPEND.equals(behaviour) || BEHAVIOUR_REPLACE_ALL.equals(behaviour)
                || BEHAVIOUR_REPLACE_APPEND.equals(behaviour) || BEHAVIOUR_CLEAR.equals(behaviour));
    }

    public boolean isItemValid() {
        return null != item && item.isValid();
    }

}
