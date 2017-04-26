package com.rokid.rkengine.bean;


import com.rokid.rkengine.bean.action.ActionResponse;
import com.rokid.rkengine.bean.action.BaseBean;
import com.rokid.rkengine.bean.nlp.NLPBean;

/**
 * Created by showingcp on 3/13/17.
 */

public class CommonResponse extends BaseBean {

    /**
     * corresponding asr result for current response
     */
    private String asr;

    /**
     * corresponding nlp result for current response
     */
    private NLPBean nlp;

    /**
     * corresponding CloudAppResponse for current response
     */
    private ActionResponse action;

    public String getAsr() {
        return asr;
    }

    public void setAsr(String asr) {
        this.asr = asr;
    }

    public NLPBean getNlp() {
        return nlp;
    }

    public void setNlp(NLPBean nlp) {
        this.nlp = nlp;
    }

    public ActionResponse getAction() {
        return action;
    }

    public void setAction(ActionResponse action) {
        this.action = action;
    }
}
