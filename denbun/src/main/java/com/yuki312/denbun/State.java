package com.yuki312.denbun;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.yuki312.denbun.internal.DenbunId;

import static com.yuki312.denbun.Util.nonNull;

/**
 * Denbun state.
 * This object is immutable.
 *
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

  @CheckResult public State frequency(Frequency frequency) {
    return new State(id, frequency, recent, count);
  }

  public boolean isSuppress() {
    return frequency.isLimited();
  }

  public boolean isShowable() {
    return !frequency.isLimited();
  }

  @Override public String toString() {
    return "State{" +
        "id=" + id +
        ", frequency=" + frequency.value +
        ", recent=" + recent +
        ", count=" + count +
        '}';
  }
}
