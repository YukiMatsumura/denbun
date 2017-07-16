package com.yuki312.denbun.internal;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import com.yuki312.denbun.Frequency;
import com.yuki312.denbun.State;

import static android.support.annotation.RestrictTo.Scope.LIBRARY;
import static com.yuki312.denbun.Util.nonNull;
import static com.yuki312.denbun.internal.PreferenceKey.Count;
import static com.yuki312.denbun.internal.PreferenceKey.Freq;
import static com.yuki312.denbun.internal.PreferenceKey.Recent;

/**
 * Created by Yuki312 on 2017/07/01.
 */
@RestrictTo(LIBRARY)
public class Record {

  private static final int FREQ_DEFAULT = 0;
  private static final long RECENT_DEFAULT = 0L;
  private static final int COUNT_DEFAULT = 0;

  private final DenbunId id;
  private final SharedPreferences pref;

  public Record(@NonNull DenbunId id, @NonNull SharedPreferences pref) {
    this.id = nonNull(id);
    this.pref = nonNull(pref);
  }

  public State save(@NonNull State state) {
    pref.edit().putInt(Freq.of(id), state.frequency.value).apply();
    pref.edit().putLong(Recent.of(id), state.recent).apply();
    pref.edit().putInt(Count.of(id), state.count).apply();
    return load();
  }

  public State load() {
    return new State(
        id,
        Frequency.of(pref.getInt(Freq.of(id), FREQ_DEFAULT)),
        pref.getLong(Recent.of(id), RECENT_DEFAULT),
        pref.getInt(Count.of(id), COUNT_DEFAULT));
  }
}
