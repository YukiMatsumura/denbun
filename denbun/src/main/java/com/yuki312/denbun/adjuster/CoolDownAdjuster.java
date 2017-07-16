package com.yuki312.denbun.adjuster;

import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import com.yuki312.denbun.Frequency;
import com.yuki312.denbun.State;
import com.yuki312.denbun.time.Time;
import java.util.concurrent.TimeUnit;

/**
 * Created by Yuki312 on 2017/07/17.
 */
public class CoolDownAdjuster implements FrequencyAdjuster {

  private final float rate;
  private final long interval;

  public CoolDownAdjuster(@FloatRange(from = 0.0, to = 1.0) float increaseRate,
      long interval, TimeUnit unit) {
    this.rate = increaseRate;
    this.interval = TimeUnit.MILLISECONDS.convert(interval, unit);
  }

  @Override public Frequency increment(@NonNull State state) {
    Frequency freq = state.frequency
        .plus((int) (state.frequency.value + (Frequency.MAX.value * rate)));
    if (freq.isLimited()) {
      return state.recent + interval <= Time.now() ? Frequency.MIN : Frequency.MAX;
    }
    return freq;
  }
}
