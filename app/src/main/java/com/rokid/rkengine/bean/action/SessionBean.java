package com.rokid.rkengine.bean.action;

/**
 * Session indicates the session for the CloudApp that currently be requested.
 * Whenever a request is created, the session information will be updated by system.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/7
 */
public class SessionBean<T> extends BaseBean {

    private String sessionId;
    /**
     * indicates whether it is a new session
     */
    private boolean newSession;
    private String applicationId;
    private String domain;
    /**
     * session attributes set by CloudApp in Response
     */
    private T attributes;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isNewSession() {
        return newSession;
    }

    public void setNewSession(boolean newSession) {
        this.newSession = newSession;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public T getAttributes() {
        return attributes;
    }

    public void setAttributes(T attributes) {
        this.attributes = attributes;
    }

}
