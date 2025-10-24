package com.codegen.openapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.codegen.openapi.objects.Method;
import com.codegen.openapi.objects.Param;
import com.codegen.openapi.objects.RequestBody;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;

public class OpenApiToJavaMapper {

    private final OpenAPI openAPI;

    /**
     * constructor
     * 
     * @param openAPI
     */
    public OpenApiToJavaMapper(OpenAPI openAPI) {
        this.openAPI = openAPI;
    }

    /**
     * 
     * @param method
     * @param path
     * @param op
     * @return
     */
    public Method buildOperation(String method, String path, Operation op) {
        Method result = new Method();
        result.setPath(path);
        result.setHttpMethod(capitalize(method));
        result.setMethodName(op.getOperationId());
        result.setSummary(op.getSummary());
        result.setDescription(op.getDescription());
        result.setReturnType(resolveReturnType(op));

        List<Param> rawParams = getParams(op);
        AtomicInteger index = new AtomicInteger();
        int lastIndex = rawParams.size() - 1;

        List<Param> params = rawParams.stream().map(param -> {
            param.setIsLast(index.get() == lastIndex);
            index.getAndIncrement();
            return param;
        }).collect(Collectors.toList());
        result.setParams(params);

        result.setRequestBody(resolveRequestBody(op));
        return result;
    }

    /**
     * 
     * @param op
     * @return
     */
    private List<Param> getParams(Operation op) {
        List<Param> result = new ArrayList<>();
        List<Parameter> parameters = op.getParameters();
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                Param param = new Param();
                param.setIsPath("path".equals(parameter.getIn()));
                param.setName(parameter.getName());
                param.setType(resolveParamType(parameter));
                param.setRequired(parameter.getRequired());

                Schema<?> schema = resolveRefSchema(parameter.getSchema());
                Object defaultValue = schema != null ? schema.getDefault() : null;
                param.setDefaultValue(defaultValue != null ? defaultValue.toString() : "");

                result.add(param);
            }
        }
        return result;

    }

    /**
     * 
     * @param parameter
     * @return
     */
    private String resolveParamType(Parameter parameter) {
        Parameter parameterResolved = resolveRefParam(parameter);
        Schema<?> schema = resolveRefSchema(parameterResolved.getSchema());

        return resolveSchemaType(schema);
    }

    /**
     * 
     * @param openAPI
     * @param parameter
     * @return
     */
    private Parameter resolveRefParam(Parameter parameter) {
        if (parameter.get$ref() != null) {
            String refName = parameter.get$ref().substring(parameter.get$ref().lastIndexOf("/") + 1);
            Map<String, Parameter> parametersMap = openAPI.getComponents().getParameters();
            return parametersMap.get(refName);
        }
        return parameter;
    }

    /**
     * 
     * @param schema
     * @return
     */
    private Schema<?> resolveRefSchema(Schema<?> schema) {
        if (schema.get$ref() != null) {
            String ref = schema.get$ref();
            String name = ref.substring(ref.lastIndexOf("/") + 1);
            return openAPI.getComponents().getSchemas().get(name);
        }
        return schema;
    }

    private String resolveReturnType(Operation op) {
        if (op.getResponses() == null)
            return "void";

        ApiResponse response = null;
        if (op.getResponses().get("200") != null) {
            response = op.getResponses().get("200");
        } else if (op.getResponses().get("201") != null) {
            response = op.getResponses().get("201");
        } else if (op.getResponses().get("default") != null) {
            response = op.getResponses().get("default");
        } else {
            return null;
        }

        if (response.getContent() == null || response.getContent().isEmpty()) {
            return null;
        }

        MediaType media = response.getContent().get("application/json");
        if (media == null) {
            media = response.getContent().values().iterator().next();
        }

        Schema<?> schema = media.getSchema();
        return resolveSchemaType(schema);
    }

    private RequestBody resolveRequestBody(Operation op) {
        if (op.getRequestBody() == null)
            return null;

        io.swagger.v3.oas.models.parameters.RequestBody rawRequestBody = op.getRequestBody();

        if (rawRequestBody.get$ref() != null) {
            String refName = rawRequestBody.get$ref().substring(rawRequestBody.get$ref().lastIndexOf("/") + 1);
            rawRequestBody = openAPI.getComponents().getRequestBodies().get(refName);
        }

        MediaType mediaType = rawRequestBody.getContent()
                .values().stream().findFirst().orElse(null);

        if (mediaType == null)
            return null;

        Schema<?> schema = mediaType.getSchema();

        RequestBody requestBody = new RequestBody();
        requestBody.setRequired(Boolean.TRUE.equals(rawRequestBody.getRequired()));
        requestBody.setIsMultipath(isMultipartType(rawRequestBody));
        requestBody.setName(schema.getTitle() != null ? schema.getTitle() : "body");
        requestBody.setType(resolveSchemaType(schema));

        return requestBody;

    }

    private boolean isMultipartType(io.swagger.v3.oas.models.parameters.RequestBody body) {
        if (body.getContent() == null)
            return false;
        return body.getContent().containsKey("multipart/form-data");
    }

    /**
     * 
     * @param str
     * @return
     */
    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * 
     * @param schema
     * @return
     */
    private String resolveSchemaType(Schema<?> schema) {
        String type = resolveSchemaTypeSafe(schema);
        String format = schema.getFormat();

        // ref
        if (schema.get$ref() != null) {
            String ref = schema.get$ref();
            return ref.substring(ref.lastIndexOf("/") + 1);
        }

        // array
        if ("array".equals(type)) {
            Schema<?> items = schema.getItems();
            return "List<" + resolveSchemaType(items) + ">";
        }

        // integer
        if ("integer".equals(type)) {
            if ("int64".equals(format))
                return "Long";
            return "Integer";
        }

        // number
        if ("number".equals(type)) {
            if ("float".equals(format))
                return "Float";
            if ("double".equals(format))
                return "Double";
            return "BigDecimal";
        }

        // string
        if ("string".equals(type)) {
            if ("date-time".equals(format))
                return "LocalDateTime";
            if ("date".equals(format))
                return "LocalDate";
            return "String";
        }

        // boolean
        if ("boolean".equals(type)) {
            return "Boolean";
        }

        // object
        if ("object".equals(type)) {
            if (schema.getProperties() == null) {
                return "Map<String, Object>";
            } else if (schema.getTitle() != null) {
                return schema.getTitle();
            } else {
                return "Object";
            }
        }

        return null;
    }

    private String resolveSchemaTypeSafe(Schema<?> schema) {
        String type = schema.getType();
        if (type == null && schema.getTypes() != null) {
            return schema.getTypes().stream()
                    .filter(t -> !"null".equals(t))
                    .findFirst()
                    .orElse("Object");
        }
        return type != null ? type : "Object";
    }
}
