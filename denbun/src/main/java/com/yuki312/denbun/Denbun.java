package com.yuki312.denbun;

import android.support.annotation.NonNull;
import com.yuki312.denbun.adjuster.FrequencyAdjuster;
import com.yuki312.denbun.internal.DenbunId;
import com.yuki312.denbun.time.Time;

import static com.yuki312.denbun.Util.nonNull;

/**
 * Denbun("Message").
 * This state is saved/restored to/from Preference.
 * And Denbun instance will cached in DenbunPool and recycled.
 *
 * Created by Yuki312 on 2017/07/03.
 */
public class Denbun {

  /**
   * If you want to take action on Denbun.show(), you can use this interface.
   */
  public interface ShowingAction {

    /**
     * Called *before* denbun.show() method calling.
     * This means that the state of Denbun is not yet updated.
     */
    void call();
  }

  public static FrequencyAdjuster DEFAULT_FREQUENCY_ADAPTER = new FrequencyAdjuster() {
    @Override public Frequency increment(@NonNull State state) {
      // The default behavior is to always return the same value.
      return state.frequency;
    }
  };

  private final Dao dao;
  private final DenbunId id;
  private final FrequencyAdjuster adjuster;

  private State state;

  private Denbun(@NonNull DenbunId id, @NonNull FrequencyAdjuster adjuster, @NonNull Dao dao) {
    this.id = nonNull(id);
    this.adjuster = nonNull(adjuster);
    this.dao = nonNull(dao);
    this.state = dao.find(id);
  }

  @NonNull public String id() {
    return id.value;
  }

  /**
   * Whether to display is suppressed or not.
   * It is also suppressed when message display frequency is high.
   *
   * @return true: suppressed. false: not suppressed.
   */
  public boolean isSuppress() {
    return state.frequency.isLimited();
  }

  /**
   * Last displayed time of message.
   *
   * @return the difference, measured in milliseconds, between the current time and midnight,
   * January 1, 1970 UTC.
   */
  public long recent() {
    return state.recent;
  }

  /**
   * Display count of message.
   */
  public int count() {
    return state.count;
  }

  /**
   * Suppress message.
   * As a result, the display frequency is highest.
   */
  public Denbun suppress(boolean suppress) {
    Frequency freq = (suppress ? Frequency.MAX : Frequency.MIN);
    updateState(state.frequency(freq));
    return this;
  }

  /**
   * Check if this message can be displayed.
   * Check as to whether it can be displayed is performed after the display frequency of the
   * message is evaluated.
   * In other words, this method returns false if it is judged that the display frequency is
   * excessive by displaying a message.
   *
   * @return true: can be displayed. false: can not.
   */
  public boolean isShowable() {
    return state.frequency(adjuster.increment(state)).isShowable();
  }

  /**
   * Record that the message was displayed.
   */
  public Denbun shown() {
    updateState(new State(
        state.id,
        adjuster.increment(state),
        Time.now(),
        state.count + 1));
    return this;
  }

  /**
   * Record that the message was displayed.
   * ShowingAction is called before being recorded.
   */
  public Denbun shown(ShowingAction action) {
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

    private final DenbunId id;
    private FrequencyAdjuster adjuster;
    private Dao dao;

    Builder(@NonNull DenbunId id) {
      this.id = nonNull(id);
    }

    Builder dao(@NonNull Dao dao) {
      this.dao = nonNull(dao);
      return this;
    }

    Builder adjuster(@NonNull FrequencyAdjuster adjuster) {
      this.adjuster = nonNull(adjuster);
      return this;
    }

    Denbun build() {
      nonNull(id, "Denbun ID can not be null");
      nonNull(adjuster, "FrequencyAdjuster can not be null");
      nonNull(dao, "DenbunPool has no DAO. initialized DenbunPool?");
      return new Denbun(id, adjuster, dao);
    }
  }
}
