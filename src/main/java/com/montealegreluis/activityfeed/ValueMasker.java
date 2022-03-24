package com.montealegreluis.activityfeed;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public interface ValueMasker<T> {
  void serialize(T value, JsonGenerator generator, SerializerProvider provider) throws IOException;
}
