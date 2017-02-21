package org.jboss.errai.polymer.shared;

import java.util.Map;

/**
 * Created by treblereel on 3/10/17.
 */
public interface MaterialWidgetFactoryNG {

    java.util.Optional<MaterialWidgetDefinition> getWidgetDefIfExist(String tag);

    java.util.Optional<Tuple<String,Class>> getMethodDefIfExist(String tag, String method);

}
