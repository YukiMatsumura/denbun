package com.yuki312.denbun.time;

import android.support.annotation.NonNull;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Created by YukiMatsumura on 2017/07/12.
 */
public class TimeRule implements TestRule {

  /**
   * 現在日時を{@code value}値で固定する.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface Now {

    /**
     * 固定する日時を指定する.
     * 日時フォーマットはISO8601形式, TimeZone UTC±00:00.
     */
    String value() default "2000-01-01T00:00:00Z";
  }

  // 多重lock/unlockを検知するためのフラグ. スレッドセーフは保証しない.
  private static boolean locked = false;

  private long now;

  @Override public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override public void evaluate() throws Throwable {
        Now annotation = description.getAnnotation(Now.class);
        if (annotation == null) {
          base.evaluate();  // 現在時刻を固定しない
          return;
        }

        try {
          now = parse(annotation.value());
          lockCurrentTime(() -> now);
          base.evaluate();
        } finally {
          unlockCurrentTime();
        }
      }
    };
  }

  public void advanceTimeTo(long epochMs) {
    now = epochMs;
  }

  private void lockCurrentTime(@NonNull Time.NowProvider provider) {
    if (TimeRule.locked) {
      throw new IllegalMonitorStateException("CurrentTimeProvider is locked.");
    }
    TimeRule.locked = true;
    Time.fixedCurrentTime(provider);
  }

  private void unlockCurrentTime() {
    if (!TimeRule.locked) {
      throw new IllegalMonitorStateException("CurrentTimeProvider is unlocked.");
    }
    Time.tickCurrentTime();
    TimeRule.locked = false;
  }

  private long parse(String iso8601z) {
    try {
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:dd'Z'");
      format.setTimeZone(TimeZone.getTimeZone("UTC"));
      return format.parse(iso8601z).getTime();
    } catch (ParseException e) {
      throw new AnnotationFormatError(e);
    }
  }
}