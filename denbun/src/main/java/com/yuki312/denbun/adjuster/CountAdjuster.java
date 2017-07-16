package com.yuki312.denbun.adjuster;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import com.yuki312.denbun.Frequency;
import com.yuki312.denbun.State;

/**
 * Created by Yuki312 on 2017/07/17.
 */
public class CountAdjuster implements FrequencyAdjuster {

  private final int limit;

  public CountAdjuster(@IntRange(from = 1) int limit) {
    this.limit = limit;
  }

  @Override public Frequency increment(@NonNull State state) {
    return state.count < limit ? Frequency.MIN : Frequency.MAX;
  }
}
