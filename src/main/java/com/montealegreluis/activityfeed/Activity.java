package com.montealegreluis.activityfeed;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

public final class Activity {
  private final Level level;
  private final String message;
  private final Map<String, Object> context;

  public static Activity info(String identifier, String message, Map<String, Object> context) {
    return new Activity(Level.INFO, identifier, message, context);
  }

  public static Activity warning(String identifier, String message, Map<String, Object> context) {
    return new Activity(Level.WARNING, identifier, message, context);
  }

  public static Activity error(String identifier, String message, Map<String, Object> context) {
    return new Activity(Level.SEVERE, identifier, message, context);
  }

  public static Activity debug(String identifier, String message, Map<String, Object> context) {
    return new Activity(Level.CONFIG, identifier, message, context);
  }

  public String message() {
    return message;
  }

  public Map<String, Object> context() {
    return context;
  }

  public Level level() {
    return level;
  }

  private Activity(Level level, String identifier, String message, Map<String, Object> context) {
    this.level = level;
    this.message = message;
    context.put("identifier", identifier);
    this.context = new LinkedHashMap<>();
    this.context.put("context", context);
  }
}
