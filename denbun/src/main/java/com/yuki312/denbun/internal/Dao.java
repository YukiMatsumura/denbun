package com.yuki312.denbun.internal;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import static com.yuki312.denbun.Util.nonNull;

/**
 * Created by Yuki312 on 2017/07/04.
 */
public class Dao {

  private SharedPreferences pref;

  Dao(@NonNull SharedPreferences preference) {
    pref = nonNull(preference);
  }

  @NonNull public State find(@NonNull DenbunId id) {
    return new Record(id, pref).load();
  }

  @NonNull public State update(@NonNull State state) {
    return new Record(state.id, pref).save(state);
  }
}
