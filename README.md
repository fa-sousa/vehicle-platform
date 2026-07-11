# Vehicle Platform

Microsserviço responsável por orquestrar as operações da plataforma de venda de veículos.

A aplicação recebe solicitações de compra, comunica-se via HTTP com o
`vehicle-sale-service` e registra os eventos da operação no MongoDB.

## Arquitetura

```text
Cliente
   |
   | HTTP
   v
Vehicle Platform :8082
   |
   | HTTP
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

## Responsabilidades

### Vehicle Platform

- Receber solicitações de compra;
- Chamar o `vehicle-sale-service`;
- Registrar eventos da operação no MongoDB;
- Retornar uma resposta consolidada ao cliente.

### Vehicle Sale Service

- Cadastrar e atualizar veículos;
- Listar veículos disponíveis e vendidos;
- Validar a disponibilidade do veículo;
- Criar a venda;
- Alterar o veículo para `PENDING_PAYMENT`;
- Persistir veículos e vendas no PostgreSQL.

## Tecnologias

- Kotlin
- Java 17
- Spring Boot
- Spring Web
- Spring WebFlux/WebClient
- Spring Data MongoDB
- MongoDB
- Maven
- Docker
- JUnit 5
- Mockito

## Pré-requisitos

- Java 17
- Maven 3.8 ou superior
- Docker
- Docker Compose
- `vehicle-sale-service` rodando na porta `8080`

## Configuração

A aplicação utiliza as seguintes configurações:

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

## Como executar localmente

Durante o desenvolvimento, recomenda-se executar apenas os bancos no Docker
e as aplicações pelo Maven.

### 1. Subir o PostgreSQL do Vehicle Sale Service

No projeto `vehicle-sale-service`:

```bash
docker compose up -d postgres
```

Caso o serviço do Docker Compose tenha outro nome, execute:

```bash
docker compose up -d
docker stop vehicle_sale_service_app
```

O PostgreSQL ficará disponível na porta `5434`.

### 2. Executar o Vehicle Sale Service

No projeto `vehicle-sale-service`:

```bash
mvn spring-boot:run
```

A aplicação ficará disponível em:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

### 3. Subir o MongoDB

No projeto `vehicle-platform`:

```bash
docker compose up -d
```

O MongoDB ficará disponível na porta `27017`.

### 4. Executar o Vehicle Platform

```bash
mvn spring-boot:run
```

A aplicação ficará disponível em:

```text
http://localhost:8082
```

## Fluxo de compra

1. Um veículo é cadastrado no `vehicle-sale-service`;
2. O veículo recebe o status `AVAILABLE`;
3. O cliente solicita a compra pela `vehicle-platform`;
4. A platform chama o endpoint de compra do `vehicle-sale-service`;
5. O sale service valida se o veículo está disponível;
6. O status do veículo é alterado para `PENDING_PAYMENT`;
7. Uma venda com pagamento `PENDING` é criada;
8. A platform registra um evento no MongoDB;
9. A resposta consolidada é devolvida ao cliente.

## Endpoints

### Vehicle Platform

#### Solicitar compra

```http
POST /api/platform/vehicles/{vehicleId}/purchase
```

Exemplo:

```bash
curl -X POST http://localhost:8082/api/platform/vehicles/1/purchase \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "12345678900"
  }'
```

Exemplo de resposta:

```json
{
  "saleId": 1,
  "vehicleId": 1,
  "cpf": "12345678900",
  "paymentCode": "74ff6036-0597-498e-98b4-932cf0667506",
  "paymentStatus": "PENDING",
  "platformEventId": "6870247624e8121b5f57df91"
}
```

### Vehicle Sale Service

```http
POST /api/vehicles
GET /api/vehicles/available
GET /api/vehicles/sold
PUT /api/vehicles/{id}
POST /api/vehicles/{id}/purchase
```

## Exemplo de teste completo

### 1. Cadastrar um veículo

```bash
curl -X POST http://localhost:8080/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{
    "brand": "Honda",
    "model": "Civic",
    "year": 2024,
    "color": "Prata",
    "price": 125000.00
  }'
```

### 2. Consultar veículos disponíveis

```bash
curl http://localhost:8080/api/vehicles/available
```

### 3. Comprar pela platform

Utilize um ID retornado como `AVAILABLE`:

```bash
curl -X POST http://localhost:8082/api/platform/vehicles/1/purchase \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "12345678900"
  }'
```

> Cada veículo disponível deve ser comprado somente uma vez. Após a
> solicitação, o status muda para `PENDING_PAYMENT`.

## Executar testes

```bash
mvn clean test
```

Para executar build e testes:

```bash
mvn clean verify
```

## Estrutura do projeto

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

## Bancos de dados

### Vehicle Platform

- MongoDB
- Database: `vehicle_platform_db`
- Collection: `payment_events`
- Porta local: `27017`

### Vehicle Sale Service

- PostgreSQL
- Database: `vehicle_sale_service_db`
- Porta local: `5434`

## Regras importantes

- Apenas veículos com status `AVAILABLE` podem ser comprados;
- Ao iniciar a compra, o status passa para `PENDING_PAYMENT`;
- A platform não deve duplicar as regras do sale service;
- O sale service é o responsável pelas regras de domínio;
- A platform é responsável pela orquestração e registro do evento.

## Saúde da aplicação

```text
GET http://localhost:8082/actuator/health
```

## Repositórios

- `vehicle-platform`: microsserviço de orquestração;
- `vehicle-sale-service`: microsserviço de veículos e vendas.