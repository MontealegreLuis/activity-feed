package com.montealegreluis.activityfeed;

import static com.montealegreluis.activityfeed.ContextAssertions.assertContextSize;
import static com.montealegreluis.activityfeed.ContextAssertions.assertContextValueEquals;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    assertContextSize(2, context);
    assertContextValueEquals(name, "name", context);
    assertContextValueEquals(age, "age", context);
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

  private static final class PersonalInformation {
    private final String name;
    private final int age;

    public PersonalInformation(String name, int age) {
      this.name = name;
      this.age = age;
    }
  }
}
