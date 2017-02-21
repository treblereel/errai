package org.jboss.errai.polymer.shared;


import com.google.gwt.dom.client.Element;

import java.util.Map;

/**
 * Created by treblereel on 2/28/17.
 */
public interface MyDomVisitor {

    /**
     * Visits an element in the dom, returns true if the visitor should
     * continue visiting down the dom.
     * @param element the root element to visit
     */
    boolean visit(Element element);

}
