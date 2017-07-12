package com.yuki312.denbun;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.yuki312.denbun.history.Frequency;
import com.yuki312.denbun.history.History;
import com.yuki312.denbun.time.Time;
import java.util.HashMap;

import static com.yuki312.denbun.Util.nonNull;
import static com.yuki312.denbun.Util.notBlank;

/**
 * Created by Yuki312 on 2017/07/03.
 */
public class Denbun {

  public static FrequencyAdapter DEFAULT_FREQUENCY_ADAPTER = new FrequencyAdapter() {
    @Override public Frequency increment(@NonNull HistoryRecord historyRecord) {
      // The default behavior is to always return the same value.
      return historyRecord.frequency();
    }
  };

  private static DenbunConfig config;
  private static HashMap<String, Denbun> shared;

  private final String id;
  private final History history;
  private final FrequencyAdapter frequencyAdapter;

  public static void init(@NonNull DenbunConfig config) {
    if (initialized()) {
      throw new IllegalStateException(
          "Denbun is already initialized. Denbun.init(config) calls are allowed only once.");
    }

    nonNull(config);
    Denbun.config = config;
    Denbun.shared = new HashMap<>();
  }

  @VisibleForTesting static void reset() {
    config = null;
    shared = null;
  }

  private static boolean initialized() {
    return config != null;
  }

  @CheckResult public static Denbun of(@NonNull String id) {
    return of(id, Denbun.DEFAULT_FREQUENCY_ADAPTER);
  }

  @CheckResult public static Denbun of(@NonNull String id, @Nullable FrequencyAdapter adapter) {
    if (!initialized()) {
      throw new IllegalStateException(
          "Denbun is not initialized. Call Denbun.init(config) in Application.onCreate().");
    }

    if (shared.containsKey(id)) {
      return shared.get(id);
    }

    Denbun msg =
        new Denbun.Builder(id).history(config.historyProvider().create(id, config.preference()))
            .frequencyAdapter(adapter)
            .build();
    shared.put(id, msg);

    return msg;
  }

  public static Denbun prepare(String id) {
    return of(id);
  }

  private Denbun(@NonNull String id, @NonNull FrequencyAdapter frequencyAdapter,
      @NonNull History history) {
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
    return history.frequency().isHigh();
  }

  /**
   * このメッセージが最後に表示された日時.
   */
  public long recent() {
    return history.recent();
  }

  /**
   * このメッセージが表示制限状態であるか.
   *
   * @param suppress 表示を制限する場合はtrue, それ以外はfalse.
   */
  public Denbun suppress(boolean suppress) {
    history.suppress(suppress);
    return this;
  }

  public Denbun clearFrequency() {
    history.frequency(Frequency.LOW);
    return this;
  }

  /**
   * このメッセージが表示可能かどうかを確認する.
   * 表示可能性は, メッセージの表示頻度が評価された上で算出される.
   * つまり, このメッセージを表示することによって表示頻度が過度であると判断される場合はfalseを返す.
   *
   * @return 表示可能であればtrue, それ以外はfalse.
   */
  public boolean isShowable() {
    return !history.suppress() && !frequencyAdapter.increment(history).isHigh();
  }

  /**
   * メッセージを表示したことを通知する.
   */
  public Denbun shown() {
    history.frequency(frequencyAdapter.increment(history));
    history.recent(Time.now());
    return this;
  }

  /*
   * Denbun Builder.
   */
  private static class Builder {

    private final String id;
    private History history;
    private FrequencyAdapter adapter = DEFAULT_FREQUENCY_ADAPTER;

    Builder(@NonNull String id) {
      this.id = notBlank(id);
    }

    Builder history(@NonNull History history) {
      this.history = nonNull(history);
      return this;
    }

    Builder frequencyAdapter(@Nullable FrequencyAdapter adapter) {
      this.adapter = (adapter == null ? DEFAULT_FREQUENCY_ADAPTER : adapter);
      return this;
    }

    Denbun build() {
      notBlank(id);
      nonNull(history);
      return new Denbun(id, adapter, history);
    }
  }
}
