---
sidebar_position: 7
---

# Logging

### Using with SLF4J and Logback

Add a `logback.xml` file to your `src/main/resources` folder with the following content:

```xml

<configuration>
    <root level="DEBUG">
        <appender-ref ref="STDOUT"/>
    </root>
    
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

</configuration>

```