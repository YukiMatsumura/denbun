package com.yuki312.denbun;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.yuki312.denbun.history.Frequency;
import com.yuki312.denbun.history.History;
import com.yuki312.denbun.history.HistoryProvider;
import com.yuki312.denbun.time.Time;
import com.yuki312.denbun.time.TimeRule;
import java.util.Calendar;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static com.yuki312.denbun.history.History.Key.Frequent;
import static com.yuki312.denbun.history.History.Key.Recent;
import static com.yuki312.denbun.history.History.Key.Suppressed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Yuki312 on 2017/07/08.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MessageTest {

  @Rule public TimeRule timeRule = new TimeRule();

  private Application app = RuntimeEnvironment.application;
  private DenbunConfig config;
  private History history;

  private void preset(boolean suppress, int frequency, long prevTime) {
    config.preference().edit().putBoolean(Suppressed.of("id"), suppress).apply();
    config.preference().edit().putInt(Frequent.of("id"), frequency).apply();
    config.preference().edit().putLong(Recent.of("id"), prevTime).apply();
  }

  @Before public void setup() {
    config = new DenbunConfig(app);
    config.historyProvider(new HistoryProvider() {
      @Override public History create(@NonNull String id, @NonNull SharedPreferences preference) {
        history = super.create(id, preference);
        return history;
      }
    });
  }

  @After public void teardown() {
    Denbun.reset();
  }

  @Test public void create() {
    Denbun.init(config);
    Denbun msg = Denbun.of("id");
  }

  @Test(expected = NullPointerException.class) public void initNull() {
    Denbun.init(null);
  }

  @Test(expected = NullPointerException.class) public void nullPreference() {
    config.preference(null);
  }

  @Test(expected = IllegalStateException.class) public void notInitialized() {
    Denbun msg = Denbun.of("id");
  }

  @Test(expected = IllegalStateException.class) public void initTwice() {
    Denbun.init(config);
    Denbun.init(config);
  }

  @Test(expected = IllegalArgumentException.class) public void nullId() {
    Denbun.init(config);
    Denbun msg = Denbun.of(null);
  }

  @Test(expected = IllegalArgumentException.class) public void blankId() {
    Denbun.init(config);
    Denbun msg = Denbun.of("");
  }

  @Test public void readDefaultPreference() {
    preset(true, Frequency.HIGH.value, 100L);
    Denbun.init(config);

    Denbun msg = Denbun.of("id");
    assertThat(msg.id()).isEqualTo("id");
    assertThat(msg.isSuppress()).isTrue();
    assertThat(msg.isFrequency()).isTrue();
    assertThat(msg.isShowable()).isFalse();
  }

  @Test public void readCustomPreference() {
    config.preference(app.getSharedPreferences("readCustomPreference.xml", Context.MODE_PRIVATE));
    Denbun.init(config);

    Denbun msg = Denbun.of("id");
    assertThat(msg.isSuppress()).isFalse();
    assertThat(msg.isFrequency()).isFalse();
    assertThat(msg.isShowable()).isTrue();
  }

  @Test public void updateState() {
    Denbun.init(config);
    FrequencyAdapter spy = spy(new FrequencyAdapter() {
      @Override public Frequency increment(@NonNull HistoryRecord history) {
        return history.frequency();  // no-op
      }
    });
    Denbun msg = Denbun.of("id", spy);
    msg.suppress(true);
    msg.shown();  // update frequency

    verify(spy, times(1)).increment(any());
    assertThat(msg.isSuppress()).isTrue();
    assertThat(msg.isShowable()).isFalse();
    assertThat(msg.isFrequency()).isFalse();
  }

  @Test public void incrementFrequency() {
    Denbun.init(config);
    FrequencyAdapter spy = spy(new FrequencyAdapter() {
      @Override public Frequency increment(@NonNull HistoryRecord history) {
        return history.frequency().plus(30);
      }
    });
    Denbun msg = Denbun.of("id", spy);
    msg.suppress(false);

    msg.shown();  // frequency is now 30
    verify(spy, times(1)).increment(any());
    assertThat(history.frequency().value).isEqualTo(30);
    assertThat(msg.isSuppress()).isFalse();
    assertThat(msg.isShowable()).isTrue();
    assertThat(msg.isFrequency()).isFalse();

    msg.shown();  // frequency is now 60
    verify(spy, times(3)).increment(any());  // increment(..) was called by isShowable() and shown()
    assertThat(history.frequency().value).isEqualTo(60);
    assertThat(msg.isSuppress()).isFalse();
    assertThat(msg.isShowable()).isTrue();
    assertThat(msg.isFrequency()).isFalse();

    msg.shown();  // frequency is now 90
    verify(spy, times(5)).increment(any());
    assertThat(history.frequency().value).isEqualTo(90);
    assertThat(msg.isSuppress()).isFalse();
    assertThat(msg.isShowable()).isFalse();
    assertThat(msg.isFrequency()).isFalse();

    msg.shown();  // frequency is now 100(high)
    verify(spy, times(7)).increment(any());
    assertThat(history.frequency().value).isEqualTo(Frequency.HIGH.value);
    assertThat(msg.isSuppress()).isFalse();
    assertThat(msg.isShowable()).isFalse();
    assertThat(msg.isFrequency()).isTrue();
  }

  @Test public void showPerDay() {
    Denbun.init(config);
    long now = System.currentTimeMillis();
    timeRule.advanceTimeTo(now);

    FrequencyAdapter freq = new FrequencyAdapter() {
      @Override public Frequency increment(@NonNull HistoryRecord historyRecord) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(Time.now());
        cal2.setTimeInMillis(historyRecord.recent());

        cal2.add(Calendar.DATE, 1);
        if (cal1.compareTo(cal2) >= 0) {
          return Frequency.LOW;
        } else {
          return Frequency.HIGH;
        }
      }
    };

    Denbun msg = Denbun.of("id", freq);
    assertThat(msg.isShowable()).isTrue();
    msg.shown();

    assertThat(msg.isShowable()).isFalse();

    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(now);
    cal.add(Calendar.DATE, 1);
    timeRule.advanceTimeTo(cal.getTimeInMillis());

    assertThat(msg.isShowable()).isTrue();
  }
}
