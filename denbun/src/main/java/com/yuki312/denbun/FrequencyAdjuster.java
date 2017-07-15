package com.yuki312.denbun;

import android.support.annotation.NonNull;
import com.yuki312.denbun.internal.Frequency;
import com.yuki312.denbun.internal.State;

/**
 * Created by Yuki312 on 2017/07/08.
 */
public interface FrequencyAdjuster {
  Frequency increment(@NonNull State state);
}
