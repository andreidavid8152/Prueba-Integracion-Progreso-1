package com.ejemplo;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ApiRuta extends RouteBuilder {

    // Almacén en memoria
    private final List<Envio> enviosEnMemoria = new ArrayList<>();
    private final Environment env;
    private boolean startupLogged = false;

    public ApiRuta(Environment env) {
        this.env = env;
    }

    @Override
    public void configure() throws Exception {

        // --- Configuración de la API RESTful ---
        restConfiguration()
            .component("netty-http")
            .port(8080)
            .bindingMode(RestBindingMode.json) // JSON automático
            .apiContextPath("/api-doc") // Ruta para OpenAPI
            .apiProperty("api.title", "EcoLogistics API")
            .apiProperty("api.version", "1.0.0");

        // --- Definición de Endpoints ---
        rest("/envios")
            .description("API REST de Envíos")

            // GET /envios
            .get()
                .description("Lista todos los envíos")
                .outType(Envio[].class)
                .to("direct:get-todos-envios")

            // GET /envios/{id}
            .get("/{id}")
                .description("Obtiene un envío específico por ID")
                .outType(Envio.class)
                .to("direct:get-envio-id")

            // POST /envios
            .post()
                .description("Registra un nuevo envío")
                .type(Envio.class)
                .outType(Envio.class)
                .to("direct:post-envio")
        ;

        // Rutas direct que implementan la lógica de los endpoints

        from("direct:get-todos-envios")
            .routeId("get-todos-envios")
            .process(exchange -> {
                java.nio.file.Path p = java.nio.file.Paths.get("processed", "json", "envios.json");
                String json = "[]";
                if (java.nio.file.Files.exists(p)) {
                    json = java.nio.file.Files.readString(p);
                }
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                Envio[] arr = mapper.readValue(json, Envio[].class);
                exchange.getIn().setBody(arr);
            });

        from("direct:get-envio-id")
            .routeId("get-envio-id")
            .process(exchange -> {
                String id = exchange.getIn().getHeader("id", String.class);
                java.nio.file.Path p = java.nio.file.Paths.get("processed", "json", "envios.json");
                String json = "[]";
                if (java.nio.file.Files.exists(p)) {
                    json = java.nio.file.Files.readString(p);
                }
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                Envio[] arr = mapper.readValue(json, Envio[].class);
                Optional<Envio> encontrado = java.util.Arrays.stream(arr)
                    .filter(e -> e.getId().equals(id))
                    .findFirst();

                if (encontrado.isPresent()) {
                    exchange.getIn().setBody(encontrado.get());
                } else {
                    exchange.getIn().setBody(null);
                    exchange.getIn().setHeader(org.apache.camel.Exchange.HTTP_RESPONSE_CODE, 404);
                }
            });

        from("direct:post-envio")
            .routeId("post-envio")
            .process(exchange -> {
                Envio nuevoEnvio = exchange.getIn().getBody(Envio.class);

                java.nio.file.Path p = java.nio.file.Paths.get("processed", "json", "envios.json");
                String json = "[]";
                if (java.nio.file.Files.exists(p)) {
                    json = java.nio.file.Files.readString(p);
                }
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                Envio[] arr = mapper.readValue(json, Envio[].class);
                java.util.List<Envio> list = new java.util.ArrayList<>(java.util.Arrays.asList(arr));
                list.add(nuevoEnvio);
                String outJson = mapper.writeValueAsString(list);

                java.nio.file.Path dir = java.nio.file.Paths.get("processed", "json");
                if (!java.nio.file.Files.exists(dir)) {
                    java.nio.file.Files.createDirectories(dir);
                }
                java.nio.file.Files.writeString(p, outJson);

                exchange.getIn().setBody(nuevoEnvio);
                exchange.getIn().setHeader(org.apache.camel.Exchange.HTTP_RESPONSE_CODE, 201);
            })
            .log("Nuevo envío registrado vía POST: ID ${body.id}");
    }
}