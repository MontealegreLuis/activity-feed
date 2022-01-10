package com.montealegreluis.activityfeed;

import java.util.Map;

public interface ContextFactory {
  void addEntries(Map<String, Object> entries);
}
