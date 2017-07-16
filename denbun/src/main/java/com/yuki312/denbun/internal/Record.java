package com.yuki312.denbun.internal;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import static com.yuki312.denbun.Util.nonNull;
import static com.yuki312.denbun.internal.Record.Key.Count;
import static com.yuki312.denbun.internal.Record.Key.Freq;
import static com.yuki312.denbun.internal.Record.Key.Recent;

/**
 * Created by Yuki312 on 2017/07/01.
 */
class Record {

  public static final String RESERVED_WORD = "__dnbn_";

  enum Key {
    Freq(RESERVED_WORD + "freq"),
    Recent(RESERVED_WORD + "recent"),
    Count(RESERVED_WORD + "cnt");

    public final String SUFFIX;

    Key(String suffix) {
      this.SUFFIX = suffix;
    }

    public String of(@NonNull DenbunId id) {
      return id.value + SUFFIX;
    }
  }

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
