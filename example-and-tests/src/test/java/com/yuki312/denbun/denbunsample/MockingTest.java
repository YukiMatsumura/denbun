package com.yuki312.denbun.denbunsample;

import android.app.Application;
import com.yuki312.denbun.BuildConfig;
import com.yuki312.denbun.Dao;
import com.yuki312.denbun.DenbunConfig;
import com.yuki312.denbun.DenbunPool;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Yuki312 on 2017/07/16.
 */
@RunWith(RobolectricTestRunner.class)
public class MockingTest {

  private Application app = RuntimeEnvironment.application;
  private Dao spyDao;

  @Before public void setup() {
    DenbunPool.reset();
  }

  @Test public void spyDenbunIO() {
    DenbunConfig conf = new DenbunConfig(app);

    // spy original DaoProvider
    Dao.Provider originalDaoProvider = conf.daoProvider();
    conf.daoProvider(pref -> (spyDao = spy(originalDaoProvider.create(pref))));
    DenbunPool.init(conf);

    DenbunPool.take("id").shown();
    verify(spyDao, times(1)).update(any());
  }
}
