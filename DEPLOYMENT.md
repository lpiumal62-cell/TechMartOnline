# TechMart Online - Deployment Guide

## Prerequisites

- JDK 11+
- Apache Maven 3.8+
- WildFly 26+ (Java EE 8 / Jakarta EE compatible)
- MySQL 8.0+

## 1. Database Setup

```bash
mysql -u root -p < database/techmart.sql
```

## 2. WildFly Datasource Configuration

Edit `WILDFLY_HOME/standalone/configuration/standalone-full.xml` (required for JMS).

Add MySQL driver module (if not present):

```bash
mkdir -p WILDFLY_HOME/modules/com/mysql/main
cp mysql-connector-j-8.3.0.jar WILDFLY_HOME/modules/com/mysql/main/
```

Create `module.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.9" name="com.mysql">
    <resources>
        <resource-root path="mysql-connector-j-8.3.0.jar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
        <module name="javax.transaction.api"/>
    </dependencies>
</module>
```

Add datasource via CLI:

```bash
WILDFLY_HOME/bin/jboss-cli.sh --connect

/subsystem=datasources/jdbc-driver=mysql:add(\
  driver-name=mysql,\
  driver-module-name=com.mysql,\
  driver-class-name=com.mysql.cj.jdbc.Driver)

data-source add \
  --name=TechMartDS \
  --jndi-name=java:/jdbc/TechMartDS \
  --driver-name=mysql \
  --connection-url=jdbc:mysql://localhost:3306/techmart_db?useSSL=false&serverTimezone=UTC \
  --user-name=techmart \
  --password=techmart_pass \
  --use-java-context=true \
  --enabled=true
```

## 3. JMS Setup

Start WildFly with full profile for embedded messaging:

```bash
WILDFLY_HOME/bin/standalone.sh -c standalone-full.xml
```

JMS destinations are auto-created via `@JMSDestinationDefinitions` in `JMSConfig.java`:

| Destination | JNDI Name | Type |
|-------------|-----------|------|
| OrderQueue | `java:/jms/queue/OrderQueue` | Point-to-Point Queue |
| InventoryTopic | `java:/jms/topic/InventoryTopic` | Publish-Subscribe Topic |

Manual CLI alternative:

```bash
/subsystem=messaging-activemq/server=default/jms-queue=OrderQueue:add(entries=["java:/jms/queue/OrderQueue"])
/subsystem=messaging-activemq/server=default/jms-topic=InventoryTopic:add(entries=["java:/jms/topic/InventoryTopic"])
```

## 4. Build and Deploy

```bash
cd TechMartOnline
mvn clean package
mvn wildfly:deploy
```

Or copy WAR manually:

```bash
cp target/TechMartOnline.war WILDFLY_HOME/standalone/deployments/
```

## 5. Access Application

| Page | URL |
|------|-----|
| Home | http://localhost:8080/TechMartOnline/ |
| Products | http://localhost:8080/TechMartOnline/products |
| Cart | http://localhost:8080/TechMartOnline/cart |
| Checkout | http://localhost:8080/TechMartOnline/checkout |
| Dashboard | http://localhost:8080/TechMartOnline/dashboard |
| Performance | http://localhost:8080/TechMartOnline/performance |

## 6. Run Tests

Unit tests only:

```bash
mvn test -Dtest="!*IT"
```

Arquillian integration tests (requires running WildFly):

```bash
export JBOSS_HOME=/path/to/wildfly
mvn verify -Parquillian-wildfly-managed
```

## 7. JNDI Lookup Reference

| Component | JNDI Name |
|-----------|-----------|
| ProductServiceBean | `java:global/TechMartOnline/ProductServiceBean!com.techmart.stateless.ProductServiceBean` |
| ShoppingCartBean | `java:global/TechMartOnline/ShoppingCartBean!com.techmart.stateful.ShoppingCartBean` |
| InventoryManagerBean | `java:global/TechMartOnline/InventoryManagerBean!com.techmart.singleton.InventoryManagerBean` |
| OrderMDB | `java:global/TechMartOnline/OrderMDB` |
| Datasource | `java:/jdbc/TechMartDS` |
| ConnectionFactory | `java:/ConnectionFactory` |

## 8. Production Recommendations

- Enable HTTPS and configure `secure=true` on session cookies
- Replace plain-text passwords with BCrypt hashing
- Configure MySQL master-slave replication for failover
- Deploy behind Apache/Nginx load balancer with sticky sessions for stateful beans
- Enable WildFly clustering for horizontal scaling
- Configure connection pool: min=10, max=50, prefill=true
