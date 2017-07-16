package com.yuki312.denbunsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.yuki312.denbun.Denbun;
import com.yuki312.denbun.DenbunPool;
import com.yuki312.denbun.Frequency;

/**
 * Created by Yuki312 on 2017/07/08.
 */
public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Denbun msg = DenbunPool.take("id", state -> state.count <= 10 ? Frequency.MIN : Frequency.MAX);
    Toast t = Toast.makeText(this, "test:" + msg.count(), Toast.LENGTH_SHORT);

    if (msg.isShowable()) {
      msg.shown(t::show);
    }
  }
}
