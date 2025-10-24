package com.codegen.openapi.objects;

import java.util.List;

public class Method {
    private String methodName;
    private String path;
    private String httpMethod;
    private String returnType;
    private String summary;
    private String description;
    private List<Param> params;
    private RequestBody requestBody;

    public String getMethodName() {
        return methodName;
    }

    public List<Param> getParams() {
        return params;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getDescription() {
        return description;
    }

    public String getSummary() {
        return summary;
    }

    public String getPath() {
        return path;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setMethodName(String name) {
        this.methodName = name;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

}
