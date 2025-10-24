package com.codegen.openapi.objects;

import java.util.List;

public class Dto {
    private String name;
    private List<Field> fields;

    public List<Field> getFields() {
        return fields;
    }

    public String getName() {
        return name;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public void setName(String name) {
        this.name = name;
    }
}
