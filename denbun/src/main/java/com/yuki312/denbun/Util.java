package com.yuki312.denbun;

/**
 * Created by Yuki312 on 2017/07/05.
 */
public abstract class Util {

  public static <T> T nonNull(T o) {
    if (o == null) {
      throw new NullPointerException("Require Non null object");
    }
    return o;
  }

  public static String notBlank(String s) {
    if (s == null || s.length() == 0) {
      throw new IllegalArgumentException("Require not blank");
    }
    return s;
  }
}
