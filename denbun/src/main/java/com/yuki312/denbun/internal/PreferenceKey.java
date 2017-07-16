package com.yuki312.denbun.internal;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import static android.support.annotation.VisibleForTesting.PACKAGE_PRIVATE;

/**
 * Created by Yuki312 on 2017/07/16.
 */
@VisibleForTesting(otherwise = PACKAGE_PRIVATE)
public enum PreferenceKey {

  Freq("__dnbn_freq"),
  Recent("__dnbn_recent"),
  Count("__dnbn_cnt");

  public final String SUFFIX;

  PreferenceKey(String suffix) {
    this.SUFFIX = suffix;
  }

  public String of(@NonNull DenbunId id) {
    return id.value + SUFFIX;
  }

  public static String reservedWord() {
    return "__dnbn_";
  }
}
