package org.jboss.errai.polymer.shared;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.base.HasInitialClasses;
import gwt.material.design.client.base.MaterialWidget;
import gwt.material.design.client.ui.*;
import org.jboss.errai.ioc.client.IOCUtil;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by treblereel on 3/2/17.
 */
public class MaterialWidgetFactory {

    static Logger logger = LoggerFactory.getLogger(MaterialWidgetFactory.class);

    public static <T extends MaterialWidget> T getWidget(Class<T> clazz) {
        logger.warn("getWidget " + clazz.getName());
        MaterialWidget candidate = null;

        if (clazz.getName().equals(MaterialButton.class.getName())) {
            candidate =  new MaterialButton();
        } else if (clazz.getName().equals(MaterialCollapsible.class.getName())) {
            candidate =  new MaterialCollapsible();
        } else if (clazz.getName().equals(MaterialCollapsibleItem.class.getName())) {
            candidate = new MaterialCollapsibleItem();
        } else if (clazz.getName().equals(MaterialCollapsibleHeader.class.getName())) {
            candidate = new MaterialCollapsibleHeader();
        } else if (clazz.getName().equals(MaterialCollapsibleBody.class.getName())) {
            candidate = new MaterialCollapsibleBody();
        } else if (clazz.getName().equals(MaterialLink.class.getName())) {
            candidate = new MaterialLink();
        } else if (clazz.getName().equals(MaterialLabel.class.getName())) {
            candidate = new MaterialLabel();
        }


        if (candidate.getInitialClasses() != null) {
            StringBuffer sb = new StringBuffer();
            Arrays.stream(candidate.getInitialClasses()).forEach(css -> {
                sb.append(css + " ");
            });
            candidate.getElement().setAttribute("class", sb.toString().trim());
        }

        return (T) candidate;
    }

    public static Class getClassFromTag(String tag) {
        if (tag.equals("material-button")) {
            return MaterialButton.class;
        } else if (tag.equals("material-collapsible")) {
            return MaterialCollapsible.class;
        } else if (tag.equals("material-collapsibleitem")) {
            return MaterialCollapsibleItem.class;
        } else if (tag.equals("material-collapsibleheader")) {
            return MaterialCollapsibleHeader.class;
        } else if (tag.equals("material-collapsiblebody")) {
            return MaterialCollapsibleBody.class;
        } else if (tag.equals("material-link")) {
            return MaterialLink.class;
        } else if (tag.equals("material-label")) {
            return MaterialLabel.class;
        }
        return null;
    }

    public static MaterialWidget getWidgetFromTag(String tag) {
        return getWidget(getClassFromTag(tag));
    }

    public static MaterialWidget get(Element elm) {
        logger.warn("Element tag " + elm.getTagName());
        MaterialWidget obj = getWidgetFromTag(elm.getTagName().toLowerCase());
        return obj;
    }

/*    public static void processMaterialTags(final List<String> tags, final Map<String, Element> dataFieldElements){
        String[] widgets = {"material-button",
                "material-collapsible", "material-collapsibleItem", "material-collapsibleHeader",
                "material-link", "material-link", "material-collapsibleBody", "material-label"};

        Arrays.stream(widgets).forEach(tag -> Arrays.stream($(tag).get()).forEach(widget -> replace(widget, dataFieldElements)));


    }*/

    // do map lookup after
    public static MaterialWidget replace(Element e) {


        MaterialWidget obj;
        obj = get(e);
        copyAttrs(e, obj.getElement());
        StringBuffer sb = new StringBuffer();
        //logger.warn(" OBJ " + obj.getClass() + " " + sb.toString());
        String inner = e.getInnerHTML();

        e.getParentElement().replaceChild(obj.getElement(), e);

  /*      if (obj.getInitialClasses() != null) {
            Arrays.stream(obj.getInitialClasses()).forEach(css -> {
                sb.append(css + " ");
            });
            obj.getElement().setAttribute("class", sb.toString().trim());
        }*/
        obj.getElement().setInnerHTML(e.getInnerHTML());
        logger.warn("DO REPLACE " +e.getTagName() + " with " + obj.getElement().getTagName() + " and html  " + e.getInnerHTML() );
        return obj;
    }


