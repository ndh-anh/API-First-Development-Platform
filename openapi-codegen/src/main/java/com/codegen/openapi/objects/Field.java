package com.codegen.openapi.objects;

public class Field {
    private String fieldName;
    private String fieldType;
    private String pattern;

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public String getPattern() {
        return pattern;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

}
