package com.montealegreluis.activityfeed;

import static net.logstash.logback.marker.Markers.appendEntries;

import java.util.logging.Level;
import org.slf4j.Logger;

public final class ActivityFeed {
  private final Logger logger;

  public ActivityFeed(Logger logger) {
    this.logger = logger;
  }

  public void log(Activity activity) {
    if (Level.INFO.equals(activity.level())) {
      logger.info(appendEntries(activity.context()), activity.message());
    } else if (Level.WARNING.equals(activity.level())) {
      logger.warn(appendEntries(activity.context()), activity.message());
    } else if (Level.SEVERE.equals(activity.level())) {
      logger.error(appendEntries(activity.context()), activity.message());
    } else {
      logger.debug(appendEntries(activity.context()), activity.message());
    }
  }
}
