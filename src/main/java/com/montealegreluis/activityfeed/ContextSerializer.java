package com.montealegreluis.activityfeed;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.montealegreluis.assertions.Assert;
import io.vavr.control.Try;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ContextSerializer {
  private final ObjectMapper mapper;

  /**
   * Some features on the mapper are enabled by default
   *
   * <ul>
   *   <li>Visibility is set to any
   *   <li>Property accessor is set to Field, so no getters are required
   *   <li>Objects without properties will not produce an error
   */
  public ContextSerializer(ObjectMapper mapper) {
    Assert.notNull(mapper, "Object mapper cannot be null");
    this.mapper =
        mapper
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
  }

  public Map<String, Object> toContextMap(Object value) {
    return Try.of(() -> toMap(value))
        .getOrElseThrow((cause) -> new SerializerFailure(value, cause));
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> toMap(Object value) throws JsonProcessingException {
    String json = mapper.writeValueAsString(value);
    return (Map<String, Object>) mapper.readValue(json, LinkedHashMap.class);
  }
}
