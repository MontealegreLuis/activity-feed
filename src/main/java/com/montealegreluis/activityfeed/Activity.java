package com.montealegreluis.activityfeed;

import com.montealegreluis.assertions.Assert;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(exclude = "factory")
public final class Activity {
  private final Level level;
  private final String identifier;
  private final String message;
  private final ContextFactory factory;
  private final Map<String, Object> context = new LinkedHashMap<>();

  public static Activity info(String identifier, String message) {
    return info(identifier, message, null);
  }

  public static Activity info(String identifier, String message, ContextFactory factory) {
    return new Activity(Level.INFO, identifier, message, factory);
  }

  public static Activity warning(String identifier, String message) {
    return warning(identifier, message, null);
  }

  public static Activity warning(String identifier, String message, ContextFactory factory) {
    return new Activity(Level.WARNING, identifier, message, factory);
  }

  public static Activity error(String identifier, String message) {
    return error(identifier, message, null);
  }

  public static Activity error(String identifier, String message, ContextFactory factory) {
    return new Activity(Level.SEVERE, identifier, message, factory);
  }

  public static Activity debug(String identifier, String message) {
    return debug(identifier, message, null);
  }

  public static Activity debug(String identifier, String message, ContextFactory factory) {
    return new Activity(Level.CONFIG, identifier, message, factory);
  }

  public String message() {
    return message;
  }

  public Map<String, Object> context() {
    Map<String, Object> context = new LinkedHashMap<>();
    context.put("identifier", identifier);
    if (factory != null) factory.addEntries(context);
    this.context.put("context", context);
    return this.context;
  }

  public Level level() {
    return level;
  }

  private Activity(Level level, String identifier, String message, ContextFactory factory) {
    this.level = level;
    Assert.notBlank(identifier, "Activity identifier cannot be blank. '%s' given");
    this.identifier = identifier;
    Assert.notBlank(message, "Activity message cannot be blank. '%s' given");
    this.message = message;
    this.factory = factory;
  }
}
