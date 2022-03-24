package com.montealegreluis.activityfeed;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializerFactory {
  public static <T> StdSerializer<T> forType(Class<T> target) {
    return forType(target, "*****");
  }

  public static <T> StdSerializer<T> forType(Class<T> target, String mask) {
    return new StdSerializer<>(target) {
      @Override
      public void serialize(T value, JsonGenerator generator, SerializerProvider provider)
          throws IOException {
        generator.writeString(mask);
      }
    };
  }

  public static <T> StdSerializer<T> forType(Class<T> target, ValueMasker<T> masker) {
    return new StdSerializer<>(target) {
      @Override
      public void serialize(T value, JsonGenerator generator, SerializerProvider provider)
          throws IOException {
        masker.serialize(value, generator, provider);
      }
    };
  }
}
