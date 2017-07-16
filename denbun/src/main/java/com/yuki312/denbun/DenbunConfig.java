package com.yuki312.denbun;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.yuki312.denbun.internal.DaoImpl;

import static android.content.Context.MODE_PRIVATE;
import static android.support.annotation.VisibleForTesting.PACKAGE_PRIVATE;
import static com.yuki312.denbun.Util.nonNull;

/**
 * Denbun system-wide configuration.
 */
public final class DenbunConfig {

  public static final String PREF_NAME = "com.yuki312.denbun.xml";

  private SharedPreferences preference;
  private Dao.Provider daoProvider;

  public DenbunConfig(@NonNull Application app) {
    nonNull(app);

    // default config
    this.preference = app.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    this.daoProvider = new Dao.Provider() {
      @Override public Dao create(@NonNull SharedPreferences preference) {
        return new DaoImpl(nonNull(preference));
      }
    };
  }

  public DenbunConfig preference(@NonNull SharedPreferences preferences) {
    nonNull(preferences);

    this.preference = preferences;
    return this;
  }

  SharedPreferences preference() {
    return preference;
  }

  @VisibleForTesting(otherwise = PACKAGE_PRIVATE)
  public DenbunConfig daoProvider(@NonNull Dao.Provider provider) {
    nonNull(provider);

    this.daoProvider = provider;
    return this;
  }

  @VisibleForTesting(otherwise = PACKAGE_PRIVATE)
  public Dao.Provider daoProvider() {
    return daoProvider;
  }
}
