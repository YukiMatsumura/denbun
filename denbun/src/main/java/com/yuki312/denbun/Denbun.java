package com.yuki312.denbun;

import android.support.annotation.NonNull;
import com.yuki312.denbun.internal.Dao;
import com.yuki312.denbun.internal.Frequency;
import com.yuki312.denbun.internal.State;
import com.yuki312.denbun.time.Time;

import static com.yuki312.denbun.Util.nonNull;
import static com.yuki312.denbun.Util.notBlank;

/**
 * Created by Yuki312 on 2017/07/03.
 */
public class Denbun {

  public interface Action {
    void call();
  }

  public static FrequencyAdjuster DEFAULT_FREQUENCY_ADAPTER = new FrequencyAdjuster() {
    @Override public Frequency increment(@NonNull State state) {
      // The default behavior is to always return the same value.
      return state.frequency;
    }
  };

  private final Dao dao;
  private final String id;
  private final FrequencyAdjuster adjuster;

  private State state;

  private Denbun(@NonNull String id, @NonNull FrequencyAdjuster adjuster, @NonNull Dao dao) {
    this.id = nonNull(id);
    this.adjuster = nonNull(adjuster);
    this.dao = nonNull(dao);
    this.state = dao.find(id);
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
    return state.frequency.isLimited();
  }

  /**
   * このメッセージが最後に表示された日時.
   */
  public long recent() {
    return state.recent;
  }

  /**
   * メッセージの表示回数を取得する
   */
  public int count() {
    return state.count;
  }

  /**
   * メッセージを抑制する
   */
  public Denbun suppress(boolean suppress) {
    Frequency freq = (suppress ? Frequency.MAX : Frequency.MIN);
    updateState(new State(state.id, freq, state.recent, state.count));
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
    return new State(state.id,
        adjuster.increment(state), state.recent, state.count).isShowable();
  }

  /**
   * メッセージを表示したことを通知する.
   */
  public Denbun shown() {
    updateState(new State(
        state.id,
        adjuster.increment(state),
        Time.now(),
        state.count + 1));
    return this;
  }

  public Denbun shown(Action action) {
    action.call();
    return shown();
  }

  private void updateState(State newState) {
    this.state = dao.update(newState);
  }

  /*
   * Denbun Builder.
   */
  static class Builder {

    private final String id;
    private FrequencyAdjuster adjuster;
    private Dao core;

    Builder(@NonNull String id) {
      this.id = notBlank(id);
    }

    Builder dao(@NonNull Dao core) {
      this.core = nonNull(core);
      return this;
    }

    Builder adjuster(@NonNull FrequencyAdjuster adjuster) {
      this.adjuster = nonNull(adjuster);
      return this;
    }

    Denbun build() {
      notBlank(id);
      nonNull(adjuster);
      nonNull(core);
      return new Denbun(id, adjuster, core);
    }
  }
}
