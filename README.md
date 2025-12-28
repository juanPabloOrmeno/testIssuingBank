# 🏦 Issuing Bank - Sistema de Procesamiento de Pagos

## 📌 Descripción General

**Issuing Bank** es un servicio backend que actúa como **banco emisor** en un sistema de procesamiento de pagos. Su responsabilidad principal es autorizar, procesar y gestionar transacciones de pago que provienen de comercios o acquirers.

### ¿Qué problema resuelve?

Este sistema simula el rol de un banco emisor en una infraestructura de pagos:
- Valida y autoriza transacciones de pago
- Almacena el historial de transacciones
- Proporciona consultas de estado de pagos
- Expone una API REST para integración con acquirers o comercios

### Tipo de sistema

**Payment Gateway / Issuing Bank Service**
- Actúa como el servicio final que aprueba o rechaza transacciones
- Gestiona la lógica de negocio relacionada con validación de fondos
- Mantiene un registro persistente de todas las transacciones

### Stack tecnológico

| Tecnología | Versión | Propósito |
|-----------|---------|-----------|
| **Java** | 25 (compatible con Java 25) | Lenguaje principal |
| **Spring Boot** | 4.0.1 | Framework principal |
| **Spring Web** | 4.0.1 | API REST |
| **Spring Data JPA** | 4.0.1 | Persistencia de datos |
| **Spring Validation** | 4.0.1 | Validación de DTOs |
| **H2 Database** | 2.x | Base de datos en memoria |
| **SpringDoc OpenAPI** | 2.7.0 | Documentación Swagger |
| **Logback + Logstash** | 8.0 | Logging estructurado JSON |
| **Maven** | 3.9+ | Gestión de dependencias |
| **Docker** | - | Containerización |

---

## 🧱 Arquitectura del Proyecto

### Arquitectura de capas (Layered Architecture)

El proyecto sigue una arquitectura de **capas bien definidas** con separación de responsabilidades:

```
┌─────────────────────────────────────┐
│     Controller Layer (API REST)     │  ← Expone endpoints HTTP
├─────────────────────────────────────┤
│     Service Layer (Lógica)          │  ← Reglas de negocio
├─────────────────────────────────────┤
│     Repository Layer (Persistencia) │  ← Acceso a base de datos
├─────────────────────────────────────┤
│     Model Layer (Entidades)         │  ← Entidades JPA
└─────────────────────────────────────┘
```

### Estructura de carpetas

```
issuingBank/
├── src/
│   ├── main/
│   │   ├── java/org/bank/issuingbank/
│   │   │   ├── controller/          # Controladores REST
│   │   │   │   └── PaymentController.java
│   │   │   ├── service/             # Interfaces y lógica de negocio
│   │   │   │   ├── PaymentService.java
│   │   │   │   └── impl/
│   │   │   │       └── PaymentServiceImpl.java
│   │   │   ├── repository/          # Repositorios JPA
│   │   │   │   └── PaymentRepository.java
│   │   │   ├── model/               # Entidades JPA
│   │   │   │   └── Payment.java
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── request/
│   │   │   │   │   └── PaymentRequest.java
│   │   │   │   └── response/
│   │   │   │       ├── PaymentResponse.java
│   │   │   │       └── ErrorResponse.java
│   │   │   ├── enums/               # Enumeraciones
│   │   │   │   └── TransactionStatus.java
│   │   │   ├── exception/           # Excepciones personalizadas
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── TransactionNotFoundException.java
│   │   │   ├── config/              # Configuraciones
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── logging/             # Utilidades de logging
│   │   │   │   └── LoggingAspect.java
│   │   │   └── IssuingBankApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── logback-spring.xml
│   └── test/                        # Tests unitarios e integración
│       └── java/org/bank/issuingbank/
│           ├── controller/
│           ├── service/
│           └── dto/
├── logs/                            # Archivos de log
├── target/                          # Artefactos compilados
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── LOGGING_README.md
└── README.md
```

### Descripción de módulos

| Módulo | Responsabilidad |
|--------|----------------|
| **controller** | Expone endpoints REST, valida entrada HTTP, mapea respuestas |
| **service** | Implementa lógica de negocio, orquesta operaciones, valida reglas |
| **repository** | Abstrae el acceso a datos, ejecuta consultas JPA |
| **model** | Define entidades del dominio, mapea tablas de base de datos |
| **dto** | Define contratos de entrada/salida de la API |
| **exception** | Manejo centralizado de errores y excepciones personalizadas |
| **config** | Configuración de beans, Swagger, seguridad |
| **logging** | Logging estructurado con MDC y correlationId |

---

## ⚙️ Requisitos Previos

### Obligatorios

- ☕ **Java 17 o superior** (el proyecto está configurado para Java 25)
  ```bash
  java -version  # Debe mostrar 17+
  ```

