package com.montealegreluis.activityfeed;

import static net.logstash.logback.marker.Markers.appendEntries;

import java.util.logging.Level;
import org.slf4j.Logger;

public final class ActivityFeed {
  private final Logger logger;

  public ActivityFeed(Logger logger) {
    this.logger = logger;
  }

  public void record(Activity activity) {
    if (Level.INFO.equals(activity.level()) && logger.isInfoEnabled()) {
      logger.info(appendEntries(activity.context()), activity.message());
    } else if (Level.WARNING.equals(activity.level()) && logger.isWarnEnabled()) {
      logger.warn(appendEntries(activity.context()), activity.message());
    } else if (Level.SEVERE.equals(activity.level()) && logger.isErrorEnabled()) {
      logger.error(appendEntries(activity.context()), activity.message());
    } else if (Level.CONFIG.equals(activity.level()) && logger.isDebugEnabled()) {
      logger.debug(appendEntries(activity.context()), activity.message());
    }
  }
}
