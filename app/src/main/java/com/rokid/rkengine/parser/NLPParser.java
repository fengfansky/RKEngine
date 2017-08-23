package com.rokid.rkengine.parser;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.rokid.rkengine.bean.nlp.NLPBean;

/**
 * Created by fanfeng on 2017/4/16.
 */

public class NLPParser extends BaseParser<String, NLPBean> {

    public NLPParser(String response) {
        super(response);
    }

    @Override
    boolean checkIegality() {
        return !TextUtils.isEmpty(response);

    }

    @Override
    NLPBean parse() {

        // parse NLP result
        NLPBean nlp = null;
        try {
            nlp = new Gson().fromJson(response, NLPBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nlp;
    }
}
