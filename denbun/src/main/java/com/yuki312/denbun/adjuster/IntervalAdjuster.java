package com.yuki312.denbun.adjuster;

import android.support.annotation.NonNull;
import com.yuki312.denbun.Frequency;
import com.yuki312.denbun.State;
import com.yuki312.denbun.time.Time;
import java.util.concurrent.TimeUnit;

/**
 * Created by Yuki312 on 2017/07/17.
 */
public class IntervalAdjuster implements FrequencyAdjuster {

  private final long interval;

  public IntervalAdjuster(long interval, TimeUnit unit) {
    this.interval = TimeUnit.MILLISECONDS.convert(interval, unit);
  }

  @Override public Frequency increment(@NonNull State state) {
    return state.recent + interval <= Time.now() ?
        Frequency.MIN : Frequency.MAX;
  }
}
