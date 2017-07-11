package com.yuki312.denbun.history;

import android.support.annotation.NonNull;
import com.yuki312.denbun.History;

import static com.yuki312.denbun.history.HistoryImpl.KeyType.Frequent;
import static com.yuki312.denbun.history.HistoryImpl.KeyType.PreviousTime;
import static com.yuki312.denbun.history.HistoryImpl.KeyType.Suppressed;
import static com.yuki312.denbun.Util.nonNull;
import static com.yuki312.denbun.Util.notBlank;

/**
 * Created by Yuki312 on 2017/07/04.
 */
public class HistoryImpl implements History {

  private final Pref pref;
  private final String id;

  private boolean suppressed = false;
  private Frequency frequency = Frequency.LOW;
  private long previousTime = 0L;

  public enum KeyType {
    Suppressed("_supp"),
    Frequent("_freq"),
    PreviousTime("_prev");

    public final String SUFFIX;

    KeyType(String suffix) {
      this.SUFFIX = suffix;
    }

    public String of(@NonNull String originKey) {
      return originKey + SUFFIX;
    }
  }

  public HistoryImpl(@NonNull String id, @NonNull Pref pref) {
    this.pref = nonNull(pref);
    this.id = notBlank(id);
    load();
  }

  private void load() {
    this.suppressed = pref.getBoolean(Suppressed.of(id), false);
    this.frequency = new Frequency(pref.getFloat(Frequent.of(id), 0f));
    this.previousTime = pref.getLong(PreviousTime.of(id), 0L);
  }

  private void save() {
    pref.setBoolean(Suppressed.of(id), suppressed);
    pref.setFloat(Frequent.of(id), frequency.get());
    pref.setLong(PreviousTime.of(id), previousTime);
    load();
  }

  @Override public boolean suppress() {
    return suppressed;
  }

  public HistoryImpl suppress(boolean suppressed) {
    this.suppressed = suppressed;
    save();
    return this;
  }

  @Override public Frequency frequency() {
    return new Frequency(frequency.get());
  }

  public HistoryImpl frequency(Frequency frequency) {
    this.frequency = frequency;
    save();
    return this;
  }

  @Override public long previousTime() {
    return previousTime;
  }

  public HistoryImpl previousTime(long epochMs) {
    this.previousTime = epochMs;
    save();
    return this;
  }
}
