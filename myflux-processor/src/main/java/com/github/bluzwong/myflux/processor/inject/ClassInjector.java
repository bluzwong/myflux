package com.github.bluzwong.myflux.processor.inject;

import java.util.*;

/**
 * Created by geminiwen on 15/5/21.
 */
public class ClassInjector {

    private final String classPackage;
    private final String className;
    private final String originClassName;
    private final Set<MethodInjector> methods;
    private final List<String> types = new ArrayList<String>();
    private static final String SUFFIX = "_Flux_Dispatcher";

    public ClassInjector(String classPackage, String className) {
        this.classPackage = classPackage;
        this.originClassName = className;
        this.className = className + SUFFIX;
        this.methods = new LinkedHashSet<MethodInjector>();
    }

    public void addMethod(MethodInjector e) {
        methods.add(e);
    }
    public void addType(String type) {
        if (types.contains(type)) {
            return;
        }
        types.add(type);
    }
    public String getFqcn() {
        return classPackage + "." + className;
    }


    public String brewJava() throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(classPackage).append(";\n");
        builder.append("import com.github.bluzwong.myflux.lib.switchtype.ReceiveTypeDispatcher;\n");
        builder.append("import java.util.Map;\n");
        builder.append("public class ").append(className).append(" implements ReceiveTypeDispatcher {\n");
        builder.append("@Override\n");
        builder.append("public void dispatchType(Object target, Map<String, Object> dataMap, String type) {\n");
        builder.append(originClassName).append(" receiver = (").append(originClassName).append(") target;\n");
        builder.append("switch(type) {\n");

        for (String type : types) {
            builder.append("case ").append(type).append(" : {\n");
            for (MethodInjector method : methods) {
                builder.append(method.brewInvokeType(type));
            }
            builder.append("break;\n");
            builder.append("}\n");
        }

        builder.append("}\n");
        builder.append("}\n");
        builder.append("}\n");
        return builder.toString();
    }
}
