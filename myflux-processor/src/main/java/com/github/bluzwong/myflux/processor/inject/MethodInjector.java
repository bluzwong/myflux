package com.github.bluzwong.myflux.processor.inject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bluzwong on 2016/2/3.
 */
public class MethodInjector {
    String methodName = "";
    boolean needResponse = false;
    List<String> types = new ArrayList<String>();

    public MethodInjector(String methodName, boolean needResponse) {
        this.methodName = methodName;
        this.needResponse = needResponse;
    }

    public void addType(String type) {
        if (types.contains(type)) {
            return;
        }
        types.add(type);
    }

    public String brewInvokeType(String type) {
        if (!types.contains(type)) {
            return "";

        }
        if (needResponse) {
            return "receiver." + methodName + "(fluxResponse);\n";
        }
        return "receiver." + methodName + "();\n";
    }
}
