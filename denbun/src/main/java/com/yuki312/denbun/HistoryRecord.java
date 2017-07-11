package com.yuki312.denbun;

import com.yuki312.denbun.history.Frequency;

/**
 * Created by Yuki312 on 2017/07/08.
 */
public interface HistoryRecord {

  boolean suppress();

  Frequency frequency();

  public long previousTime();
}
