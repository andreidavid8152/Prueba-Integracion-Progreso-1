package com.ejemplo;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FileTransferRoute extends RouteBuilder {

    // Almacén temporal local para la ruta de transferencia de archivo
    private final List<Envio> enviosEnMemoria = new ArrayList<>();

    @Override
    public void configure() throws Exception {

        // leer envios.csv
        from("file:?fileName=envios.csv&noop=true&initialDelay=1000")
            .routeId("filetransfer-carga-csv")
            .convertBodyTo(String.class)
            .process(exchange -> {
                String body = exchange.getIn().getBody(String.class);
                if (body == null || body.isBlank()) {
                    log.info("Archivo vacío o no leído.");
                    return;
                }

                String[] lines = body.split("\\r?\\n");
                if (lines.length <= 1) {
                    log.info("[INFO] No hay filas de datos en el CSV.");
                    return;
                }

                String headerLine = lines[0];
                String[] headers = headerLine.split(",");

                int registros = 0;
                for (int i = 1; i < lines.length; i++) {
                    String line = lines[i].trim();
                    if (line.isEmpty()) continue;
                    String[] cols = line.split(",", -1);

                    java.util.Map<String, String> row = new java.util.HashMap<>();
                    for (int j = 0; j < Math.min(headers.length, cols.length); j++) {
                        row.put(headers[j].trim(), cols[j].trim());
                    }

                    Envio envio = new Envio();
                    envio.setId(row.getOrDefault("id_envio", row.getOrDefault("id", "")).trim());
                    envio.setCliente(row.getOrDefault("cliente", "").trim());
                    envio.setDireccion(row.getOrDefault("direccion", "").trim());
                    envio.setEstado(row.getOrDefault("estado", "").trim());

                    enviosEnMemoria.add(envio);
                    registros++;
                }

                log.info("[INFO] Archivo cargado con {} registros.", registros);
                log.info("[INFO] Datos transformados a formato JSON.");
            })
            .setBody(constant(enviosEnMemoria))
            .marshal().json(JsonLibrary.Jackson)
            .process(exchange -> {
                java.nio.file.Path dir = java.nio.file.Paths.get("processed", "json");
                if (!java.nio.file.Files.exists(dir)) {
                    java.nio.file.Files.createDirectories(dir);
                }
                String json = exchange.getIn().getBody(String.class);
                java.nio.file.Path out = dir.resolve("envios.json");
                java.nio.file.Files.writeString(out, json);
            })
            .log("[INFO] Archivo JSON generado en processed/json/envios.json");
    }
}
