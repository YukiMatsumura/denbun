package com.yuki312.denbun;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.yuki312.denbun.internal.CoreProvider;

import static android.content.Context.MODE_PRIVATE;
import static com.yuki312.denbun.Util.nonNull;

/**
 * Denbun system-wide configuration.
 */
public final class DenbunConfig {

  public static final String PREF_NAME = "com.yuki312.denbun.xml";

  private SharedPreferences preference;
  private CoreProvider coreProvider;

  public DenbunConfig(@NonNull Application app) {
    nonNull(app);

    // default config
    this.preference = app.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    this.coreProvider = new CoreProvider();
  }

  public DenbunConfig preference(@NonNull SharedPreferences preferences) {
    nonNull(preferences);

    this.preference = preferences;
    return this;
  }

  SharedPreferences preference() {
    return preference;
  }

  DenbunConfig coreProvider(@NonNull CoreProvider factory) {
    nonNull(factory);

    this.coreProvider = factory;
    return this;
  }

  CoreProvider coreProvider() {
    return coreProvider;
  }
}
