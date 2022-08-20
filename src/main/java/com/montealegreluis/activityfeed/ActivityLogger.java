package com.montealegreluis.activityfeed;

import static net.logstash.logback.marker.Markers.appendEntries;
import static org.slf4j.event.Level.*;

import com.montealegreluis.assertions.Assert;
import org.slf4j.Logger;

public final class ActivityLogger implements ActivityRecorder {
  private final Logger logger;

  public ActivityLogger(Logger logger) {
    Assert.notNull(logger, "Logger cannot be null");
    this.logger = logger;
  }

  @Override
  public void record(Activity activity) {
    if (INFO.equals(activity.level()) && logger.isInfoEnabled()) {
      logger.info(appendEntries(activity.context()), activity.message());
    } else if (WARN.equals(activity.level()) && logger.isWarnEnabled()) {
      logger.warn(appendEntries(activity.context()), activity.message());
    } else if (ERROR.equals(activity.level()) && logger.isErrorEnabled()) {
      logger.error(appendEntries(activity.context()), activity.message());
    } else if (DEBUG.equals(activity.level()) && logger.isDebugEnabled()) {
      logger.debug(appendEntries(activity.context()), activity.message());
    } else if (TRACE.equals(activity.level()) && logger.isTraceEnabled()) {
      logger.trace(appendEntries(activity.context()), activity.message());
    }
  }
}
