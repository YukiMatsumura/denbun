package com.yuki312.denbun;

import android.support.annotation.NonNull;
import com.yuki312.denbun.core.Frequency;
import com.yuki312.denbun.core.State;

/**
 * Created by Yuki312 on 2017/07/08.
 */
public interface FrequencyAdjuster {
  Frequency increment(@NonNull State state);
}
