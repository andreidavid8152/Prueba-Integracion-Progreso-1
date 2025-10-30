# EcoLogistics API - Evaluación Práctica

## Descripción

Este proyecto implementa una API RESTful para la gestión de envíos, utilizando Apache Camel y Spring Boot. Permite cargar datos desde un archivo CSV, transformarlos a JSON y exponerlos mediante endpoints REST para consulta y registro de envíos.

## Características principales

- Carga y transformación de datos desde `envios.csv` a JSON.
- API REST con endpoints para listar, consultar por ID y registrar nuevos envíos.
- Documentación OpenAPI disponible en `/api-doc`.
- Ejemplo de colección Postman para pruebas.

## Estructura de carpetas

```
├── envios.csv                  # Archivo fuente de envíos
├── openapi.yaml                # Especificación OpenAPI
├── pom.xml                     # Configuración Maven
├── Prueba.postman_collection.json     # Colección Postman para pruebas
├── processed/
│   └── json/
│       └── envios.json         # Archivo JSON generado
├── src/
│   ├── main/
│   │   ├── java/com/ejemplo/
│   │   │   ├── ApiRuta.java            # Rutas REST
│   │   │   ├── FileTransferRoute.java  # Transformación CSV a JSON
│   │   │   ├── Envio.java              # Modelo de datos
│   │   │   └── MainApp.java            # Clase principal
│   │   └── resources/
│   │       └── application.properties  # Configuración
│   └── test/
│       └── java/com/ejemplo/           # Pruebas
└── target/                    # Archivos generados por Maven
```

## Dependencias principales

- Java 17
- Spring Boot 3.2.5
- Apache Camel 4.6.0
  - camel-spring-boot-starter
  - camel-csv-starter
  - camel-jackson-starter
  - camel-netty-http-starter
  - camel-openapi-java-starter
- spring-boot-starter-logging

## Instalación y ejecución

1. **Requisitos previos:**
   - Java 17
   - Maven
2. **Compilar el proyecto:**
   ```cmd
   mvn clean package
   ```
3. **Ejecutar la aplicación:**
   ```cmd
   mvn spring-boot:run
   ```
   o bien
   ```cmd
   java -jar target/evaluacion-practica-ecologistics-1.0.0.jar
   ```
4. **Acceder a la API:**
   - Listar envíos: `GET http://localhost:8080/envios`
   - Consultar envío por ID: `GET http://localhost:8080/envios/{id}`
   - Registrar envío: `POST http://localhost:8080/envios`
   - Documentación: `http://localhost:8080/api-doc`

## Pruebas

- Utiliza la colección Postman incluida (`Prueba.postman_collection.json`) para probar los endpoints.
- Los datos de ejemplo están en `envios.csv` y el resultado en `processed/json/envios.json`.

## Autor

- Andrei Flores
