package com.codegen.openapi;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.codegen.openapi.objects.Method;
import com.codegen.openapi.objects.Template;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;

public class OpenApiCodegen {

    private static final String SPEC_DIR = "/home/pengu/hrm/openapi-codegen/docs/openapi";
    private static final String OUTPUT_DIR = "src/main/java/com/codegen/openapi/generated";
    private static final String TEMPLATE_CONTROLLER = "templates/controller.mustache";
    private static final String TEMPLATE_SERVICE = "templates/service.mustache";
    private static final String PACKAGE_BASE = "com.dev.hrm_api.generated";

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Paths.get(OUTPUT_DIR));

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(SPEC_DIR), "*.{yaml,yml,json}")) {
            for (Path entry : stream) {
                String fileName = entry.getFileName().toString();
                String serviceName = toPascal(fileName.replaceAll("\\..*$", ""));
                processOpenApi(entry.toString(), serviceName);
            }
        }
    }

    private static void processOpenApi(String specPath, String serviceName) throws IOException {
        OpenAPI openAPI = new OpenAPIV3Parser().read(specPath);
        if (openAPI == null) {
            System.err.println("Failed to parse: " + specPath);
            return;
        }

        String controllerClass = serviceName + "Controller";
        String serviceClass = serviceName + "Service";

        /**
         * controller
         */
        Template controller = new Template();
        controller.setPackageName(PACKAGE_BASE + "." + serviceName + ".controllers");
        controller.setName(controllerClass);
        controller.setMethods(buildMethos(openAPI));

        /**
         * service
         */
        Template service = new Template();
        service.setPackageName(PACKAGE_BASE + "." + serviceName + ".services");
        service.setName(serviceClass);
        service.setMethods(buildMethos(openAPI));

        /**
         * create folder
         */
        Path controllersDir = Paths.get(OUTPUT_DIR, "controllers");
        Path servicesDir = Paths.get(OUTPUT_DIR, "services");
        Path dtosDir = Paths.get(OUTPUT_DIR, "dtos");

        Files.createDirectories(controllersDir);
        Files.createDirectories(servicesDir);
        Files.createDirectories(dtosDir);

        /**
         * render controller
         */
        renderTemplate(TEMPLATE_CONTROLLER, controller, controllersDir.resolve(controllerClass + ".java"));

        /**
         * render service
         */
        renderTemplate(TEMPLATE_SERVICE, service, servicesDir.resolve(serviceClass + ".java"));

        // generateDtos(openAPI, serviceName);

        System.out.println("Generated: " + controllerClass + " & " + serviceClass);
    }

    private static List<Method> buildMethos(OpenAPI openAPI) {
        OpenApiToJavaMapper openApiToJavaMapper = new OpenApiToJavaMapper(openAPI);
        List<Method> methods = new ArrayList<>();
        if (openAPI.getPaths() != null) {
            for (Map.Entry<String, PathItem> entry : openAPI.getPaths().entrySet()) {
                String path = entry.getKey();
                PathItem pathItem = entry.getValue();
                pathItem.readOperationsMap().forEach((method, op) -> {
                    Method methodObj = openApiToJavaMapper.buildOperation(method.toString(), path,
                            op);
                    methods.add(methodObj);
                });
            }
        }
        return methods;
    }

    /**
     * Generator files
     * 
     * @param templatePath path to mustache template
     * @param context      dynamic data for mustache
     * @param outputPath   directory generated files
     * @throws IOException
     */
    private static void renderTemplate(String templatePath, Template context, Path outputPath)
            throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(templatePath);
        try (Writer writer = new FileWriter(outputPath.toFile())) {
            mustache.execute(writer, context).flush();
        }
    }

    /**
     * Convert to PascalCase
     * 
     * @param input "hello-world"
     * @return "HelloWorld"
     */
    private static String toPascal(String input) {
        String[] parts = input.split("[-_]");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty())
                sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.toString();
    }

    /**
     * 
     * @param input "hello-world"
     * @return "helloWorld"
     */
    private static String toCamel(String input) {
        String pascal = toPascal(input);
        return Character.toLowerCase(pascal.charAt(0)) + pascal.substring(1);
    }
}