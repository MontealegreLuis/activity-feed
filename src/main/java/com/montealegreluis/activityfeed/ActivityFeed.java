package com.montealegreluis.activityfeed;

import com.montealegreluis.assertions.Assert;
import java.util.List;
import org.slf4j.Logger;

public final class ActivityFeed {
  private final List<ActivityRecorder> recorders;

  public static ActivityFeed withLogging(Logger logger) {
    return new ActivityFeed(List.of(new ActivityLogger(logger)));
  }

  public ActivityFeed(List<ActivityRecorder> recorders) {
    Assert.notEmpty(recorders);
    this.recorders = recorders;
  }

  public void add(Activity activity) {
    recorders.forEach(recorder -> recorder.record(activity));
  }
}
