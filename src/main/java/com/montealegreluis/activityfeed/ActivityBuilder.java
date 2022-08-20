package com.montealegreluis.activityfeed;

import static com.montealegreluis.activityfeed.ExceptionContextFactory.contextFrom;

import com.montealegreluis.assertions.Assert;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.event.Level;

public final class ActivityBuilder {
  private final Map<String, Object> context = new LinkedHashMap<>();
  private final Level level;
  private String identifier;
  private String message;

  public static ActivityBuilder aTracingActivity() {
    return new ActivityBuilder(Level.TRACE);
  }

  public static ActivityBuilder aDebuggingActivity() {
    return new ActivityBuilder(Level.DEBUG);
  }

  public static ActivityBuilder anInformationalActivity() {
    return new ActivityBuilder(Level.INFO);
  }

  public static ActivityBuilder anErrorActivity() {
    return new ActivityBuilder(Level.ERROR);
  }

  public static ActivityBuilder aWarningActivity() {
    return new ActivityBuilder(Level.WARN);
  }

  private ActivityBuilder(Level level) {
    this.level = level;
  }

  public ActivityBuilder withIdentifier(String identifier) {
    this.identifier = identifier;
    return this;
  }

  public ActivityBuilder withMessage(String message) {
    this.message = message;
    return this;
  }

  public ActivityBuilder withException(Throwable exception) {
    Assert.notNull(exception, "Exception cannot be null");
    context.put("exception", contextFrom(exception));
    return this;
  }

  public ActivityBuilder with(String key, Object value) {
    context.put(key, value);
    return this;
  }

  public Activity build() {
    return Activity.withLevel(
        level, identifier, message, (context) -> context.putAll(this.context));
  }
}
