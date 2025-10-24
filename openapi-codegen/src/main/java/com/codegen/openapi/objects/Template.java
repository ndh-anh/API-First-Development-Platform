package com.codegen.openapi.objects;

import java.util.List;

public class Template {
    private String name;
    private String packageName;
    private List<Method> methods;

    public List<Method> getMethods() {
        return methods;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
