package com.yuki312.denbun;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.yuki312.denbun.state.Frequency;
import com.yuki312.denbun.state.DenbunCore;
import com.yuki312.denbun.state.CoreProvider;
import com.yuki312.denbun.state.State;
import com.yuki312.denbun.time.Time;
import com.yuki312.denbun.time.TimeRule;
import com.yuki312.denbun.time.TimeRule.Now;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;

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
  private DenbunCore core;

  private void set(boolean suppress, int frequency, long recent) {
    core.suppress(suppress);
    core.frequency(Frequency.of(frequency));
    core.recent(recent);
  }

  @Before public void setup() {
    config = new DenbunConfig(app);
    config.coreProvider(new CoreProvider() {
      @Override
      public DenbunCore create(@NonNull String id, @NonNull SharedPreferences preference) {
        core = super.create(id, preference);
        return core;
      }
    });
  }

  @After public void teardown() {
    DenbunPool.reset();
  }

  @Test public void create() {
    DenbunPool.init(config);
    Denbun msg = DenbunPool.get("id");
  }

  @Test(expected = NullPointerException.class) public void initNull() {
    DenbunPool.init(null);
  }

  @Test(expected = NullPointerException.class) public void nullPreference() {
    config.preference(null);
  }

  @Test(expected = IllegalStateException.class) public void notInitialized() {
    Denbun msg = DenbunPool.get("id");
  }

  @Test(expected = IllegalStateException.class) public void initTwice() {
    DenbunPool.init(config);
    DenbunPool.init(config);
  }

  @Test(expected = IllegalArgumentException.class) public void nullId() {
    DenbunPool.init(config);
    Denbun msg = DenbunPool.get(null);
  }

  @Test(expected = IllegalArgumentException.class) public void blankId() {
    DenbunPool.init(config);
    Denbun msg = DenbunPool.get("");
  }

  @Test public void readDefaultPreference() {
    DenbunPool.init(config);

    Denbun msg = DenbunPool.get("id");
    set(true, Frequency.HIGH.value, 100L);

    assertThat(msg.id()).isEqualTo("id");
    assertThat(msg.isSuppress()).isTrue();
    assertThat(msg.isFrequency()).isTrue();
    assertThat(msg.isShowable()).isFalse();
  }

  @Test public void readCustomPreference() {
    config.preference(app.getSharedPreferences("readCustomPreference.xml", Context.MODE_PRIVATE));
    DenbunPool.init(config);

    Denbun msg = DenbunPool.get("id");
    assertThat(msg.isSuppress()).isFalse();
    assertThat(msg.isFrequency()).isFalse();
    assertThat(msg.isShowable()).isTrue();

    set(true, Frequency.HIGH.value, 100L);
    assertThat(msg.isSuppress()).isTrue();
    assertThat(msg.isFrequency()).isTrue();
    assertThat(msg.isShowable()).isFalse();

    SharedPreferences pref = app.getSharedPreferences("readCustomPreference.xml", Context.MODE_PRIVATE);
    assertThat(pref.getBoolean("id_supp", false)).isTrue();
    assertThat(pref.getInt("id_freq", -1)).isEqualTo(100);
    assertThat(pref.getLong("id_recent", -1)).isEqualTo(100L);
  }

  @Test public void updateState() {
    DenbunPool.init(config);
    FrequencyInterceptor spy = spy(new FrequencyInterceptor() {
      @Override public Frequency increment(@NonNull State state) {
        return state.frequency;  // no-op
      }
    });
    Denbun msg = DenbunPool.get("id")
        .frequencyInterceptor(spy)
        .suppress(true)
        .shown();  // update frequency

    verify(spy, times(1)).increment(any());
    assertThat(msg.isSuppress()).isTrue();
    assertThat(msg.isShowable()).isFalse();
    assertThat(msg.isFrequency()).isFalse();
  }

  @Test public void incrementFrequency() {
    DenbunPool.init(config);
    FrequencyInterceptor spy = spy(new FrequencyInterceptor() {
      @Override public Frequency increment(@NonNull State history) {
        return history.frequency.plus(30);
      }
    });
    Denbun msg = DenbunPool.get("id")
        .frequencyInterceptor(spy)
        .suppress(false);

    msg.shown();  // frequency is now 30
    verify(spy, times(1)).increment(any());
    assertThat(core.state().frequency.value).isEqualTo(30);
    assertThat(msg.isSuppress()).isFalse();
    assertThat(msg.isShowable()).isTrue();
    assertThat(msg.isFrequency()).isFalse();

    msg.shown();  // frequency is now 60
    verify(spy, times(3)).increment(any());  // increment(..) was called by isShowable() and shown()
    assertThat(core.state().frequency.value).isEqualTo(60);
    assertThat(msg.isSuppress()).isFalse();
    assertThat(msg.isShowable()).isTrue();
    assertThat(msg.isFrequency()).isFalse();

    msg.shown();  // frequency is now 90
    verify(spy, times(5)).increment(any());
    assertThat(core.state().frequency.value).isEqualTo(90);
    assertThat(msg.isSuppress()).isFalse();
    assertThat(msg.isShowable()).isFalse();
    assertThat(msg.isFrequency()).isFalse();

    msg.shown();  // frequency is now 100(high)
    verify(spy, times(7)).increment(any());
    assertThat(core.state().frequency.value).isEqualTo(Frequency.HIGH.value);
    assertThat(msg.isSuppress()).isFalse();
    assertThat(msg.isShowable()).isFalse();
    assertThat(msg.isFrequency()).isTrue();
  }

  @Test @Now public void showablePerDay() {
    DenbunPool.init(config);
    long now = System.currentTimeMillis();
    timeRule.advanceTimeTo(now);

    FrequencyInterceptor freq = new FrequencyInterceptor() {
      @Override public Frequency increment(@NonNull State state) {
        LocalDateTime now = OffsetDateTime.ofInstant(Instant.ofEpochMilli(Time.now()), ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime recent = OffsetDateTime.ofInstant(Instant.ofEpochMilli(state.recent), ZoneId.systemDefault()).toLocalDateTime();

        if (recent.isBefore(yesterday) || recent.isEqual(yesterday)) {
          return Frequency.LOW;
        } else {
          return Frequency.HIGH;
        }
      }
    };

    Denbun msg = DenbunPool.get("id")
        .frequencyInterceptor(freq);
    assertThat(msg.isShowable()).isTrue();
    msg.shown();

    assertThat(msg.isShowable()).isFalse();

    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(now);
    cal.add(Calendar.DATE, 1);

    timeRule.advanceTimeTo(cal.getTimeInMillis());

    assertThat(msg.isShowable()).isTrue();
  }

  @Test public void showableOnly3Times() {
    DenbunPool.init(config);

    FrequencyInterceptor freq = new FrequencyInterceptor() {
      @Override public Frequency increment(@NonNull State state) {
        return state.frequency.plus(Frequency.HIGH.value / 3);
      }
    };

    Denbun msg = DenbunPool.get("id")
        .frequencyInterceptor(freq);
    assertThat(msg.isShowable()).isTrue();
    msg.shown();

    assertThat(msg.isShowable()).isTrue();
    msg.shown();

    assertThat(msg.isShowable()).isTrue();
    msg.shown();

    assertThat(msg.isShowable()).isFalse();
  }
}
