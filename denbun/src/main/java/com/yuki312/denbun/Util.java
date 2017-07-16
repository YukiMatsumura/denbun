package com.yuki312.denbun;

import android.support.annotation.RestrictTo;

import static android.support.annotation.RestrictTo.Scope.LIBRARY;

/**
 * Created by Yuki312 on 2017/07/05.
 */
@RestrictTo(LIBRARY)
public class Util {

  private Util() {
  }

  public static <T> T nonNull(T o) {
    return nonNull(o, "Require Non null object");
  }

  public static <T> T nonNull(T o, String msg) {
    if (o == null) {
      throw new NullPointerException(msg);
    }
    return o;
  }

  public static String notBlank(String s) {
    return notBlank(s, "Require not blank");
  }

  public static String notBlank(String s, String msg) {
    if (s == null || s.length() == 0) {
      throw new IllegalArgumentException(msg);
    }
    return s;
  }
}
