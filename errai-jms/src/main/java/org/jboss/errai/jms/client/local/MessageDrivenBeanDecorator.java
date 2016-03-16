package org.jboss.errai.jms.client.local;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicator interface, used in internal startup stage
 *  
 * @author Dmitrii Tikhomirov
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageDrivenBeanDecorator {

}
