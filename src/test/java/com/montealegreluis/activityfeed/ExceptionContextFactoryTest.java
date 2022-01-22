package com.montealegreluis.activityfeed;

import static com.montealegreluis.activityfeed.ContextAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class ExceptionContextFactoryTest {
  @Test
  void it_extracts_context_information_from_exception() {
    var message = "Something went wrong";
    var exception = new RuntimeException(message);

    var context = ExceptionContextFactory.contextFrom(exception);

    assertContextSize(5, context);
    assertContextValueEquals(message, "message", context);
    assertContextValueEquals(
        ExceptionContextFactoryTest.class.getCanonicalName(), "class", context);
    assertContextHasKey("line", context);
    assertTrue(
        context.get("file").toString().contains(ExceptionContextFactoryTest.class.getSimpleName()));
    @SuppressWarnings("unchecked")
    List<String> trace = (List<String>) (context.get("trace"));
    assertTrue(trace.size() > 0);
    assertContextHasNoKey("previous", context);
  }

  @Test
  void it_extracts_context_information_from_exception_with_cause_information() {
    var causeMessage = "Previous exception";
    var cause = new IllegalArgumentException(causeMessage);
    var message = "Something went wrong";
    var exception = new RuntimeException(message, cause);

    var context = ExceptionContextFactory.contextFrom(exception);

    assertContextSize(6, context);
    assertContextValueEquals(message, "message", context);
    assertContextValueEquals(
        ExceptionContextFactoryTest.class.getCanonicalName(), "class", context);
    assertContextHasKey("line", context);
    assertTrue(
        context.get("file").toString().contains(ExceptionContextFactoryTest.class.getSimpleName()));
    @SuppressWarnings("unchecked")
    List<String> trace = (List<String>) (context.get("trace"));
    assertTrue(trace.size() > 0);
    // Previous exception
    assertContextHasKey("previous", context);
    @SuppressWarnings("unchecked")
    var previous = (Map<String, Object>) (context.get("previous"));
    assertEquals(causeMessage, previous.get("message"));
    assertContextValueEquals(causeMessage, "message", previous);
    assertContextValueEquals(
        ExceptionContextFactoryTest.class.getCanonicalName(), "class", previous);
    assertContextHasKey("line", context);
    assertTrue(
        previous
            .get("file")
            .toString()
            .contains(ExceptionContextFactoryTest.class.getSimpleName()));
    @SuppressWarnings("unchecked")
    List<String> previousTrace = (List<String>) (context.get("trace"));
    assertTrue(previousTrace.size() > 0);
    assertContextHasNoKey("previous", previous);
  }

  @Test
  void it_extracts_context_information_from_exception_without_stack_trace() {
    var message = "Something went wrong";
    var exception = new NoStackTraceException(message);

    var context = ExceptionContextFactory.contextFrom(exception);

    assertContextSize(2, context);
    assertContextValueEquals(message, "message", context);
    assertContextHasNoKey("class", context);
    assertContextHasNoKey("line", context);
    assertContextHasNoKey("file", context);
    @SuppressWarnings("unchecked")
    List<String> trace = (List<String>) (context.get("trace"));
    assertEquals(0, trace.size());
    assertContextHasNoKey("previous", context);
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
