# IntelliJ Docker Container Naming Issue - Support Ticket Reproduction

## Issue Summary
When running a Spring Boot service through IntelliJ's Docker Compose remote target, the container gets a completely random name each time instead of using the `container_name` specified in the docker-compose.yml. This breaks service discovery between containers that depend on the predictable container name.

## Expected Behavior
- Container should use the name specified in `container_name: producer-service` from docker-compose.yml
- Other services should be able to reach it at `http://producer-service:8080`

## Actual Behavior
- IntelliJ generates random container names like `producer-service_run_abc123` 
- Service discovery fails because dependent services can't find the expected container name

## Environment
- **IntelliJ IDEA**: [Version]
- **Docker**: [Version]
- **Docker Compose**: [Version]
- **Java**: 21 (Amazon Corretto)
- **Spring Boot**: 3.x
- **OS**: Linux

## Project Structure
```
inconsistent-container-names-intelliJ/
├── docker-compose.yml           # Consumer service + network setup
├── producer-service/
│   ├── docker-compose.producer.yml   # Producer service config
│   ├── .idea/remote-targets.xml      # IntelliJ remote target config
│   ├── src/main/java/...             # Spring Boot application
│   └── build.gradle                  # Gradle build file
└── consumer-service/
    ├── src/main/java/...             # Spring Boot application
    └── build.gradle                  # Gradle build file
```

## Reproduction Steps

### Step 1: Set up the Environment
1. Clone this repository
2. Ensure Docker and Docker Compose are installed and running
3. Open IntelliJ IDEA

### Step 2: Create Docker Network
```bash
docker network create app-network
```

### Step 3: Start Consumer Service Stack
```bash
# From project root directory
docker-compose up -d
```
This starts:
- Consumer service on port 8081
- Creates the app-network
- Consumer expects to reach producer at `http://producer-service:8080`

### Step 4: Configure IntelliJ Remote Target (Issue Location)
1. Open the `producer-service` directory in IntelliJ
2. The remote target is already configured in `.idea/remote-targets.xml`:
   ```xml
   <target name="producer-service" type="docker-compose">
     <option name="configurationFiles">
       <option value="$PROJECT_DIR$/../docker-compose.yml" />
     </option>
     <option name="serviceName" value="producer-service" />
   </target>
   ```

### Step 5: Run Producer Service Through IntelliJ (Issue Trigger)
1. In IntelliJ, open the producer-service project
2. Go to Run Configurations
3. Create/Select the Docker Compose remote target configuration
4. Run the producer service using the remote target

### Step 6: Observe the Issue
1. Check running containers:
   ```bash
   docker ps --format "table {{.Names}}\t{{.Image}}\t{{.Ports}}"
   ```
2. **Expected**: Container named `producer-service`
3. **Actual**: Container with random name like `producer-service_run_abc123def456`

### Step 7: Verify Service Discovery Failure
1. Test consumer service endpoint:
   ```bash
   curl http://localhost:8081/api/health
   ```
2. Consumer service will fail to connect to producer because it's looking for `producer-service` container name
3. Check consumer logs:
   ```bash
   docker logs consumer-app
   ```

## Key Configuration Files

### docker-compose.yml (Root)
Contains consumer service and expects producer-service container to exist:
```yaml
services:
  consumer-service:
    container_name: consumer-app
    environment:
      - PRODUCER_SERVICE_URL=http://producer-service:8080  # This fails!
```

### producer-service/docker-compose.producer.yml
Defines the producer service with expected container name:
```yaml
services:
  producer-service:
    image: amazoncorretto:21-alpine
    container_name: producer-service  # IntelliJ ignores this!
    networks:
      - app-network
```

### .idea/remote-targets.xml
IntelliJ remote target configuration that should respect container_name but doesn't:
```xml
<target name="producer-service" type="docker-compose">
  <option name="serviceName" value="producer-service" />
</target>
```

## Expected Resolution
IntelliJ should respect the `container_name` specified in docker-compose.yml when running services through remote targets, allowing proper service discovery between containers.

## Additional Information
- This issue prevents local development workflows where you need to:
  1. Run dependent services via docker-compose
  2. Run the service under development via IntelliJ for debugging
  3. Have them communicate via container names

- The issue is reproducible 100% of the time with this setup
- Standard `docker-compose up` works correctly and uses the specified container names
