package com.montealegreluis.activityfeed;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

final class MaskedValueTest {
  @Test
  void it_masks_a_value() {
    var fullName = new FullName("Jane Doe");

    var maskedValue = fullName.maskedValue();

    assertEquals("*****", maskedValue);
  }
}
