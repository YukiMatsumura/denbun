package com.yuki312.denbun;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Yuki312 on 2017/07/16.
 */
@RunWith(RobolectricTestRunner.class)
public class DenbunPoolTest {

  private Application app = RuntimeEnvironment.application;
  private DenbunConfig config;

  @Before public void setup() {
    DenbunPool.reset();
    config = new DenbunConfig(app);
  }

  @After public void teardown() {
    DenbunPool.reset();
  }

  @Test public void create() {
    DenbunPool.init(config);
    Denbun msg = DenbunPool.find("id");
  }

  @Test(expected = NullPointerException.class) public void initNull() {
    DenbunPool.init(null);
  }

  @Test(expected = NullPointerException.class) public void nullPreference() {
    config.preference(null);
  }

  @Test(expected = IllegalStateException.class) public void notInitialized() {
    Denbun msg = DenbunPool.find("id");
  }

  @Test public void initTwice() {
    DenbunPool.init(config);
    DenbunPool.init(config);
  }

  @Test(expected = NullPointerException.class) public void nullId() {
    DenbunPool.init(config);
    Denbun msg = DenbunPool.find(null);
  }

  @Test(expected = IllegalArgumentException.class) public void blankId() {
    DenbunPool.init(config);
    Denbun msg = DenbunPool.find("");
  }

  @Test public void defaultPreference() {
    SharedPreferences pref = app.getSharedPreferences(DenbunConfig.PREF_NAME, Context.MODE_PRIVATE);
    DenbunPool.init(config);

    assertThat(pref.getAll().isEmpty()).isTrue();

    DenbunPool.find("id").shown();

    assertThat(pref.getAll().isEmpty()).isFalse();
  }

  @Test public void setCustomPreference() {
    SharedPreferences pref = app.getSharedPreferences("custom.xml", Context.MODE_PRIVATE);
    config.preference(pref);
    DenbunPool.init(config);

    assertThat(pref.getAll().isEmpty()).isTrue();

    DenbunPool.find("id").shown();

    assertThat(pref.getAll().isEmpty()).isFalse();
  }

  @Test public void recycle() {
    DenbunPool.init(config);

    Denbun msg1 = DenbunPool.find("id");
    Denbun msg2 = DenbunPool.find("id");

    assertThat(msg1).isEqualTo(msg2);
  }

  @Test public void removeId() {
    SharedPreferences pref = config.preference();
    DenbunPool.init(config);

    assertThat(pref.getAll().isEmpty()).isTrue();

    Denbun msg = DenbunPool.find("id");
    msg.shown();

    assertThat(pref.getAll().isEmpty()).isFalse();

    DenbunPool.remove("id");

    assertThat(pref.getAll().isEmpty()).isTrue();
  }

  @Test public void exist() {
    SharedPreferences pref = config.preference();
    DenbunPool.init(config);

    assertThat(pref.getAll().isEmpty()).isTrue();

    Denbun msg = DenbunPool.find("id");
    msg.shown();

    assertThat(pref.getAll().isEmpty()).isFalse();
    assertThat(DenbunPool.exist("id")).isTrue();
  }

  @Test public void forCoverage() throws NoSuchMethodException, IllegalAccessException,
      InvocationTargetException, InstantiationException {
    Constructor<DenbunPool> constructor = DenbunPool.class.getDeclaredConstructor();
    assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
    constructor.setAccessible(true);
    constructor.newInstance();
  }
}
