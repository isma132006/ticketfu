# 💚💚Ticketfu - Plataforma de Venta de Boletos & Control de Concurrencia💚💚

![Java](https://img.shields.io/badge/Java-17-red?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker)
![JWT](https://img.shields.io/badge/JWT-Authentication-black?style=for-the-badge&logo=jsonwebtokens)

---

#  Descripción

Ticketfu es una API REST para la gestión y venta de boletos de eventos desarrollada con Spring Boot.

El sistema implementa autenticación mediante JWT, control de concurrencia para evitar sobreventa de boletos, generación de códigos QR únicos y creación de pases digitales en formato PDF.

El objetivo principal es simular una plataforma real de venta de tickets considerando seguridad, consistencia de datos y buenas prácticas de desarrollo backend(NO UI).

---

# Funcionalidades Principales

##  Seguridad y autenticación

- Autenticación stateless mediante Spring Security + JWT.
- Registro e inicio de sesión de usuarios.
- Contraseñas cifradas mediante BCrypt.

---

## Gestión de eventos y categorías

- CRUD completo de eventos.
- CRUD completo de categorías.
- Validaciones mediante Jakarta Validation.
- Manejo global de excepciones mediante `@RestControllerAdvice`.
- Respuestas de error estandarizadas.

---

## Compra y validación de boletos

- Compra de tickets mediante transacciones.
- Control de disponibilidad de boletos.
- Prevención de doble compra mediante bloqueo pesimista.
- Actualización segura del aforo del evento.
- Estados del ticket:
   - AVAILABLE
   - PURCHASED
   - CANCELLED
   - USED
   - RESERVED (Aun  no hace nada)

---

## Código QR y generación de PDF

- Generación de códigos QR únicos utilizando ZXing.
- Creación de boletos digitales mediante OpenPDF.
- Validación de acceso mediante código QR.
- Prevención de uso de boletos duplicados o cancelados.

---

#  Arquitectura del Proyecto

El proyecto utiliza una arquitectura por capas:

CONTROLLER -> SERVICE -> REPOSITORY -> DB


## Controller

Responsable de manejar las peticiones HTTP y construir las respuestas.

## Service

Contiene la lógica de negocio, validaciones y manejo de transacciones.

## Repository

Encargado del acceso a datos mediante Spring Data JPA.

## DTOs

Separan la información expuesta por la API de las entidades internas.

---

# Tecnologías utilizadas

- **Lenguaje:** Java 17 
- **Framework:** Spring Boot 3
- **Persistencia:** Spring Data JPA + Hibernate
- **Seguridad:** Spring Security + JWT
- **Base de datos:** PostgreSQL 16
- **Contenedores:** Docker y Docker Compose
- **Documentación API:** Swagger UI / OpenAPI 3

## Librerías utilizadas

- JJWT
- ZXing
- OpenPDF
- Lombok

---

# Instalación y ejecución

## Requisitos

Antes de ejecutar el proyecto necesitas:

- Java 17 o superior.
- Docker y Docker Compose.
- Maven.

---

## 1. Clonar repositorio

```bash
git clone https://github.com/tu-usuario/ticketfu.git

cd ticketfu
```
## 2. Levantar DB
```
docker compose up -d
```
OJOOO: La db se levanta en:
```
localhost:5433
```
Si se quiere cambiar es mediante application.properties

## 3. Ejecutar aplicacion Spring Boot 
con maven:
````
./mvnw spring-boot:run
````
O ejecutando durectamente:
```
TicketfuApplication.java
```
## 4. Documentación API
Swagger UI:

http://localhost:8080/swagger-ui/index.html

# Desarrollado como proyecto personal para aplicar buenas prácticas de backend con Java y Spring Boot 💚
