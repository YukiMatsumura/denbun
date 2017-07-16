package com.yuki312.denbun.adapter;

import android.app.Application;
import com.yuki312.denbun.Dao;
import com.yuki312.denbun.Denbun;
import com.yuki312.denbun.DenbunConfig;
import com.yuki312.denbun.DenbunPool;
import com.yuki312.denbun.Frequency;
import com.yuki312.denbun.State;
import com.yuki312.denbun.adjuster.CooldownAdjuster;
import com.yuki312.denbun.adjuster.IntervalAdjuster;
import com.yuki312.denbun.internal.DenbunId;
import com.yuki312.denbun.time.TimeRule;
import com.yuki312.denbun.time.TimeRule.Now;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Yuki312 on 2017/07/17.
 */
@RunWith(RobolectricTestRunner.class)
public class CooldownAdjusterTest {

  @Rule public TimeRule timeRule = new TimeRule();

  private Application app = RuntimeEnvironment.application;
  private Dao dao;

  @Before public void setup() {
    DenbunPool.reset();
    DenbunConfig config = new DenbunConfig(app);
    Dao.Provider defaultDaoProvider = config.daoProvider();
    config.daoProvider(preference -> (dao = defaultDaoProvider.create(preference)));
    DenbunPool.init(config);
  }

  private void preset(String id, int frequency, long recent, int count) {
    dao.update(new State(DenbunId.of(id), Frequency.of(frequency), recent, count));
  }

  @Test @Now public void basic() {
    timeRule.advanceTimeTo(100L);
    preset("id", Frequency.MAX.value, 100L, 1);

    Denbun msg = DenbunPool.find("id", new CooldownAdjuster(1f / 3f, 100L, TimeUnit.MILLISECONDS));
    assertThat(msg.isShowable()).isFalse();

    timeRule.advanceTimeTo(199L);
    assertThat(msg.isShowable()).isFalse();

    timeRule.advanceTimeTo(200L);
    assertThat(msg.isShowable()).isTrue();

    msg.shown();  // saved recent to 200 and Frequency 0.333...

    assertThat(msg.isShowable()).isTrue();

    timeRule.advanceTimeTo(210L);

    msg.shown();  // saved recent to 210 and Frequency 0.666...

    assertThat(msg.isShowable()).isTrue();

    timeRule.advanceTimeTo(220L);

    msg.shown();  // saved recent to 220 and Frequency 0.999...

    assertThat(msg.isShowable()).isFalse();

    timeRule.advanceTimeTo(320L);  // interval grater than eq. 100

    assertThat(msg.isShowable()).isTrue();
  }
}
