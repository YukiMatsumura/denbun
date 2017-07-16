package com.yuki312.denbun.internal;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import com.yuki312.denbun.Dao;
import com.yuki312.denbun.State;

import static android.support.annotation.RestrictTo.Scope.LIBRARY;
import static com.yuki312.denbun.Util.nonNull;

/**
 * Created by Yuki312 on 2017/07/04.
 */
@RestrictTo(LIBRARY)
public class DaoImpl implements Dao {

  private SharedPreferences pref;

  public DaoImpl(@NonNull SharedPreferences preference) {
    pref = nonNull(preference);
  }

  @Override @NonNull public State find(@NonNull DenbunId id) {
    return new Record(id, pref).load();
  }

  @Override @NonNull public State update(@NonNull State state) {
    return new Record(state.id, pref).save(state);
  }

  @Override public boolean delete(@NonNull DenbunId id) {
    return new Record(id, pref).delete();
  }

  @Override public boolean exist(@NonNull DenbunId id) {
    return new Record(id, pref).exist();
  }
}
