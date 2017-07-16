package com.yuki312.denbun;

import android.app.Application;
import android.support.annotation.NonNull;
import com.yuki312.denbun.adjuster.FrequencyAdjuster;
import com.yuki312.denbun.internal.DenbunId;
import com.yuki312.denbun.time.Time;
import com.yuki312.denbun.time.TimeRule;
import com.yuki312.denbun.time.TimeRule.Now;
import java.util.Calendar;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.threeten.bp.Instant;
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
public class DenbunTest {

  @Rule public TimeRule timeRule = new TimeRule();

  private Application app = RuntimeEnvironment.application;
  private DenbunConfig config;
  private Dao dao;

  private void preset(DenbunId id, int frequency, long recent, int count) {
    dao.update(new State(id, Frequency.of(frequency), recent, count));
  }

  @Before public void setup() {
    DenbunPool.reset();
    config = new DenbunConfig(app);
    Dao.Provider defaultDaoProvider = config.daoProvider();
    config.daoProvider(preference -> (dao = defaultDaoProvider.create(preference)));
  }

  @Test public void defaultState() {
    DenbunPool.init(config);

    Denbun msg = DenbunPool.take("id");

    assertThat(msg.id()).isEqualTo("id");
    assertThat(msg.isSuppress()).isFalse();
    assertThat(msg.recent()).isEqualTo(0L);
    assertThat(msg.count()).isEqualTo(0);
    assertThat(msg.isShowable()).isTrue();
  }

  @Test @Now public void updateState() {
    DenbunPool.init(config);
    FrequencyAdjuster spy = spy(new FrequencyAdjuster() {
      @Override public Frequency increment(@NonNull State state) {
        return state.frequency;  // no-op
      }
    });
    timeRule.advanceTimeTo(100L);

    Denbun msg = DenbunPool.take("id", spy)
        .suppress(true)
        .shown();

    assertThat(msg.isSuppress()).isTrue();
    assertThat(msg.recent()).isEqualTo(100L);
    assertThat(msg.count()).isEqualTo(1);
  }

  @Test public void incrementFrequency() {
    DenbunPool.init(config);
    FrequencyAdjuster spy = spy(new FrequencyAdjuster() {
      @Override public Frequency increment(@NonNull State history) {
        return history.frequency.plus(30);
      }
    });
    Denbun msg = DenbunPool.take("id", spy)
        .suppress(false);

    msg.shown();  // frequency is now 30
    verify(spy, times(1)).increment(any());
    assertThat(dao.find(DenbunId.of("id")).frequency.value).isEqualTo(30);
    assertThat(msg.isSuppress()).isFalse();
    assertThat(msg.isShowable()).isTrue();

    msg.shown();  // frequency is now 60
    verify(spy, times(3)).increment(any());  // increment(..) was called by isShowable() and shown()
    assertThat(dao.find(DenbunId.of("id")).frequency.value).isEqualTo(60);
    assertThat(msg.isSuppress()).isFalse();
    assertThat(msg.isShowable()).isTrue();

    msg.shown();  // frequency is now 90
    verify(spy, times(5)).increment(any());
    assertThat(dao.find(DenbunId.of("id")).frequency.value).isEqualTo(90);
    assertThat(msg.isSuppress()).isFalse();
    assertThat(msg.isShowable()).isFalse();

    msg.shown();  // frequency is now 100(high)
    verify(spy, times(7)).increment(any());
    assertThat(dao.find(DenbunId.of("id")).frequency.value).isEqualTo(Frequency.MAX.value);
    assertThat(msg.isSuppress()).isTrue();
    assertThat(msg.isShowable()).isFalse();
  }

  @Test public void resetFrequency() {
    DenbunPool.init(config);
    Denbun msg = DenbunPool.take("id")
        .suppress(true);
    assertThat(msg.isSuppress()).isTrue();

    msg.suppress(false);
    assertThat(msg.isSuppress()).isFalse();
  }

  @Test @Now public void showablePerDay() {
    DenbunPool.init(config);
    long now = System.currentTimeMillis();
    timeRule.advanceTimeTo(now);

    FrequencyAdjuster freq = new FrequencyAdjuster() {
      @Override public Frequency increment(@NonNull State state) {
        LocalDateTime now =
            OffsetDateTime.ofInstant(Instant.ofEpochMilli(Time.now()), ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime recent =
            OffsetDateTime.ofInstant(Instant.ofEpochMilli(state.recent), ZoneId.systemDefault())
                .toLocalDateTime();

        if (recent.isBefore(yesterday) || recent.isEqual(yesterday)) {
          return Frequency.MIN;
        } else {
          return Frequency.MAX;
        }
      }
    };

    Denbun msg = DenbunPool.take("id", freq);
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

    FrequencyAdjuster freq = new FrequencyAdjuster() {
      @Override public Frequency increment(@NonNull State state) {
        return state.frequency.plus(Frequency.MAX.value / 3);
      }
    };

    Denbun msg = DenbunPool.take("id", freq);
    assertThat(msg.isShowable()).isTrue();
    msg.shown();

    assertThat(msg.isShowable()).isTrue();
    msg.shown();

    assertThat(msg.isShowable()).isTrue();
    msg.shown();

    assertThat(msg.isShowable()).isFalse();
  }

  @Test public void showingAction() {
    DenbunPool.init(config);

    Denbun.Action action = spy(new Denbun.Action() {
      @Override public void call() {
      }
    });
    Denbun msg = DenbunPool.take("id");

    msg.shown(action);

    verify(action, times(1)).call();
  }
}
