# Microservicio de Carga de Pedidos - Prueba Técnica

Este proyecto implementa un microservicio REST para la carga masiva, validación y persistencia de pedidos desde archivos CSV, desarrollado bajo los principios de **Arquitectura Hexagonal** y procesamiento por lotes (Batch).

## Stack Tecnológico
* **Java 21** (Compatible con Java 17+)
* **Spring Boot 3**
* **PostgreSQL** (Persistencia)
* **Flyway** (Versionamiento de esquema de base de datos)
* **JUnit 5 & Mockito** (Testing y Calidad)
* **OpenAPI / Swagger** (Documentación de API)
* **Lombok** (Reducción de código repetitivo)

---

## Instrucciones de Ejecución

### 1. Prerrequisitos
* Tener instalado Java 17 o superior.
* Tener instalado PostgreSQL.
* Crear una base de datos vacía llamada `pedidos_db`.

### 2. Configuración
Asegúrate de que el archivo `src/main/resources/application.properties` tenga las credenciales correctas de tu base de datos local:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pedidos_db
spring.datasource.username=postgres
spring.datasource.password=TU_CONTRASEÑA
server.port=8085