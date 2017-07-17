package com.yuki312.denbun.internal;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.yuki312.denbun.DenbunPool;
import com.yuki312.denbun.Frequency;
import com.yuki312.denbun.State;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static com.yuki312.denbun.internal.PreferenceKey.Count;
import static com.yuki312.denbun.internal.PreferenceKey.Freq;
import static com.yuki312.denbun.internal.PreferenceKey.Recent;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by Yuki312 on 2017/07/16.
 */
@RunWith(RobolectricTestRunner.class)
public class RecordTest {

  private Application app = RuntimeEnvironment.application;

  @Before public void setup() {
    DenbunPool.reset();
  }

  @Test public void preferenceKeyFormat() {
    for (PreferenceKey k : PreferenceKey.values()) {
      assertThat(k.SUFFIX.startsWith(PreferenceKey.reservedWord())).isTrue();
    }
  }

  @Test public void readCustomPreference() {
    DaoImpl dao =
        new DaoImpl(app.getSharedPreferences("readCustomPreference.xml", Context.MODE_PRIVATE));
    DenbunId id = DenbunId.of("id");
    State state = dao.find(id);

    // read non saved data.
    assertThat(state.frequency).isEqualTo(Frequency.MIN);
    assertThat(state.recent).isEqualTo(0L);
    assertThat(state.count).isEqualTo(0);
    assertThat(state.isSuppress()).isFalse();
    assertThat(state.isShowable()).isTrue();

    // update and reload
    dao.update(new State(
        id,
        Frequency.MAX,
        100L,
        2));
    state = dao.find(id);

    assertThat(state.frequency).isEqualTo(Frequency.MAX);
    assertThat(state.recent).isEqualTo(100L);
    assertThat(state.count).isEqualTo(2);
    assertThat(state.isSuppress()).isTrue();
    assertThat(state.isShowable()).isFalse();

    // read preference directly
    SharedPreferences pref =
        app.getSharedPreferences("readCustomPreference.xml", Context.MODE_PRIVATE);
    assertThat(pref.getInt(Freq.of(id), -1)).isEqualTo(100);
    assertThat(pref.getLong(Recent.of(id), -1)).isEqualTo(100L);
    assertThat(pref.getInt(Count.of(id), -1)).isEqualTo(2);
  }
}
