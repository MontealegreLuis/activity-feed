package com.montealegreluis.activityfeed;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

public final class ContextAssertions {
  public static void assertContextSize(int size, Map<String, Object> context) {
    assertEquals(size, context.size(), "Context size does not equals " + size);
  }

  public static void assertContextValueEquals(
      Object value, String key, Map<String, Object> context) {
    assertTrue(context.containsKey(key), "Context does not have key '" + key + "'");
    assertEquals(
        value, context.get(key), "Context key '" + key + "' does not equals '" + value + "'");
  }

  public static void assertContextHasKey(String key, Map<String, Object> context) {
    assertTrue(context.containsKey(key), "Context does not have key '" + key + "'");
  }

  public static void assertContextHasNoKey(String key, Map<String, Object> context) {
    assertFalse(context.containsKey(key), "Context has key '" + key + "'");
  }
}
