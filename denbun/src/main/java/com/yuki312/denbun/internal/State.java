package com.yuki312.denbun.internal;

import android.support.annotation.NonNull;

import static com.yuki312.denbun.Util.nonNull;

/**
 * Created by Yuki312 on 2017/07/08.
 */
public class State {

  @NonNull public final DenbunId id;
  @NonNull public final Frequency frequency;
  public final long recent;
  public final int count;

  public State(@NonNull DenbunId id, @NonNull Frequency frequency, long recent, int count) {
    this.id = nonNull(id);
    this.frequency = nonNull(frequency);
    this.recent = recent;
    this.count = count;
  }

  public boolean isSuppress() {
    return frequency.isLimited();
  }

  public boolean isShowable() {
    return !frequency.isLimited();
  }
}
