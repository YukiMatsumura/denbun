package com.yuki312.denbun;

import com.yuki312.denbun.internal.DenbunId;
import com.yuki312.denbun.internal.PreferenceKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Yuki312 on 2017/07/16.
 */
@RunWith(RobolectricTestRunner.class)
public class DenbunIdTest {

  private final String RESERVED_WORD = PreferenceKey.reservedWord();

  @Before public void setup() {
    DenbunPool.reset();
  }

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

  @Test public void equalsTest() {
    DenbunId id1 = DenbunId.of("id");
    DenbunId id2 = DenbunId.of("id");

    assertThat(id1).isEqualTo(id1);
    assertThat(id1).isEqualTo(id2);

    DenbunId id3 = DenbunId.of("idid");

    assertThat(id1).isNotEqualTo(id3);
  }

  @Test public void hashTest() {
    DenbunId id1 = DenbunId.of("id");
    DenbunId id2 = DenbunId.of("id");

    assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
  }

  @Test public void toStringTest() {
    DenbunId id1 = DenbunId.of("my-id");
    assertThat(id1.toString()).isEqualTo("DenbunID=my-id");
  }
}
