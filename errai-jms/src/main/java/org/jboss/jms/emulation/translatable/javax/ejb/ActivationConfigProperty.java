package javax.ejb;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation for adding properties to messaging bean annotations (i.e. @MessageDriven, @Consumer)
 * 
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @version $Revision$
 */
@Retention(RUNTIME)
@Target({})
public @interface ActivationConfigProperty
{
   String propertyName();

   String propertyValue();
}
