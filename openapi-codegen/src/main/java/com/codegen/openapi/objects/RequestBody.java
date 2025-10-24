package com.codegen.openapi.objects;

public class RequestBody {
    private String name;
    private String type;
    private Boolean required;
    private Boolean isMultipath;

    public String getName() {
        return name;
    }

    public Boolean getRequired() {
        return required;
    }

    public Boolean getIsMultipath() {
        return isMultipath;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIsMultipath(Boolean isMultipath) {
        this.isMultipath = isMultipath;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }
}
