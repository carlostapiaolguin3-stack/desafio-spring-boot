# Reporte Técnico: API REST para Gestión de Tareas

Este repositorio contiene la implementación del desafío técnico para la empresa **NUEVO SPA**, consistente en una plataforma de gestión de tareas. La solución ha sido diseñada bajo estándares de ingeniería de software para entornos productivos, priorizando la mantenibilidad, escalabilidad y seguridad.

---

## Cumplimiento de Requisitos Técnicos

Siguiendo los requerimientos técnicos del desafío, el proyecto implementa:

*   **Java 21 (LTS)**: Uso extensivo de **Records** para DTOs inmutables, **Lambdas** y **Streams** para el procesamiento de colecciones.
*   **Spring Boot 3.5.0**: Framework base actualizado a la última versión estable solicitada.
*   **Base de Datos H2**: Persistencia en memoria con esquema de navegación (`usuarios`, `tareas`, `estados_tarea`).
*   **JPA / Hibernate**: Capa de persistencia robusta con relaciones bien definidas para asegurar la integridad de los datos.
*   **Seguridad JWT**: Autenticación stateless mediante JSON Web Tokens para todas las operaciones CRUD.
*   **OpenAPI 3.0**: Documentación interactiva y contrato de API estandarizado.

---

## Decisiones de Arquitectura y Diseño

### Enfoque "API-First" (Bonus Extra)
Como parte del enfoque de excelencia técnica, se adoptó la metodología **API-First**. El contrato definido en `openapi.yml` rige la implementación:
*   **Generación de Código**: Se utiliza el `openapi-generator-maven-plugin` para generar interfaces de controladores y DTOs, garantizando que la implementación sea 100% fiel al contrato.
*   **Contrato como Fuente de Verdad**: Cualquier cambio en la API se inicia en la especificación, lo que permite un desarrollo desacoplado y seguro.

### Capas y Patrones
*   **Clean Architecture**: Separación clara entre controladores, servicios, repositorios y modelos.
*   **Mapeo de Datos**: Uso de **MapStruct** para transformaciones eficientes entre entidades y DTOs, evitando código repetitivo.
*   **Rate Limiting**: Implementación de un interceptor personalizado para control de tráfico por IP, fortaleciendo la resiliencia del sistema.

---

## Calidad y Pruebas

La robustez del sistema está respaldada por una cobertura de pruebas del **100% en el core de negocio**:
*   **Unitarias**: Lógica de servicios y mappers.
*   **Integración**: Flujos de controladores y filtros de seguridad.
*   **End-to-End (E2E)**: Escenarios de uso completo desde autenticación hasta persistencia.

---

## Guía de Levantamiento y Evaluación

### Despliegue con Docker
Para una evaluación rápida en un entorno aislado:
```bash
docker-compose up --build
```

### Ejecución Local (Maven)
Requisitos: JDK 21+.
1.  **Generar fuentes y compilar**: `mvn clean compile`
2.  **Iniciar aplicación**: `mvn spring-boot:run`

### Documentación y Pruebas Manuales
*   **Swagger UI**: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
*   **Postman**: Se adjunta la colección `desafio-previred.postman_collection.json` y su entorno `desafio-previred.postman_environment.json`. La colección incluye scripts de pre-solicitud para la captura automática de tokens.

---

## Credenciales de Acceso

| Rol | Usuario | Password |
| :--- | :--- | :--- |
| **Administrador** | `admin` | `admin123` |
| **Usuario Estándar** | `user` | `user123` |

---
*Desarrollado para el proceso de selección técnica de PreviRed.*
