package com.yuki312.denbun.history;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Yuki312 on 2017/07/01.
 */
public class Pref {

  private final SharedPreferences pref;

  public Pref(@NonNull SharedPreferences pref) {
    this.pref = pref;
  }

  public void setInt(@NonNull String key, int value) {
    edit().putInt(key, value).apply();
  }

  public int getInt(@NonNull String key, int defValue) {
    return pref.getInt(key, defValue);
  }

  public void setLong(@NonNull String key, long value) {
    edit().putLong(key, value).apply();
  }

  public long getLong(@NonNull String key, long defValue) {
    return pref.getLong(key, defValue);
  }

  public void setFloat(@NonNull String key, float value) {
    edit().putFloat(key, value).apply();
  }

  public float getFloat(@NonNull String key, float defValue) {
    return pref.getFloat(key, defValue);
  }

  public void setString(@NonNull String key, String value) {
    edit().putString(key, value).apply();
  }

  public String getString(@NonNull String key, @Nullable String defValue) {
    return pref.getString(key, defValue);
  }

  public void setBoolean(@NonNull String key, boolean value) {
    edit().putBoolean(key, value).apply();
  }

  public boolean getBoolean(@NonNull String key, boolean defValue) {
    return pref.getBoolean(key, defValue);
  }

  private SharedPreferences.Editor edit() {
    return pref.edit();
  }
}
