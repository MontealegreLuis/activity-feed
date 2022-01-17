package com.montealegreluis.activityfeed;

public class SerializerFailure extends RuntimeException {
  public SerializerFailure(Object value, Throwable cause) {
    super(
        String.format(
            "Cannot serialize value with type %s because: %s",
            value.getClass().getName(), cause.getMessage()),
        cause);
  }
}
