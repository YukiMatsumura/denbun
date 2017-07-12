package com.yuki312.denbun.history;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.yuki312.denbun.HistoryRecord;

import static com.yuki312.denbun.history.History.Key.Frequent;
import static com.yuki312.denbun.history.History.Key.Recent;
import static com.yuki312.denbun.history.History.Key.Suppressed;
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
  private long recent = 0L;

  public enum Key {
    Suppressed("_supp"), Frequent("_freq"), Recent("_recent");

    public final String SUFFIX;

    Key(String suffix) {
      this.SUFFIX = suffix;
    }

    public String of(@NonNull String baseKey) {
      return baseKey + SUFFIX;
    }
  }

  History(@NonNull String id, @NonNull SharedPreferences preference) {
    notBlank(id);
    nonNull(preference);

    this.id = id;
    this.pref = new Pref(preference);
    load();
  }

  private void load() {
    this.suppressed = pref.getBoolean(Suppressed.of(id), false);
    this.frequency = Frequency.of(pref.getInt(Frequent.of(id), 0));
    this.recent = pref.getLong(Recent.of(id), 0L);
  }

  private void save() {
    pref.setBoolean(Suppressed.of(id), suppressed);
    pref.setInt(Frequent.of(id), frequency.value);
    pref.setLong(Recent.of(id), recent);
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
    return frequency;
  }

  public History frequency(Frequency frequency) {
    this.frequency = frequency;
    save();
    return this;
  }

  @Override public long recent() {
    return recent;

  }
  public History recent(long epochMs) {
    this.recent = epochMs;
    save();
    return this;
  }
}
