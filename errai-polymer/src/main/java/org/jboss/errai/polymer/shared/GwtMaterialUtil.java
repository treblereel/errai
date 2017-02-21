package org.jboss.errai.polymer.shared;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.base.MaterialWidget;
import gwt.material.design.jquery.client.api.JQuery;
import gwt.material.design.jquery.client.api.JQueryElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by treblereel on 2/24/17.
 */
public class GwtMaterialUtil {

    final static Logger logger = LoggerFactory.getLogger(GwtMaterialUtil.class.getName());

    private static final String[] widgets = {"material-button",
            "material-collapsible", "material-collapsibleItem", "material-collapsibleHeader",
            "material-link", "material-link", "material-collapsibleBody", "material-label"};

    public static boolean checkIfWidgetSupported(String name) {
        logger.warn(name);


        if (!name.startsWith("gwt.material.design")) {
            return false;
        }
        return name.equals("MaterialButton")
                || name.equals("MaterialDatePicker")
                || name.equals("MaterialCollectionItem")
                || name.equals("MaterialCollection")
                || name.equals("MaterialLink")
                || name.equals("MaterialBadge")
                || name.equals("MaterialAutoComplete");
    }

    public String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public String lowerize(final String line) {
        return Character.toLowerCase(line.charAt(0)) + line.substring(1);
    }

/*    public static boolean checkIfWidgetSupported(Object o) {
        //return o instanceof MaterialWidget || o instanceof Widget  || o instanceof Panel;
        logger.warn(" check 1 " + (o instanceof HasWidgets) + " " + o.getClass().getCanonicalName());

        return o instanceof com.google.gwt.user.client.ui.HasWidgets;
    }

    static MyDomVisitor unmanagedTagsMyDomVisitor = new MyDomVisitor() {

        @Override
        public boolean visit(Element element) {
            if (element.getTagName() == null) {
                return false;
            }

            if (element.getTagName().toLowerCase().contains("material")) {
                if (element.getAttribute("data-field").equals("")) {
                    logger.warn(" unmanagedTagsMyDomVisitor " + element.getTagName());
                    replace(element).getElement();
                }
            }
            return true;
        }
    };*/

    public static void print(Element element) {
        logger.warn("childs of " + element.getTagName() + " " + element.getAttribute("data-field"));

        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            Element child = (Element) element.getChildNodes().getItem(i);
            if (child != null) {
                logger.warn(child.getTagName());
            }
        }
    }

/*    private static Element process(Element element) {
        logger.warn("element " + " " + element.getChildCount() + " " + element.getChildNodes().getLength());
        logger.warn("element 2 " + element.getTagName());


        if (element.getChildCount() != 0) {

            for (int i = 0; i < element.getChildNodes().getLength(); i++) {
                Element child = (Element) element.getChildNodes().getItem(i);
                if (child.getChildNodes().getLength() > 0) {

                    if (child != null) {
                        print(child);
                        process(child);
                    }
                    if (child != null && child.getTagName() != null) {
                        if (child.getTagName().toLowerCase().contains("material")) {
                            if (child.getAttribute("data-field").equals("")) {
                                replace(child);
                            }else{
                            }
                        }
                    }
                } else {
                    if (child != null && child.getTagName() != null) {
                        if (child.getTagName().toLowerCase().contains("material")) {
                            if (child.getAttribute("data-field").equals("")) {
                                replace(child);
                            }else{
                            }
                        }
                    }
                }

            }
        } else {
            replace(element);
        }
        return element;
    }*/

/*    public static MaterialWidget replace(Element element) {
        final Element parentElement = element.getParentElement();
        MaterialWidget obj = MaterialWidgetFactory.get(element);

        MaterialWidgetFactory.copyAttrs(element, obj.getElement());

        Node firstNode = element.getFirstChild();
        while (firstNode != null) {
            if (firstNode != element.getFirstChildElement())
                obj.getElement().appendChild(element.getFirstChild());
            else {
                obj.getElement().appendChild(element.getFirstChildElement());
            }
            firstNode = element.getFirstChild();
        }

        parentElement.replaceChild(obj.getElement(), element);


        if (obj.getInitialClasses() != null) {
            StringBuffer sb = new StringBuffer();
            for (String clazz : obj.getInitialClasses()) {
                sb.append(clazz + " ");
            }
            if (!sb.toString().equals("")) {
                obj.getElement().setAttribute("class", sb.toString().trim());
            }
        }
        return obj;
    }*/

    public static void processUnmanagedTags(Element composite, Map<String, Element> dataFieldElements, Map<String, String> datafieldsInnerHtml) {
        new GwtMaterialBootstrap(composite, dataFieldElements, datafieldsInnerHtml).processTemplate();
    }

}
