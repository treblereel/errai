package org.jboss.errai.polymer;


import com.google.gwt.thirdparty.guava.common.collect.ImmutableSet;
import com.google.gwt.thirdparty.guava.common.reflect.ClassPath;
import org.apache.commons.lang3.ClassUtils;
import org.jboss.errai.polymer.rebind.MaterialWidgetFactoryGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by treblereel on 3/2/17.
 */
public class MaterialWidgetsClassDescriptionTest {
    private static final Logger logger = LoggerFactory.getLogger(MaterialWidgetsClassDescriptionTest.class.getName());


    @Test
    public void lookupClassesByPackage() throws IOException {
        System.out.println("ololo");
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        ImmutableSet<ClassPath.ClassInfo> topLevelClasses = com.google.gwt.thirdparty.guava.common.reflect.ClassPath.from(loader).getTopLevelClasses();
        Set<String> methods = new TreeSet<>();

        for (final com.google.gwt.thirdparty.guava.common.reflect.ClassPath.ClassInfo info : topLevelClasses) {
            if (info.getName().equals("gwt.material.design.client.ui.MaterialLink")) {
                final Class<?> clazz = info.load();
                ClassUtils.getAllSuperclasses(clazz).stream().forEach(s -> {
                    ClassUtils.getAllInterfaces(s).stream().forEach(ifaces -> {
                        if (ifaces.getName().startsWith("gwt.material.design.client.base") ||
                                ifaces.getName().startsWith("com.google.gwt.user.client.ui.HasVisibility") ||
                                ifaces.getName().startsWith("com.google.gwt.user.client.ui.HasEnabled") ||
                                ifaces.getName().startsWith("com.google.gwt.user.client.ui.Focusable")
                                ) {
                            Arrays.stream(ifaces.getMethods()).forEach(method -> {
                                if (method.getName().startsWith("set")) {
                                    methods.add(method.getName());
                                }
                            });
                        }
                    });
                });
            }
        }
        ;


        methods.forEach(m -> {
            logger.warn(m);
        });
        Assert.assertTrue("Hey you", false);
    }

    @Test
    public void gwtMaterialWidgetStoreTest() {
        new MaterialWidgetFactoryGenerator();

    }


}
