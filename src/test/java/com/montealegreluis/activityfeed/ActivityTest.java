package com.montealegreluis.activityfeed;

import static com.montealegreluis.activityfeed.ContextAssertions.assertContextSize;
import static com.montealegreluis.activityfeed.ContextAssertions.assertContextValueEquals;
import static org.junit.jupiter.api.Assertions.*;

import com.montealegreluis.assertions.IllegalArgumentException;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class ActivityTest {
  @Test
  void its_context_includes_its_identifier() {
    var identifier = "an-identifier";
    var activity = Activity.debug(identifier, "A message");

    var activityContext = activity.context();

    assertTrue(activityContext.containsKey("context"));
    @SuppressWarnings("unchecked")
    Map<String, Object> context = (Map<String, Object>) (activityContext.get("context"));
    assertContextSize(1, context);
    assertContextValueEquals(identifier, "identifier", context);
  }

  @Test
  void its_adds_custom_values_to_its_context() {
    var userId = "5fa21160-f175-43dc-bac1-80d6e5d2d3bd";
    var correlationId = "9ea6ab93-f7ad-4497-a899-e6bc3938f3ba";
    var identifier = "an-identifier";
    var activity =
        Activity.info(
            identifier,
            "A message",
            (context) -> {
              context.put("userId", userId);
              context.put("correlationId", correlationId);
            });

    var activityContext = activity.context();

    assertTrue(activityContext.containsKey("context"));
    @SuppressWarnings("unchecked")
    Map<String, Object> context = (Map<String, Object>) (activityContext.get("context"));
    assertContextSize(3, context);
    assertContextValueEquals(identifier, "identifier", context);
    assertContextValueEquals(userId, "userId", context);
    assertContextValueEquals(correlationId, "correlationId", context);
  }

  @Test
  void it_knows_its_message() {
    var message = "A message";
    var error = Activity.error("identifier", message);
    var info = Activity.info("identifier", message);
    var warning = Activity.warning("identifier", message);
    var debug = Activity.debug("identifier", message);

    assertEquals(message, error.message());
    assertEquals(message, info.message());
    assertEquals(message, warning.message());
    assertEquals(message, debug.message());
  }

  @Test
  void it_prevents_blank_or_null_identifiers() {
    assertThrows(IllegalArgumentException.class, () -> Activity.info(" ", "A message"));
    assertThrows(IllegalArgumentException.class, () -> Activity.warning(null, "A message"));
  }

  @Test
  void it_prevents_blank_or_null_messages() {
    assertThrows(IllegalArgumentException.class, () -> Activity.info("identifier", " "));
    assertThrows(IllegalArgumentException.class, () -> Activity.warning("identifier", null));
  }
}
