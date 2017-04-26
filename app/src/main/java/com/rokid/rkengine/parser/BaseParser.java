package com.rokid.rkengine.parser;

/**
 * Created by fanfeng on 2017/4/16.
 */

public abstract class BaseParser<M, T> {

    protected M response;

    BaseParser(M response) {
        this.response = response;
    }

    abstract boolean checkIegality();

    abstract T parse();

    public T execute() {

        if (!checkIegality())
            return null;

        return parse();
    }

}
