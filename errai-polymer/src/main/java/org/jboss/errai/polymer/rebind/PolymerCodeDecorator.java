/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.polymer.rebind;

import com.google.gwt.junit.GWTMockUtilities;
import gwt.material.design.client.base.MaterialWidget;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.exception.GenerationException;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.ioc.client.api.CodeDecorator;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCDecoratorExtension;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Injectable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.FactoryController;
import org.jboss.errai.polymer.shared.GwtMaterialBootstrap;
import org.jboss.errai.polymer.shared.GwtMaterialUtil;
import org.jboss.errai.ui.rebind.DataFieldCodeDecorator;
import org.jboss.errai.ui.rebind.TemplatedCodeDecorator;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.util.TypeLiteral;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.jboss.errai.codegen.util.Stmt.*;

/**
 * Generates the code required for {@link Templated} classes.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@CodeDecorator
public class PolymerCodeDecorator extends IOCDecoratorExtension<Templated> {
    private static final String CONSTRUCTED_TEMPLATE_SET_KEY = "constructedTemplate";
    private Document html;
    private Map<String, Map<String, MethodHolder>> classMethods = new HashMap<>();
    private List<Statement> stmts;
    private static final String gwtMaterialElementInnerHTMLContentMap = "gwtMaterialElementInnerHTMLContentMap";
    private static final String gwtMaterialElementMap = "gwtMaterialElementMap";
    private static final String gwtMaterialTagsToScan = "gwtMaterialTagsToScan";


    private static final String[] widgets = {"material-button",
            "material-collapsible", "material-collapsibleItem", "material-collapsibleHeader",
            "material-link", "material-link", "material-collapsibleBody", "material-label"};

    // private Map<String, MetaClass> types;

    private static final Logger logger = LoggerFactory.getLogger(PolymerCodeDecorator.class);

    public PolymerCodeDecorator(final Class<Templated> decoratesWith) {
        super(decoratesWith);
    }

    @Override
    public void generateDecorator(final Decorable decorable, final FactoryController controller) {
        logger.error("\n\n generateDecorator: \n\n" + decorable.getEnclosingInjectable().getInjectedType().getCanonicalName());

        final MetaClass declaringClass = decorable.getDecorableDeclaringType();

        List<MetaMethod> currentPostConstructs = declaringClass.getDeclaredMethodsAnnotatedWith(PostConstruct.class);
        if (currentPostConstructs.size() > 1) {
            throw new RuntimeException(declaringClass.getFullyQualifiedName() + " has multiple @PostConstruct methods.");
        }

        initTemplateParser(declaringClass); // ?


        decorable.getInjectionContext().getAllElementMappings().stream().forEach(k -> {
            logger.warn(" => " + k.getValue().getName() + " " + k.getKey().name());
        });


        stmts = new ArrayList<>();

/*        final MetaClass gwtElement = MetaClassFactory.get(MaterialWidget.class);
        Statement parentOfRootTemplateElementVarName = Stmt.invokeStatic(MetaClassFactory.get("org.jboss.errai.polymer.shared.GwtMaterialUtil"),
                "processTemplate", Stmt.create().loadVariable("parentElementForTemplateOfApp"));
        stmts.add(parentOfRootTemplateElementVarName);*/


        // stmts.add(Stmt.create().loadVariable("this"));

        stmts.add(declareVariable(gwtMaterialElementInnerHTMLContentMap, new TypeLiteral<Map<String, String>>() {
                },
                newObject(new TypeLiteral<LinkedHashMap<String, String>>() {
                }))
        );

        stmts.add(declareVariable(gwtMaterialElementMap, new TypeLiteral<Map<String, MaterialWidget>>() {
                },
                newObject(new TypeLiteral<LinkedHashMap<String, MaterialWidget>>() {
                }))
        );

        controller.getExposedFields().stream().forEach(field -> {

            logger.warn("my field " + field.getName());
        });

        // controller.getInvokeAfterStatements(currentPostConstruct).add(invokeStatic(GwtMaterialUtil.class, "processTemplate", Arrays.stream(widgets).collect(Collectors.joining("@"))));

        //stmts.add(invokeStatic(GwtMaterialUtil.class, "addEvent",loadVariable("instance")));


        //   Stmt.load(ArrayList<String, String>);


