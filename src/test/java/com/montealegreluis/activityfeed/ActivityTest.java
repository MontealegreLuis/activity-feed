package com.montealegreluis.activityfeed;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

final class ActivityTest {
  @Test
  void its_context_includes_its_identifier() {
    String userId = "5fa21160-f175-43dc-bac1-80d6e5d2d3bd";
    String identifier = "an-identifier";
    var activity =
        Activity.info(identifier, "A message", (context) -> context.put("userId", userId));

    var activityContext = activity.context();

    assertTrue(activityContext.containsKey("context"));
    @SuppressWarnings("unchecked")
    Map<String, Object> contextValues = (Map<String, Object>) (activityContext.get("context"));
    assertTrue(contextValues.containsKey("identifier"));
    assertEquals(identifier, contextValues.get("identifier"));
    assertTrue(contextValues.containsKey("userId"));
    assertEquals(userId, contextValues.get("userId"));
  }

  @Test
  void it_knows_its_message() {
    var message = "A message";
    var activity = Activity.info("identifier", message, (context) -> context.put("key", "value"));

    assertEquals(message, activity.message());
  }
}
