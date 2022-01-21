package com.montealegreluis.activityfeed;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

public final class MaskedValueSerializer extends StdSerializer<MaskedValue> {
  public MaskedValueSerializer() {
    super(MaskedValue.class);
  }

  @Override
  public void serialize(MaskedValue value, JsonGenerator generator, SerializerProvider provider)
      throws IOException {
    generator.writeString(value.maskedValue());
  }
}
