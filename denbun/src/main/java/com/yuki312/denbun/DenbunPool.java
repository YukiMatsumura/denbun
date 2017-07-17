package com.yuki312.denbun;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import com.yuki312.denbun.adjuster.FrequencyAdjuster;
import com.yuki312.denbun.internal.DenbunId;
import java.util.HashMap;

import static com.yuki312.denbun.Util.nonNull;
import static com.yuki312.denbun.Util.notBlank;

/**
 * Class that pooling the Denbun.
 * Denbun instance is obtained from this class.
 * The generated Denbun instance is pooled and reused.
 *
 * Created by Yuki312 on 2017/07/13.
 */
public class DenbunPool {

  private DenbunPool() {
  }

  private static DenbunConfig config;
  private static HashMap<DenbunId, Denbun> pool;
  private static Dao dao;

  /**
   * Initialize Pool.
   * DenbunPool can not be initialized more than once
   */
  public static void init(@NonNull DenbunConfig config) {
    nonNull(config, "DenbunConfig can not be null");

    if (initialized()) {
      Log.w("DenbunPool", "Denbun is already initialized.");
      return;
    }

    DenbunPool.config = config;
    DenbunPool.pool = new HashMap<>();
    DenbunPool.dao = config.daoProvider().create(config.preference());
  }

  @VisibleForTesting public static void reset() {
    config = null;
    pool = null;
  }

  private static boolean initialized() {
    return config != null;
  }

  /**
   * Get Denbun with the specified ID from the pool.
   * When an ID that does not exist in the pool, Denbun that FrequencyAdjuster is not
   * set is generated and returned. Denbun is set with persisted information.
   */
  @CheckResult public static Denbun find(@NonNull String id) {
    return find(id, Denbun.DEFAULT_FREQUENCY_ADAPTER);
  }

  /**
   * Get Denbun with the specified ID from the pool.
   * When an ID that does not exist in the pool, Denbun with adjuster is created and returned.
   * Denbun is set with persisted information.
   */
  @CheckResult public static Denbun find(@NonNull String id, @NonNull FrequencyAdjuster adjuster) {
    nonNull(id, "Denbun ID can not be null");
    nonNull(adjuster, "FrequencyAdjuster can not be null.");
    checkInitialized();

    DenbunId denbunId = DenbunId.of(id);

    if (pool.containsKey(denbunId)) {
      return pool.get(denbunId);
    }

    Denbun msg =
        new Denbun.Builder(DenbunId.of(id))
            .dao(dao)
            .adjuster(adjuster)
            .build();
    pool.put(denbunId, msg);

    return msg;
  }

  /**
   * Delete the Denbun with the specified ID from the DenbunPool and the persisted information.
   */
  public static void remove(@NonNull String id) {
    nonNull(id, "Denbun ID can not be null");
    checkInitialized();

    DenbunId denbunId = DenbunId.of(id);
    if (pool.containsKey(denbunId)) {
      pool.remove(denbunId);
    }

    dao.delete(denbunId);
  }

  /**
   * Check existence of Denbun.
   *
   * @return true: exist. false: not exist.
   */
  public static boolean exist(@NonNull String id) {
    notBlank(id);
    checkInitialized();

    return dao.exist(DenbunId.of(id));
  }

  private static void checkInitialized() {
    if (!initialized()) {
      throw new IllegalStateException(
          "Denbun is not initialized. Call Denbun.init(config) in Application.onCreate().");
    }
  }
}
