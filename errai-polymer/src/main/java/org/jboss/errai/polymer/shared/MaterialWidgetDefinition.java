package org.jboss.errai.polymer.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by treblereel on 3/10/17.
 */
public class MaterialWidgetDefinition {

    private Class clazz;
    private String tag;

    private Map<String,Class> methods= new HashMap<>();

    public MaterialWidgetDefinition(String tag, Class clazz){
        this.tag = tag;
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MaterialWidgetDefinition)) return false;

        MaterialWidgetDefinition that = (MaterialWidgetDefinition) o;

        if (!getClazz().equals(that.getClazz())) return false;
        if (!getTag().equals(that.getTag())) return false;
        return getMethods() != null ? getMethods().equals(that.getMethods()) : that.getMethods() == null;

    }

    @Override
    public int hashCode() {
        int result = getClazz().hashCode();
        result = 31 * result + getTag().hashCode();
        result = 31 * result + (getMethods() != null ? getMethods().hashCode() : 0);
        return result;
    }

    public Map<String, Class> getMethods() {
        return methods;
    }

    public void setMethods(Map<String, Class> methods) {
        this.methods = methods;
    }
}
