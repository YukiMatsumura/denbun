package com.yuki312.denbunsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.yuki312.denbun.Denbun;
import com.yuki312.denbun.DenbunConfig;
import com.yuki312.denbun.DenbunPool;
import com.yuki312.denbun.internal.Frequency;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Denbun msg1 = DenbunPool.take("id");
    msg1.shown(() -> Toast.makeText(this, "TEST", Toast.LENGTH_SHORT).show());

    Denbun msg2 = DenbunPool.take("id2",
        state -> state.count == 0 ? Frequency.MAX : Frequency.MIN);
  }
}
