package com.rokid.rkengine.bean.action.response.action.media;

import com.rokid.rkengine.bean.action.response.action.BaseActionBean;

/**
 * Media is used to play streaming media.
 * Both audio and video are supported. Action, behaviour and media item are defined in media section.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/7
 */
public class MediaBean extends BaseActionBean {

    /**
     * Defines the action of the media player.
     * ONLY TWO actions are supported, which are PLAY and PAUSE.
     */

    private MediaItemBean item;

    public MediaItemBean getItem() {
        return item;
    }

    public void setItem(MediaItemBean item) {
        this.item = item;
    }

    public boolean isValid() {
        return isActionValid() && isItemValid();
    }

    public boolean isItemValid() {
        return null != item && item.isValid();
    }

}
