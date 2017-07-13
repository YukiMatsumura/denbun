package com.yuki312.denbun;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import java.util.HashMap;

import static com.yuki312.denbun.Util.nonNull;

/**
 * Created by Yuki312 on 2017/07/13.
 */
public class DenbunPool {

  private static DenbunConfig config;
  private static HashMap<String, Denbun> pool;

  public static void init(@NonNull DenbunConfig config) {
    if (initialized()) {
      throw new IllegalStateException(
          "Denbun is already initialized. Denbun.init(config) calls are allowed only once.");
    }

    nonNull(config);
    DenbunPool.config = config;
    DenbunPool.pool = new HashMap<>();
  }

  @VisibleForTesting static void reset() {
    config = null;
    pool = null;
  }

  private static boolean initialized() {
    return config != null;
  }

  @CheckResult public static Denbun get(@NonNull String id) {
    if (!initialized()) {
      throw new IllegalStateException(
          "Denbun is not initialized. Call Denbun.init(config) in Application.onCreate().");
    }

    if (pool.containsKey(id)) {
      return pool.get(id);
    }

    Denbun msg =
        new Denbun.Builder(id)
            .history(
                config.coreProvider().create(id, config.preference()))
            .build();
    pool.put(id, msg);

    return msg;
  }
}
