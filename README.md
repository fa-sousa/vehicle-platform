# Vehicle Platform

Microsserviço responsável por orquestrar o fluxo de compra de veículos.

A aplicação recebe a solicitação do cliente, chama o `vehicle-sale-service`
por HTTP e registra o evento da operação no MongoDB.

## Responsabilidades

- Receber solicitações de compra;
- Comunicar-se com o `vehicle-sale-service`;
- Consolidar a resposta da compra;
- Registrar eventos no MongoDB;
- Isolar o cliente da implementação interna do serviço de vendas.

## Arquitetura

```text
Cliente
   |
   | HTTP
   v
Vehicle Platform :8082
   |
   | WebClient / HTTP
   v
Vehicle Sale Service :8080
   |
   v
PostgreSQL

Vehicle Platform
   |
   v
MongoDB
```

## Estrutura

```text
src/main/kotlin/com/fasousa/vehicleplatform
├── application
│   └── service
├── domain
│   └── model
├── infrastructure
│   ├── client
│   │   └── dto
│   └── persistence
│       ├── entity
│       ├── mapper
│       └── repository
├── presentation
│   ├── controller
│   └── dto
└── VehiclePlatformApplication.kt
```

## Tecnologias

- Kotlin
- Java 17
- Spring Boot 3
- Spring Web
- Spring WebFlux
- WebClient
- Spring Data MongoDB
- MongoDB
- Maven
- Docker
- Docker Compose
- JUnit 5
- Mockito
- JaCoCo

## Pré-requisitos

- Java 17
- Maven
- Docker
- Docker Compose
- `vehicle-sale-service` disponível em `http://localhost:8080`

## Configuração

### Variáveis de Ambiente

Create a `.env` file in the root directory (copy from `.env.example`):

```bash
cp .env.example .env
```

Configure the following environment variables:

```env
# MongoDB Configuration
MONGODB_URI=mongodb://platform_user:your_secure_password@localhost:27017/vehicle_platform_db?authSource=admin
MONGO_INITDB_ROOT_USERNAME=platform_user
MONGO_INITDB_ROOT_PASSWORD=your_secure_password

# Vehicle Sale Service Configuration
VEHICLE_SALE_SERVICE_URL=http://localhost:8080
VEHICLE_SALE_SERVICE_TIMEOUT=5000

# Security - Basic Authentication
SERVER_PORT=8082
```

### Application Configuration

The application automatically reads environment variables:

```yaml
server:
  port: ${SERVER_PORT:8082}

spring:
  application:
    name: vehicle-platform

  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/vehicle_platform_db}

vehicle-sale-service:
  base-url: ${VEHICLE_SALE_SERVICE_URL:http://localhost:8080}
  timeout: ${VEHICLE_SALE_SERVICE_TIMEOUT:5000}
```

### Security Configuration

**Authentication**: Basic HTTP Authentication  
**Default Credentials** (development only):
- Username: `vehicle-client`
- Password: `change_me_in_production`

⚠️ **Production**: Change credentials in SecurityConfig or use environment-based configuration.

Protected Endpoints:
- `POST /api/platform/vehicles/{vehicleId}/purchase` - Requires authentication

Unprotected Endpoints:
- `GET /actuator/health` - Health check
- `GET /actuator/health/live` - Liveness probe
- `GET /actuator/health/ready` - Readiness probe

## Executar durante o desenvolvimento

### 1. Subir o MongoDB

```bash
docker compose up -d mongodb
```

### 2. Executar a aplicação

```bash
mvn spring-boot:run
```

A aplicação ficará disponível em:

```text
http://localhost:8082
```

## Ordem para executar todo o ambiente

### Terminal 1 — Vehicle Sale Service

```bash
cd ~/vehicle-sale-service
docker compose up -d postgres
mvn spring-boot:run
```

### Terminal 2 — Vehicle Platform

```bash
cd ~/vehicle-platform
docker compose up -d mongodb
mvn spring-boot:run
```

## Endpoint

### Iniciar compra pela plataforma

```http
POST /api/platform/vehicles/{vehicleId}/purchase
Authorization: Basic vehicle-client:change_me_in_production
Content-Type: application/json
```

Body:

```json
{
  "cpf": "12345678900"
}
```

Resposta (201 Created):

```json
{
  "saleId": 1,
  "vehicleId": 1,
  "paymentCode": "1855f7f0-3395-455a-bca4-a249695311e0",
  "paymentStatus": "PENDING",
  "platformEventId": "6870247624e8121b5f57df91"
}
```

**Notes:**
- ⚠️ CPF is no longer returned in the response for data privacy
- ✅ CPF must be 11 digits (Brazilian format: 12345678900)
- ✅ Authorization header is required (Basic Authentication)

### Exemplo com curl:

```bash
curl -X POST http://localhost:8082/api/platform/vehicles/1/purchase \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic $(echo -n 'vehicle-client:change_me_in_production' | base64)" \
  -d '{"cpf":"12345678900"}'
```

## Fluxo de compra

1. O cliente solicita a compra à platform;
2. A platform chama o sale service;
3. O sale service valida o veículo;
4. O veículo muda para `PENDING_PAYMENT`;
5. A venda é salva no PostgreSQL;
6. O sale service devolve a venda;
7. A platform registra o evento no MongoDB;
8. A resposta consolidada é devolvida ao cliente.

## Teste manual

Primeiro, cadastre um veículo no sale service:

```bash
curl -X POST http://localhost:8080/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{
    "brand": "Toyota",
    "model": "Corolla",
    "year": 2024,
    "color": "Preto",
    "price": 130000.00
  }'
```

Consulte o ID disponível:

```bash
curl http://localhost:8080/api/vehicles/available
```

Compre pela platform (com autenticação):

```bash
curl -X POST http://localhost:8082/api/platform/vehicles/1/purchase \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic $(echo -n 'vehicle-client:change_me_in_production' | base64)" \
  -d '{"cpf":"12345678900"}'
```

**Important**: 
- ⚠️ Always use authentication (Basic Auth header)
- ⚠️ CPF must be 11 digits
- ⚠️ Do not purchase directly on sale service before using platform

## MongoDB

Database:

```text
vehicle_platform_db
```

Collection:

```text
payment_events
```

Credenciais locais:

```text
Usuário: platform_user
Senha: platform_pass
Porta: 27017
```

## Testes

```bash
mvn clean test
```

Cobertura:

```bash
mvn clean verify
```

Relatório:

```text
target/site/jacoco/index.html
```

## Respostas de erro

### 400 — Bad Request

Corpo da requisição inválido ou CPF ausente/inválido. (Exemplo: CPF menor que 11 dígitos)

### 401 — Unauthorized

Requisição sem autenticação ou credenciais inválidas. Forneça o header `Authorization` com Basic Auth.

### 404 — Not Found

O sale service informou que o veículo solicitado não existe.

### 409 — Conflict

O sale service informou que o veículo não está disponível para compra.

### 502 — Bad Gateway

A platform não conseguiu se comunicar com o sale service ou recebeu resposta inválida. Verifique se o sale-service está rodando em `http://localhost:8080`.

### 503 — Service Unavailable

O sale service está temporariamente indisponível. Tente novamente em alguns momentos.

## Health check

```text
GET /actuator/health
```

## Repositórios relacionados

- `vehicle-platform`: orquestração e MongoDB;
- `vehicle-sale-service`: domínio de veículos, vendas e PostgreSQL.