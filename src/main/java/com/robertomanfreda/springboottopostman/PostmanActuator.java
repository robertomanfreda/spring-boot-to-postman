package com.robertomanfreda.springboottopostman;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robertomanfreda.springboottopostman.models.parent.Container;
import com.robertomanfreda.springboottopostman.models.parent.DispatcherServlet;
import com.robertomanfreda.springboottopostman.models.postman.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(value = "from-spring-boot-to-postman.enabled", havingValue = "true")
@Endpoint(id = "postman-dump")
@Slf4j
public class PostmanActuator {

    private final HttpServletRequest httpServletRequest;
    private final String parentPackage;
    private final String parentArtifactID;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PostmanActuator(@Autowired HttpServletRequest httpServletRequest, @Autowired Environment environment) {
        this.httpServletRequest = httpServletRequest;

        parentPackage = environment.getProperty("from-spring-boot-to-postman.parent.package");
        parentArtifactID = environment.getProperty("from-spring-boot-to-postman.parent.artifact-id");

        customizeObjectMapper();
    }

    private void customizeObjectMapper() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        objectMapper.disable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);
    }

    @ReadOperation
    public String getData() throws JsonProcessingException {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8080/actuator/mappings",
                String.class);

        Container container = objectMapper.readValue(responseEntity.getBody(), Container.class);

        List<DispatcherServlet> dispatcherServlet = container.getContexts().getApplication().getMappings().getDispatcherServlets()
                .getDispatcherServlet()
                .stream()
                .filter(Objects::nonNull)
                .filter(ds -> null != ds.getDetails())
                .filter(ds -> null != ds.getDetails().getHandlerMethod())
                .filter(ds -> null != ds.getDetails().getHandlerMethod().getClassName())
                .filter(ds -> ds.getDetails().getHandlerMethod().getClassName().contains(parentPackage + "." + parentArtifactID))
                .collect(Collectors.toList());

        Map<String, Set<Method>> endpointMethods = new HashMap<>();

        dispatcherServlet.forEach(ds -> {
            try {
                String m = ds.getDetails().getHandlerMethod().getClassName();
                Class<?> restController = Class.forName(m);

                Set<Method> methodsInController = new HashSet<>();

                Arrays.asList(restController.getDeclaredMethods())
                        .forEach(dm -> {
                            dm.setAccessible(true);
                            if (Arrays.asList(dm.getDeclaredAnnotationsByType(PostmanItem.class)).size() > 0) {
                                methodsInController.add(dm);
                            }
                        });

                String controllerPath = restController.getDeclaredAnnotationsByType(RequestMapping.class)[0].value()[0];
                endpointMethods.put(controllerPath, methodsInController);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        List<Item> items = new ArrayList<>();

        endpointMethods.forEach((controllerPath, methods) -> {
            methods.forEach(m -> {
                RequestMapping[] declaredAnnotations = m.getDeclaredAnnotationsByType(RequestMapping.class);
                String name = declaredAnnotations[0].value()[0];
                String method = declaredAnnotations[0].method()[0].name().toUpperCase();
                String path = declaredAnnotations[0].value()[0];
                String protocol = "http";
                if (httpServletRequest.getRequestURL().toString().contains("https:")) {
                    protocol = "https";
                }
                String host = httpServletRequest.getRemoteHost();
                String port = String.valueOf(httpServletRequest.getLocalPort());
                String raw = (host + ":" + port + controllerPath + "/" + path).replaceAll("//+?", "/");
                raw = protocol + "://" + raw;

                items.add(Item.builder()
                        .name(name) // /test
                        .request(Request.builder()
                                .method(method) // GET
                                .header(new ArrayList<>()) // []
                                .url(Url.builder()
                                        .raw(raw) // http://localhost:8080/test
                                        .protocol(protocol) // http
                                        .host(List.of(host)) // [ "localhost" ]
                                        .port(port) // 8080
                                        .path(List.of(path.replaceFirst("/", ""))) // [ "test" ]
                                        .build())
                                .build())
                        .response(new ArrayList<>())
                        .build());
            });
        });

        PostmanCollection postmanCollection = PostmanCollection.builder()
                .info(Info.builder()
                        ._postman_id(UUID.randomUUID().toString())
                        .name(parentPackage + "." + parentArtifactID + "-" + System.currentTimeMillis())
                        .schema("https://schema.getpostman.com/json/collection/v2.1.0/collection.json")
                        .build())
                .item(items)
                .build();

        // TODO check for path params
        // TODO manage with body

        return objectMapper.writeValueAsString(postmanCollection);
    }


}