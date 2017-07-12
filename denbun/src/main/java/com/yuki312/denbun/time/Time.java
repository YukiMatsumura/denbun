package com.yuki312.denbun.time;

import android.annotation.SuppressLint;
import android.support.annotation.VisibleForTesting;
import java.lang.annotation.AnnotationFormatError;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by YukiMatsumura on 2017/07/12.
 */

public class Time {

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

  /**
   * ISO8601(UTC)形式の文字列をepoch timeに変換
   */
  @SuppressLint("SimpleDateFormat") public static long parseIso8601Z(String iso8601z) {
    try {
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:dd'Z'");
      format.setTimeZone(TimeZone.getTimeZone("UTC"));
      return format.parse(iso8601z).getTime();
    } catch (ParseException e) {
      throw new AnnotationFormatError(e);
    }
  }
}
