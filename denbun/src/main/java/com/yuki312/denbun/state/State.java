package com.yuki312.denbun.state;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import static com.yuki312.denbun.Util.nonNull;

/**
 * Created by Yuki312 on 2017/07/08.
 */
public class State {

  public final boolean suppress;
  @NonNull public final Frequency frequency;
  public final long recent;

  State(boolean suppress, @NonNull Frequency frequency, long recent) {
    this.suppress = suppress;
    this.frequency = nonNull(frequency);
    this.recent = recent;
  }
}
