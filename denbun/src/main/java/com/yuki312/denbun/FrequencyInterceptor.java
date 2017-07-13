package com.yuki312.denbun;

import android.support.annotation.NonNull;
import com.yuki312.denbun.state.Frequency;
import com.yuki312.denbun.state.State;

/**
 * Created by Yuki312 on 2017/07/08.
 */
public interface FrequencyInterceptor {
  Frequency increment(@NonNull State state);
}
