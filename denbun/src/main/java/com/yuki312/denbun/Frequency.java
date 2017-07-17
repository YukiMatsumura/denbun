package com.yuki312.denbun;

import android.support.annotation.CheckResult;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Message frequency.
 * This object is immutable.
 *
 * Created by Yuki312 on 2017/07/08.
 */
public class Frequency {

  private static final int UPPER = 100;
  private static final int LOWER = 0;

  public static final Frequency MAX = new Frequency(UPPER);
  public static final Frequency MIN = new Frequency(LOWER);

  public final int value;

  /**
   * Create Frequency with value.
   * If a value below (or above) the range is specified, it is rounded.
   */
  @CheckResult public static Frequency of(@IntRange(from = LOWER, to = UPPER) int value) {
    return new Frequency(value);
  }

  private Frequency(int value) {
    this.value = max(LOWER, min(value, UPPER));
  }

  @CheckResult public Frequency plus(@Nullable Frequency frequency) {
    if (frequency == null) return this;
    return plus(frequency.value);
  }

  @CheckResult public Frequency plus(int value) {
    return Frequency.of(this.value + value);
  }

  @CheckResult public Frequency minus(@Nullable Frequency frequency) {
    if (frequency == null) return this;
    return minus(frequency.value);
  }

  @CheckResult public Frequency minus(int value) {
    return Frequency.of(this.value - value);
  }

  public boolean isLimited() {
    return this.value == UPPER;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Frequency frequency = (Frequency) o;

    return value == frequency.value;
  }

  @Override public int hashCode() {
    return value;
  }

  @Override public String toString() {
    return "Frequency{value=" + value + '}';
  }
}