/*
        MetaMethod field = new BuildMetaField(List.class, null, Scope.Private, ArrayList<String>, "this").


        controller.addExposedField(field);*/


        //  final Statement materialMap = Stmt.loadVariable(gwtMaterialElementsMapVarName);


        logger.warn("\n\n\n generateDecorator " + declaringClass.getFullyQualifiedName());

        Map<String, MetaClass> types = DataFieldCodeDecorator.aggregateDataFieldTypeMap(decorable, decorable.getEnclosingInjectable().getInjectedType());


        // only datafielded !!!
        for (final Map.Entry<String, MetaClass> field : types.entrySet()) {
            if (checkIfWidgetSupported(field.getValue()))
                processMaterialTag(field.getKey(), field.getValue(), types);
        }

        final String fieldsMapVarName = "templateFieldsMap";
        final Statement fieldsMap = Stmt.loadVariable(fieldsMapVarName);


        final Map<String, Statement> dataFields = DataFieldCodeDecorator.aggregateDataFieldMap(decorable, decorable.getEnclosingInjectable().getInjectedType());
/*        for (final Map.Entry<String, Statement> field : dataFields.entrySet()) {
            stmts.add(invokeStatic(GwtMaterialUtil.class, "compositeComponentReplace", decorable.getDecorableDeclaringType()
                            .getFullyQualifiedName(), TemplatedCodeDecorator.getTemplateFileName(decorable.getDecorableDeclaringType()), Cast.to(Widget.class, field.getValue()),
                    loadVariable("dataFieldElements"), loadVariable("dataFieldMetas"), field.getKey()));
        }*/


        //*************************************************************
/*        stmts.add(invokeStatic(GwtMaterialUtil.class, "compositeComponentReplace", decorable.getDecorableDeclaringType()
                        .getFullyQualifiedName(), TemplatedCodeDecorator.getTemplateFileName(decorable.getDecorableDeclaringType()),
                loadVariable("dataFieldElements"), Stmt.loadVariable(gwtMaterialElementInnerHTMLContentMap)));*/







    //    controller.getInstancePropertyStmt()



        //  controller.addExposedField();


        // controller.getInvokeAfterStatements(currentPostConstruct).add(invokeStatic(GwtMaterialUtil.class, "processTemplate", Arrays.stream(widgets).collect(Collectors.joining("@"))));
/*       controller.getInvokeAfterStatements(currentPostConstruct).add( loadVariable("proxiedInstance").invoke("getElement").invoke("removeAllChildren"));
       controller.getInvokeAfterStatements(currentPostConstruct).add( loadVariable("proxiedInstance").invoke("getElement").invoke("removeFromParent"));

        controller.getExposedConstructors().stream().forEach(con ->{
            logger.warn("\n\n\n getExposedConstructors " + con.getName());

        });*/

        stmts.add(invokeStatic(GwtMaterialUtil.class, "processUnmanagedTags", loadVariable("parentElementForTemplateOfApp"), loadVariable("dataFieldElements"), loadVariable("gwtMaterialElementInnerHTMLContentMap")));

        //stmts.add(invokeStatic(GwtMaterialUtil.class, "processDatafields", loadVariable("templateFieldsMap"), Stmt.loadVariable(gwtMaterialElementInnerHTMLContentMap),  loadVariable("instance")));


        controller.addInitializationStatementsToEnd(stmts);
    }

    private MetaMethod gatherPostConstructs(final Injectable injectable) {
        MetaClass type = injectable.getInjectedType();
        final Deque<MetaMethod> postConstructs = new ArrayDeque<>();


        return null;
    }


    private boolean checkIfWidgetSupported(MetaClass className) {
        return className.getFullyQualifiedName().startsWith("gwt.material.design.client.ui");
    }

    private void processMaterialTag(String name, MetaClass className, Map<String, MetaClass> types) {
        Element elm = getElementFromTemplate(name);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < elm.childNodes().size(); i++) {
            String c2 = elm.childNode(i).outerHtml();
            sb.append(c2);
        }

        stmts.add(Stmt.loadVariable(gwtMaterialElementInnerHTMLContentMap).invoke("put", name, sb.toString()));




