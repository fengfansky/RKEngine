package com.rokid.rkengine.bean.action.response.action;

import android.text.TextUtils;

import com.rokid.rkengine.bean.action.BaseBean;


/**
 * Created by fanfeng on 2017/6/24.
 */

public class BaseActionBean extends BaseBean {

    public String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }


    public boolean isActionValid() {
        return !TextUtils.isEmpty(action);
    }
}
