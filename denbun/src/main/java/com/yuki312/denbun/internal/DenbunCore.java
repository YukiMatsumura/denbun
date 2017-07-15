package com.yuki312.denbun.internal;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.yuki312.denbun.FrequencyAdjuster;
import com.yuki312.denbun.time.Time;

import static com.yuki312.denbun.Util.nonNull;
import static com.yuki312.denbun.Util.notBlank;

/**
 * Created by Yuki312 on 2017/07/04.
 */
public class DenbunCore {

  public static FrequencyAdjuster DEFAULT_FREQUENCY_ADAPTER = new FrequencyAdjuster() {
    @Override public Frequency increment(@NonNull State state) {
      // The default behavior is to always return the same value.
      return state.frequency;
    }
  };

  private final String id;
  private final Pref pref;

  private State state;

  private FrequencyAdjuster frequencyAdjuster = DEFAULT_FREQUENCY_ADAPTER;

  DenbunCore(@NonNull String id, @NonNull SharedPreferences preference) {
    notBlank(id);
    nonNull(preference);

    this.id = id;
    this.pref = new Pref(id, preference);
    load();
  }

  public State state() {
    return state;
  }

  public DenbunCore show() {
    frequency(frequencyAdjuster.increment(state));
    recent(Time.now());
    count(state.count + 1);
    return this;
  }

  public boolean showable() {
    Frequency frequency = frequencyAdjuster.increment(state);
    return !frequency.isLimit();
  }

  public DenbunCore frequency(Frequency frequency) {
    save(new State(frequency, state.recent, state.count));
    return this;
  }

  public DenbunCore recent(long epochMs) {
    save(new State(state.frequency, epochMs, state.count));
    return this;
  }

  public DenbunCore count(int count) {
    save(new State(state.frequency, state.recent, count));
    return this;
  }

  public DenbunCore frequencyAdjuster(@Nullable FrequencyAdjuster interceptor) {
    frequencyAdjuster = (interceptor == null ? DEFAULT_FREQUENCY_ADAPTER : interceptor);
    return this;
  }

  private void load() {
    this.state = pref.load();
  }

  private void save(State state) {
    pref.save(state);
    load();
  }
}
