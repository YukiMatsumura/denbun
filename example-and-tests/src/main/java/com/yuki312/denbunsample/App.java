package com.yuki312.denbunsample;

import android.app.Application;
import com.facebook.stetho.Stetho;

/**
 * Created by Yuki312 on 2017/07/08.
 */

public class App extends Application {
  @Override public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);
  }
}
