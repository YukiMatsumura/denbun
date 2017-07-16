package com.yuki312.denbun.time;

import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;

import static android.support.annotation.RestrictTo.Scope.LIBRARY;

/**
 * Created by YukiMatsumura on 2017/07/12.
 */
@RestrictTo(value = LIBRARY)
public class Time {

  private Time() {
  }

  interface NowProvider {
    long now();
  }

  private static NowProvider systemCurrentTimeProvider = new NowProvider() {
    @Override public long now() {
      return System.currentTimeMillis();
    }
  };

  private static NowProvider nowProvider = systemCurrentTimeProvider;

  @VisibleForTesting protected static void fixedCurrentTime(NowProvider provider) {
    nowProvider = provider;
  }

  @VisibleForTesting protected static void tickCurrentTime() {
    nowProvider = systemCurrentTimeProvider;
  }

  public static long now() {
    return nowProvider.now();
  }
}
