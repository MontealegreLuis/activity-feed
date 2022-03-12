package com.montealegreluis.activityfeed;

import static com.montealegreluis.activityfeed.ExceptionContextFactory.contextFrom;

import com.montealegreluis.assertions.Assert;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

public final class ActivityBuilder {
  private final Map<String, Object> context = new LinkedHashMap<>();
  private Level level;
  private String identifier;
  private String message;

  public static ActivityBuilder anActivity() {
    return new ActivityBuilder();
  }

  public ActivityBuilder debug() {
    level = Level.CONFIG;
    return this;
  }

  public ActivityBuilder info() {
    level = Level.INFO;
    return this;
  }

  public ActivityBuilder warning() {
    level = Level.WARNING;
    return this;
  }

  public ActivityBuilder error() {
    level = Level.SEVERE;
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
    if (Level.CONFIG.equals(level)) {
      return Activity.debug(identifier, message, (context) -> context.putAll(this.context));
    } else if (Level.INFO.equals(level)) {
      return Activity.info(identifier, message, (context) -> context.putAll(this.context));
    } else if (Level.WARNING.equals(level)) {
      return Activity.warning(identifier, message, (context) -> context.putAll(this.context));
    }
    return Activity.error(identifier, message, (context) -> context.putAll(this.context));
  }
}
