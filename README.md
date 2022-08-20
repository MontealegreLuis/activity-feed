# Activity Feed

[![CI workflow](https://github.com/montealegreluis/activity-feed/actions/workflows/ci.yml/badge.svg)](https://github.com/montealegreluis/activity-feed/actions/workflows/ci.yml)
[![Release workflow](https://github.com/montealegreluis/activity-feed/actions/workflows/release.yml/badge.svg)](https://github.com/montealegreluis/activity-feed/actions/workflows/release.yml)
[![semantic-release: conventional-commits](https://img.shields.io/badge/semantic--release-conventionalcommits-e10079?logo=semantic-release)](https://github.com/semantic-release/semantic-release)

Activity Feed is a thin abstraction to standardize your implementation of structured logging.

## Installation

1. [Authenticating to GitHub Packages](https://github.com/MontealegreLuis/activity-feed/blob/main/docs/installation/authentication.md)
2. [Maven](https://github.com/MontealegreLuis/activity-feed/blob/main/docs/installation/maven.md)
3. [Gradle](https://github.com/MontealegreLuis/activity-feed/blob/main/docs/installation/gradle.md)

## Usage

```java
var logger = LoggerFactory.getLogger(Application.class);
var feed = ActivityFeed.withLogging(logger);

// .. 

feed.add(Activity.info("identifier","Something happened");)
```

### Activities

An **activity** is a log entry consisting of a message, an **identifier**, and a **context**.

Representing your logging events as **activities** makes them easily **searchable** and **queryable**, because they're structured by default.

Activities have 5 **levels** as shown below.

```java
Activity.trace("identifier","Message");
Activity.debug("identifier","Message");
Activity.info("identifier","Message");
Activity.warn("identifier","Message");
Activity.error("identifier","Message");
```

All the examples above would be represented as JSON as follows.

```json
{
  "message": "Message",
  "context": {
    "identifier": "identifier"
  }
}
```

#### Adding context to activities

Suppose we have an application to purchase products, and we want to know what people is usually searching for and how long a search usually takes.

We would create an activity as shown below.

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
  "message": "Search Products completed",
  "context": {
    "identifier": "search-products",
    "maximumPrice": 200,
    "category": "Toys",
    "durationInMilliseconds": 200
  }
}
```

#### Adding objects to an activity context

We can add any object to an activity context using the `ContextSerializer`.
The context serializer depends on an [ObjectMapper](https://fasterxml.github.io/jackson-databind/javadoc/2.7/com/fasterxml/jackson/databind/ObjectMapper.html).

Suppose you want to know the information of new accounts in your application.

```java
import com.fasterxml.jackson.databind.ObjectMapper;

// ...

var serializer = new ContextSerializer(new ObjectMapper());
var user = User.signUp("Jane Doe", 23);

Activity.info(
  "sign-up-customer",
  "Customer sign-up completed",
  (context) 
    -> context.put("customer", serializer.toMap(customer)));
```

The example above would be represented as JSON as follows

```json
{
  "message": "Customer sign-up completed",
  "context": {
    "identifier": "sign-up-customer",
    "customer": {
      "name": "Jane Doe",
      "age": 23
    }
  }
}
```

#### Adding an exception to an activity context

You can use the `ExceptionContextFactory` to add exception information to an activity as shown below.

```java
import static com.montealegreluis.activityfeed.ExceptionContextFactory.contextFrom;

// ...

Activity.error(
  "unhandled-exception",
  exception.getMessage(),
  (context) -> 
    context.put("exception", contextFrom(exception)));
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

#### Activity builder

You can also create an activity using the `ActivityBuilder`.

```java
anErrorActivity()
  .withIdentifier("application-error")
  .withMessage("Application error")
  .with("code", 500)
  .withException(exception)
  .build();
```

The snippet above will produce the following JSON.

```json
{
  "message": "Application error",
  "context": {
    "identifier": "application-error",
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

#### Factories for Activities

In order to make your code more maintainable and readable, you could abstract the creation of the activity in a factory.

```java
import static com.montealegreluis.activityfeed.Activity.error;
import static com.montealegreluis.activityfeed.ExceptionContextFactory.contextFrom;

public class ActivityFactory {
  public static Activity anExceptionWasThrown(Throwable exception) {
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

feed.add(anExceptionWasThrown(exception));
```

#### Masking sensitive information

In order to mask a sensitive value, you could either create a marker interface or an interface with a default implementation that masks the value you want to log.

```java
// Marker interface
public interface MaskedValue {}

// Interface with a default implementation
public interface MaskedValue {
  default String maskedValue() {
    return "*****";
  }
}
```

You would then use the interface on the classes with identifiable information. For instance:

```java
// This value will be redacted in logs
public final class FullName implements MaskedValue {}

public final class Passport {
  private final FullName fullName;
  
  public Passport(FullName fullName) {
    this.fullName = fullName;
  }
}
```

The second step is to use the `SerializerFactory` to create a serializer that will mask values implementing for your interface.

```java
// With default mask *****
var serializer = SerializerFactory.forType(MaskedValue.class);

// With custom mask REDACTED
var serializer = SerializerFactory.forType(
    MaskedValue.class, 
    "REDACTED");

// Using a lambda (`ValueMasker` functional interface)
var serializer = SerializerFactory.forType(
    MaskedValue.class,
    (MaskedValue value, JsonGenerator generator, SerializerProvider provider) -> {
      var stringValue = value.toString();
      // Original value first and last character
      generator.writeString(
              stringValue.charAt(0)
              + "*****"
              + stringValue.substring(stringValue.length() - 1));
    }));
```

The final step is to configure your `ObjectMapper` as follows.

```java
var mapper = new ObjectMapper();
var module = new SimpleModule();
module.addSerializer(serializer);
mapper.registerModule(module);
```

Once the object mapper is configured, your activity feed is ready to serialize and mask sensitive information.

```java
var serializer = new ContextSerializer(mapper);

// ...

feed.add(Activity.info(
  "save-travel-information",
  "Travel information has been saved",
  (context) -> context.put("passport", serializer.toMap(passport))
));
```

The example above would be represented as JSON as follows

```json
{
  "message": "Travel information has been saved",
  "context": {
    "identifier": "save-travel-information",
    "passport": {
      "fullName": "*****"
    }
  }
}
```

### Activity Feed

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
  public static void main(String[] args) {
    var logger = LoggerFactory.getLogger(Application.class);
    var feed = ActivityFeed.withLogging(logger);
  }
}
```

### Activity recorders

The activity feed comes by default with a single recorder called `ActivityLogger`.
The `ActivityLogger` is instantiated and passed to the feed when using the factory method `ActivityFeed.withLogging(logger)`, as shown in the previous section.

If you need to send some activities to other places, you could implement your own `ActivityRecorder`

```java
public interface ActivityRecorder {
  void record(Activity activity);
}
```

And pass your recorders to the feed, as shown below

```java
var feed = new ActivityFeed(List.of(
    new ActivityLogger(logger),
    new SentryRecorder(), // This is just an example
    new AnotherCustomRecorder()
));
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

The `ActivityLogger` takes advantage of Log Markers.
Log Markers stamp individual log entries with unique tokens (the context passed to activities).
Markers improve your ability to **search and filter** log data.

Below is an example on how to query AWS Cloudwatch using the markers provided by your Activity Feed.

```sql
fields @timestamp, `x-correlation-id`, message
| filter `context.identifier` = "payment-processed"
| sort @timestamp desc
| limit 50
```

You could filter using any value within `context`.

## Contribute

Please refer to our [contribution guidelines](https://github.com/MontealegreLuis/activity-feed/blob/main/CONTRIBUTING.md) for information on how to contribute to this project.

## License

Released under the [BSD-3-Clause](https://github.com/MontealegreLuis/activity-feed/blob/main/LICENSE).
