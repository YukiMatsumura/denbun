package com.yuki312.denbun.core;

import android.support.annotation.NonNull;

import static com.yuki312.denbun.Util.nonNull;

/**
 * Created by Yuki312 on 2017/07/08.
 */
public class State {

  @NonNull public final Frequency frequency;
  public final long recent;
  public final int count;

  State(@NonNull Frequency frequency, long recent, int count) {
    this.frequency = nonNull(frequency);
    this.recent = recent;
    this.count = count;
  }
}
