package com.yuki312.denbun;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.yuki312.denbun.adjuster.CountAdjuster;
import com.yuki312.denbun.adjuster.FrequencyAdjuster;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by Yuki312 on 2017/07/16.
 */
@RunWith(RobolectricTestRunner.class)
public class DenbunBoxTest {

  private Application app = RuntimeEnvironment.application;
  private DenbunConfig config;

  @Before public void setup() {
    DenbunBox.reset();
    config = new DenbunConfig(app);
  }

  @After public void teardown() {
    DenbunBox.reset();
  }

  @Test public void create() {
    DenbunBox.init(config);
    Denbun msg = DenbunBox.get("id");
  }

  @Test(expected = NullPointerException.class) public void initNull() {
    DenbunBox.init(null);
  }

  @Test(expected = NullPointerException.class) public void nullPreference() {
    config.preference(null);
  }

  @Test(expected = IllegalStateException.class) public void notInitializedGet() {
    Denbun msg = DenbunBox.get("id");
  }

  @Test(expected = IllegalStateException.class) public void notInitializedPresetSingle() {
    DenbunBox.preset("id", new CountAdjuster(1));
  }

  @Test(expected = IllegalStateException.class) public void notInitializedPresetMulti() {
    Map<String, FrequencyAdjuster> map = new HashMap<>();
    map.put("id", new CountAdjuster(1));
    DenbunBox.preset(map);
  }

  @Test public void initTwice() {
    DenbunBox.init(config);
    DenbunBox.init(config);
  }

  @Test(expected = IllegalArgumentException.class) public void nullId() {
    DenbunBox.init(config);
    Denbun msg = DenbunBox.get(null);
  }

  @Test(expected = IllegalArgumentException.class) public void blankId() {
    DenbunBox.init(config);
    Denbun msg = DenbunBox.get("");
  }

  @Test public void defaultPreference() {
    SharedPreferences pref = app.getSharedPreferences(DenbunConfig.PREF_NAME, Context.MODE_PRIVATE);
    DenbunBox.init(config);

    assertThat(pref.getAll().isEmpty()).isTrue();

    DenbunBox.get("id").shown();

    assertThat(pref.getAll().isEmpty()).isFalse();
  }

  @Test public void setCustomPreference() {
    SharedPreferences pref = app.getSharedPreferences("custom.xml", Context.MODE_PRIVATE);
    config.preference(pref);
    DenbunBox.init(config);

    assertThat(pref.getAll().isEmpty()).isTrue();

    DenbunBox.get("id").shown();

    assertThat(pref.getAll().isEmpty()).isFalse();
  }

  @Test public void notRecycle() {
    DenbunBox.init(config);

    Denbun msg1 = DenbunBox.get("id");
    Denbun msg2 = DenbunBox.get("id");

    assertThat(msg1).isNotEqualTo(msg2);
  }

  @Test public void removeId() {
    SharedPreferences pref = config.preference();
    DenbunBox.init(config);

    assertThat(pref.getAll().isEmpty()).isTrue();

    Denbun msg = DenbunBox.get("id");
    msg.shown();

    assertThat(pref.getAll().isEmpty()).isFalse();

    DenbunBox.remove("id");

    assertThat(pref.getAll().isEmpty()).isTrue();
  }

  @Test public void exist() {
    SharedPreferences pref = config.preference();
    DenbunBox.init(config);

    assertThat(pref.getAll().isEmpty()).isTrue();

    Denbun msg = DenbunBox.get("id");
    msg.shown();

    assertThat(pref.getAll().isEmpty()).isFalse();
    assertThat(DenbunBox.exist("id")).isTrue();
  }

  @Test public void setMultiFrequencyAdapter() {
    DenbunBox.init(config);

    Denbun msg1 = DenbunBox.get("id", new FrequencyAdjuster() {
      @Override public Frequency increment(@NonNull State state) {
        return Frequency.MAX;
      }
    });
    Denbun msg2 = DenbunBox.get("id", new FrequencyAdjuster() {
      @Override public Frequency increment(@NonNull State state) {
        return Frequency.MIN;
      }
    });

    assertThat(msg1.isShowable()).isFalse();
    assertThat(msg2.isShowable()).isTrue();
  }

  @Test public void presetSingle() {
    DenbunBox.init(config);

    DenbunBox.preset("id", new FrequencyAdjuster() {
      @Override public Frequency increment(@NonNull State state) {
        return Frequency.MAX;
      }
    });

    Denbun msg = DenbunBox.get("id");
    assertThat(msg.isShowable()).isFalse();
  }

  @Test public void presetMulti() {
    DenbunBox.init(config);

    Map<String, FrequencyAdjuster> map = new HashMap<>();
    map.put("id", new FrequencyAdjuster() {
      @Override public Frequency increment(@NonNull State state) {
        return Frequency.MAX;
      }
    });
    DenbunBox.preset(map);

    Denbun msg = DenbunBox.get("id");
    assertThat(msg.isShowable()).isFalse();
  }

  @Test public void presetMultiEmpty() {
    DenbunBox.init(config);

    Map<String, FrequencyAdjuster> map = new HashMap<>();
    DenbunBox.preset(map);

    Denbun msg = DenbunBox.get("id");
    assertThat(msg.isShowable()).isTrue();
  }

  @Test public void forCoverage()
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
      InstantiationException {
    Constructor<DenbunBox> constructor = DenbunBox.class.getDeclaredConstructor();
    assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
    constructor.setAccessible(true);
    constructor.newInstance();
  }
}
