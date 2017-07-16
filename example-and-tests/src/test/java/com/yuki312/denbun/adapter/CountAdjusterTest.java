package com.yuki312.denbun.adapter;

import android.app.Application;
import com.yuki312.denbun.Dao;
import com.yuki312.denbun.Denbun;
import com.yuki312.denbun.DenbunConfig;
import com.yuki312.denbun.DenbunPool;
import com.yuki312.denbun.adjuster.CountAdjuster;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Yuki312 on 2017/07/17.
 */
@RunWith(RobolectricTestRunner.class)
public class CountAdjusterTest {

  private Application app = RuntimeEnvironment.application;
  private Dao dao;

  @Before public void setup() {
    DenbunPool.reset();
    DenbunConfig config = new DenbunConfig(app);
    Dao.Provider defaultDaoProvider = config.daoProvider();
    config.daoProvider(preference -> (dao = defaultDaoProvider.create(preference)));
    DenbunPool.init(config);
  }

  @Test public void basic() {
    Denbun msg = DenbunPool.find("id", new CountAdjuster(5));
    assertThat(msg.isShowable()).isTrue();

    for (int i = 0; i < 4; i++) {
      msg.shown();
      assertThat(msg.isShowable()).isTrue();
    }

    msg.shown();
    assertThat(msg.isShowable()).isFalse();
  }
}
