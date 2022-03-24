package com.montealegreluis.activityfeed;

public final class FullName implements MaskedValue {
  private final String fullName;

  public FullName(String fullName) {
    this.fullName = fullName;
  }

  @Override
  public String toString() {
    return fullName;
  }
}
