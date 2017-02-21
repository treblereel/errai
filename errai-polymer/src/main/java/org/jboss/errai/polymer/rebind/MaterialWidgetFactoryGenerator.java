package org.jboss.errai.polymer.rebind;

import com.google.gwt.thirdparty.guava.common.collect.ImmutableSet;
import com.google.gwt.thirdparty.guava.common.reflect.ClassPath;
import com.google.gwt.thirdparty.guava.common.reflect.ClassPath.ClassInfo;
import com.sun.codemodel.*;
import com.sun.codemodel.writer.SingleStreamCodeWriter;
import gwt.material.design.client.base.MaterialWidget;
import org.apache.commons.lang3.ClassUtils;
import org.jboss.errai.polymer.shared.MaterialWidgetDefinition;
import org.jboss.errai.polymer.shared.MaterialWidgetFactoryNG;
import org.jboss.errai.polymer.shared.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * Created by treblereel on 3/9/17.
 */
public class MaterialWidgetFactoryGenerator {

    private static final Logger logger = LoggerFactory.getLogger(MaterialWidgetFactoryGenerator.class);
    private static final String DEFAULT_BUILD_LOCATION = "target/classes";
    private static final String GWT_MATERIAL_COMMON_PACKAGE = "gwt.material.design.client.ui.";
    private static final String GWT_MATERIAL_METHOD_PACKAGE = "gwt.material.design.client.base";
    private static final String GWT_MATERIAL_FACTORY_PACKAGE_NAME = "org.jboss.errai.polymer.shared";
    private static final String GWT_MATERIAL_FACTORY_CLASS_NAME = "MaterialWidgetFactoryNGImpl";
    private Set<String> defaultMethods = new HashSet<>();

    private static final String[] BLACKLISTED_PROPERTIES = {"FlexAlignItems", "TargetHistoryToken", "Activates", "InitialClasses", "Id", "AccessKey", "Scrollspy", "DataAttribute", "ErrorHandler", "ErrorHandlerType", "Validators", "FlexJustifyContent", "Class"};


