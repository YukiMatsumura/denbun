package com.yuki312.denbun.internal;

import android.support.annotation.NonNull;

import static com.yuki312.denbun.Util.notBlank;
import static com.yuki312.denbun.internal.Record.RESERVED_WORD;

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

    if (id.contains(RESERVED_WORD)) {
      throw new IllegalArgumentException("Bad Denbun ID. ID can not be include " + RESERVED_WORD);
    }

    this.value = id;
  }

  @Override public String toString() {
    return value;
  }
}
