# Container Dependencies Demo - Java Spring Boot Application

This project demonstrates a containerized Java application with service dependencies using Docker Compose. It consists of two Spring Boot services that communicate with each other within the same Docker network.

## Architecture

- **Producer Service** (Port 8080): Exposes REST API endpoints that provide data
- **Consumer Service** (Port 8081): Depends on and consumes data from the producer service

## Services Overview

### Producer Service
- Runs on port 8080
- Provides endpoints:
  - `GET /api/health` - Health check endpoint
  - `GET /api/data` - Returns sample data
  - `GET /api/data/{id}` - Returns data for specific ID

### Consumer Service
- Runs on port 8081
- Depends on the producer service
- Provides endpoints:
  - `GET /api/health` - Health check endpoint
  - `GET /api/consume` - Fetches data from producer service
  - `GET /api/consume/{id}` - Fetches specific data from producer service
  - `GET /api/check-producer` - Checks producer service health

## Key Features Demonstrating Container Dependencies

1. **Service Discovery**: Consumer service connects to producer using Docker service name (`producer-service`)
2. **Network Communication**: Both services run in the same Docker network (`app-network`)
3. **Dependency Management**: Consumer service depends on producer service in docker-compose.yml
4. **Environment Configuration**: Services use environment variables for configuration

## Quick Start

### Prerequisites
- Docker and Docker Compose installed
- Java 17+ (for local development)
- Maven (for local development)

### Running with Docker Compose

1. **Build and start all services:**
   ```bash
   docker-compose up --build
   ```

2. **Test the producer service:**
   ```bash
   curl http://localhost:8080/api/health
   curl http://localhost:8080/api/data
   ```

3. **Test the consumer service (demonstrates dependency):**
   ```bash
   curl http://localhost:8081/api/health
   curl http://localhost:8081/api/consume
   curl http://localhost:8081/api/check-producer
   ```

### Testing Container Dependencies

The consumer service demonstrates container dependencies in several ways:

1. **Service Communication**: 
   ```bash
   curl http://localhost:8081/api/consume
   ```
   This endpoint shows the consumer service successfully calling the producer service.

2. **Health Check Chain**:
   ```bash
   curl http://localhost:8081/api/check-producer
   ```
   This shows the consumer checking the health of the producer service.

3. **Data Retrieval with Parameters**:
   ```bash
   curl http://localhost:8081/api/consume/123
   ```
   This demonstrates parameterized calls between services.

## Container Network Configuration

The services communicate using:
- **Service Name**: `producer-service` (defined in docker-compose.yml)
- **Internal Port**: 8080 (producer) and 8081 (consumer)
- **Network**: `app-network` (bridge network)
- **DNS Resolution**: Docker automatically resolves service names to container IPs

## Environment Variables

- `PRODUCER_SERVICE_URL`: URL for the producer service (set in docker-compose.yml)
- `SPRING_PROFILES_ACTIVE`: Spring profile (set to 'docker' for containerized deployment)

## Troubleshooting

If the consumer service cannot reach the producer service:

1. Check if both services are running:
   ```bash
   docker-compose ps
   ```

2. Check logs:
   ```bash
   docker-compose logs producer-service
   docker-compose logs consumer-service
   ```

3. Test network connectivity:
   ```bash
   docker-compose exec consumer-service ping producer-service
   ```

## Development

### Local Development (without Docker)

1. Start producer service:
   ```bash
   cd producer-service
   mvn spring-boot:run
   ```

2. Start consumer service (in another terminal):
   ```bash
   cd consumer-service
   PRODUCER_SERVICE_URL=http://localhost:8080 mvn spring-boot:run
   ```

### Building Individual Services

```bash
# Build producer service
cd producer-service
docker build -t producer-service .

# Build consumer service
cd consumer-service
docker build -t consumer-service .
```

## Stopping the Application

```bash
docker-compose down
```

To remove volumes and networks:
```bash
docker-compose down -v --remove-orphans
```
