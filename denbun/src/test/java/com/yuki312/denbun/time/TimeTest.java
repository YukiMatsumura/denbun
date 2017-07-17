package com.yuki312.denbun.time;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by Yuki312 on 2017/07/16.
 */
public class TimeTest {


  @Test public void forCoverage() throws NoSuchMethodException, IllegalAccessException,
      InvocationTargetException, InstantiationException {
    Constructor<Time> constructor = Time.class.getDeclaredConstructor();
    assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
    constructor.setAccessible(true);
    constructor.newInstance();
  }
}
