package com.yuki312.denbun.history;

import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import java.math.BigDecimal;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by Yuki312 on 2017/07/08.
 */
public class Frequency {

  private static final float MAX = 1f;
  private static final float MIN = 0f;
  public static final Frequency HIGH = new Frequency(1f);
  public static final Frequency LOW = new Frequency(0f);

  @FloatRange(from = MIN, to = MAX)
  private float value;

  public Frequency(@FloatRange(from = MIN, to = MAX) float value) {
    set(value);
  }

  @FloatRange(from = MIN, to = MAX)
  public float get() {
    return value;
  }

  public void set(@FloatRange(from = MIN, to = MAX) float value) {
    this.value = max(MIN, min(value, MAX));
  }

  public Frequency plus(@Nullable Frequency frequency) {
    if (frequency == null) return this;
    set(value + frequency.get());
    return this;
  }

  public Frequency plus(float value) {
    set(new BigDecimal(this.value).add(new BigDecimal(value)).floatValue());
    return this;
  }

  public boolean high() {
    return this.value == MAX;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Frequency that = (Frequency) o;
    return Float.compare(that.value, value) == 0;
  }

  @Override public int hashCode() {
    return (value != +0f ? Float.floatToIntBits(value) : 0);
  }
}
