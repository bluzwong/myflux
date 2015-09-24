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
        builder.append("savingData.put(\"").append(fieldName).append("\", target.").append(fieldName).append(");\n");
        builder.append("savedNames.add(\"").append(fieldName).append("\");\n");
        return builder.toString();
    }

    public String brewJavaRestore()  {
        StringBuilder builder = new StringBuilder();
        builder.append("target." + fieldName).append("=tmp;\n");
        return builder.toString();
    }
}