    // do map lookup after
    public static Element replace(Element e, final Map<String, Widget> dataFieldElements, Map<String, String> htmls) {



        logger.warn("replace MaterialWidget " + e.getTagName() + " " + e.getAttribute("data-field") + " <<<" + " " + dataFieldElements.get(e.getAttribute("data-field")).getClass());

        String data_field = e.getAttribute("data-field");
        Element candidate = dataFieldElements.get(data_field).getElement();
        e.getParentElement().replaceChild(candidate, e);
        candidate.setInnerHTML(htmls.get(data_field));




  /*      // FROM MAP BY DATA-FIELD
        // obj = dataFieldElements.get(e.getAttribute("data-field"));

        MaterialWidget obj = TemplateUtil.nativeCast(dataFieldElements.get(e.getAttribute("data-field")));
        copyAttrs(e, obj.getElement());

        e.getParentElement().replaceChild(obj.getElement(), e);

//            copyAttrs(e, obj.getElement());
        StringBuffer sb = new StringBuffer();
        logger.warn(" OBJ " + obj.getClass() + " " + sb.toString());

        if (obj.getInitialClasses() != null) {
            Arrays.stream(obj.getInitialClasses()).forEach(css -> {
                sb.append(css + " ");
            });
            obj.getElement().setAttribute("class", sb.toString().trim());
        }
        obj.getElement().setInnerHTML(e.getInnerHTML());*/

        return candidate;
    }



    /*    instance.addWidget(MaterialCollection.class, "material-collection");
    instance.addWidget(MaterialContainer.class, "material-container");
    instance.addWidget(MaterialSearchResult.class, "material-searchresult");
    instance.addWidget(MaterialModal.class, "material-modal");
    instance.addWidget(MaterialFABList.class, "material-fablist");
    instance.addWidget(MaterialIntegerBox.class, "material-integerbox");
    instance.addWidget(MaterialChip.class, "material-chip");
    instance.addWidget(MaterialNumberBox.class, "material-numberbox");
    instance.addWidget(MaterialTabItem.class, "material-tabitem");
    instance.addWidget(MaterialHeader.class, "material-header");
    instance.addWidget(MaterialHelpBlock.class, "material-helpblock");
    instance.addWidget(MaterialPushpin.class, "material-pushpin");
    instance.addWidget(MaterialModalContent.class, "material-modalcontent");
    instance.addWidget(MaterialBadge.class, "material-badge");
    instance.addWidget(MaterialTopNav.class, "material-topnav");
    instance.addWidget(MaterialDivider.class, "material-divider");
    instance.addWidget(MaterialListValueBox.class, "material-listvaluebox");
    instance.addWidget(MaterialCardAction.class, "material-cardaction");
    instance.addWidget(MaterialCollectionItem.class, "material-collectionitem");
    instance.addWidget(MaterialFooter.class, "material-footer");
    instance.addWidget(MaterialCollectionSecondary.class, "material-collectionsecondary");
    instance.addWidget(MaterialLoader.class, "material-loader");
    instance.addWidget(MaterialCheckBox.class, "material-checkbox");
    instance.addWidget(MaterialInfo.class, "material-info");
    instance.addWidget(MaterialProgress.class, "material-progress");
    instance.addWidget(MaterialPreLoader.class, "material-preloader");
    instance.addWidget(MaterialSideNav.class, "material-sidenav");
    instance.addWidget(MaterialBreadcrumb.class, "material-breadcrumb");
    instance.addWidget(MaterialSlideCaption.class, "material-slidecaption");
    instance.addWidget(MaterialAnchorButton.class, "material-anchorbutton");
    instance.addWidget(MaterialSection.class, "material-section");
    instance.addWidget(MaterialButton.class, "material-button");
    instance.addWidget(MaterialCollapsibleHeader.class, "material-collapsibleheader");
    instance.addWidget(MaterialFAB.class, "material-fab");
    instance.addWidget(MaterialPager.class, "material-pager");
    instance.addWidget(MaterialTextArea.class, "material-textarea");
    instance.addWidget(MaterialColumn.class, "material-column");
    instance.addWidget(MaterialModalFooter.class, "material-modalfooter");
    instance.addWidget(MaterialTab.class, "material-tab");
    instance.addWidget(MaterialCardContent.class, "material-cardcontent");
    instance.addWidget(MaterialSplashScreen.class, "material-splashscreen");
    instance.addWidget(MaterialLabel.class, "material-label");
    instance.addWidget(MaterialCollapsibleBody.class, "material-collapsiblebody");
    instance.addWidget(MaterialListBox.class, "material-listbox");
    instance.addWidget(MaterialLongBox.class, "material-longbox");
    instance.addWidget(MaterialDatePicker.class, "material-datepicker");
    instance.addWidget(MaterialWeather.class, "material-weather");
    instance.addWidget(MaterialCardTitle.class, "material-cardtitle");
    instance.addWidget(MaterialParallax.class, "material-parallax");
    instance.addWidget(MaterialTextBox.class, "material-textbox");
    instance.addWidget(MaterialCollapsibleItem.class, "material-collapsibleitem");
    instance.addWidget(MaterialRadioButton.class, "material-radiobutton");
    instance.addWidget(MaterialNoResult.class, "material-noresult");
    instance.addWidget(MaterialNavSection.class, "material-navsection");
    instance.addWidget(MaterialCard.class, "material-card");
    instance.addWidget(MaterialSearch.class, "material-search");
    instance.addWidget(MaterialDropDown.class, "material-dropdown");
    instance.addWidget(MaterialRange.class, "material-range");
    instance.addWidget(MaterialSpinner.class, "material-spinner");
    instance.addWidget(MaterialVideo.class, "material-video");
    instance.addWidget(MaterialTitle.class, "material-title");
    instance.addWidget(MaterialSwitch.class, "material-switch");
    instance.addWidget(MaterialLink.class, "material-link");
    instance.addWidget(MaterialFloatBox.class, "material-floatbox");
    instance.addWidget(MaterialRow.class, "material-row");
    instance.addWidget(MaterialSlider.class, "material-slider");
    instance.addWidget(MaterialCardImage.class, "material-cardimage");
    instance.addWidget(MaterialIcon.class, "material-icon");
    instance.addWidget(MaterialCardReveal.class, "material-cardreveal");
    instance.addWidget(MaterialToast.class, "material-toast");
    instance.addWidget(MaterialInput.class, "material-input");
    instance.addWidget(MaterialScrollspy.class, "material-scrollspy");
    instance.addWidget(MaterialFooterCopyright.class, "material-footercopyright");
    instance.addWidget(MaterialTooltip.class, "material-tooltip");
    instance.addWidget(MaterialNavBrand.class, "material-navbrand");
    instance.addWidget(MaterialPanel.class, "material-panel");
    instance.addWidget(MaterialSlideItem.class, "material-slideitem");
    instance.addWidget(MaterialDoubleBox.class, "material-doublebox");
    instance.addWidget(MaterialValueBox.class, "material-valuebox");
    instance.addWidget(MaterialNavBar.class, "material-navbar");
    instance.addWidget(MaterialImage.class, "material-image");
    instance.addWidget(MaterialCollapsible.class, "material-collapsible");

    */

