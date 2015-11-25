package org.jboss.errai.jms.generator;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable;
import org.jboss.errai.jms.server.ClientQueueListener;
import org.jboss.errai.jms.server.ClientReceiver;
import org.jboss.errai.jms.server.ClientReceiverFactory;
import org.jboss.errai.jms.shared.ErraiJMSMDBClientUtil;
import org.jboss.errai.jms.util.ErraiJMSMDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.ext.GeneratorContext;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class MessageDrivenBeanReceiverGenerator {

  public static final String MDB_PARAM_ACKNOWLEDGE = "MessageDrivenBeanAcknowledge";
  public static final String MDB_PARAM_NAME = "MessageDrivenBeanName";
  public static final String SERVER_MDB_PACKAGE_NAME = "org.jboss.errai.server.mdb";

  public MessageDrivenBeanReceiverGenerator(Decorable decorable,
          MessageDrivenBeanConfiguration config) {

    GeneratorContext generatorContext = decorable.getInjectionContext()
            .getProcessingContext().getGeneratorContext();

    String outputDirCdt = ErraiJMSMDBUtil
            .getOutputDirCandidate(generatorContext);
    generateServerRepeater(config, outputDirCdt);
  }

  private String generateFullClassPath(String path,
          MessageDrivenBeanConfiguration config) {
    return path
            + "/"
            + ErraiJMSMDBClientUtil
                    .replaceDotsWithSlashes(SERVER_MDB_PACKAGE_NAME) + "/"
            + config.getClassName() + "Receiver.java";
  }

  private void generateServerRepeater(MessageDrivenBeanConfiguration config,
          final String outputPath) {
    final File outputDir = new File(outputPath + File.separator);
    if (!outputDir.exists())
      outputDir.mkdirs();
    writeCodeModel(config, outputDir.getAbsolutePath());
  }

  // Method to get JType based on any String Value
  public JType getTypeDetailsForCodeModel(JCodeModel jCodeModel, String type) {
    if (type.equals("Unsigned32")) {
      return jCodeModel.LONG;
    }
    else if (type.equals("Unsigned64")) {
      return jCodeModel.LONG;
    }
    else if (type.equals("Integer32")) {
      return jCodeModel.INT;
    }
    else if (type.equals("Integer64")) {
      return jCodeModel.LONG;
    }
    else if (type.equals("Enumerated")) {
      return jCodeModel.INT;
    }
    else if (type.equals("Float32")) {
      return jCodeModel.FLOAT;
    }
    else if (type.equals("Float64")) {
      return jCodeModel.DOUBLE;
    }
    else {
      return null;
    }
  }

  public void writeCodeModel(MessageDrivenBeanConfiguration config, String path) {

    try {
      JCodeModel jCodeModel = new JCodeModel();
      JPackage jp = jCodeModel._package(SERVER_MDB_PACKAGE_NAME);
      JDefinedClass jc = jp._class(config.getClassName() + "Receiver");
      jc._implements(MessageListener.class);
      JAnnotationUse messageDrivenAnnotation = jc.annotate(MessageDriven.class);
      messageDrivenAnnotation.param("name", config.getMappedName());
      JAnnotationArrayMember jAnnotationArray = messageDrivenAnnotation
              .paramArray("activationConfig");

      JAnnotationUse jAnnotationUseDestinationType = jAnnotationArray
              .annotate(ActivationConfigProperty.class);
      jAnnotationUseDestinationType.param("propertyName", "destinationType");
      jAnnotationUseDestinationType.param("propertyValue",
              config.getDestinationType());

      if (config.getDestinationLookup() != null) {
        JAnnotationUse jAnnotationUseDestination = jAnnotationArray
                .annotate(ActivationConfigProperty.class);
        jAnnotationUseDestination.param("propertyName", "destinationLookup");
        jAnnotationUseDestination.param("propertyValue",
                config.getDestinationLookup());
      }
      else if (config.getDestination() != null) {
        JAnnotationUse jAnnotationUseDestination = jAnnotationArray
                .annotate(ActivationConfigProperty.class);
        jAnnotationUseDestination.param("propertyName", "destination");
        jAnnotationUseDestination.param("propertyValue",
                config.getDestination());
      }

      if (config.getAcknowledgeMode() != null) {
        JAnnotationUse jAnnotationUseAcknowledgeMode = jAnnotationArray
                .annotate(ActivationConfigProperty.class);
        jAnnotationUseAcknowledgeMode.param("propertyName", "acknowledgeMode");
        jAnnotationUseAcknowledgeMode.param("propertyValue",config.getAcknowledgeMode());
      }
      jc.constructor(JMod.PUBLIC);

      JDocComment jDocComment = jc.javadoc();
      jDocComment.add("Autogenerated MDB server-side reciever for "
              + config.getClassName() + " class");
      // logger's initialization
      JVar jLoggerVar = jc.field(JMod.PRIVATE | JMod.STATIC | JMod.FINAL,
              Logger.class, "logger");
      jLoggerVar.init(jCodeModel.ref(LoggerFactory.class)
              .staticInvoke("getLogger")
              .arg(JExpr.lit(config.getClassName() + ".class")));

      JVar clientReceiverFactory = jc.field(JMod.PRIVATE,
              ClientReceiverFactory.class, "clientReceiverFactory");
      clientReceiverFactory.annotate(EJB.class);

      JMethod initMethod = jc.method(JMod.PRIVATE,
              JCodeModel.boxToPrimitive.get(Void.class), "init");
      initMethod.annotate(PostConstruct.class);

      JBlock initMethodBody = initMethod.body();
      if (config.getDestinationType().equals("javax.jms.Queue")) {
        JVar clientQueueListenerVar = jc.field(JMod.PRIVATE,
                ClientQueueListener.class, "clientQueueListener");
        clientQueueListenerVar.annotate(EJB.class);
        initMethodBody.invoke(clientQueueListenerVar, "subscribe").arg(
                ErraiJMSMDBClientUtil
                        .getDestinationFromAnnotatedProperty(config
                                .getDestinationLookup()));
      }

      JMethod onMessageMethod = jc.method(JMod.PUBLIC,
              JCodeModel.boxToPrimitive.get(Void.class), "onMessage");
      onMessageMethod.param(Message.class, "message");

      JBlock onMessageMethodBody = onMessageMethod.body();

      JVar jMessage = onMessageMethod.params().get(0);
      JTryBlock jtryBlock = onMessageMethodBody._try();
      JClass jClientReceiver = jCodeModel.ref(ClientReceiver.class);
      JVar clientReceiver = jtryBlock.body().decl(
              jClientReceiver,
              "clientReceiver",
              clientReceiverFactory.invoke("getClientReceiver").arg(
                      jMessage.invoke("getJMSDestination")));
      jtryBlock.body().invoke(clientReceiver, "processToMessageBus")
              .arg(jMessage);
      JCatchBlock jCatchBlock = jtryBlock._catch(jCodeModel
              .ref(JMSException.class));
      jCatchBlock.body()._throw(
              JExpr._new(jCodeModel.ref(EJBException.class)).arg(
                      jCatchBlock.param("_x")));

      jCodeModel.build(new File(path));
      String classPath = generateFullClassPath(path, config);
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      compiler.run(null, null, null, classPath);

    } catch (Exception ex) {
      ex.printStackTrace();
      throw new RuntimeException(ex);
    }
  }

}
