package com.montealegreluis.activityfeed.builders;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ContextBuilder {
  private final Map<String, Object> context = new LinkedHashMap<>();

  public static ContextBuilder aContext() {
    return new ContextBuilder();
  }

  public ContextBuilder withEntry(String key, Object value) {
    context.put(key, value);
    return this;
  }

  public Map<String, Object> build() {
    return context;
  }
}
