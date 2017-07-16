package com.yuki312.denbun.internal;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import static com.yuki312.denbun.Util.nonNull;

/**
 * Created by YukiMatsumura on 2017/07/11.
 */
public class DaoProvider {

  public Dao create(@NonNull SharedPreferences preference) {
    nonNull(preference);
    return new Dao(preference);
  }
}
