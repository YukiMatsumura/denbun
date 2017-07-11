package com.yuki312.denbun.history;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import static com.yuki312.denbun.Util.nonNull;
import static com.yuki312.denbun.Util.notBlank;

/**
 * Created by YukiMatsumura on 2017/07/11.
 */
public class HistoryProvider {

  public History create(@NonNull String id, @NonNull SharedPreferences preference) {
    notBlank(id);
    nonNull(preference);
    return new History(id, preference);
  }
}