/*

            Map<String, String> fields = getDataFieldElmAttrs(elm);
            Optional<String> parentDataFieldName = getParentDataFieldName(elm);
            if (fields.size() > 0 || parentDataFieldName.isPresent()) {
                BlockBuilder block = Stmt.if_(BooleanExpressionBuilder
                        .create(Stmt.loadVariable("templateFieldsMap").invoke("containsKey", name),
                                BooleanOperator.Equals, Stmt.loadLiteral(true)))
                        .append(Stmt.create().declareVariable("field", Stmt.castTo(className, Stmt.loadVariable("templateFieldsMap")
                                .invoke("get", name))));

                if (fields.size() > 0) {
                    for (Map.Entry<String, String> kv : fields.entrySet()) {
                        if (!kv.getKey().equals("data-field")) {
                            MethodHolder method = getMethodByClassAndByName(className, kv.getKey());
                            if (method != null)
                                generateSetBlock(block, method.clazz, method.name, kv.getValue());
                        }
                    }
                }*/

        //     logger.warn("PARENT " + parentDataFieldName.get() + " elm " + elm.tagName());

/*
                if (parentDataFieldName.isPresent() && types.get(parentDataFieldName.get()).isAssignableTo(HasWidgets.class)) {
                    logger.warn("isAssignableTo");

                    block.append(Stmt.if_(BooleanExpressionBuilder
                            .create(Stmt.loadVariable("templateFieldsMap").invoke("containsKey", parentDataFieldName.get()),
                                    BooleanOperator.Equals, Stmt.loadLiteral(true))).append(
                            //   Stmt.loadVariable("templateFieldsMap").invoke("get", parentDataFieldName.get()).invoke("add",Stmt.loadVariable("field"))).finish());

     *//*                       Stmt.castTo(MetaClassFactory.get("com.google.gwt.user.client.ui.HasWidgets"),Stmt.loadVariable("templateFieldsMap")
                                    .invoke("get",parentDataFieldName.get()).invoke("add",Stmt.loadVariable("field")))).finish());*//*

                            Stmt.castTo(MetaClassFactory.get("com.google.gwt.user.client.ui.HasWidgets"), Stmt.loadVariable("templateFieldsMap")
                                    .invoke("get", parentDataFieldName.get())).invoke("add", Stmt.loadVariable("field"))).finish());


                    // Stmt.loadVariable("field")).finish());
                }*/
/*                    block.append(Stmt.if_(BooleanExpressionBuilder
                            .create(Stmt.loadVariable("templateFieldsMap").invoke("containsKey", parentDataFieldName.get()),
                                    BooleanOperator.Equals, Stmt.loadLiteral(true))).append(
                           // Stmt.loadVariable("templateFieldsMap").invoke("get", parentDataFieldName.get()).invoke("add",Stmt.loadVariable("field"))).finish());

                    Stmt.create().declareVariable("supported",Stmt.invokeStatic(
                            "checkIfWidgetSupported", Stmt.loadVariable("templateFieldsMap")
                                    .invoke("get", parentDataFieldName.get())
                                     )))

                            .append(
                                    Stmt.if_(BooleanExpressionBuilder
                                            .create(Stmt.loadVariable("supported"), BooleanOperator.Equals, true))
                                            .append(Stmt.invokeStatic(MetaClassFactory.get("com.google.gwt.user.client.Window"),"alert","BooleanExpressionBuilder "+name))
                                            .append(Stmt.castTo(MetaClassFactory.get("com.google.gwt.user.client.ui.HasWidgets"),Stmt.loadVariable("templateFieldsMap")
                                            .invoke("get", parentDataFieldName.get())).invoke("add",Stmt.loadVariable("field")))
                                            .finish()).finish());
                }*/
        /*        Statement check = (Statement) block.finish();
                stmts.add(check);*/