- 📦 **Maven 3.9+**
  ```bash
  mvn -version
  ```

### Opcionales

- 🐳 **Docker** (para ejecución containerizada)
  ```bash
  docker --version
  docker-compose --version
  ```

### Sistema operativo

Compatible con:
- ✅ Linux (Ubuntu, Debian, RHEL, etc.)
- ✅ macOS (Intel y Apple Silicon)
- ✅ Windows 10/11 (con WSL2 recomendado)

---

## 🚀 Cómo Levantar el Proyecto

### Opción 1: Ejecución local con Maven

```bash
# 1. Clonar el repositorio (si aplica)
cd issuingBank

# 2. Compilar el proyecto
./mvnw clean package

# 3. Ejecutar la aplicación
./mvnw spring-boot:run

# O ejecutar el JAR generado
java -jar target/issuingBank-0.0.1-SNAPSHOT.jar
```

La aplicación estará disponible en: **http://localhost:8080**

### Opción 2: Ejecución con Docker

```bash
# 1. Construir la imagen
docker build -t issuingbank:latest .

# 2. Ejecutar el contenedor
docker run -p 8080:8080 issuingbank:latest
```

### Opción 3: Ejecución con Docker Compose

```bash
# Levantar todos los servicios
docker-compose up --build

# Detener los servicios
docker-compose down
```

### Verificar que el servicio está activo

```bash
# Health check (si está configurado actuator)
curl http://localhost:8080/actuator/health

# Swagger UI
open http://localhost:8080/swagger-ui.html
```

---

## 🔗 Endpoints Disponibles

### Base URL
```
http://localhost:8080
```

### 1. Procesar un pago

**POST** `/payments`

Autoriza y procesa una nueva transacción de pago.

#### Request

```json
{
  "merchantId": "MERCHANT_001",
  "amount": 50000.0,
  "currency": "CLP",
  "cardToken": "tok_abc123xyz456",
  "expirationDate": "12/26"
}
```

#### Response (200 OK)

```json
{
  "transactionId": "123e4567-e89b-12d3-a456-426614174000",
  "status": "APPROVED",
  "responseCode": "00",
  "createdAt": "2025-12-28T10:30:00"
}
```

#### Códigos de respuesta

| Estado | Descripción |
|--------|-------------|
| `APPROVED` | Transacción aprobada exitosamente |
| `DECLINED` | Transacción rechazada (fondos insuficientes, etc.) |
| `ERROR` | Error en el procesamiento |

#### Ejemplo con curl

```bash
curl -X POST http://localhost:8080/payments \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": "MERCHANT_001",
    "amount": 50000.0,
    "currency": "CLP",
    "cardToken": "tok_abc123xyz456",
    "expirationDate": "12/26"
  }'
```

---

### 2. Consultar estado de transacción

**GET** `/payments/{transactionId}`

Obtiene el estado actual de una transacción por su ID.

#### Request

```bash
GET /payments/123e4567-e89b-12d3-a456-426614174000
```

#### Response (200 OK)

```json
{
  "transactionId": "123e4567-e89b-12d3-a456-426614174000",
  "status": "APPROVED",
  "responseCode": "00",
  "createdAt": "2025-12-28T10:30:00"
}
```

#### Ejemplo con curl

```bash
curl http://localhost:8080/payments/123e4567-e89b-12d3-a456-426614174000
```

---

## 📘 Swagger / OpenAPI

### Acceso a la documentación interactiva

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml

### Cómo probar la API desde Swagger

1. Abrir http://localhost:8080/swagger-ui.html
2. Seleccionar el endpoint deseado (ej: `POST /payments`)
3. Hacer clic en **"Try it out"**
4. Editar el JSON de ejemplo
5. Hacer clic en **"Execute"**
6. Ver la respuesta en tiempo real

### Captura de pantalla esperada