    public MaterialWidgetFactoryGenerator() {

        try {
            buildFactory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        logger.info("MaterialWidgetFactoryGenerator");
        new MaterialWidgetFactoryGenerator();
    }

    private void buildFactory() throws IOException {

        JCodeModel jCodeModel = new JCodeModel();
        JPackage jp = jCodeModel._package(GWT_MATERIAL_FACTORY_PACKAGE_NAME);
        JDefinedClass jc = null;
        try {
            jc = jp._class(GWT_MATERIAL_FACTORY_CLASS_NAME);
        } catch (JClassAlreadyExistsException e) {
            e.printStackTrace();
        }
        jc._implements(MaterialWidgetFactoryNG.class);
        jc.annotate(ApplicationScoped.class);

        JMethod constructor = jc.constructor(JMod.PUBLIC);

        JClass keyClass = jCodeModel.ref(String.class);
        JClass valueClass = jCodeModel.ref(MaterialWidgetDefinition.class);

        String detailName = "widgets";
        JClass rawLLclazz = jCodeModel.ref(Map.class);
        JClass fieldClazz = rawLLclazz.narrow(keyClass, valueClass);

        JClass defClazz = jCodeModel.ref(HashMap.class);
        JClass defFieldClazz = defClazz.narrow(keyClass, valueClass);

        JFieldVar detailField = jc.field(JMod.PRIVATE, fieldClazz, detailName, JExpr._new(defFieldClazz));

        JClass defaultMethodsClass = jCodeModel.ref(Map.class);
        JClass fieldDefaultMethodsClass = defaultMethodsClass.narrow(jCodeModel.ref(String.class), jCodeModel.ref(Class.class));

        JClass defDefaultMethodsClass = jCodeModel.ref(HashMap.class);
        JClass defFieldDefaultMethodsClass = defDefaultMethodsClass.narrow(jCodeModel.ref(String.class), jCodeModel.ref(Class.class));
        jc.field(JMod.PRIVATE, fieldDefaultMethodsClass, "defaulMethods", JExpr._new(defFieldDefaultMethodsClass));

        constructGetWidgetDefIfExist(jc, jCodeModel);
        constructGetMethodDefIfExist(jc, jCodeModel);

        try {
            processGwtMaterialWidgets(jc, jCodeModel, constructor);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        jCodeModel.build(new File(DEFAULT_BUILD_LOCATION));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodeWriter codeWriter = new SingleStreamCodeWriter(baos);
        jCodeModel.build(codeWriter);


        // jCodeModel.build(new File("target/classes"));

        // JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        // compiler.run(null, null, null, GWT_MATERIAL_FACTORY_PACKAGE_NAME + "."+GWT_MATERIAL_FACTORY_CLASS_NAME);

    }

    private void constructGetWidgetDefIfExist(JDefinedClass jc, JCodeModel jCodeModel) {
        JType returnType = jCodeModel.ref(Optional.class).narrow(MaterialWidgetDefinition.class);
        JClass optional = jCodeModel.directClass(Optional.class.getCanonicalName());

        JMethod method = jc.method(JMod.PUBLIC, returnType, "getWidgetDefIfExist");
        method.param(java.lang.String.class, "tag");
        method.annotate(Override.class);

        JBlock block = method.body()._if(JExpr.ref("widgets").invoke("containsKey").arg(JExpr.ref("tag"))
        )._then();

        //result.getMethods().putAll(defaulMethods);

        JVar var = block.decl(jCodeModel.ref(MaterialWidgetDefinition.class), "field");
        var.init(JExpr.ref("widgets").invoke("get").arg(JExpr.ref("tag")));
        block.add(JExpr.ref("field").invoke("getMethods").invoke("putAll").arg(JExpr.ref("defaulMethods")));
        block._return(optional.staticInvoke("of").arg(JExpr.ref("field")));


             //   _return(optional.staticInvoke("of").arg(JExpr.ref("widgets").invoke("get").arg(JExpr.ref("tag"))));
        method.body()._return(optional.staticInvoke("empty"));
    }

    private void constructGetMethodDefIfExist(JDefinedClass jc, JCodeModel jCodeModel) {

        JType returnTuple = jCodeModel.ref(Tuple.class).narrow(String.class, Class.class);
        JType returnType = jCodeModel.ref(Optional.class).narrow(returnTuple);
        JClass optional = jCodeModel.directClass(Optional.class.getCanonicalName());

        JMethod method = jc.method(JMod.PUBLIC, returnType, "getMethodDefIfExist");
        method.param(java.lang.String.class, "tag");
        method.param(java.lang.String.class, "method");
        method.annotate(Override.class);

        method.body()._if(JExpr.ref("widgets").invoke("get").arg(JExpr.ref("tag"))
                .invoke("getMethods")
                .invoke("containsKey")
                .arg(JExpr.ref("method")))._then()._return(optional.staticInvoke("of").arg(JExpr._new(jCodeModel.ref(Tuple.class)
                .narrow(String.class).narrow(Class.class)).arg(JExpr.ref("tag")).arg(JExpr.ref("widgets").invoke("get").arg(JExpr.ref("tag"))
                .invoke("getMethods")
                .invoke("get")
                .arg(JExpr.ref("method")))));

        method.body()._if(JExpr.ref("defaulMethods").invoke("containsKey").arg(JExpr.ref("tag")))
                ._then()._return(optional.staticInvoke("of").arg(JExpr._new(jCodeModel.ref(Tuple.class)
                .narrow(String.class).narrow(Class.class)).arg(JExpr.ref("tag")).arg(JExpr.ref("defaulMethods")
                .invoke("get")
                .arg(JExpr.ref("tag")))));


        method.body()._return(optional.staticInvoke("empty"));
    }

    private void processGwtMaterialWidgets(JDefinedClass jc, JCodeModel jCodeModel, JMethod constructor) throws IOException, ClassNotFoundException {

        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        ImmutableSet<ClassPath.ClassInfo> topLevelClasses = com.google.gwt.thirdparty.guava.common.reflect.ClassPath.from(loader).getTopLevelClasses();
        Set<Method> methods = new HashSet<>();
        Arrays.stream(MaterialWidget.class.getDeclaredMethods()).forEach(m -> {
            methods.add(m);
        });
        generateMaterialBaseWidgetMethodsDeclaretion(jc, jCodeModel, constructor, methods);

        for (final com.google.gwt.thirdparty.guava.common.reflect.ClassPath.ClassInfo info : topLevelClasses) {
            if (info.getName().startsWith(GWT_MATERIAL_COMMON_PACKAGE)) {
                if (info.load().getCanonicalName().equals(GWT_MATERIAL_COMMON_PACKAGE + info.load().getSimpleName())) {
                    Set<Method> properties = new HashSet<>();
                    processMethods(info, properties);
                    generateWidgetDeclaretion(jc, jCodeModel, constructor, info.load(), properties);
                }
            }
        }
    }


    private void generateMaterialBaseWidgetMethodsDeclaretion(JDefinedClass jc, JCodeModel jCodeModel, JMethod constructor, Set<Method> methods) {
        List<String> blackList = Arrays.asList(BLACKLISTED_PROPERTIES);
        JMethod method = jc.method(JMod.PRIVATE, void.class, "addMaterialBaseWidgetMethods");
        for (Method s : new HashSet<>(methods)) {
            if (s.getName().startsWith("set")) {
                String candidate = s.getName().replace("set", "");
                if (!blackList.contains(candidate)) {

                    Class<?>[] pType = s.getParameterTypes();
                    Class param = pType[0];
                    if (param.isPrimitive()) {
                        if (param.equals(boolean.class)) {
                            param = Boolean.class;
                        } else if (param.equals(int.class)) {
                            param = Integer.class;
                        } else if (param.equals(long.class)) {
                            param = Long.class;
                        } else if (param.equals(double.class)) {
                            param = Double.class;
                        } else if (param.equals(float.class)) {
                            param = Float.class;
                        }
                    }

                    method.body().add(JExpr._this().ref("defaulMethods").invoke("put").arg(candidate).arg(jCodeModel.ref(param).dotclass()));
                    defaultMethods.add(candidate);

                }
            }
        }
        method.body().add(JExpr._this().ref("defaulMethods").invoke("put").arg("Offset").arg(jCodeModel.ref(String.class).dotclass()));
        method.body().add(JExpr._this().ref("defaulMethods").invoke("put").arg("Visible").arg(jCodeModel.ref(Boolean.class).dotclass()));
        defaultMethods.add("Offset");
        defaultMethods.add("Visible");

        constructor.body().invoke("addMaterialBaseWidgetMethods");

    }

    private void processMethods(ClassInfo materialWidget, Set<Method> methods) {
        ClassUtils.getAllSuperclasses(materialWidget.load()).stream().forEach(s -> {
            ClassUtils.getAllInterfaces(s).stream().forEach(ifaces -> {
                if (ifaces.getName().startsWith("gwt.material.design.client.base") ||
                        ifaces.getName().startsWith("com.google.gwt.user.client.ui.HasVisibility") ||
                        ifaces.getName().startsWith("com.google.gwt.user.client.ui.HasEnabled") ||
                        ifaces.getName().startsWith("com.google.gwt.user.client.ui.Focusable")
                        ) {
                    Arrays.stream(ifaces.getMethods()).forEach(method -> {
                        if (method.getName().startsWith("set")) {
                            maybeAddProperty(methods, method);
                        }
                    });
                }
            });
        });
    }


    private void generateWidgetDeclaretion(JDefinedClass jc, JCodeModel jCodeModel, JMethod constructor, Class clazz, Set<Method> properties) {
        String tag = Utils.tagFromClass(clazz).get();

        JMethod method = jc.method(JMod.PRIVATE, void.class, "add" + tag);
        JVar var = method.body().decl(jCodeModel.ref(MaterialWidgetDefinition.class), "field");
        var.init(JExpr._new(jCodeModel.ref(MaterialWidgetDefinition.class)).arg(tag).arg(jCodeModel.ref(clazz).dotclass()));
        for (Method s : properties) {
            if (!defaultMethods.contains(s.getName().replace("set", ""))) {

                Class<?>[] pType = s.getParameterTypes();
                Class param = pType[0];
                if (param.isPrimitive()) {
                    if (param.equals(boolean.class)) {
                        param = Boolean.class;
                    } else if (param.equals(int.class)) {
                        param = Integer.class;
                    } else if (param.equals(long.class)) {
                        param = Long.class;
                    } else if (param.equals(double.class)) {
                        param = Double.class;
                    } else if (param.equals(float.class)) {
                        param = Float.class;
                    }
                }

                method.body().add(var.invoke("getMethods").invoke("put").arg(s.getName().replace("set", "")).arg(jCodeModel.ref(param).dotclass()));
            }
        }
        method.body().add(JExpr._this().ref("widgets").invoke("put").arg(tag).arg(JExpr.ref("field")));
        constructor.body().invoke("add" + tag);
    }

    private void maybeAddProperty(Set<Method> properties, Method property) {
        if (property.getName().startsWith("set")) {
            String candidate = property.getName().replaceFirst("set", "");
            if (!Arrays.asList(BLACKLISTED_PROPERTIES).contains(candidate)) {
                properties.add(property);
            }
        }
    }

}




/*


		equals.body()._if(JExpr.FALSE.eq(JExpr._super().invoke("equals").arg(_obj)))._then()._return(JExpr.FALSE);

 */







