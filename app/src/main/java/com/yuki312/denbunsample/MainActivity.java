package com.yuki312.denbunsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.yuki312.denbun.Denbun;
import com.yuki312.denbun.DenbunConfig;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    DenbunConfig config = new DenbunConfig(getApplication());
    Denbun.init(config);

    Denbun msg = Denbun.of("hoge");
    Log.e("TEST", "suppress= " + msg.isSuppress());
    Log.e("TEST", "frequency= " + msg.isFrequency());
    Log.e("TEST", "prevtime= " + msg.previousTime());

    msg.shown();
  }
}
