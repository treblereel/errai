package org.jboss.errai.polymer.shared;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by treblereel on 2/28/17.
 */
public class MyDomVisiter {

    static Logger logger = LoggerFactory.getLogger(MyDomVisiter.class.getName());


    Map<String, Element> dataFieldElements;

    public MyDomVisiter() {

    }

    public MyDomVisiter(Map<String, Element> dataFieldElements) {
        this.dataFieldElements = dataFieldElements;
    }

    /**
     * Called to traverse and visit the tree of {@link org.w3c.dom.Element}s.
     *
     * @param element the root of the tree to traverse and visit
     * @param visitor the visitor to be called on each of the nodes.
     */
    public static void visit(Element element, MyDomVisitor visitor) {

        logger.warn(" visit " + element.getTagName());

        if (!visitor.visit(element))
            return;

        if (element.hasChildNodes()) {
            NodeList<Node> childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Element elm = (Element) childNodes.getItem(i);
                visit(elm, visitor);
            }
        }
    }

}
