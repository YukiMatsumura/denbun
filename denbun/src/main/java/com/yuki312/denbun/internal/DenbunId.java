package com.yuki312.denbun.internal;

import android.support.annotation.NonNull;

import static com.yuki312.denbun.Util.notBlank;

/**
 * Created by Yuki312 on 2017/07/16.
 */
public class DenbunId {

  public final String value;

  public static DenbunId of(String id) {
    return new DenbunId(id);
  }

  private DenbunId(@NonNull String id) {
    notBlank(id, "Denbun ID can not be null");

    if (id.contains(PreferenceKey.reservedWord())) {
      throw new IllegalArgumentException(
          "Bad Denbun ID. ID can not be include " + PreferenceKey.reservedWord());
    }

    this.value = id;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DenbunId denbunId = (DenbunId) o;

    return value.equals(denbunId.value);
  }

  @Override public int hashCode() {
    return value.hashCode();
  }

  @Override public String toString() {
    return "DenbunID=" + value;
  }
}
