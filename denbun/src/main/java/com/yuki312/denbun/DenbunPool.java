package com.yuki312.denbun;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.yuki312.denbun.internal.Dao;
import java.util.HashMap;

import static com.yuki312.denbun.Util.nonNull;

/**
 * Created by Yuki312 on 2017/07/13.
 */
public class DenbunPool {

  private static DenbunConfig config;
  private static HashMap<String, Denbun> pool;
  private static Dao dao;

  public static void init(@NonNull DenbunConfig config) {
    nonNull(config, "DenbunConfig can not be null");

    if (initialized()) {
      throw new IllegalStateException(
          "Denbun is already initialized. Denbun.init(config) calls are allowed only once.");
    }

    DenbunPool.config = config;
    DenbunPool.pool = new HashMap<>();
    DenbunPool.dao = config.daoProvider().create(config.preference());
  }

  @VisibleForTesting static void reset() {
    config = null;
    pool = null;
  }

  private static boolean initialized() {
    return config != null;
  }

  @CheckResult public static Denbun take(@NonNull String id) {
    return take(id, Denbun.DEFAULT_FREQUENCY_ADAPTER);
  }

  @CheckResult public static Denbun take(@NonNull String id, @NonNull FrequencyAdjuster adjuster) {
    nonNull(id, "Message ID can not be null");
    nonNull(adjuster, "FrequencyAdjuster can not be null.");

    if (!initialized()) {
      throw new IllegalStateException(
          "Denbun is not initialized. Call Denbun.init(config) in Application.onCreate().");
    }

    if (pool.containsKey(id)) {
      return pool.get(id);
    }

    Denbun msg =
        new Denbun.Builder(id)
            .dao(dao)
            .adjuster(adjuster)
            .build();
    pool.put(id, msg);

    return msg;
  }
}
