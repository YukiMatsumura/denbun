package com.yuki312.denbun.history;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.yuki312.denbun.HistoryRecord;

import static com.yuki312.denbun.history.History.KeyType.Frequent;
import static com.yuki312.denbun.history.History.KeyType.PreviousTime;
import static com.yuki312.denbun.history.History.KeyType.Suppressed;
import static com.yuki312.denbun.Util.nonNull;
import static com.yuki312.denbun.Util.notBlank;

/**
 * Created by Yuki312 on 2017/07/04.
 */
public class History implements HistoryRecord {

  private final Pref pref;
  private final String id;

  private boolean suppressed = false;
  private Frequency frequency = Frequency.LOW;
  private long previousTime = 0L;

  public enum KeyType {
    Suppressed("_supp"), Frequent("_freq"), PreviousTime("_prev");

    public final String SUFFIX;

    KeyType(String suffix) {
      this.SUFFIX = suffix;
    }

    public String of(@NonNull String originKey) {
      return originKey + SUFFIX;
    }
  }

  public History(@NonNull String id, @NonNull SharedPreferences preference) {
    notBlank(id);
    nonNull(preference);

    this.id = id;
    this.pref = new Pref(preference);
    load();
  }

  private void load() {
    this.suppressed = pref.getBoolean(Suppressed.of(id), false);
    this.frequency = new Frequency(pref.getInt(Frequent.of(id), 0));
    this.previousTime = pref.getLong(PreviousTime.of(id), 0L);
  }

  private void save() {
    pref.setBoolean(Suppressed.of(id), suppressed);
    pref.setInt(Frequent.of(id), frequency.value);
    pref.setLong(PreviousTime.of(id), previousTime);
    load();
  }

  @Override public boolean suppress() {
    return suppressed;
  }

  public History suppress(boolean suppressed) {
    this.suppressed = suppressed;
    save();
    return this;
  }

  @Override public Frequency frequency() {
    return new Frequency(frequency.value);
  }

  public History frequency(Frequency frequency) {
    this.frequency = frequency;
    save();
    return this;
  }

  @Override public long previousTime() {
    return previousTime;
  }

  public History previousTime(long epochMs) {
    this.previousTime = epochMs;
    save();
    return this;
  }
}
