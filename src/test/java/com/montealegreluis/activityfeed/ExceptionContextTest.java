package com.montealegreluis.activityfeed;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class ExceptionContextTest {
  @Test
  void it_extracts_context_information_from_exception() {
    var message = "Something went wrong";
    var exception = new RuntimeException(message);

    var context = ExceptionContext.extractForm(exception);

    assertEquals(message, context.get("message"));
    assertEquals(ExceptionContextTest.class.getCanonicalName(), context.get("class"));
    assertTrue(context.containsKey("line"));
    assertTrue(context.get("file").toString().contains(ExceptionContextTest.class.getSimpleName()));
    @SuppressWarnings("unchecked")
    List<String> trace = (List<String>) (context.get("trace"));
    assertTrue(trace.size() > 0);
    assertFalse(context.containsKey("previous"));
  }

  @Test
  void it_extracts_context_information_from_exception_with_cause_information() {
    var causeMessage = "Previous exception";
    var cause = new IllegalArgumentException(causeMessage);
    var message = "Something went wrong";
    var exception = new RuntimeException(message, cause);

    var context = ExceptionContext.extractForm(exception);

    assertEquals(message, context.get("message"));
    assertEquals(ExceptionContextTest.class.getCanonicalName(), context.get("class"));
    assertTrue(context.containsKey("line"));
    assertTrue(context.get("file").toString().contains(ExceptionContextTest.class.getSimpleName()));
    @SuppressWarnings("unchecked")
    List<String> trace = (List<String>) (context.get("trace"));
    assertTrue(trace.size() > 0);
    // Previous exception
    assertTrue(context.containsKey("previous"));
    @SuppressWarnings("unchecked")
    var previous = (Map<String, Object>) (context.get("previous"));
    assertEquals(causeMessage, previous.get("message"));
    assertEquals(ExceptionContextTest.class.getCanonicalName(), previous.get("class"));
    assertTrue(previous.containsKey("line"));
    assertTrue(
        previous.get("file").toString().contains(ExceptionContextTest.class.getSimpleName()));
    @SuppressWarnings("unchecked")
    List<String> previousTrace = (List<String>) (context.get("trace"));
    assertTrue(previousTrace.size() > 0);
    assertFalse(previous.containsKey("previous"));
  }

  @Test
  void it_extracts_context_information_from_exception_without_stack_trace() {
    var message = "Something went wrong";
    var exception = new NoStackTraceException(message);

    var context = ExceptionContext.extractForm(exception);

    assertEquals(message, context.get("message"));
    assertFalse(context.containsKey("class"));
    assertFalse(context.containsKey("line"));
    assertFalse(context.containsKey("file"));
    @SuppressWarnings("unchecked")
    List<String> trace = (List<String>) (context.get("trace"));
    assertEquals(0, trace.size());
    assertFalse(context.containsKey("previous"));
  }

  private static class NoStackTraceException extends Exception {
    public NoStackTraceException(String message) {
      super(message);
    }

    @Override
    public StackTraceElement[] getStackTrace() {
      return new StackTraceElement[0];
    }
  }
}
