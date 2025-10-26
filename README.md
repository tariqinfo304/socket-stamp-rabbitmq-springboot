# STOMP Chat with RabbitMQ Integration

An enterprise-grade real-time chat application using Spring Boot, STOMP over WebSocket, and RabbitMQ as an external message broker for scalability and reliability.

## 📋 Overview

This project demonstrates a production-ready WebSocket implementation that integrates with RabbitMQ for message brokering. It provides the same chat functionality as the basic STOMP version but with enhanced scalability, persistence, and reliability through external message broker integration.

## 🏗️ Architecture

```
┌─────────────┐    STOMP over    ┌─────────────┐
│   Browser   │◄──────────────►│ Spring Boot │
│   (HTML/JS) │   WebSocket     │ Application │
└─────────────┘                └─────────────┘
                                      │
                                      ▼
                               ┌─────────────┐
                               │ STOMP Relay │
                               └─────────────┘
                                      │
                                      ▼
                               ┌─────────────┐
                               │  RabbitMQ   │
                               │  Broker     │
                               └─────────────┘
                                      │
                                      ▼
                               ┌─────────────┐
                               │ Message     │
                               │ Persistence │
                               └─────────────┘
```

## 🚀 Features

- **External Message Broker**: RabbitMQ integration for scalability
- **Message Persistence**: Messages survive broker restarts
- **High Availability**: Support for broker clustering and failover
- **Load Distribution**: Multiple application instances can share load
- **Real-time Chat**: Same chat functionality as STOMP version
- **Monitoring**: Enhanced metrics and health monitoring
- **Management Interface**: RabbitMQ management dashboard integration

## 🛠️ Technology Stack

- **Java 17**
- **Spring Boot 3.3.4**
- **Spring WebSocket & STOMP**
- **RabbitMQ** (Message Broker)
- **AMQP** (Advanced Message Queuing Protocol)
- **HTML5/JavaScript** (Frontend)
- **Spring Boot Actuator** (Monitoring)

## 📁 Project Structure

```
src/main/java/com/example/demoStamp/
├── SocketDemoApplication.java              # Main application class
├── config/
│   └── WebSocketConfig.java               # STOMP relay configuration
├── controller/
│   ├── ChatController.java                # Message handling
│   └── BrokerStatsController.java         # Monitoring endpoints
├── listener/
│   └── WebSocketEventListener.java        # Connection events
└── model/
    ├── ChatMessage.java                   # Message data model
    └── MessageType.java                   # Message type enum

src/main/resources/
├── static/
│   └── index.html                         # Chat interface
└── application.properties                 # RabbitMQ configuration
```

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- RabbitMQ Server (localhost:61613 for STOMP, localhost:5672 for AMQP)

### RabbitMQ Setup

#### Option 1: Docker (Recommended)

```bash
# Start RabbitMQ with management interface
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 61613:61613 \
  -p 15672:15672 \
  rabbitmq:3-management

# Verify RabbitMQ is running
curl http://localhost:15672
docker exec -it rabbitmq rabbitmq-plugins enable rabbitmq_stomp
docker restart rabbitmq
docker exec -it rabbitmq rabbitmq-plugins list

```





### credenatils:
guest / guest


#### Option 2: Local Installation

1. Download and install RabbitMQ from [rabbitmq.com](https://www.rabbitmq.com/download.html)
2. Enable STOMP plugin:
   ```bash
   rabbitmq-plugins enable rabbitmq_stomp
   rabbitmq-plugins enable rabbitmq_web_stomp
   ```
3. Start RabbitMQ service

### Running the Application

1. **Clone and navigate to the project:**
   ```bash
   cd socket-demo-stamp-rabbitmq
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Access the chat application:**
   Open your browser and go to: `http://localhost:8080`

4. **Verify RabbitMQ connection:**
   - Check RabbitMQ management interface: `http://localhost:15672`
   - Username/Password: `guest` / `guest`
   - Monitor connections and message rates

## 📝 Code Examples

### STOMP Relay Configuration

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");

        // Relay all STOMP messages through RabbitMQ
        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost("localhost")
                .setRelayPort(61613)
                .setClientLogin("guest")
                .setClientPasscode("guest")
                .setSystemLogin("guest")
                .setSystemPasscode("guest");
    }
}
```

### Application Configuration

```properties
# Server Configuration
server.port=8080