![Swagger UI](https://via.placeholder.com/800x400?text=Swagger+UI+Screenshot)

---

## 🧪 Testing

### Ejecutar todos los tests

```bash
./mvnw test
```

### Ejecutar tests con reporte de cobertura

```bash
./mvnw clean test jacoco:report
```

El reporte estará disponible en: `target/site/jacoco/index.html`

### Tipos de tests

| Tipo | Ubicación | Descripción |
|------|-----------|-------------|
| **Unitarios** | `src/test/.../service/` | Validan lógica de negocio aislada |
| **Integración** | `src/test/.../controller/` | Validan endpoints completos (MockMvc) |
| **DTO Validation** | `src/test/.../dto/` | Validan anotaciones de validación |

### Estructura de tests

```
src/test/java/org/bank/issuingbank/
├── controller/
│   └── PaymentControllerIntegrationTest.java    # 10 tests
├── service/
│   └── impl/
│       └── PaymentServiceImplTest.java          # 15 tests
└── dto/
    └── PaymentRequestTest.java                  # 13 tests
```

### Estadísticas de cobertura

```
Total de tests: 38
Tests pasando: 38 ✅
Cobertura estimada: >85%
```

---

## 🗂️ Configuración

### Variables de entorno

Puedes sobrescribir la configuración mediante variables de entorno:

```bash
# Base de datos
export SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb
export SPRING_DATASOURCE_USERNAME=sa
export SPRING_DATASOURCE_PASSWORD=

# Puerto del servidor
export SERVER_PORT=8080

# Nivel de logging
export LOGGING_LEVEL_ROOT=INFO
export LOGGING_LEVEL_ORG_BANK=DEBUG
```

### Archivo application.properties

```properties
# Nombre de la aplicación
spring.application.name=issuingBank

# Base de datos H2 (en memoria)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Consola H2 (para debugging)
spring.h2.console.enabled=true
# Acceso: http://localhost:8080/h2-console

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop

# Logging
logging.level.org.hibernate=WARN
logging.level.org.bank=INFO

# Swagger
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

### Configuración por ambiente

Para usar diferentes configuraciones por ambiente:

```bash
# Desarrollo
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Producción
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

Crear archivos:
- `application-dev.properties`
- `application-prod.properties`

---

## 🔐 Manejo de Errores

### Estructura de errores

Todos los errores siguen el formato `ErrorResponse`:

```json
{
  "errorCode": "VALIDATION_ERROR",
  "message": "Amount is required",
  "status": 400,
  "path": "/payments",
  "timestamp": "2025-12-28T10:30:00"
}
```

### Códigos HTTP utilizados

| Código | Escenario |
|--------|-----------|
| `200 OK` | Transacción procesada exitosamente |
| `400 BAD REQUEST` | Datos de entrada inválidos (validación fallida) |
| `404 NOT FOUND` | Transacción no encontrada |
| `500 INTERNAL SERVER ERROR` | Error inesperado en el servidor |
| `503 SERVICE UNAVAILABLE` | Servicio temporalmente no disponible |

### Ejemplos de respuestas de error

#### Validación fallida

```json
{
  "errorCode": "BAD_REQUEST",
  "message": "Amount must be greater than zero",
  "status": 400,
  "path": "/payments",
  "timestamp": "2025-12-28T10:30:00"
}
```

#### Transacción no encontrada

```json
{
  "errorCode": "NOT_FOUND",
  "message": "Transaction with ID abc123 not found",
  "status": 404,
  "path": "/payments/abc123",
  "timestamp": "2025-12-28T10:30:00"
}
```

---

## 🧠 Decisiones Técnicas

### ¿Por qué Spring Boot?

- ✅ **Convención sobre configuración**: Reduce boilerplate
- ✅ **Ecosistema maduro**: Amplia comunidad y documentación
- ✅ **Spring Data JPA**: Simplifica persistencia de datos
- ✅ **Embedded server**: No requiere servidor de aplicaciones externo
- ✅ **Actuator**: Métricas y health checks out-of-the-box
- ✅ **Testing**: Excelente soporte para tests de integración

### ¿Por qué arquitectura en capas?

- ✅ **Separación de responsabilidades**: Cada capa tiene un propósito claro
- ✅ **Mantenibilidad**: Cambios en una capa no afectan otras
- ✅ **Testabilidad**: Fácil mockear dependencias entre capas
- ✅ **Escalabilidad**: Permite escalar componentes independientemente
- ✅ **Estándar de industria**: Patrón ampliamente reconocido

### ¿Por qué usar DTOs?

- ✅ **Desacoplamiento**: Separar modelo de dominio del contrato API
- ✅ **Validación explícita**: Anotaciones de validación centralizadas
- ✅ **Seguridad**: No exponer entidades JPA directamente
- ✅ **Flexibilidad**: Transformar datos sin afectar el modelo
- ✅ **Documentación**: DTOs documentan contratos de API claramente

### ¿Por qué H2 Database?

- ✅ **Desarrollo rápido**: Base de datos en memoria, sin instalación
- ✅ **Testing**: Ideal para tests que requieren base de datos
- ✅ **Portabilidad**: No requiere configuración externa
- ⚠️ **No recomendado para producción**: Usar PostgreSQL/MySQL en prod

### ¿Por qué Records de Java?

Los DTOs usan `record` (Java 14+):
- ✅ **Inmutabilidad**: Records son inmutables por defecto
- ✅ **Menos código**: Generan automáticamente getters, equals, hashCode, toString
- ✅ **Claridad**: Sintaxis concisa y legible

### ¿Por qué logging estructurado (JSON)?

- ✅ **Parseable**: Fácil indexación en ELK/Splunk/Datadog
- ✅ **Correlación**: Incluye `correlationId` para tracing distribuido
- ✅ **Búsquedas eficientes**: Queries rápidas en logs
- ✅ **Integración con observability**: Compatible con herramientas modernas

---

## 🧪 Cómo Probar Rápidamente

### 1. Procesar un pago exitoso

```bash
curl -X POST http://localhost:8080/payments \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": "MERCHANT_001",
    "amount": 75000,
    "currency": "CLP",
    "cardToken": "tok_valid_card_123",
    "expirationDate": "12/27"
  }' | jq
```

### 2. Consultar el estado de la transacción

```bash
# Guarda el transactionId de la respuesta anterior
TRANSACTION_ID="<pegar-transaction-id-aqui>"

curl http://localhost:8080/payments/$TRANSACTION_ID | jq
```

### 3. Validar errores de validación

```bash
# Enviar monto negativo (debe fallar)
curl -X POST http://localhost:8080/payments \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": "MERCHANT_001",
    "amount": -100,
    "currency": "CLP",
    "cardToken": "tok_123",
    "expirationDate": "12/27"
  }' | jq
```

### 4. Consultar transacción inexistente

```bash
curl http://localhost:8080/payments/nonexistent-id | jq
```

### 5. Ver logs estructurados

```bash
# Ver logs en tiempo real
tail -f logs/app.log

# Buscar por correlationId
grep "correlationId" logs/app.log | jq
```

---

## 📦 Estructura del Proyecto (Árbol Completo)

```
issuingBank/
├── .mvn/                                    # Maven wrapper
├── logs/                                    # Archivos de log
│   ├── app.log                             # Log general
│   └── app.log.2025-12-27                  # Logs rotados
├── src/
│   ├── main/
│   │   ├── java/org/bank/issuingbank/
│   │   │   ├── IssuingBankApplication.java           # Clase principal
│   │   │   ├── config/
│   │   │   │   └── OpenApiConfig.java                # Configuración Swagger
│   │   │   ├── controller/
│   │   │   │   └── PaymentController.java            # Endpoints REST
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   └── PaymentRequest.java           # DTO de entrada
│   │   │   │   └── response/
│   │   │   │       ├── PaymentResponse.java          # DTO de salida
│   │   │   │       └── ErrorResponse.java            # DTO de error
│   │   │   ├── enums/
│   │   │   │   └── TransactionStatus.java            # Estados de transacción
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java       # Manejo global de errores
│   │   │   │   └── TransactionNotFoundException.java # Excepción custom
│   │   │   ├── logging/
│   │   │   │   └── LoggingAspect.java                # Logging con AOP
│   │   │   ├── model/
│   │   │   │   └── Payment.java                      # Entidad JPA
│   │   │   ├── repository/
│   │   │   │   └── PaymentRepository.java            # Repositorio JPA
│   │   │   └── service/
│   │   │       ├── PaymentService.java               # Interface del servicio
│   │   │       └── impl/
│   │   │           └── PaymentServiceImpl.java       # Implementación lógica
│   │   └── resources/
│   │       ├── application.properties                # Configuración principal
│   │       └── logback-spring.xml                    # Configuración de logging
│   └── test/
│       └── java/org/bank/issuingbank/
│           ├── IssuingBankApplicationTests.java      # Test de contexto
│           ├── controller/
│           │   └── PaymentControllerIntegrationTest.java  # Tests de endpoints
│           ├── dto/
│           │   └── PaymentRequestTest.java           # Tests de validación
│           └── service/
│               └── impl/
│                   └── PaymentServiceImplTest.java   # Tests de lógica
├── target/                                  # Artefactos compilados
│   ├── classes/                            # Clases compiladas
│   ├── generated-sources/                  # Fuentes generadas
│   ├── test-classes/                       # Tests compilados
│   ├── surefire-reports/                   # Reportes de tests
│   └── issuingBank-0.0.1-SNAPSHOT.jar      # JAR ejecutable
├── .gitignore
├── docker-compose.yml                       # Orquestación Docker
├── Dockerfile                               # Imagen Docker
├── HELP.md                                  # Ayuda Spring Boot
├── LOGGING_README.md                        # Documentación de logging
├── mvnw                                     # Maven wrapper (Unix)
├── mvnw.cmd                                 # Maven wrapper (Windows)
├── pom.xml                                  # Configuración Maven
└── README.md                                # Este archivo
```

---

## 📝 Notas Adicionales

## 👥 Contribuciones

Este proyecto es parte de un sistema de pagos educativo/evaluativo.

---

## 📄 Licencia

[Especificar licencia si aplica]

---

## 📧 Contacto

Para preguntas o soporte:
- **Email**: [email]
- **GitHub**: [repo]

---

**Desarrollado con ☕ y Spring Boot**
