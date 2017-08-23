package com.rokid.rkengine.bean;


import com.google.gson.Gson;

public class BaseBean {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
