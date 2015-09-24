package com.github.bluzwong.myflux.processor.inject;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by geminiwen on 15/5/21.
 */
public class ClassInjector {

    private final String classPackage;
    private final String className;
    private final Set<FieldInjector> fields;

    public ClassInjector(String classPackage, String className) {
        this.classPackage = classPackage;
        this.className = className;
        this.fields = new LinkedHashSet<>();
    }

    public void addField(FieldInjector e) {
        fields.add(e);
    }

    public String getFqcn() {
        return classPackage + "." + className;
    }

    public String brewJava() throws Exception {
        StringBuilder builder = new StringBuilder("package " + this.classPackage + ";\n");
        builder.append("public class " + this.className + " {\n");
        /*builder.append("import org.gemini.httpengine.library.*;\n");

        String action = this.isInterface ? "implements" : "extends";

        builder.append("public class " + this.className + " " + action + " " + this.targetClass + " {\n");*/
        for (FieldInjector methodInjector : fields) {
        //    builder.append(methodInjector.brewJava());
        }
        builder.append("}\n");
        return builder.toString();
    }
}
