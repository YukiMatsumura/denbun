package com.yuki312.denbun;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.yuki312.denbun.state.DenbunCore;
import com.yuki312.denbun.state.Frequency;

import static com.yuki312.denbun.Util.nonNull;
import static com.yuki312.denbun.Util.notBlank;

/**
 * Created by Yuki312 on 2017/07/03.
 */
public class Denbun {

  private final String id;
  private final DenbunCore core;

  private Denbun(@NonNull String id, @NonNull DenbunCore core) {
    this.id = nonNull(id);
    this.core = nonNull(core);
  }

  @NonNull public String id() {
    return id;
  }

  /**
   * このメッセージが表示を制限されているか否か.
   *
   * 当分の間, メッセージの表示を抑制したい場合などにこのフラグは使用できる.
   */
  public boolean isSuppress() {
    return core.state().suppress;
  }

  /**
   * このメッセージが過度に表示されているか否か.
   *
   * 短時間の間に複数回メッセージが表示されたかを判断したい場合などにこのフラグは使用できる.
   */
  public boolean isFrequency() {
    return core.state().frequency.isHigh();
  }

  /**
   * このメッセージが最後に表示された日時.
   */
  public long recent() {
    return core.state().recent;
  }

  /**
   * このメッセージが表示制限状態であるか.
   *
   * @param suppress 表示を制限する場合はtrue, それ以外はfalse.
   */
  public Denbun suppress(boolean suppress) {
    core.suppress(suppress);
    return this;
  }

  /**
   *
   * @return
   */
  public Denbun clearFrequency() {
    core.frequency(Frequency.LOW);
    return this;
  }

  /**
   * このメッセージが表示可能かどうかを確認する.
   * 表示可能性は, メッセージの表示頻度が評価された上で算出される.
   * つまり, このメッセージを表示することによって表示頻度が過度であると判断される場合はfalseを返す.
   *
   * @return 表示可能であればtrue, それ以外はfalse.
   */
  public boolean isShowable() {
    return core.showable();
  }

  /**
   * メッセージを表示したことを通知する.
   */
  public Denbun shown() {
    core.show();
    return this;
  }

  /**
   *
   * @param interceptor
   * @return
   */
  public Denbun frequencyInterceptor(@Nullable FrequencyInterceptor interceptor) {
    core.frequencyInterceptor(interceptor);
    return this;
  }

  /*
   * Denbun Builder.
   */
  static class Builder {

    private final String id;
    private DenbunCore core;

    Builder(@NonNull String id) {
      this.id = notBlank(id);
    }

    Builder history(@NonNull DenbunCore core) {
      this.core = nonNull(core);
      return this;
    }

    Denbun build() {
      notBlank(id);
      nonNull(core);
      return new Denbun(id, core);
    }
  }
}
