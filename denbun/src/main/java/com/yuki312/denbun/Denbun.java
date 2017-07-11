package com.yuki312.denbun;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.yuki312.denbun.history.Frequency;
import com.yuki312.denbun.history.HistoryImpl;
import com.yuki312.denbun.history.Pref;
import java.util.HashMap;

import static com.yuki312.denbun.Util.nonNull;
import static com.yuki312.denbun.Util.notBlank;

/**
 * Created by Yuki312 on 2017/07/03.
 */
public class Denbun {

  public static FrequencyAdapter DEFAULT_FREQUENCY_ADAPTER = new FrequencyAdapter() {
    @Override public Frequency increment(@NonNull History history) {
      // The default behavior is to always return the same value.
      return new Frequency(history.frequency().get());
    }
  };

  private static boolean initialized = false;
  private static Pref pref;
  private static HashMap<String, Denbun> shared;

  private final String id;
  private final HistoryImpl history;
  private final FrequencyAdapter frequencyAdapter;

  public static void init(@NonNull DenbunConfig config) {
    if (initialized) {
      throw new IllegalStateException(
          "Denbun is already initialized. Denbun.init(config) calls are allowed only once.");
    }

    nonNull(config);
    Denbun.pref = config.preference();
    Denbun.shared = new HashMap<>();
    initialized = true;
  }

  @VisibleForTesting
  static void reset() {
    initialized = false;
    pref = null;
    shared = null;
  }

  public static Denbun of(@NonNull String id) {
    return of(id, Denbun.DEFAULT_FREQUENCY_ADAPTER);
  }

  public static Denbun of(@NonNull String id, @Nullable FrequencyAdapter adapter) {
    if (!initialized) {
      throw new IllegalStateException(
          "Denbun is not initialized. Call Denbun.init(config) before use.");
    }

    if (shared.containsKey(id)) {
      return shared.get(id);
    }
    Denbun msg = new Denbun.Builder(id)
        .frequencyAdapter(adapter)
        .build();
    shared.put(id, msg);
    return msg;
  }

  private Denbun(@NonNull String id,
      @NonNull FrequencyAdapter frequencyAdapter,
      @NonNull HistoryImpl history) {
    this.id = nonNull(id);
    this.frequencyAdapter = nonNull(frequencyAdapter);
    this.history = nonNull(history);
  }

  @NonNull public String id() {
    return id;
  }

  /**
   * このメッセージが表示を制限されているか否か.
   *
   * 当分の間, メッセージの表示を抑制したい場合などにこのフラグは使用できる.
   */
  public boolean isSuppress() {
    return history.suppress();
  }

  /**
   * このメッセージが過度に表示されているか否か.
   *
   * 短時間の間に複数回メッセージが表示されたかを判断したい場合などにこのフラグは使用できる.
   */
  public boolean isFrequency() {
    return history.frequency().high();
  }

  public float frequency() {
    return history.frequency().get();
  }

  /**
   * このメッセージが最後に表示された日時.
   */
  public long previousTime() {
    return history.previousTime();
  }

  public Denbun suppress(boolean suppress) {
    history.suppress(suppress);
    return this;
  }

  public boolean isShowable() {
    return isShowable(false);
  }

  public boolean isShowable(boolean checkBeforeShowing) {
    Frequency original = history.frequency();
    if (checkBeforeShowing) {
      original.plus(frequencyAdapter.increment(history));
    }
    return !history.suppress() && !original.high();
  }

  public Denbun shown() {
    history.frequency(frequencyAdapter.increment(history));
    history.previousTime(System.currentTimeMillis());
    return this;
  }

  private static class Builder {

    private final String id;
    private FrequencyAdapter adapter = DEFAULT_FREQUENCY_ADAPTER;

    Builder(@NonNull String id) {
      this.id = notBlank(id);
    }

    Builder frequencyAdapter(@Nullable FrequencyAdapter adapter) {
      this.adapter = (adapter == null ? DEFAULT_FREQUENCY_ADAPTER : adapter);
      return this;
    }

    Denbun build() {
      notBlank(id);

      HistoryImpl history = new HistoryImpl(id, pref);
      return new Denbun(id, adapter, history);
    }
  }
}
