package com.yuki312.denbun;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.yuki312.denbun.history.Pref;

import static android.content.Context.MODE_PRIVATE;
import static com.yuki312.denbun.Util.nonNull;

/**
 * Denbun system-wide configuration.
 */
public class DenbunConfig {

  private Pref pref;

  public DenbunConfig(@NonNull Application app) {
    nonNull(app);

    // default config
    this.pref = new Pref(app.getSharedPreferences(Pref.PREF_NAME, MODE_PRIVATE));
  }

  public DenbunConfig preference(@NonNull SharedPreferences preferences) {
    nonNull(preferences);

    this.pref = new Pref(preferences);
    return this;
  }

  public Pref preference() {
    return pref;
  }
}