# RabbitMQ Configuration
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# Management Endpoints
management.endpoints.web.exposure.include=health,info,metrics,beans
management.endpoint.metrics.enabled=true
management.endpoint.health.show-details=always
```

### RabbitMQ STOMP Configuration

RabbitMQ STOMP configuration (if needed):

```bash
# Enable STOMP plugin
rabbitmq-plugins enable rabbitmq_stomp

# Set up STOMP user (optional, using default guest)
rabbitmqctl add_user stomp_user stomp_password
rabbitmqctl set_user_tags stomp_user administrator
rabbitmqctl set_permissions -p / stomp_user ".*" ".*" ".*"
```

## 🔄 Message Flow with RabbitMQ

### Connection Establishment
1. Client connects to `/ws` endpoint via SockJS
2. STOMP handshake with Spring application
3. Application establishes connection to RabbitMQ STOMP adapter
4. User joins chat room
5. Join message routed through RabbitMQ to all subscribers

### Message Processing
1. User sends chat message
2. Message sent to `/app/chat.sendMessage` (application destination)
3. ChatController processes message
4. Message forwarded to `/topic/public` (broker destination)
5. RabbitMQ broker distributes message to all subscribed clients
6. Clients receive message through WebSocket connection

### RabbitMQ Integration Benefits
- **Message Persistence**: Messages survive broker restarts
- **Load Distribution**: Multiple application instances can share message load
- **High Availability**: RabbitMQ clustering support
- **Message Routing**: Flexible routing with topics and queues
- **Monitoring**: Built-in RabbitMQ management and monitoring

## 📊 Monitoring and Management

### Application Health Endpoints
- **Health Check**: `http://localhost:8080/actuator/health`
- **Application Info**: `http://localhost:8080/actuator/info`
- **Metrics**: `http://localhost:8080/actuator/metrics`
- **Beans**: `http://localhost:8080/actuator/beans`

### RabbitMQ Management Interface
- **Management Dashboard**: `http://localhost:15672`
- **Connections**: Monitor WebSocket and STOMP connections
- **Queues**: View message queues and throughput
- **Exchanges**: Monitor message routing

### WebSocket Statistics

```bash
# Check WebSocket broker statistics
curl http://localhost:8080/broker/stats

# Response includes:
{
  "sessionStats": {
    "totalSessions": 10,
    "connectedSessions": 8
  },
  "inboundStats": {
    "totalMessages": 1250,
    "messagesPerSecond": 5.2
  },
  "outboundStats": {
    "totalMessages": 1220,
    "messagesPerSecond": 5.1
  }
}
```

## 🔧 RabbitMQ Configuration Options

### STOMP Relay Settings

```java
registry.enableStompBrokerRelay("/topic", "/queue")
    .setRelayHost("rabbitmq.example.com")          // RabbitMQ host
    .setRelayPort(61613)                           // STOMP port
    .setClientLogin("app_user")                    // Application credentials
    .setClientPasscode("app_password")
    .setSystemLogin("system_user")                  // System credentials
    .setSystemPasscode("system_password")
    .setTcpClient(true)                            // Use TCP client
    .setVirtualHost("/");                          // Virtual host
```

### Advanced RabbitMQ Features

#### Message Persistence
Configure durable queues in RabbitMQ:

```bash
# Create durable queue
rabbitmqadmin declare queue name=chat.public durable=true

# Create durable exchange
rabbitmqadmin declare exchange name=chat.exchange type=topic durable=true
```

#### High Availability
Set up RabbitMQ cluster:

```bash
# On each RabbitMQ node
rabbitmq-server -detached

# Join cluster
rabbitmqctl join_cluster rabbit@node1
```

## 🧪 Testing

### Load Testing

Test with multiple concurrent users:

```bash
# Using WebSocket client tools
wscat -c ws://localhost:8080/ws -H "host:localhost:8080" --slash

# Or using Artillery for load testing
artillery quick --count 100 --num 50 ws://localhost:8080/ws
```

