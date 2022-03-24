package com.montealegreluis.activityfeed;

import static com.montealegreluis.activityfeed.ContextAssertions.assertContextSize;
import static com.montealegreluis.activityfeed.ContextAssertions.assertContextValueEquals;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class SerializerFactoryTest {
  @Test
  void it_masks_sensitive_values_with_default_mask() {
    module.addSerializer(SerializerFactory.forType(MaskedValue.class));
    mapper.registerModule(module);
    var fullName = new FullName("Jane Doe");

    var context = serializer.toContextMap(new SerializerFactoryTest.Passport(fullName));

    assertContextSize(1, context);
    assertContextValueEquals("*****", "fullName", context);
  }

  @Test
  void it_masks_sensitive_values_with_custom_mask() {
    module.addSerializer(SerializerFactory.forType(MaskedValue.class, "REDACTED"));
    mapper.registerModule(module);
    var fullName = new FullName("Jane Doe");

    var context = serializer.toContextMap(new SerializerFactoryTest.Passport(fullName));

    assertContextSize(1, context);
    assertContextValueEquals("REDACTED", "fullName", context);
  }

  @Test
  void it_masks_sensitive_values_with_custom_serializer_function() {
    module.addSerializer(
        SerializerFactory.forType(
            MaskedValue.class,
            (MaskedValue value, JsonGenerator generator, SerializerProvider provider) -> {
              var stringValue = value.toString();
              generator.writeString(
                  stringValue.charAt(0)
                      + "*****"
                      + stringValue.substring(stringValue.length() - 1));
            }));
    mapper.registerModule(module);
    var fullName = new FullName("Jane Doe");

    var context = serializer.toContextMap(new SerializerFactoryTest.Passport(fullName));

    assertContextSize(1, context);
    assertContextValueEquals("J*****e", "fullName", context);
  }

  @BeforeEach
  void let() {
    module = new SimpleModule();
    mapper = new ObjectMapper();
    serializer = new ContextSerializer(mapper);
  }

  private static final class Passport {
    private final FullName fullName;

    public Passport(FullName fullName) {
      this.fullName = fullName;
    }
  }

  private ObjectMapper mapper;
  private ContextSerializer serializer;
  private SimpleModule module;
}
