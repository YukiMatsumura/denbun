package com.yuki312.denbun;

import com.yuki312.denbun.internal.DenbunId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by Yuki312 on 2017/07/17.
 */
@RunWith(RobolectricTestRunner.class)
public class StateTest {

  @Test public void toStringTest() {
    String s = new State(DenbunId.of("id"), Frequency.MIN, 0L, 0).toString();
  }
}
