# Redis Cloud Setup Guide

## Environment Variables Required

Set these environment variables before running your Spring Boot application:

### For Cloud Redis (AWS ElastiCache, Redis Cloud, Azure Cache, etc.):

```bash
# Redis Configuration
SPRING_REDIS_HOST=your-redis-host.cache.amazonaws.com
SPRING_REDIS_PORT=6379
SPRING_REDIS_PASSWORD=your-redis-password
SPRING_REDIS_SSL=true

# Database Configuration (if using cloud DB)
SPRING_DATASOURCE_URL=jdbc:postgresql://your-postgres-host:5432/your-database
DB_USER=your-db-username
DB_PASSWORD=your-db-password

# Encryption Key
ENCRYPTION_KEY=your-32-character-encryption-key-here
```

### For Local Redis (if you want to test locally):

```bash
# Redis Configuration
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379
SPRING_REDIS_PASSWORD=
SPRING_REDIS_SSL=false
```

## Cloud Provider Examples:

### AWS ElastiCache:
- Host: `your-cluster.cache.amazonaws.com`
- Port: `6379` (or `6380` for SSL)
- SSL: `true`

### Redis Cloud:
- Host: `redis-12345.c1.us-east-1-1.ec2.cloud.redislabs.com`
- Port: `6379`
- SSL: `true`

### Azure Cache for Redis:
- Host: `your-cache.redis.cache.windows.net`
- Port: `6380`
- SSL: `true`

## How to Set Environment Variables:

### Windows (PowerShell):
```powershell
$env:SPRING_REDIS_HOST="your-redis-host"
$env:SPRING_REDIS_PORT="6379"
$env:SPRING_REDIS_PASSWORD="your-password"
$env:SPRING_REDIS_SSL="true"
```

### Windows (Command Prompt):
```cmd
set SPRING_REDIS_HOST=your-redis-host
set SPRING_REDIS_PORT=6379
set SPRING_REDIS_PASSWORD=your-password
set SPRING_REDIS_SSL=true
```

### Linux/Mac:
```bash
export SPRING_REDIS_HOST=your-redis-host
export SPRING_REDIS_PORT=6379
export SPRING_REDIS_PASSWORD=your-password
export SPRING_REDIS_SSL=true
```

## Testing the Connection:

1. Set your environment variables
2. Run the application: `mvn spring-boot:run`
3. Check the console output for Redis connection status
4. The `RedisConnectionTest` will show if the connection is successful

## Troubleshooting:

- **Connection refused**: Check if the Redis host and port are correct
- **Authentication failed**: Verify the password is correct
- **SSL errors**: Make sure SSL is enabled for cloud Redis services
- **Timeout errors**: Check if your firewall allows connections to the Redis port
