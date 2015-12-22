package org.jboss.errai.jms.generator;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;

import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable;

/**
 * MDB configuration holder.
 * 
 * @author Dmitrii Tikhomirov
 *
 */

public class MessageDrivenBeanConfiguration {
	private String acknowledgeMode;
	private String className;
	private String destination;
	private String destinationLookup;
	private String destinationType;
	private String mappedName;
	private String classPackageName;
	private static final int LENGHT_RANDOM_STRING = 8;

	public MessageDrivenBeanConfiguration() {

	}

	public MessageDrivenBeanConfiguration(Decorable decorable) {

		MessageDriven annotation = decorable.getDecorableDeclaringType()
				.getAnnotation(MessageDriven.class);
		this.setMappedName(annotation.name());

		if (annotation.activationConfig() == null) {
			throw new RuntimeException("There is no activationConfig");
		}

		for (ActivationConfigProperty activationConfig : annotation
				.activationConfig()) {
			if (activationConfig.propertyName().equals("destinationType")) {
				this.setDestinationType(activationConfig.propertyValue());
			}
			if (activationConfig.propertyName().equals("destinationLookup")) {
				this.setDestinationLookup(activationConfig.propertyValue());
			}
			if (activationConfig.propertyName().equals("destination")) {
				this.setDestination(activationConfig.propertyValue());
			}
			if (activationConfig.propertyName().equals("acknowledgeMode")) {
				this.setAcknowledgeMode(activationConfig.propertyValue());
			}
		}
		this.setClassName(decorable.getDecorableDeclaringType().asClass()
				.getSimpleName());
		this.setClassPackageName(decorable.getDecorableDeclaringType().asClass().getPackage().getName());
		if (destinationType == null || destinationLookup == null) {
			throw new RuntimeException("ActivationConfig corrupted");
		}
	}

	public String getAcknowledgeMode() {
		return acknowledgeMode;
	}

	public String getClassName() {
		return className;
	}

	public String getDestination() {
		return destination;
	}

	public String getDestinationLookup() {
		return destinationLookup;
	}

	public String getDestinationType() {
		return destinationType;
	}

	public String getMappedName() {
		return mappedName;
	}

	private void setAcknowledgeMode(String acknowledgeMode) {
		this.acknowledgeMode = acknowledgeMode;
	}

	/**
	 * Because there are could be several beans with the same name,
	 * we will add a random string to the generated bean name
	 * 
	 * @param className of server side MDBean
	 */
	public void setClassName(String className) {
		this.className = className+RandomStringUtils.randomAlphabetic(LENGHT_RANDOM_STRING);
	}

	private void setDestination(String destination) {
		this.destination = destination;
	}

	private void setDestinationLookup(String destinationLookup) {
		this.destinationLookup = destinationLookup;
	}

	private void setDestinationType(String destinationType) {
		this.destinationType = destinationType;
	}

	private void setMappedName(String mappedName) {
		this.mappedName = mappedName;
	}

	public String getClassPackageName() {
		return classPackageName;
	}

	public void setClassPackageName(String classPackageName) {
		this.classPackageName = classPackageName;
	}

}
