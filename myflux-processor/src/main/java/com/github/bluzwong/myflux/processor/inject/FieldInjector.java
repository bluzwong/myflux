package com.github.bluzwong.myflux.processor.inject;

/**
 * Created by wangzhijie@wind-mobi.com on 2015/9/24.
 */
public class FieldInjector {
    private String fieldName;

    public FieldInjector(String fieldName) {
        this.fieldName = fieldName;
    }

    public String brewJava() throws Exception{
        StringBuilder builder = new StringBuilder();
        builder.append("\nsavingData.put(").append(fieldName).append(", value);");
        return builder.toString();
    }
}