    private static boolean checkDataField(Element e) {
        JsArray<Node> nodes = getAttributes(e);
        for (int t = 0; t < nodes.length(); t++) {
            Node n = nodes.get(t);
            if (n.getNodeName().contains("data-field")) {
                return false;
            }
        }
        return true;
    }

    public static void copyAttrs(Element e, Element obj) {
        JsArray<Node> nodes = getAttributes(e);
        logger.warn("nodes " + nodes.length() + " " + e.getTagName());
        for (int t = 0; t < nodes.length(); t++) {
            Node n = nodes.get(t);
            logger.warn("          node " + n.getNodeName() + " " + n.getNodeValue());

            obj.setAttribute(n.getNodeName(), n.getNodeValue());
        }
    }

    public static native JsArray<Node> getAttributes(Element elem) /*-{
        return elem.attributes;
    }-*/;

    public static native JsArray<Node> getInitialClasses(Element elem) /*-{
        return elem.initialClasses;
    }-*/;

    public static native boolean checkProp(MaterialWidget elem, String prop) /*-{

        console.log(Object.getOwnPropertyNames(elem).filter(function (property) {
            return typeof elem[property] == 'function';
        }))


        if (elem.setText !== undefined) {
            return true;
        }
        return false;
    }-*/;

    //  public static native void check(MaterialButton obj, String s) /*-{
    public static native void check(Object obj, String s, String holder) /*-{
        obj.@gwt.material.design.client.ui.MaterialButton::setText(Ljava/lang/String;)(s);
    }-*/;


}
