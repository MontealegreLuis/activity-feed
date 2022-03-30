package com.montealegreluis.activityfeed;

import static com.montealegreluis.activityfeed.ExceptionContextFactory.contextFrom;

import com.montealegreluis.assertions.Assert;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.event.Level;

public final class ActivityBuilder {
  private final Map<String, Object> context = new LinkedHashMap<>();
  private Level level;
  private String identifier;
  private String message;

  public static ActivityBuilder anActivity() {
    return new ActivityBuilder();
  }

  public ActivityBuilder debug() {
    level = Level.DEBUG;
    return this;
  }

  public ActivityBuilder info() {
    level = Level.INFO;
    return this;
  }

  public ActivityBuilder warning() {
    level = Level.WARN;
    return this;
  }

  public ActivityBuilder error() {
    level = Level.ERROR;
    return this;
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
