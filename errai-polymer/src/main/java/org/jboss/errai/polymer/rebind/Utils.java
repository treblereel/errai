package org.jboss.errai.polymer.rebind;

import org.jboss.errai.codegen.meta.MetaClass;
import java.util.Optional;

/**
 * Created by treblereel on 3/2/17.
 */
public class Utils {

    public static Optional<String> tagFromClass(MetaClass type) {
        return tagFromClass(type.asClass());
    }

    public static Optional<String> tagFromClass(Class type) {
        String className = type.getSimpleName();
        return Optional.of(className);
    }

}
