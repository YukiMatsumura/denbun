package com.yuki312.denbun.core;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import static com.yuki312.denbun.Util.nonNull;
import static com.yuki312.denbun.Util.notBlank;
import static com.yuki312.denbun.core.Pref.Key.Count;
import static com.yuki312.denbun.core.Pref.Key.Freq;
import static com.yuki312.denbun.core.Pref.Key.Recent;

/**
 * Created by Yuki312 on 2017/07/01.
 */
class Pref {

  enum Key {
    Freq("_freq"), Recent("_recent"), Count("_cnt");

    public final String SUFFIX;

    Key(String suffix) {
      this.SUFFIX = suffix;
    }

    public String of(@NonNull String baseKey) {
      return baseKey + SUFFIX;
    }
  }

  private static final int FREQ_DEFAULT = 0;
  private static final long RECENT_DEFAULT = 0L;
  private static final int COUNT_DEFAULT = 0;

  private final String id;
  private final SharedPreferences pref;

  public Pref(@NonNull String id, @NonNull SharedPreferences pref) {
    this.id = notBlank(id);
    this.pref = nonNull(pref);
  }

  public void save(State state) {
    pref.edit().putInt(Freq.of(id), state.frequency.value).apply();
    pref.edit().putLong(Recent.of(id), state.recent).apply();
    pref.edit().putInt(Count.of(id), state.count).apply();
  }

  public State load() {
    return new State(
        Frequency.of(pref.getInt(Freq.of(id), FREQ_DEFAULT)),
        pref.getLong(Recent.of(id), RECENT_DEFAULT),
        pref.getInt(Count.of(id), COUNT_DEFAULT));
  }
}
