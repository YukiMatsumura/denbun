package com.yuki312.denbunsample;

import android.app.Application;
import com.facebook.stetho.Stetho;
import com.yuki312.denbun.DenbunConfig;
import com.yuki312.denbun.DenbunPool;

/**
 * Created by Yuki312 on 2017/07/08.
 */

public class App extends Application {
  @Override public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);

    DenbunPool.init(new DenbunConfig(this));
  }
}
