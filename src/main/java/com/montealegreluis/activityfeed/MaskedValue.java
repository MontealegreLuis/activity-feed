package com.montealegreluis.activityfeed;

public interface MaskedValue {
  default String maskedValue() {
    return "*****";
  }
}
