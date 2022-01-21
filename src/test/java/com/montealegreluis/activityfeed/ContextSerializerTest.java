package com.montealegreluis.activityfeed;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.montealegreluis.assertions.IllegalArgumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class ContextSerializerTest {
  @Test
  void it_fails_when_no_object_mapper_is_provided() {
    var exception = assertThrows(IllegalArgumentException.class, () -> new ContextSerializer(null));

    assertEquals("Object mapper cannot be null", exception.getMessage());
  }

  @Test
  void it_fails_to_extract_context_from_a_value_that_cannot_be_JSON_encoded() {
    assertThrows(
        SerializerFailure.class, () -> serializer.toContextMap(new ClassWithCircularReferences()));
  }

  @Test
  void it_extracts_context_map_from_a_serializable_value() {
    var name = "Jane Doe";
    var age = 20;
    var context = serializer.toContextMap(new PersonalInformation(name, age));

    assertEquals(2, context.size());
    assertTrue(context.containsKey("name"));
    assertEquals(name, context.get("name"));
    assertTrue(context.containsKey("age"));
    assertEquals(age, context.get("age"));
  }

  @Test
  void it_masks_sensitive_values() {
    var mapper = new ObjectMapper();
    var module = new SimpleModule();
    module.addSerializer(new MaskedValueSerializer());
    mapper.registerModule(module);
    var serializer = new ContextSerializer(mapper);
    var fullName = new FullName("Jane Doe");

    var context = serializer.toContextMap(new Passport(fullName));

    assertEquals(1, context.size());
    assertTrue(context.containsKey("fullName"));
    assertEquals("*****", context.get("fullName"));
  }

  @BeforeEach
  void let() {
    serializer = new ContextSerializer(new ObjectMapper());
  }

  private ContextSerializer serializer;

  private static final class ClassWithCircularReferences {
    // self references produce infinite cycles
    private final ClassWithCircularReferences self = this;
  }

  private static final class Passport {
    private final FullName fullName;

    public Passport(FullName fullName) {
      this.fullName = fullName;
    }
  }

  private static final class PersonalInformation {
    private final String name;
    private final int age;

    public PersonalInformation(String name, int age) {
      this.name = name;
      this.age = age;
    }
  }
}
