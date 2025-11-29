Microservicio de Carga de Pedidos

Prueba Técnica – Desarrollado por Ricardo Suárez

Este proyecto implementa un microservicio REST para la carga masiva, validación y persistencia de pedidos a partir de archivos CSV. La solución se basa en principios de Arquitectura Hexagonal y utiliza procesamiento por lotes para garantizar eficiencia y consistencia.

Tecnologías Utilizadas:

    Java 21 (compatible con Java 17+)
    Spring Boot 3
    PostgreSQL para persistencia
    Flyway para versionamiento del esquema
    JUnit 5 y Mockito para pruebas unitarias
    OpenAPI / Swagger para documentación
    Lombok para reducción de código repetitivo

Instrucciones de Ejecución
    1. Prerrequisitos

        Java 17 o superior instalado.
        PostgreSQL instalado.
        Base de datos vacía llamada pedidos_db.

    2. Configuración

    Verifique que el archivo src/main/resources/application.properties contenga las credenciales correctas de su entorno local:

        spring.datasource.url=jdbc:postgresql://localhost:5432/pedidos_db
        spring.datasource.username=postgres
        spring.datasource.password=TU_CONTRASEÑA
        server.port=8085

    3. Compilación y Ejecución

        Ejecutar desde la raíz del proyecto:
        ./mvnw spring-boot:run

        Al iniciar, Flyway aplicará automáticamente todas las migraciones necesarias, creando tablas como pedidos, clientes, zonas y cargas_idempotencia, además de insertar datos de referencia.

Documentación de la API

Una vez la aplicación esté ejecutándose, la documentación interactiva estará disponible en:

http://localhost:8085/swagger-ui/index.html

Endpoint Principal

POST /pedidos/cargar

    Consumes: multipart/form-data
    Parámetro (form-data): file (archivo CSV a procesar)
    Header obligatorio: Idempotency-Key (cadena única para evitar duplicación de carga)

Decisiones de Diseño y Estrategia de Procesamiento
    1. Arquitectura Hexagonal

    El proyecto se estructura en tres capas claramente separadas:

        Domain: Modelos y puertos, sin dependencias externas.
        Application: Casos de uso, lógica de negocio y orquestación.
        Infrastructure: Controladores REST y adaptadores de persistencia con JPA.

    Esta distribución permite un sistema desacoplado, testable y de fácil mantenimiento.

    2. Procesamiento Batch

    Para manejar cargas de datos sin comprometer la memoria:

        Se procesa el archivo mediante InputStream y BufferedReader, sin cargarlo completo en memoria.
        Se utiliza un buffer temporal configurable (BATCH_SIZE = 500).
        La inserción se realiza con saveAll() cuando el buffer se llena o al finalizar, optimizando operaciones contra la base de datos.

    3. Idempotencia Técnica

    Se garantiza que un mismo archivo no sea procesado dos veces:

        Se exige un encabezado Idempotency-Key.
        Se calcula un hash SHA-256 del archivo recibido.
        Se verifica en la tabla cargas_idempotencia la combinación clave + hash.
        Si ya existe, la carga se rechaza de forma segura.

    4. Validaciones de Negocio

    Entre las reglas implementadas destacan:

    Fecha de entrega: No se permiten fechas pasadas, considerando zona horaria America/Lima.
    Refrigeración: Los pedidos que requieren refrigeración solo pueden asignarse a zonas que la soporten.
    Consistencia: Validación de existencia de clientes, zonas y unicidad del número de pedido.


Pruebas Unitarias

Las pruebas se centran en la capa de aplicación (CargarPedidosService), utilizando Mockito para aislar la lógica de negocio del acceso a datos. Esto garantiza un testeo preciso de reglas y flujos críticos.

Para ejecutar los tests:

./mvnw test