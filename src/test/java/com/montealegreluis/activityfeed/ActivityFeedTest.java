package com.montealegreluis.activityfeed;

import static net.logstash.logback.marker.Markers.appendEntries;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

final class ActivityFeedTest {
  @Test
  void it_logs_a_debugging_activity() {
    var activity =
        Activity.debug(
            "file-saved", "File saved", (context) -> context.put("filename", "test.pdf"));

    feed.log(activity);

    verify(logger).debug(appendEntries(activity.context()), activity.message());
  }

  @Test
  void it_logs_an_informational_activity() {
    var activity =
        Activity.info(
            "save-customer-profile",
            "Customer profile was saved",
            (context) -> context.put("customerId", "776ad420-59f5-44aa-b0b8-14b3d1c2b597"));

    feed.log(activity);

    verify(logger).info(appendEntries(activity.context()), activity.message());
  }

  @Test
  void it_logs_a_warning_activity() {
    var activity =
        Activity.warning(
            "invalid-product-price",
            "Product price is invalid",
            (context) -> context.put("productPrice", "-100"));

    feed.log(activity);

    verify(logger).warn(appendEntries(activity.context()), activity.message());
  }

  @Test
  void it_logs_an_error_activity() {
    var activity =
        Activity.error(
            "server-error",
            "Server error",
            (context) -> context.put("exceptionMessage", "Cannot connect to database server"));

    feed.log(activity);

    verify(logger).error(appendEntries(activity.context()), activity.message());
  }

  @BeforeEach
  void let() {
    logger = mock(Logger.class);
    feed = new ActivityFeed(logger);
  }

  private ActivityFeed feed;
  private Logger logger;
}
