package com.yuki312.denbun;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by Yuki312 on 2017/07/16.
 */
@RunWith(JUnit4.class)
public class UtilTest {

  @Test(expected = NullPointerException.class)
  public void nonNull() {
    Util.nonNull(null);
  }

  @Test public void nonNullArg() {
    try {
      Util.nonNull(null, "test");
      fail();
    } catch (NullPointerException e) {
      assertThat(e.getMessage()).isEqualTo("test");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void notBlank() {
    Util.notBlank(null);
  }

  @Test public void notBlankArg() {
    try {
      Util.notBlank(null, "test");
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).isEqualTo("test");
    }
  }

  @Test public void forCoverage() throws NoSuchMethodException, IllegalAccessException,
      InvocationTargetException, InstantiationException {
    Constructor<Util> constructor = Util.class.getDeclaredConstructor();
    assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
    constructor.setAccessible(true);
    constructor.newInstance();
  }
}
