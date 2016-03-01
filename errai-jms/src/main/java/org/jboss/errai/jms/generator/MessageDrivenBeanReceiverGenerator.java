package org.jboss.errai.jms.generator;

import java.io.File;

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
import org.jboss.errai.jms.server.ClientReceiver;
import org.jboss.errai.jms.server.ClientReceiverFactory;
import org.jboss.logging.Logger;

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
import com.sun.codemodel.JVar;

/**
 * Construct server side MDBean based on Client side configuration. Bean will be
 * located on "org.jboss.errai.server.mdb" package.
 * 
 * @author Dmitrii Tikhomirov
 *
 */

public class MessageDrivenBeanReceiverGenerator extends
		MessageDrivenBeanGenerator {

	public static final String MDB_PARAM_ACKNOWLEDGE = "MessageDrivenBeanAcknowledge";
	public static final String MDB_PARAM_NAME = "MessageDrivenBeanName";

	public MessageDrivenBeanReceiverGenerator(Decorable decorable,
			MessageDrivenBeanConfiguration config) {
		super(decorable, config);
	}

	public void generate(MessageDrivenBeanConfiguration config,
			final String outputPath) {
		final File outputDir = new File(outputPath + File.separator);
		if (!outputDir.exists())
			outputDir.mkdirs();
		writeCodeModel(config, outputDir.getAbsolutePath());
	}

	/**
	 * Generate class by specific path calculated in generateFullClassPath
	 * method.
	 * 
	 * @param config
	 * @param path
	 */
	public void writeCodeModel(MessageDrivenBeanConfiguration config,
			String path) {

		try {
			JCodeModel jCodeModel = new JCodeModel();
			JPackage jp = jCodeModel._package(SERVER_MDB_PACKAGE_NAME);
			JDefinedClass jc = jp._class(config.getClassName() + "Receiver");
			jc._implements(MessageListener.class);
			JAnnotationUse messageDrivenAnnotation = jc
					.annotate(MessageDriven.class);
			messageDrivenAnnotation.param("name", config.getMappedName());
			JAnnotationArrayMember jAnnotationArray = messageDrivenAnnotation
					.paramArray("activationConfig");

			JAnnotationUse jAnnotationUseDestinationType = jAnnotationArray
					.annotate(ActivationConfigProperty.class);
			jAnnotationUseDestinationType.param("propertyName",
					"destinationType");
			jAnnotationUseDestinationType.param("propertyValue",
					config.getDestinationType());

			if (config.getDestinationLookup() != null) {
				JAnnotationUse jAnnotationUseDestination = jAnnotationArray
						.annotate(ActivationConfigProperty.class);
				jAnnotationUseDestination.param("propertyName",
						"destinationLookup");
				jAnnotationUseDestination.param("propertyValue",
						config.getDestinationLookup());
			} else if (config.getDestination() != null) {
				JAnnotationUse jAnnotationUseDestination = jAnnotationArray
						.annotate(ActivationConfigProperty.class);
				jAnnotationUseDestination.param("propertyName", "destination");
				jAnnotationUseDestination.param("propertyValue",
						config.getDestination());
			}

			if (config.getAcknowledgeMode() != null) {
				JAnnotationUse jAnnotationUseAcknowledgeMode = jAnnotationArray
						.annotate(ActivationConfigProperty.class);
				jAnnotationUseAcknowledgeMode.param("propertyName",
						"acknowledgeMode");
				jAnnotationUseAcknowledgeMode.param("propertyValue",
						config.getAcknowledgeMode());
			}
			jc.constructor(JMod.PUBLIC);

			JDocComment jDocComment = jc.javadoc();
			jDocComment.add("Autogenerated MDB server-side reciever for "
					+ config.getClassName() + " class");
			// logger's initialization
			JVar jLoggerVar = jc.field(JMod.PRIVATE | JMod.STATIC | JMod.FINAL,
					Logger.class, "logger");
			jLoggerVar.init(jCodeModel.ref(Logger.class)
					.staticInvoke("getLogger")
					.arg(JExpr.lit(config.getClassName() + ".class")));

			JVar clientReceiverFactory = jc.field(JMod.PRIVATE,
					ClientReceiverFactory.class, "clientReceiverFactory");
			clientReceiverFactory.annotate(EJB.class);

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
			String classPath = generateFullClassPath(config, path);
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			compiler.run(null, null, null, classPath);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	@Override
	public String getGeneratedBeanName(MessageDrivenBeanConfiguration config) {
		return config.getClassName() + "Receiver.java";
	}

}
