package org.jboss.errai.jms.generator;

import java.io.File;
import java.io.IOException;

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
 * 
 * @author Dmitrii Tikhomirov
 *
 */
public class MessageDrivenBeanActivatorGenerator extends
		MessageDrivenBeanGenerator {
	public static final String BEAN_NAME = "MessageDrivenBeanActivator";

	public MessageDrivenBeanActivatorGenerator(Decorable decorable,
			MessageDrivenBeanConfiguration config) {
		super(decorable, config);
	}

	@Override
	public void generate(MessageDrivenBeanConfiguration config,
			String outputPath) {
		System.out.println("\n\n GENERATE");
		generateActivator(outputPath);
	}

	private void generateActivator(String outputPath) {
		JCodeModel jCodeModel = new JCodeModel();
		JPackage jp = jCodeModel._package(SERVER_MDB_PACKAGE_NAME);
		try {
			JDefinedClass jc = jp._class(BEAN_NAME);
			jc.annotate(Startup.class);
			jc.annotate(Singleton.class);
			addQueueRegistrator(jc);
			
			jCodeModel.build(new File(outputPath));
			String classPath = generateFullClassPath(config, outputPath);
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			compiler.run(null, null, null, classPath);
		} catch (JClassAlreadyExistsException e) {
			extendActivator(outputPath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void addQueueRegistrator(JDefinedClass jc) {
		JMethod initMethod = jc.method(JMod.PRIVATE,
				JCodeModel.boxToPrimitive.get(Void.class), "init"+config.getMappedName());
		initMethod.annotate(PostConstruct.class);
		
		JBlock initMethodBody = initMethod.body();
		if (config.getDestinationType().equals("javax.jms.Queue")) {
			JVar clientQueueListenerVar = jc.field(JMod.PRIVATE,
					ClientQueueListener.class, "clientQueueListener");
			clientQueueListenerVar.annotate(Inject.class);
			initMethodBody.invoke(clientQueueListenerVar, "subscribe").arg(
					ErraiJMSMDBClientUtil
							.getDestinationFromAnnotatedProperty(config
									.getDestinationLookup()));
		}
	}

	private void extendActivator(String outputPath) {

	}

	@Override
	public String getGeneratedBeanName(MessageDrivenBeanConfiguration config) {
		return BEAN_NAME+".java";
	}

}
