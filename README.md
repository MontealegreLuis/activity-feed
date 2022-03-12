# Activity Feed

[![CI workflow](https://github.com/montealegreluis/activity-feed/actions/workflows/ci.yml/badge.svg)](https://github.com/montealegreluis/activity-feed/actions/workflows/ci.yml)
[![Release workflow](https://github.com/montealegreluis/activity-feed/actions/workflows/release.yml/badge.svg)](https://github.com/montealegreluis/activity-feed/actions/workflows/release.yml)
[![semantic-release: conventional-commits](https://img.shields.io/badge/semantic--release-conventionalcommits-e10079?logo=semantic-release)](https://github.com/semantic-release/semantic-release)

Activity Feed is a thin abstraction for structured logging that takes advantage of Log Markers.
Log Markers allow you to stamp individual log entries with unique tokens, improving your ability to search and filter log data.

## Installation

1. [Authenticating to GitHub Packages](https://github.com/MontealegreLuis/activity-feed/blob/main/docs/installation/authentication.md)
2. [Maven](https://github.com/MontealegreLuis/activity-feed/blob/main/docs/installation/maven.md)
3. [Gradle](https://github.com/MontealegreLuis/activity-feed/blob/main/docs/installation/gradle.md)

## Usage

The activity feed supports 4 logging levels.

```java
Activity.debug("identifier","Message");
Activity.info("identifier","Message");
Activity.warn("identifier","Message");
Activity.error("identifier","Message");
```

Log markers are added as `Map` entries. 
This Maven package defines one root key in the `Map` called `context`. 
The value of the `context` key is another `Map` that includes the given `identifier` by default.

The examples above would be represented as JSON as follows.

```json
{
  "context": {
    "identifier": "identifier"
  }
}
```

### Adding context values to log messages

```java
Activity.info(
  "search-products",
  "Search Products completed",
  (context)->{
    context.put("maximumPrice",2000);
    context.put("category","Toys");
    context.put("durationInMilliseconds",200);
  });
```

The example above would be represented as JSON as follows.

```json
{
  "context": {
    "identifier": "search-products",
    "maximumPrice": 200,
    "category": "Toys",
    "durationInMilliseconds": 200
  }
}
```

### Adding objects as context entries

This library provides a `ContextSerializer` that depends on an [ObjectMapper](https://fasterxml.github.io/jackson-databind/javadoc/2.7/com/fasterxml/jackson/databind/ObjectMapper.html) to convert any type of object into a context entry.

```java
import com.fasterxml.jackson.databind.ObjectMapper;

// ...

var serializer = new ContextSerializer(new ObjectMapper());
var user = User.signUp("Jane Doe", 23);

Activity.info(
  "sign-up-user",
  "User sign-up completed",
  (context)-> context.put("user", serializer.toMap(user)));
```

The example above would be represented as JSON as follows

```json
{
  "context": {
    "identifier": "sign-up-user",
    "user": {
      "name": "Jane Doe",
      "age": 23
    }
  }
}
```

### Masking sensitive information

This package provides a custom serializer to mask sensitive information.
In order to mask a value, it must implement the `MaskedValue` interface.

```java
public final class FullName implements MaskedValue {}

public final class Passport {
  private final FullName fullName;
  public Passport(FullName fullName) {
    this.fullName = fullName;
  }
}
```

The second step is to configure your `ObjectMapper` as follows.

```java
var mapper = new ObjectMapper();
var module = new SimpleModule();
module.addSerializer(new MaskedValueSerializer());
mapper.registerModule(module);
```

Once the object mapper is configured, your activity feed is ready to serialize and mask sensitive information.

```java
feed.record(Activity.info(
  "save-travel-information",
  "Travel information has been saved",
  (context) -> context.put("passport", passport)
));
```

The example above would be represented as JSON as follows

```json
{
  "context": {
    "identifier": "save-travel-information",
    "passport": {
      "fullName": "*****"
    }
  }
}
```

### Logging an exception

```java
Activity.error(
  "unhandled-exception",
  exception.getMessage(),
  (context) -> 
    context.put(
      "exception",
      ExceptionContextFactory.contextFrom(exception)));
```

The example above would be represented as JSON as follows.

```json
{
  "context": {
    "identifier": "unhandled-exception",
    "exception": {
      "message": "For input string \"two\"",
      "class": "java.lang.NumberFormatException",
      "line": 65,
      "file": "NumberFormatException.java",
      "trace": [
        "java.base/java.lang.NumberFormatException.forInputString(NumberFormatException.java:65)",
        "java.base/java.lang.Integer.parseInt(Integer.java:652)",
        "java.base/java.lang.Integer.parseInt(Integer.java:770)",
        "com.montealegreluis.activityfeed.Application.main(Application.java:10)"
      ]
    }
  }
}
```

`ExceptionContextFactory.contextFrom` will extract recursively information from previous exceptions.

```json
{
  "context": {
    "exception": {
      // ...
      "previous": {
        // Same keys as in exception
        "previous": {
          // ...
        }
      }
    }
  }
}
```

### Activity builder

You can also create an activity using a builder

```java
anActivity()
  .error()
  .withIdentifier("application-error")
  .withMessage("Application error")
  .with("code", 500)
  .withException(exception)
  .build();
```

The snippet above will produce the following JSON.

```json
{
  "context": {
    "identifier": "application-error",
    "message": "Application error",
    "code": 500,
    "exception": {
      "message": "For input string \"two\"",
      "class": "java.lang.NumberFormatException",
      "line": 65,
      "file": "NumberFormatException.java",
      "trace": [
        "java.base/java.lang.NumberFormatException.forInputString(NumberFormatException.java:65)",
        "java.base/java.lang.Integer.parseInt(Integer.java:652)",
        "java.base/java.lang.Integer.parseInt(Integer.java:770)",
        "com.montealegreluis.activityfeed.Application.main(Application.java:10)"
      ]
    }
  }
}
```

### Instantiating an Activity Feed

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(Application.class);
    ActivityFeed feed = new ActivityFeed(logger);
  }
}
```

### Factories for Activities

You'll probably want to abstract the creation of the activity in a factory.

```java
import static com.montealegreluis.activityfeed.Activity.error;
import static com.montealegreluis.activityfeed.ExceptionContextFactory.contextFrom;

public class ActivityFactory {
  public static Activity exceptionWasThrown(Throwable exception) {
    return error(
      "unhandled-exception",
      exception.getMessage(),
      (context) -> context.put("exception", contextFrom(exception)));
  }
}
```

This way you can record the activity as follows

```java
import static com.montealegreluis.activityfeed.ActivityFactory.exceptionWasThrown;

// ...

feed.record(exceptionWasThrown(exception));
```

## Spring Boot integration

To integrate with Spring Boot, you'll need to configure a Logstash encoder in `src/main/resources/logback.xml` as shown below.

```xml
<configuration>
  <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
  </appender>
  <root level="INFO">
    <appender-ref ref="CONSOLE"/>
  </root>
</configuration>
```

## Querying

Below is an example on how to query AWS Cloudwatch using the markers provided by Activity Feed.

```sql
fields @timestamp, `x-correlation-id`, message
| filter `context.identifier` = "payment-processed"
| sort @timestamp desc
| limit 50
```

You could filter using any value within `context`.

## Contribute

Please refer to [CONTRIBUTING](https://github.com/MontealegreLuis/activity-feed/blob/main/CONTRIBUTING.md) for information on how to contribute to Activity Feed.

## License

Released under the [BSD-3-Clause](https://github.com/MontealegreLuis/activity-feed/blob/main/LICENSE).
