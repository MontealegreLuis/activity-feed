package com.montealegreluis.activityfeed;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

public final class Activity {
  private final Level level;
  private final String identifier;
  private final String message;
  private final ContextFactory factory;
  private final Map<String, Object> context = new LinkedHashMap<>();

  public static Activity info(String identifier, String message, ContextFactory factory) {
    return new Activity(Level.INFO, identifier, message, factory);
  }

  public static Activity warning(String identifier, String message, ContextFactory factory) {
    return new Activity(Level.WARNING, identifier, message, factory);
  }

  public static Activity error(String identifier, String message, ContextFactory factory) {
    return new Activity(Level.SEVERE, identifier, message, factory);
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
    factory.addEntries(context);
    this.context.put("context", context);
    return this.context;
  }

  public Level level() {
    return level;
  }

  private Activity(Level level, String identifier, String message, ContextFactory factory) {
    this.level = level;
    this.identifier = identifier;
    this.message = message;
    this.factory = factory;
  }
}
