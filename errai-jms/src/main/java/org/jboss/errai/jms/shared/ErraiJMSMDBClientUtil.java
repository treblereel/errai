package org.jboss.errai.jms.shared;

public class ErraiJMSMDBClientUtil {

    public static String getDestinationFromAnnotatedProperty(String property) {

        if (property.contains("/")) {
            String[] array = property.split("/");
            return array[array.length - 1];
        }
        return property;
    }

    public static String replaceDotsWithSlashes(String s) {
        return s.replace(".", "/");
    }

}