/*
                logger.warn("*******************************************");
                logger.warn(elm.tagName() + " isKnownTag?: " + elm.tag().isKnownTag() + " isSelfClosing?: " + elm.tag().isSelfClosing() + " size : " + elm.childNodes().size());


                for(int i = 0; i < elm.childNodes().size(); i++){
                   // logger.warn("Child " + elm.child(i).tagName());




                }

     *//*           for(Node n: elm.childNodes()){
                    logger.warn("Child " + n.);

                }*//*



                logger.warn(elm.outerHtml());

                elm.outerHtml();


                logger.warn("*******************************************");

            }*/
        //  }
    }

    private Element getElementFromTemplate(String name) {
        Elements elms = html.body().getElementsByAttributeValue("data-field", name);
        if (elms.size() > 1) {
            throw new IllegalArgumentException("Duplicated tags " + name);
        } else if (elms.size() == 1) {
            return elms.get(0);
        }
        return null;
    }

    private Optional<String> getParentDataFieldName(Element elm) {
        logger.warn(elm.parent().attr("data-field"));
        return Optional.of(elm.parent().attr("data-field"));
    }


    private Map<String, String> getDataFieldElmAttrs(Element elm) {
        Map<String, String> map = new HashMap<>();
        for (Attribute att : elm.attributes().asList()) {
            map.put(att.getKey(), att.getValue());
            logger.warn("ELEMENTS " + elm.tagName() + " " + att.getKey() + " " + att.getValue());
        }

        if (elm.childNodes().size() > 0) {
            elm.childNodes().stream().forEach(e -> {
                logger.warn("Children " + e.nodeName() + " " + e.outerHtml());
            });
        }

        if (elm.parent() != null) {
            Element parent = elm.parent();
            logger.warn("Parent " + elm.parent().tagName());

            if (elm.parent().attr("data-field") != null)
                logger.warn(elm.parent().attr("data-field"));


        }


        return map;
    }

    private BlockBuilder generateSetBlock(BlockBuilder block, Class clazz, String method, String value) {
        logger.warn("generateSetBlock " + clazz.isEnum() + " " + clazz.getCanonicalName() + " " + method + " " + value);
        if (clazz.isEnum()) {
            return block.append(Stmt.loadVariable("field").invoke(method, Stmt.invokeStatic(resolveClassParam(clazz), "valueOf", value)));
        }
        return block.append(Stmt.loadVariable("field").invoke(method, Stmt.newObject(resolveClassParam(clazz)).withParameters(value)));

    }

    public void initTemplateParser(MetaClass declaringClass) {
        Optional<String> template = getPathFromFileName(declaringClass);
        String filename = TemplatedCodeDecorator.getTemplateFileName(declaringClass);
        if (!template.isPresent()) {
            throw new GenerationException(
                    "Cannot find template ["
                            + filename + "] in class [" + declaringClass.getFullyQualifiedName()
                            + "].");
        }

        try {
            File file = new File(template.get());
            html = Jsoup.parse(file, "UTF-8", "http://localhost/");

/*            MyDomVisiter.visit(html.body(), new MyDomVisitor() {
                @Override
                public boolean visit(Element element) {
                    logger.warn("onVisitor " + element.tagName());

                    if (element.tagName().contains("material")) {

                        logger.warn("       do replace " + element.tagName());

                        // element.tagName("div");
                        // element.dataset().put("m_index", Utils.getLettersAndDigits());

                    }


                    return true;
                }
            });*/

            //FileUtils.writeStringToFile(file, html.outerHtml(), "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MetaClass resolveClassParam(Class clazz) {
        if (clazz.equals(double.class)) {
            return MetaClassFactory.get("java.lang.Double");
        } else if (clazz.equals(int.class)) {
            return MetaClassFactory.get("java.lang.Integer");
        } else if (clazz.equals(boolean.class)) {
            return MetaClassFactory.get("java.lang.Boolean");
        }

        return MetaClassFactory.get(clazz);
    }


    public static Optional<String> getPathFromFileName(MetaClass declaringClass) {
        String fileName = TemplatedCodeDecorator.getTemplateFileName(declaringClass);
        return Optional.of(Thread.currentThread().getContextClassLoader().getResource(fileName).getFile());
    }


    private MethodHolder getMethodByClassAndByName(MetaClass clazzz, String param) {
        logger.error("in =>  " + clazzz.getCanonicalName() + " " + param);
        if (classMethods.containsKey(clazzz.getCanonicalName())) {
            return classMethods.get(clazzz.getCanonicalName()).get(param);
        } else {
            GWTMockUtilities.disarm();  //????
            classMethods.put(clazzz.getCanonicalName(), new HashMap<>());
            Arrays.stream(clazzz.getMethods()).forEach(m -> {
                if (m.getName().toLowerCase().startsWith("set")) {
                    if (m.getParameters().length == 1) {
                        classMethods.get(clazzz.getCanonicalName()).put(m.getName().replaceFirst("set", "").toLowerCase(), new MethodHolder(m.getName(), m.getParameters()[0].getType().asClass()));
                        //logger.error("getDeclaredMethods " + m.getName().replaceFirst("set", "") + " " + m.getParameters()[0].getType().getCanonicalName());
                    }
                }
            });
            GWTMockUtilities.restore();
        }
        return classMethods.get(clazzz.getCanonicalName()).get(param);
    }

    private class MethodHolder {
        String name;
        Class clazz;

        MethodHolder(String n, Class c) {
            name = n;
            clazz = c;
        }
    }
}
