package com.yuki312.denbun;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.yuki312.denbun.internal.DenbunId;

import static android.support.annotation.VisibleForTesting.PACKAGE_PRIVATE;

/**
 * Data Access Object for Preference with stored Denbun.
 *
 * Created by Yuki312 on 2017/07/16.
 */
public interface Dao {

  /**
   * Search Denbun matching the DenbunID.
   * This method is returned default State when DenbunId is not exist.
   */
  @NonNull State find(@NonNull DenbunId id);

  /**
   * Save the argument state to preferences.
   * Preference of the same DenbunID already stored is overwritten.
   */
  @NonNull State update(@NonNull State state);

  /**
   * Delete Denbun matching the DenbunID.
   *
   * @return true: delete successful. false: otherwise.
   */
  boolean delete(@NonNull DenbunId id);

  /**
   * Search Denbun matching the DenbunID.
   *
   * @return true: id exist. false: not exist.
   */
  boolean exist(@NonNull DenbunId id);

  /**
   * Dao instance provider.
   * This provider class is prepared to DAO injection for testing.
   */
  @VisibleForTesting(otherwise = PACKAGE_PRIVATE)
  interface Provider {

    /**
     * Dao factory method.
     * If you want to know the how injecting DAO, you can refer to the test code.
     *
     * @param preference Preference to save message
     */
    Dao create(@NonNull SharedPreferences preference);
  }
}
