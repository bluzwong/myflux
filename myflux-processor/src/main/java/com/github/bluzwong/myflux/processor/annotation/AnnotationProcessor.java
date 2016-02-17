package com.github.bluzwong.myflux.processor.annotation;



import com.github.bluzwong.myflux.processor.inject.ClassInjector;
import com.github.bluzwong.myflux.processor.inject.MethodInjector;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.*;

/**
 * Created by wangzhijie@wind-mobi.com on 2015/9/24.
 */
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor{


    private Filer filer;
    private Elements elementUtils;
    private Types typeUtils;
    private boolean LOG_OFF = true;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        filer = env.getFiler();
        elementUtils = env.getElementUtils();
        typeUtils = env.getTypeUtils();
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add("com.github.bluzwong.myflux.lib.switchtype.ReceiveType");
        return types;
    }

    @Override public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
    /**
     * {@inheritDoc}
     *
     * @param annotations
     * @param roundEnv
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        log("start process ");
        Map<TypeElement, ClassInjector> targetClassMap = findAndParseTargets(annotations, roundEnv);
        /*for (TypeElement te : annotations) {
            // te = zhujie
            log("size " + annotations.size());
            for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
                //log("work on -> " + e.toString());
                Name name = e.getSimpleName();
                log("name -> " + name); //
                Element enclosingElement = e.getEnclosingElement();
                log("element -> " +enclosingElement); //

//                log("simplename" + e.getSimpleName());
            }
        }
*/

        for (Map.Entry<TypeElement, ClassInjector> entry : targetClassMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            ClassInjector injector = entry.getValue();
            try {
                String value = injector.brewJava();
                log(value);
                JavaFileObject jfo = filer.createSourceFile(injector.getFqcn(), typeElement);
                Writer writer = jfo.openWriter();
                writer.write(value);
                writer.flush();
                writer.close();
                //log("finish out put~~~~~~~~~~~~~~~~~");
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), typeElement);
            }
        }

        return true;
    }

    private Map<TypeElement, ClassInjector> findAndParseTargets(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, ClassInjector> targetClassMap = new LinkedHashMap<TypeElement, ClassInjector>();

        for (TypeElement te : annotations) {
            // te = zhujie
            String annoName = te.getSimpleName().toString();
            log("annoName ==>  " + te.getQualifiedName());
            if (!getSupportedAnnotationTypes().contains(te.getQualifiedName().toString())) {
                continue;
            }
            for (Element e : roundEnv.getElementsAnnotatedWith(te)) {

                log("work on -> " + e.toString());
                Name methodName = e.getSimpleName();

                ExecutableElement executableElement = (ExecutableElement) e;
                List<? extends AnnotationMirror> annotationMirrors = executableElement.getAnnotationMirrors();

                log("methodName -> " + methodName); // methodName -> doCcf
                TypeElement className = (TypeElement) e.getEnclosingElement();
                log("fieldInClass -> " + className); // fieldInClass -> com.github.bluzwang.myflux.example.demo.DemoActivity


                log("fieldType -> " + e.asType().toString()); //fieldType -> (java.util.Map<java.lang.String,java.lang.Object>)void
                // fieldType -> (java.util.Map<java.lang.String,java.lang.Object>,java.lang.String)void
//                log("simplename" + e.getSimpleName());

                final MethodInjector methodInjector = new MethodInjector(methodName.toString(), e.asType().toString().equals("(com.github.bluzwong.myflux.lib.FluxResponse)void"));
                final ClassInjector injector = getOrCreateTargetClass(targetClassMap, className);
                for (AnnotationMirror mirror : annotationMirrors) {
                    DeclaredType annotationType = mirror.getAnnotationType();
                    log("annotationType => " + annotationType); //annotationType => com.github.bluzwong.myflux.lib.switchtype.ReceiveType
                    Map<? extends ExecutableElement, ? extends AnnotationValue> values = mirror.getElementValues();
                    for (ExecutableElement execElement:values.keySet()) {
                        AnnotationValue annotationValue = values.get(execElement);
                        log("execElement: " + execElement + " => annotationValue : " + annotationValue);
                        log("annotationValue cls => " + annotationValue.getValue());
                        if (!execElement.toString().equals("type()")) {
                            continue;
                        }
                        // execElement: type() => annotationValue : {"ccf"}
                        // execElement: type() => annotationValue : {"wsd", "ccf"}
                        String valueString = annotationValue.getValue().toString();
                        String[] typeValues = valueString.split(",");
                        for (String typeValue : typeValues) {
                            methodInjector.addType(typeValue);
                            injector.addType(typeValue);
                        }
                    }
                }

                injector.addMethod(methodInjector);

            }

        }
        return targetClassMap;
    }
    /**
     *
     *
     * @param targetClassMap
     * @param enclosingElement
     * @return
     */

    private ClassInjector getOrCreateTargetClass(Map<TypeElement, ClassInjector> targetClassMap, TypeElement enclosingElement) {
        ClassInjector injector = targetClassMap.get(enclosingElement);
        if (injector == null) {
            String classPackage = getPackageName(enclosingElement);
            String className = getClassName(enclosingElement, classPackage);
            injector = new ClassInjector(classPackage, className);
            targetClassMap.put(enclosingElement, injector);
        }
        return injector;
    }

    /**
     *
     * @param type
     * @param packageName
     * @return
     */
    private String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    /**

     *
     * @param type
     * @return
     */
    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }
    private void log(String msg) {
        if (LOG_OFF) { return;}
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
    }
}
