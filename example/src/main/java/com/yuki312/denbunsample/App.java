package com.yuki312.denbunsample;

import android.app.Application;
import com.facebook.stetho.Stetho;
import com.yuki312.denbun.DenbunBox;
import com.yuki312.denbun.DenbunConfig;

/**
 * Created by Yuki312 on 2017/07/08.
 */
public class App extends Application {
  @Override public void onCreate() {
    super.onCreate();
    initStetho();
    initDenbun();
  }

  protected void initStetho() {
    Stetho.initializeWithDefaults(this);
  }

  protected void initDenbun() {
    DenbunBox.init(new DenbunConfig(this));
  }
}
