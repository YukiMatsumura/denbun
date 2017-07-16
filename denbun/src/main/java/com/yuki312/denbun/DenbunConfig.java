package com.yuki312.denbun;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.yuki312.denbun.internal.DaoProvider;

import static android.content.Context.MODE_PRIVATE;
import static com.yuki312.denbun.Util.nonNull;

/**
 * Denbun system-wide configuration.
 */
public final class DenbunConfig {

  public static final String PREF_NAME = "com.yuki312.denbun.xml";

  private SharedPreferences preference;
  private DaoProvider daoProvider;

  public DenbunConfig(@NonNull Application app) {
    nonNull(app);

    // default config
    this.preference = app.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    this.daoProvider = new DaoProvider();
  }

  public DenbunConfig preference(@NonNull SharedPreferences preferences) {
    nonNull(preferences);

    this.preference = preferences;
    return this;
  }

  SharedPreferences preference() {
    return preference;
  }

  DenbunConfig daoProvider(@NonNull DaoProvider factory) {
    nonNull(factory);

    this.daoProvider = factory;
    return this;
  }

  DaoProvider daoProvider() {
    return daoProvider;
  }
}
