package com.montealegreluis.activityfeed;

import static net.logstash.logback.marker.Markers.appendEntries;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.Marker;

final class ActivityFeedTest {
  @Test
  void it_logs_a_debugging_activity() {
    when(logger.isDebugEnabled()).thenReturn(true);
    var activity =
        Activity.debug(
            "file-saved", "File saved", (context) -> context.put("filename", "test.pdf"));

    feed.record(activity);

    verify(logger, times(1)).debug(appendEntries(activity.context()), activity.message());
  }

  @Test
  void it_does_not_log_a_debugging_activity_if_debug_level_is_not_enabled() {
    when(logger.isDebugEnabled()).thenReturn(false);
    var activity =
        Activity.debug(
            "file-saved", "File saved", (context) -> context.put("filename", "test.pdf"));

    feed.record(activity);

    verify(logger, times(0)).debug(any(Marker.class), any());
  }

  @Test
  void it_logs_an_informational_activity() {
    when(logger.isInfoEnabled()).thenReturn(true);
    var activity =
        Activity.info(
            "save-customer-profile",
            "Customer profile was saved",
            (context) -> context.put("customerId", "776ad420-59f5-44aa-b0b8-14b3d1c2b597"));

    feed.record(activity);

    verify(logger).info(appendEntries(activity.context()), activity.message());
  }

  @Test
  void it_does_not_log_an_informational_activity_if_info_level_is_not_enabled() {
    when(logger.isInfoEnabled()).thenReturn(false);
    var activity =
        Activity.info(
            "save-customer-profile",
            "Customer profile was saved",
            (context) -> context.put("customerId", "776ad420-59f5-44aa-b0b8-14b3d1c2b597"));

    feed.record(activity);

    verify(logger, times(0)).info(any(Marker.class), any());
  }

  @Test
  void it_logs_a_warning_activity() {
    when(logger.isWarnEnabled()).thenReturn(true);
    var activity =
        Activity.warning(
            "invalid-product-price",
            "Product price is invalid",
            (context) -> context.put("productPrice", "-100"));

    feed.record(activity);

    verify(logger).warn(appendEntries(activity.context()), activity.message());
  }

  @Test
  void it_does_not_log_a_warning_activity_if_warn_level_is_not_enabled() {
    when(logger.isWarnEnabled()).thenReturn(false);
    var activity =
        Activity.warning(
            "invalid-product-price",
            "Product price is invalid",
            (context) -> context.put("productPrice", "-100"));

    feed.record(activity);

    verify(logger, times(0)).warn(any(Marker.class), any());
  }

  @Test
  void it_logs_an_error_activity() {
    when(logger.isErrorEnabled()).thenReturn(true);
    var activity =
        Activity.error(
            "server-error",
            "Server error",
            (context) -> context.put("exceptionMessage", "Cannot connect to database server"));

    feed.record(activity);

    verify(logger).error(appendEntries(activity.context()), activity.message());
  }

  @Test
  void it_does_not_log_an_error_activity_if_error_level_is_not_enabled() {
    when(logger.isErrorEnabled()).thenReturn(false);
    var activity =
        Activity.error(
            "server-error",
            "Server error",
            (context) -> context.put("exceptionMessage", "Cannot connect to database server"));

    feed.record(activity);

    verify(logger, times(0)).error(any(Marker.class), any());
  }

  @BeforeEach
  void let() {
    logger = mock(Logger.class);
    feed = new ActivityFeed(logger);
  }

  private ActivityFeed feed;
  private Logger logger;
}