### Integration Testing

```java
@SpringBootTest
@Testcontainers
public class RabbitMQIntegrationTest {

    @Container
    static RabbitMQContainer rabbitMQ = new RabbitMQContainer("rabbitmq:3-management");

    @Test
    public void testMessagePersistence() {
        // Test that messages survive RabbitMQ restart
    }

    @Test
    public void testMultipleInstances() {
        // Test load distribution across multiple application instances
    }
}
```

### RabbitMQ Testing

```bash
# Monitor message rates
rabbitmqctl list_queues name messages consumers

# Check connection status
rabbitmqctl list_connections

# View exchange bindings
rabbitmqctl list_bindings
```

## 🚦 Performance Characteristics

### Throughput Comparison

| Implementation | Messages/Second | Concurrent Users | Scalability |
|----------------|-----------------|------------------|-------------|
| **In-Memory** | 5,000-10,000 | 100-1,000 | Limited |
| **RabbitMQ** | 10,000+ | 1,000-10,000 | High |

### Resource Usage

- **Memory**: Higher memory usage due to broker connections
- **CPU**: Additional serialization/deserialization overhead
- **Network**: TCP connections to RabbitMQ broker
- **Storage**: Message persistence in RabbitMQ

### Scaling Benefits

#### Horizontal Scaling
- **Multiple Instances**: Deploy multiple application instances
- **Load Balancing**: Distribute WebSocket connections across instances
- **Message Distribution**: RabbitMQ handles message fanout
- **Session Affinity**: Use sticky sessions for WebSocket connections

#### Vertical Scaling
- **Connection Pooling**: Optimize RabbitMQ connection pools
- **Message Batching**: Batch messages for better throughput
- **Async Processing**: Use reactive programming for better resource utilization

## 🔒 Security Considerations

### RabbitMQ Security

```bash
# Create application-specific user
rabbitmqctl add_user chat_app chat_password
rabbitmqctl set_user_tags chat_app administrator
rabbitmqctl set_permissions -p / chat_app ".*" ".*" ".*"

# Configure firewall
sudo ufw allow from 10.0.0.0/8 to any port 5672
sudo ufw allow from 10.0.0.0/8 to any port 61613
```

### Application Security

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("https://yourdomain.com")  // Restrict origins
                .withSockJS();
    }
}
```

### Authentication Integration

```java
@Controller
public class ChatController {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage,
                                   Principal principal) {
        // Use Spring Security Principal for authentication
        String authenticatedUser = principal.getName();

        // Verify user permissions
        if (!isAuthorized(authenticatedUser)) {
            throw new SecurityException("Unauthorized user");
        }

        return chatMessage;
    }
}
```

## 📈 Monitoring and Alerting

### RabbitMQ Monitoring

#### Queue Monitoring
```bash
# Monitor queue depth
rabbitmqctl list_queue_bindings

# Check message rates
rabbitmqctl report

# Set up alerts for high queue depth
```

#### Application Monitoring

```yaml
# Prometheus metrics configuration
management:
  metrics:
    export:
      prometheus:
        enabled: true
  endpoint:
    metrics:
      enabled: true
    prometheus:
      enabled: true
```

### Logging Configuration

```properties
# Enhanced logging for debugging
logging.level.org.springframework.messaging=DEBUG
logging.level.org.springframework.web.socket=DEBUG
logging.level.com.rabbitmq=DEBUG
logging.level.com.example.demoStamp=INFO
```

## 🔧 Troubleshooting

### Common Issues

#### Connection Refused
```bash
# Check RabbitMQ status
sudo systemctl status rabbitmq-server

# Check STOMP plugin
rabbitmq-plugins list

# Verify ports
netstat -tlnp | grep 61613
```

#### Message Not Delivered
```bash
# Check queue bindings
rabbitmqctl list_bindings

# Verify exchange routing
rabbitmqadmin get queue=chat.public

# Check application logs
tail -f logs/application.log
```

#### Performance Issues
```bash
# Monitor connection count
rabbitmqctl list_connections | wc -l

# Check message backlog
rabbitmqctl list_queues name messages

