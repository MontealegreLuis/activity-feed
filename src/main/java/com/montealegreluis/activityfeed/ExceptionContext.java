package com.montealegreluis.activityfeed;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionContext {
  public static Map<String, Object> extractForm(Throwable exception) {
    Map<String, Object> context = new LinkedHashMap<>();
    context.put("message", exception.getMessage());
    StackTraceElement[] stackTrace = exception.getStackTrace();
    if (stackTrace.length > 0) {
      context.put("class", stackTrace[0].getClassName());
      context.put("line", stackTrace[0].getLineNumber());
      context.put("file", stackTrace[0].getFileName());
    }
    List<String> trace =
        Arrays.stream(stackTrace).map(StackTraceElement::toString).collect(Collectors.toList());
    context.put("trace", trace);
    if (exception.getCause() != null) {
      context.put("previous", extractForm(exception.getCause()));
    }
    return context;
  }
}
