package com.montealegreluis.activityfeed;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.montealegreluis.activityfeed.builders.ContextBuilder.aContext;
import static org.junit.jupiter.api.Assertions.*;

final class ActivityTest {
  @Test
  void its_context_includes_its_identifier() {
    String userId = "5fa21160-f175-43dc-bac1-80d6e5d2d3bd";
    var context = aContext().withEntry("userId", userId).build();
    String identifier = "an-identifier";
    var activity = Activity.info(identifier, "A message", context);

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
    var activity = Activity.info("identifier", message, aContext().build());

    assertEquals(message, activity.message());
  }
}
