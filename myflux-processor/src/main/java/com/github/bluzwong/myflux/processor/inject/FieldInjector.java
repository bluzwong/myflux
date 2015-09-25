package com.github.bluzwong.myflux.processor.inject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangzhijie@wind-mobi.com on 2015/9/24.
 */
public class FieldInjector {
    private String fieldName;
    private String type;
    public FieldInjector(String fieldName, String type) {
        this.fieldName = fieldName;
        this.type = type;
    }

    public String brewJava() throws Exception{
        StringBuilder builder = new StringBuilder();
        builder.append("savingData.put(\"").append(fieldName).append("\", target.").append(fieldName).append(");\n");
        return builder.toString();
    }

    public String brewJavaRestore()  {
        StringBuilder builder = new StringBuilder();

        builder.append("tmp = savedData.get(\"" + fieldName + "\");\n");
        builder.append("target." + fieldName).append("= ( " + type +")tmp;\n");
        return builder.toString();
    }
    public static void main(String[] args) {
        Map<String, String> nameTypes = new HashMap<String, String>();
        String s = "aaaa";
        Object o = s;
        String fieldName = "";
        Class clz = o.getClass();

       // String aaa = clz.cast(o);
        //System.out.println(aaa);
        nameTypes.put(fieldName, s.getClass().getCanonicalName());
    }
}
