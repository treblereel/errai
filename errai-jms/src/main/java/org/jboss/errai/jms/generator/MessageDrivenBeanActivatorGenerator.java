package org.jboss.errai.jms.generator;

import java.io.File;
import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable;
import org.jboss.errai.jms.server.ClientQueueListener;
import org.jboss.errai.jms.shared.ErraiJMSMDBClientUtil;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JVar;

/**
 * The main purpose is to generate a bean, which will register Queues in ClientQueueListener on app startup. This generator
 * creates init method for the first Queue, and on next Queue it updates that init method with new description.
 * 
 * @author Dmitrii Tikhomirov
 *
 */
public class MessageDrivenBeanActivatorGenerator extends MessageDrivenBeanGenerator {
    public static final String BEAN_NAME = "MessageDrivenBeanActivator";

    public MessageDrivenBeanActivatorGenerator(Decorable decorable, MessageDrivenBeanConfiguration config) {
        super(decorable, config);
    }

    @Override
    public void generate(MessageDrivenBeanConfiguration config, String outputPath) {
        String classPath = generateFullClassPath(config, outputPath);
        File f = new File(classPath);
        if (f.exists()) {
            extendActivator(outputPath, classPath);
        } else {
            generateActivator(outputPath, classPath);
        }
    }

    private void generateActivator(String outputPath, String classPath) {
        JCodeModel jCodeModel = new JCodeModel();
        JPackage jp = jCodeModel._package(SERVER_MDB_PACKAGE_NAME);
        try {
            JDefinedClass jc = jp._class(BEAN_NAME);
            jc.annotate(Startup.class);
            jc.annotate(Singleton.class);
            addQueueRegistrator(jc);

            jCodeModel.build(new File(outputPath));

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            compiler.run(null, null, null, classPath);
        } catch (JClassAlreadyExistsException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addQueueRegistrator(JDefinedClass jc) {
        JMethod initMethod = jc.method(JMod.PRIVATE, JCodeModel.boxToPrimitive.get(Void.class), "init");
        initMethod.annotate(PostConstruct.class);

        JBlock initMethodBody = initMethod.body();
        if (config.getDestinationType().equals("javax.jms.Queue")) {
            JVar clientQueueListenerVar = jc.field(JMod.PRIVATE, ClientQueueListener.class, "clientQueueListener");
            clientQueueListenerVar.annotate(Inject.class);
            initMethodBody.invoke(clientQueueListenerVar, "subscribe").arg(
                    ErraiJMSMDBClientUtil.getDestinationFromAnnotatedProperty(config.getDestinationLookup()));
        }
    }

    private void extendActivator(String outputPath, String classPath) {
        try {
            ClassPool cp = ClassPool.getDefault();
            cp.insertClassPath(outputPath);
            CtClass ctClass = cp.get(SERVER_MDB_PACKAGE_NAME + "." + BEAN_NAME);
            CtMethod ctMethod = ctClass.getDeclaredMethod("init");
            ctMethod.insertAfter("clientQueueListener.subscribe(\"" + config.getMappedName() + "\");");
            ctClass.writeFile(outputPath);
        } catch (NotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (CannotCompileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getGeneratedBeanName(MessageDrivenBeanConfiguration config) {
        return BEAN_NAME + ".java";
    }

}
