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

```yaml
server:
  port: 8082

spring:
  application:
    name: vehicle-platform

  data:
    mongodb:
      uri: mongodb://platform_user:platform_pass@localhost:27017/vehicle_platform_db?authSource=admin

vehicle-sale-service:
  base-url: http://localhost:8080
```

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
```

Body:

```json
{
  "cpf": "12345678900"
}
```

Resposta:

```json
{
  "saleId": 1,
  "vehicleId": 1,
  "cpf": "12345678900",
  "paymentCode": "1855f7f0-3395-455a-bca4-a249695311e0",
  "paymentStatus": "PENDING",
  "platformEventId": "6870247624e8121b5f57df91"
}
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

Compre exclusivamente pela platform:

```bash
curl -X POST http://localhost:8082/api/platform/vehicles/1/purchase \
  -H "Content-Type: application/json" \
  -d '{"cpf":"12345678900"}'
```

Não compre antes diretamente no sale service, pois isso muda o veículo para
`PENDING_PAYMENT`.

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

Corpo da requisição inválido ou CPF ausente/inválido.

### 404 — Not Found

O sale service informou que o veículo solicitado não existe.

### 409 — Conflict

O sale service informou que o veículo não está disponível.

### 502 — Bad Gateway

A platform não conseguiu se comunicar com o sale service ou recebeu resposta inválida.

### 503 — Service Unavailable

O serviço de vendas está temporariamente indisponível.

## Health check

```text
GET /actuator/health
```

## Repositórios relacionados

- `vehicle-platform`: orquestração e MongoDB;
- `vehicle-sale-service`: domínio de veículos, vendas e PostgreSQL.