package com.rokid.rkengine.bean.action;


/**
 * The response should be replied by CloudApps for client side execution.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/7
 */
public class ActionResponse extends BaseBean {

    private String appId;
    private String version;
    private boolean startWithActiveWord;
    private SessionBean session;
    private ResponseBean response;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean isStartWithActiveWord() {
        return startWithActiveWord;
    }

    public void setStartWithActiveWord(boolean startWithActiveWord) {
        this.startWithActiveWord = startWithActiveWord;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public SessionBean getSession() {
        return session;
    }

    public void setSession(SessionBean session) {
        this.session = session;
    }

    public ResponseBean getResponse() {
        return response;
    }

    public void setResponse(ResponseBean response) {
        this.response = response;
    }

}
