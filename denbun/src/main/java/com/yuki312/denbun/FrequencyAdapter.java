package com.yuki312.denbun;

import android.support.annotation.NonNull;
import com.yuki312.denbun.history.Frequency;
import com.yuki312.denbun.History;

/**
 * Created by Yuki312 on 2017/07/08.
 */
public interface FrequencyAdapter {
  Frequency increment(@NonNull History history);
}
