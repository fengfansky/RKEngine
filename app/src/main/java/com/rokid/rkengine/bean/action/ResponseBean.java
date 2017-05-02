package com.rokid.rkengine.bean.action;

import android.text.TextUtils;

import com.rokid.rkengine.bean.action.response.action.ActionBean;
import com.rokid.rkengine.bean.action.response.card.CardBean;


/**
 * There are two sections in response .
 * card is the response for mobile card display. And action is for CloudAppClient.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/7
 */

public class ResponseBean extends BaseBean {

    public static final String TYPE_INTENT = "INTENT";
    public static final String TYPE_EVENT = "EVENT";

    public static final String SHOT_SCENE = "SCENE";
    public static final String SHOT_CUT = "CUT";
    public static final String SHOT_SERVICE = "SERVICE";

    private String respId;
    /**
     * ONLY type INTENT and EVENT are available currently
     */
    private String resType;

    /**
     * the application domain for the current response
     */
    private String domain;

    /**
     * indicates the application type for the domain related
     * SCENE or CUT
     */
    private String shot;
    private CardBean card;

    private ActionBean action;

    public String getRespId() {
        return respId;
    }

    public void setRespId(String respId) {
        this.respId = respId;
    }

    public String getResType() {
        return resType;
    }

    public void setResType(String resType) {
        this.resType = resType;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getShot() {
        return shot;
    }

    public void setShot(String shot) {
        this.shot = shot;
    }

    public CardBean getCard() {
        return card;
    }

    public void setCard(CardBean card) {
        this.card = card;
    }

    public ActionBean getAction() {
        return action;
    }

    public void setAction(ActionBean action) {
        this.action = action;
    }

    public boolean isResTypeValid() {
        return !TextUtils.isEmpty(resType) && (TYPE_INTENT.equals(resType) || TYPE_EVENT.equals(resType));
    }

    public boolean isDomainValid() {
        return !TextUtils.isEmpty(domain);
    }

    public boolean isShotValid() {
        return !TextUtils.isEmpty(shot) && (SHOT_SCENE.equals(shot) || SHOT_CUT.equals(shot));
    }

}
