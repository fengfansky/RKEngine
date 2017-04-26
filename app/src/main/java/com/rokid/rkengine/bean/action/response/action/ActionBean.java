package com.rokid.rkengine.bean.action.response.action;

import android.text.TextUtils;

import com.rokid.rkengine.bean.action.BaseBean;
import com.rokid.rkengine.bean.action.response.action.display.DisplayBean;
import com.rokid.rkengine.bean.action.response.action.media.MediaBean;
import com.rokid.rkengine.bean.action.response.action.voice.VoiceBean;


/**
 * There are two kinds of action.
 * One is interaction and another is media .
 * interaction is for human-machine interaction including TTS and display.
 * media is for media streaming. Besides,action includes following properties.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/7
 */
public class ActionBean extends BaseBean {

    /**
     * When type is NORMAL , voice , display and media will be executed concurrently
     */
    public static final String TYPE_NORMAL = "NORMAL";
    /**
     * When type is EXIT , the action will be shut down immediately.
     * In this case, voice , display and media will be ignored.
     */
    public static final String TYPE_EXIT = "EXIT";

    private String version;
    /**
     * Indicates the type of current action
     */
    private String type;
    /**
     * Notifies CloudDispatcher and CloudAppClient to clear session for current CloudApp.
     * In addition, when shouldEndSession is true, EventRequests will be ignored
     */
    private boolean shouldEndSession;
    private VoiceBean voice;
    private DisplayBean display;
    private MediaBean media;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isShouldEndSession() {
        return shouldEndSession;
    }

    public void setShouldEndSession(boolean shouldEndSession) {
        this.shouldEndSession = shouldEndSession;
    }

    public VoiceBean getVoice() {
        return voice;
    }

    public void setVoice(VoiceBean voice) {
        this.voice = voice;
    }

    public DisplayBean getDisplay() {
        return display;
    }

    public void setDisplay(DisplayBean display) {
        this.display = display;
    }

    public MediaBean getMedia() {
        return media;
    }

    public void setMedia(MediaBean media) {
        this.media = media;
    }

    public boolean isTypeValid() {
        return !TextUtils.isEmpty(type) && (TYPE_NORMAL.equals(type) || TYPE_EXIT.equals(type));
    }

    public boolean isVoiceValid() {
        return null != voice && voice.isValid();
    }

    public boolean isMediaValid() {
        return null != media && media.isValid();
    }

    public boolean isDisplayValid() {
        return null != display && display.isValid();
    }

}
