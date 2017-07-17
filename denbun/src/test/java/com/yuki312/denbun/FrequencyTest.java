package com.yuki312.denbun;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by Yuki312 on 2017/07/16.
 */
@RunWith(RobolectricTestRunner.class)
public class FrequencyTest {

  private final int MIN = 0;
  private final int MAX = 100;

  @Test public void max() {
    assertThat(Frequency.MAX.value).isEqualTo(MAX);
  }

  @Test public void min() {
    assertThat(Frequency.MIN.value).isEqualTo(MIN);
  }

  @SuppressWarnings("Range") @Test public void init() {
    assertThat(Frequency.of(MAX + 1).value).isEqualTo(MAX);
    assertThat(Frequency.of(MIN - 1).value).isEqualTo(MIN);
    assertThat(Frequency.of(50).value).isEqualTo(50);
  }

  @Test public void plus() {
    assertThat(Frequency.of(MIN).plus(5)).isEqualTo(Frequency.of(MIN + 5));
    assertThat(Frequency.of(MAX).plus(1)).isEqualTo(Frequency.of(MAX));

    assertThat(Frequency.of(MIN).plus(5).value).isEqualTo(MIN + 5);
    assertThat(Frequency.of(MAX).plus(1).value).isEqualTo(MAX);

    assertThat(Frequency.of(MIN).plus(Frequency.of(5))).isEqualTo(Frequency.of(MIN + 5));
    assertThat(Frequency.of(MAX).plus(Frequency.of(1))).isEqualTo(Frequency.of(MAX));
  }

  @Test public void minus() {
    assertThat(Frequency.of(MAX).minus(5)).isEqualTo(Frequency.of(MAX - 5));
    assertThat(Frequency.of(MIN).minus(5)).isEqualTo(Frequency.of(MIN));

    assertThat(Frequency.of(MAX).minus(5).value).isEqualTo(MAX - 5);
    assertThat(Frequency.of(MIN).minus(5).value).isEqualTo(MIN);

    assertThat(Frequency.of(MAX).minus(Frequency.of(5))).isEqualTo(Frequency.of(MAX - 5));
    assertThat(Frequency.of(MIN).minus(Frequency.of(5))).isEqualTo(Frequency.of(MIN));
  }

  @Test public void equality() {
    Frequency f1 = Frequency.of(10);
    Frequency f2 = Frequency.of(10);
    Frequency f3 = Frequency.of(11).minus(1);

    assertThat(f1).isEqualTo(f2);
    assertThat(f1).isEqualTo(f3);
    assertThat(f2).isEqualTo(f3);

    Frequency fMax = Frequency.of(MAX);
    Frequency fMin = Frequency.of(MIN);
    assertThat(Frequency.of(MAX - 5)).isEqualTo(fMax.minus(5));
    assertThat(Frequency.of(MIN + 5)).isEqualTo(fMin.plus(5));
  }

  @Test public void hash() {
    assertThat(Frequency.of(10).hashCode()).isEqualTo(10);
  }

  @Test public void toStringTest() {
    String s = Frequency.of(1).toString();
  }
}
