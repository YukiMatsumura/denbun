package com.yuki312.denbun;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import com.yuki312.denbun.history.Frequency;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static com.yuki312.denbun.history.HistoryImpl.KeyType.Frequent;
import static com.yuki312.denbun.history.HistoryImpl.KeyType.PreviousTime;
import static com.yuki312.denbun.history.HistoryImpl.KeyType.Suppressed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Yuki312 on 2017/07/08.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MessageTest {

  private Application app = RuntimeEnvironment.application;
  private DenbunConfig config;

  @Before public void setup() {
    config = new DenbunConfig(app);
  }

  @After public void teardown() {
    Denbun.reset();
  }

  @Test public void create() {
    Denbun.init(config);
    Denbun.of("id");
  }

  @Test(expected = NullPointerException.class) public void initNull() {
    Denbun.init(null);
  }

  @Test(expected = NullPointerException.class) public void nullPreference() {
    config.preference(null);
  }

  @Test(expected = IllegalStateException.class) public void notInitialized() {
    Denbun.of("id");
  }

  @Test(expected = IllegalStateException.class) public void initTwice() {
    Denbun.init(config);
    Denbun.init(config);
  }

  @Test(expected = IllegalArgumentException.class) public void nullId() {
    Denbun.init(config);
    Denbun.of(null);
  }

  @Test(expected = IllegalArgumentException.class) public void blankId() {
    Denbun.init(config);
    Denbun.of("");
  }

  @Test public void readDefaultPreference() {
    config.preference().setBoolean(Suppressed.of("id"), true);
    config.preference().setInt(Frequent.of("id"), Frequency.HIGH.value);
    config.preference().setLong(PreviousTime.of("id"), 100L);
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
      @Override public Frequency increment(@NonNull History history) {
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
      @Override public Frequency increment(@NonNull History history) {
        return history.frequency().plus(30);
      }
    });
    Denbun msg = Denbun.of("id", spy);
    msg.suppress(false);
    msg.shown();  // update frequency

    // frequency is now 30
    verify(spy, times(1)).increment(any());
    assertThat(msg.isSuppress()).isFalse();
    assertThat(msg.isShowable()).isTrue();
    assertThat(msg.isShowable(false)).isTrue();
    assertThat(msg.isFrequency()).isFalse();
    assertThat(msg.frequency()).isEqualTo(30);

    // frequency is now 60
    msg.shown();
    assertThat(msg.isSuppress()).isFalse();
    assertThat(msg.isShowable()).isTrue();
    assertThat(msg.isShowable(false)).isTrue();
    assertThat(msg.isFrequency()).isFalse();
    assertThat(msg.frequency()).isEqualTo(60);

    // frequency is now 90
    msg.shown();
    assertThat(msg.isSuppress()).isFalse();
    assertThat(msg.isShowable()).isTrue();
    assertThat(msg.isShowable(false)).isTrue();
    assertThat(msg.isFrequency()).isFalse();
    assertThat(msg.frequency()).isEqualTo(90);

    assertThat(msg.isSuppress()).isFalse();
    assertThat(msg.isShowable()).isTrue();
    assertThat(msg.isShowable(false)).isTrue();
    assertThat(msg.isShowable(true)).isFalse();
    assertThat(msg.isFrequency()).isFalse();

    // frequency is now 100(high)
    msg.shown();
    assertThat(msg.isSuppress()).isFalse();
    assertThat(msg.isShowable()).isFalse();
    assertThat(msg.isShowable(true)).isFalse();
    assertThat(msg.isFrequency()).isTrue();
    assertThat(msg.frequency()).isEqualTo(Frequency.HIGH.value);
  }
}
