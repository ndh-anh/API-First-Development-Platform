package com.codegen.openapi.objects;

public class Param {
    private String name;
    private String defaultValue;
    private String type;
    private Boolean required;
    private Boolean isPath;
    private Boolean isLast;

    public String getDefaultValue() {
        return defaultValue;
    }

    public Boolean getIsPath() {
        return isPath;
    }

    public Boolean getRequired() {
        return required;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Boolean getIsLast() {
        return isLast;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setIsPath(Boolean isPath) {
        this.isPath = isPath;
    }

    public void setRequired(Boolean isRequire) {
        this.required = isRequire;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIsLast(Boolean isLast) {
        this.isLast = isLast;
    }
}
