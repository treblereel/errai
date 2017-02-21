package org.jboss.errai.polymer.shared;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import gwt.material.design.client.base.MaterialWidget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

/**
 * Created by treblereel on 3/8/17.
 */
public class GwtMaterialBootstrap {

    private Element composite;
    private Map<String, Element> dataFieldElements;
    private Map<String, String> datafieldsInnerHtml;

    final static Logger logger = LoggerFactory.getLogger(GwtMaterialBootstrap.class.getName());


    public GwtMaterialBootstrap(Element composite, Map<String, Element> dataFieldElements, Map<String, String> datafieldsInnerHtml) {
        this.composite = composite;
        this.datafieldsInnerHtml = datafieldsInnerHtml;
        this.dataFieldElements = dataFieldElements;
    }

    public void processTemplate() {
        process(composite);
    }

    private Element process(Element element) {
        //  GwtMaterialUtil.print(element);
        if (element.getChildCount() != 0) {
            for (int i = 0; i < element.getChildNodes().getLength(); i++) {
                Element child = (Element) element.getChildNodes().getItem(i);
                if (child.getChildNodes().getLength() > 0) {
                    if (child != null) {
                        process(child);
                    }
                    if (child != null && child.getTagName() != null) {
                        if (child.getTagName().toLowerCase().contains("material")) {
                            if (child.getAttribute("data-field").equals("")) {
                                replace(child);
                            } else {
                                Element datafielded = dataFieldElements.get(child.getAttribute("data-field"));
                                replace(child, datafielded);
                            }
                        }
                    }
                } else {
                    if (child != null && child.getTagName() != null) {
                        if (child.getTagName().toLowerCase().contains("material")) {
                            if (child.getAttribute("data-field").equals("")) {
                                replace(child);
                            } else {
                                Element datafielded = dataFieldElements.get(child.getAttribute("data-field"));
                                replace(child, datafielded);
                            }
                        } else {
                            if (!child.getAttribute("data-field").equals("") && child.getInnerHTML().trim().equals("")) {
                                Optional<String> opt = checkDataFieldActivateWidget(child);
                                if (opt.isPresent()) {
                                    child.setInnerHTML(opt.get());
                                    process(child);

                                }
                            }
                        }
                    }
                }
            }
        } else {
            replace(element);
        }
        return element;
    }

    private Optional<String> checkDataFieldActivateWidget(Element elm) {
        if (!elm.getAttribute("data-field").equals("")) {
            return Optional.of(getElementContentByDataField(elm));
        } else {
            return Optional.empty();
        }
    }

    public Element replace(Element oldElm, Element newElm) {
        final Element parentElement = oldElm.getParentElement();
        Node firstNode = oldElm.getFirstChild();
        while (firstNode != null) {
            if (firstNode != oldElm.getFirstChildElement())
                newElm.appendChild(oldElm.getFirstChild());
            else {
                newElm.appendChild(oldElm.getFirstChildElement());
            }
            firstNode = oldElm.getFirstChild();
        }

        parentElement.replaceChild(newElm, oldElm);
        return newElm;
    }


    public Element replace(Element element) {
        MaterialWidget obj = MaterialWidgetFactory.get(element);
        MaterialWidgetFactory.copyAttrs(element, obj.getElement());
        return replace(element, obj.getElement());
    }


    private String getElementContentByDataField(Element lookup) {
        String result = datafieldsInnerHtml.get(lookup.getAttribute("data-field"));
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException("there is no such element wirh data-field=" + lookup.getAttribute("data-field"));
    }


}