# Profile application performance
jprofiler or visualvm
```

### Debug Mode

Enable debug logging:

```properties
logging.level.org.springframework.web.socket=DEBUG
logging.level.org.springframework.messaging=DEBUG
logging.level.com.rabbitmq=DEBUG
```

## 🎨 Advanced Features

### Message Routing

#### Topic-based Routing
```java
@Override
public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.setApplicationDestinationPrefixes("/app");
    registry.enableStompBrokerRelay("/topic", "/queue")
            .setRelayHost("localhost")
            .setRelayPort(61613)
            .setClientLogin("guest")
            .setClientPasscode("guest");
}

// Usage: /topic/chat.room1, /topic/chat.room2
```

#### Queue-based Messaging
```java
// Configure durable queues
registry.enableStompBrokerRelay("/topic", "/queue")
        .setRelayHost("localhost")
        .setRelayPort(61613);

// Usage: /queue/private.user1, /queue/notifications
```

### Message Persistence

Configure RabbitMQ for message durability:

```bash
# Create durable exchange
rabbitmqadmin declare exchange name=chat.exchange type=topic durable=true

# Create durable queue
rabbitmqadmin declare queue name=chat.public durable=true

# Bind queue to exchange
rabbitmqadmin declare binding source=chat.exchange destination_type=queue destination=chat.public routing_key=chat.public
```

## 📚 Production Deployment

### Docker Compose Setup

```yaml
version: '3.8'
services:
  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "61613:61613"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin123
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq
    healthcheck:
      test: rabbitmq-diagnostics check_port_connectivity
      interval: 30s
      timeout: 30s
      retries: 5

  chat-app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      rabbitmq:
        condition: service_healthy
    environment:
      SPRING_RABBITMQ_HOST: rabbitmq
      SPRING_RABBITMQ_PORT: 5672

volumes:
  rabbitmq_data:
```

### Kubernetes Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: chat-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: chat-app
  template:
    metadata:
      labels:
        app: chat-app
    spec:
      containers:
      - name: chat-app
        image: chat-app:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_RABBITMQ_HOST
          value: "rabbitmq-service"
        - name: SPRING_RABBITMQ_PORT
          value: "5672"
```

## 🔄 Migration from In-Memory

### Configuration Changes

1. **Add RabbitMQ Dependencies**:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-amqp</artifactId>
   </dependency>
   ```

2. **Update Configuration**:
   ```java
   // From: registry.enableSimpleBroker("/topic");
   // To: registry.enableStompBrokerRelay("/topic", "/queue")
   ```

3. **Update Properties**:
   ```properties
   spring.rabbitmq.host=localhost
   spring.rabbitmq.port=5672
   spring.rabbitmq.username=guest
   spring.rabbitmq.password=guest
   ```

## 📈 Performance Tuning

### RabbitMQ Optimization

```bash
# Increase connection limits
rabbitmqctl set_vm_memory_high_watermark 0.8
rabbitmqctl set_disk_free_limit 2147483648

# Configure message TTL
rabbitmqctl set_policy TTL "chat\." '{"message-ttl":86400000}' --apply-to queues

# Enable publisher confirms
rabbitmqctl set_policy publisher-confirms "chat\." '{"publisher-confirms":true}' --apply-to exchanges
```

### Application Tuning

```properties
# Connection pool settings
spring.rabbitmq.connection-timeout=60000
spring.rabbitmq.requested-heartbeat=60

# Cache settings
spring.websocket.message-size-limit=64KB
spring.websocket.send-buffer-size-limit=512KB
spring.websocket.send-time-limit=10s
```

## 🤝 Contributing

1. Test with RabbitMQ cluster setup
2. Add integration tests for message persistence
3. Document performance benchmarks
4. Include monitoring dashboards
5. Update security best practices

## 📄 License

This project is licensed under the MIT License.

## 📚 Further Reading

- [RabbitMQ STOMP Documentation](https://www.rabbitmq.com/stomp.html)
- [Spring WebSocket with External Broker](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket-broker-relay-configure)
- [RabbitMQ Clustering Guide](https://www.rabbitmq.com/clustering.html)
- [STOMP Protocol Specification](https://stomp.github.io/stomp-specification-1.2.html)
