package com.yuki312.denbun.state;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import static com.yuki312.denbun.Util.nonNull;
import static com.yuki312.denbun.Util.notBlank;
import static com.yuki312.denbun.state.Pref.Key.Freq;
import static com.yuki312.denbun.state.Pref.Key.Recent;
import static com.yuki312.denbun.state.Pref.Key.Supp;

/**
 * Created by Yuki312 on 2017/07/01.
 */
class Pref {

  enum Key {
    Supp("_supp"), Freq("_freq"), Recent("_recent");

    public final String SUFFIX;

    Key(String suffix) {
      this.SUFFIX = suffix;
    }

    public String of(@NonNull String baseKey) {
      return baseKey + SUFFIX;
    }
  }

  private static final boolean SUPP_DEFAULT = false;
  private static final int FREQ_DEFAULT = 0;
  private static final long RECENT_DEFAULT = 0L;

  private final String id;
  private final SharedPreferences pref;

  public Pref(@NonNull String id, @NonNull SharedPreferences pref) {
    this.id = notBlank(id);
    this.pref = nonNull(pref);
  }

  public void save(State state) {
    pref.edit().putBoolean(Supp.of(id), state.suppress).apply();
    pref.edit().putInt(Freq.of(id), state.frequency.value).apply();
    pref.edit().putLong(Recent.of(id), state.recent).apply();
  }

  public State load() {
    return new State(
        pref.getBoolean(Supp.of(id), SUPP_DEFAULT),
        Frequency.of(pref.getInt(Freq.of(id), FREQ_DEFAULT)),
        pref.getLong(Recent.of(id), RECENT_DEFAULT));
  }

  public boolean suppress() {
    return pref.getBoolean(Supp.of(id), SUPP_DEFAULT);
  }
}
