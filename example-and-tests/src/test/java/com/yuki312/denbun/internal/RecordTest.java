package com.yuki312.denbun.internal;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.yuki312.denbun.BuildConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static com.yuki312.denbun.internal.Record.Key.Count;
import static com.yuki312.denbun.internal.Record.Key.Freq;
import static com.yuki312.denbun.internal.Record.Key.Recent;
import static com.yuki312.denbun.internal.Record.RESERVED_WORD;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Yuki312 on 2017/07/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RecordTest {

  private Application app = RuntimeEnvironment.application;

  @Test(expected = IllegalArgumentException.class) public void reservedId1() {
    DenbunId.of(RESERVED_WORD);
  }

  @Test(expected = IllegalArgumentException.class) public void reservedId2() {
    DenbunId.of("a" + RESERVED_WORD);
  }

  @Test(expected = IllegalArgumentException.class) public void reservedId3() {
    DenbunId.of(RESERVED_WORD + "a");
  }

  @Test public void nonReservedId() {
    DenbunId.of(RESERVED_WORD.substring(1));
    DenbunId.of("a" + RESERVED_WORD.substring(1));
    DenbunId.of(RESERVED_WORD.substring(1) + "a");
  }

  @Test public void readCustomPreference() {
    Dao dao = new Dao(app.getSharedPreferences("readCustomPreference.xml", Context.MODE_PRIVATE));
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
