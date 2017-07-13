package com.yuki312.denbun.state;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.yuki312.denbun.FrequencyInterceptor;
import com.yuki312.denbun.time.Time;

import static com.yuki312.denbun.Util.nonNull;
import static com.yuki312.denbun.Util.notBlank;

/**
 * Created by Yuki312 on 2017/07/04.
 */
public class DenbunCore {

  public static FrequencyInterceptor DEFAULT_FREQUENCY_ADAPTER = new FrequencyInterceptor() {
    @Override public Frequency increment(@NonNull State state) {
      // The default behavior is to always return the same value.
      return state.frequency;
    }
  };

  private final String id;
  private final Pref pref;

  private State state;

  private FrequencyInterceptor frequencyInterceptor = DEFAULT_FREQUENCY_ADAPTER;

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
    frequency(frequencyInterceptor.increment(state));
    recent(Time.now());
    return this;
  }

  public boolean showable() {
    Frequency frequency = frequencyInterceptor.increment(state);
    return !state.suppress && !frequency.isHigh();
  }

  public DenbunCore suppress(boolean suppressed) {
    save(new State(suppressed, state.frequency, state.recent));
    return this;
  }

  public DenbunCore frequency(Frequency frequency) {
    save(new State(state.suppress, frequency, state.recent));
    return this;
  }

  public DenbunCore recent(long epochMs) {
    save(new State(state.suppress, state.frequency, epochMs));
    return this;
  }

  public DenbunCore frequencyInterceptor(@Nullable FrequencyInterceptor interceptor) {
    frequencyInterceptor = (interceptor == null ? DEFAULT_FREQUENCY_ADAPTER : interceptor);
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
