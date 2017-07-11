package com.yuki312.denbun;

import android.support.annotation.NonNull;
import com.yuki312.denbun.history.Frequency;

/**
 * Created by Yuki312 on 2017/07/08.
 */
public interface FrequencyAdapter {
  Frequency increment(@NonNull HistoryRecord historyRecord);
}
