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

## Examples

The activity feed supports 4 logging levels

```java
Activity.debug("identifier","Message");
Activity.info("identifier","Message");
Activity.warn("identifier","Message");
Activity.error("identifier","Message");
```

Log markers are added as `Map` entries. 
This Maven package defines one root key in the `Map` called `context`. 
The value of the `context` key is another `Map` that includes the given `identifier` by default.

The examples above would be represented as JSON as follows

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

The example above would be represented as JSON as follows

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

### Logging an exception

```java
Activity.error(
    "unhandled-exception",
    exception.getMessage(),
    (context) -> 
      context.put(
        "exception",
        ExceptionContext.extractForm(exception)));
```

The example above would be represented as JSON as follows

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

`ExceptionContext.extractForm` will extract recursively information from previous exceptions.

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
import static com.montealegreluis.activityfeed.ExceptionContext.extractForm;

public class ActivityFactory {
  public static Activity exceptionWasThrown(Throwable exception) {
      return error(
          "unhandled-exception",
          exception.getMessage(),
          (context) -> context.put("exception", extractForm(exception)));
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

To integrate with Spring Boot, you'll need to add the following Logstash encoder dependency.

```groovy
// build.gradle
implementation 'net.logstash.logback:logstash-logback-encoder:7.0.1'
```

And you'll need to configure the encoder in `src/main/resources/logback.xml` as shown below

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

Below is an example on how to query AWS Cloudwatch using the markers provided by Activity Feed

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