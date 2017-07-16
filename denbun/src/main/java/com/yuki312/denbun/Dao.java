package com.yuki312.denbun;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.yuki312.denbun.internal.DenbunId;

import static android.support.annotation.VisibleForTesting.PACKAGE_PRIVATE;

/**
 * Created by Yuki312 on 2017/07/16.
 */
public interface Dao {

  @NonNull State find(@NonNull DenbunId id);

  @NonNull State update(@NonNull State state);

  @VisibleForTesting(otherwise = PACKAGE_PRIVATE)
  interface Provider {
    Dao create(@NonNull SharedPreferences preference);
  }
}
