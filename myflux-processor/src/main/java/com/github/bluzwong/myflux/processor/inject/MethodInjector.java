package com.github.bluzwong.myflux.processor.inject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bluzwong on 2016/2/3.
 */
public class MethodInjector {
    String methodName = "";
    boolean isTwoArguments = false;
    List<String> types = new ArrayList<String>();

    public MethodInjector(String methodName, boolean isTwoArguments) {
        this.methodName = methodName;
        this.isTwoArguments = isTwoArguments;
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
        if (isTwoArguments) {
            return "receiver." + methodName + "(dataMap, type);\n";
        } else {
            return "receiver." + methodName + "(dataMap);\n";
        }
    }
}
