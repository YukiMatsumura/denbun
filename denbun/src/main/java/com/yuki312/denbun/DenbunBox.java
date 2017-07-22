package com.yuki312.denbun;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import com.yuki312.denbun.adjuster.FrequencyAdjuster;
import com.yuki312.denbun.internal.DenbunId;
import java.util.HashMap;
import java.util.Map;

import static com.yuki312.denbun.Util.nonNull;
import static com.yuki312.denbun.Util.notBlank;

/**
 * Denbun factory class.
 *
 * Call get(String) method If you want to get Denbun.
 * Alternatively, if you want to get a Denbun with a specific FrequencyAdjuster,
 * call the get(String, FrequencyAdjuster) method.
 * In advance, if you want to specify a FrequencyAdjuster to be set in the Denbun,
 * it may call a preset(String, FrequencyAdjuster) method.
 *
 * Created by Yuki312 on 2017/07/13.
 */
public class DenbunBox {

  private DenbunBox() {
  }

  private static DenbunConfig config;
  private static HashMap<DenbunId, FrequencyAdjuster> presetAdjuster;
  private static Dao dao;

  /**
   * Initialize DenbunBox.
   * DenbunBox can be initialized only once.
   */
  public static void init(@NonNull DenbunConfig config) {
    nonNull(config, "DenbunConfig can not be null");

    if (initialized()) {
      Log.w("DenbunBox", "DenbunBox is already initialized.");
      return;
    }

    DenbunBox.config = config;
    DenbunBox.presetAdjuster = new HashMap<>();
    DenbunBox.dao = config.daoProvider().create(config.preference());
  }

  @VisibleForTesting public static void reset() {
    config = null;
    presetAdjuster = null;
  }

  private static boolean initialized() {
    return config != null;
  }

  /**
   * Create the Denbun instance.
   *
   * It has DEFAULT_FREQUENCY_ADJUSTER. Or specific Adjuster that you preset.
   */
  @CheckResult public static Denbun get(@NonNull String id) {
    checkInitialized();

    DenbunId denbunId = DenbunId.of(id);
    if (presetAdjuster.containsKey(denbunId)) {
      return get(denbunId, presetAdjuster.get(denbunId));
    } else {
      return get(denbunId, Denbun.DEFAULT_FREQUENCY_ADJUSTER);
    }
  }

  /**
   * Create the Denbun instance.
   *
   * It has specific Adjuster.
   */
  @CheckResult public static Denbun get(@NonNull String id, @NonNull FrequencyAdjuster adjuster) {
    checkInitialized();

    return get(DenbunId.of(id), adjuster);
  }

  private static Denbun get(@NonNull DenbunId id, @NonNull FrequencyAdjuster adjuster) {
    nonNull(adjuster, "FrequencyAdjuster can not be null.");

    return new Denbun.Builder(id).dao(dao).adjuster(adjuster).build();
  }

  /**
   * Delete the Denbun with the specified ID from the persisted information.
   */
  public static void remove(@NonNull String id) {
    checkInitialized();

    dao.delete(DenbunId.of(id));
  }

  /**
   * Check existence of Denbun in SharedPreference
   *
   * @return true: exist. false: not exist.
   */
  public static boolean exist(@NonNull String id) {
    checkInitialized();

    return dao.exist(DenbunId.of(id));
  }

  /**
   * Preset FrequencyAdjuster corresponding to ID.
   */
  public static void preset(@NonNull String id, @NonNull FrequencyAdjuster adjuster) {
    notBlank(id, "Denbun ID can not be blank");
    nonNull(adjuster);
    checkInitialized();

    presetAdjuster.put(DenbunId.of(id), adjuster);
  }

  /**
   * Preset FrequencyAdjuster corresponding to IDs.
   */
  public static void preset(@NonNull Map<String, FrequencyAdjuster> adjusters) {
    nonNull(adjusters);
    checkInitialized();

    for (Map.Entry<String, FrequencyAdjuster> e : adjusters.entrySet()) {
      presetAdjuster.put(DenbunId.of(e.getKey()), nonNull(e.getValue()));
    }
  }

  private static void checkInitialized() {
    if (!initialized()) {
      throw new IllegalStateException(
          "Denbun is not initialized. Call DenbunBox.init(config) in Application.onCreate().");
    }
  }
}
