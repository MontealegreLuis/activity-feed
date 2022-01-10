package com.montealegreluis.activityfeed;

import static com.montealegreluis.activityfeed.builders.ContextBuilder.aContext;
import static net.logstash.logback.marker.Markers.appendEntries;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

final class ActivityFeedTest {
  @Test
  void it_logs_a_debugging_activity() {
    var context = aContext().withEntry("filename", "test.pdf").build();
    var activity = Activity.debug("file-saved", "File saved", context);

    feed.log(activity);

    verify(logger).debug(appendEntries(activity.context()), activity.message());
  }

  @Test
  void it_logs_an_informational_activity() {
    var context =
        aContext().withEntry("customerId", "776ad420-59f5-44aa-b0b8-14b3d1c2b597").build();
    var activity = Activity.info("save-customer-profile", "Customer profile was saved", context);

    feed.log(activity);

    verify(logger).info(appendEntries(activity.context()), activity.message());
  }

  @Test
  void it_logs_a_warning_activity() {
    var context = aContext().withEntry("productPrice", "-100").build();
    var activity = Activity.warning("invalid-product-price", "Product price is invalid", context);

    feed.log(activity);

    verify(logger).warn(appendEntries(activity.context()), activity.message());
  }

  @Test
  void it_logs_an_error_activity() {
    var context =
        aContext().withEntry("exceptionMessage", "Cannot connect to database server").build();
    var activity = Activity.error("server-error", "Server error", context);

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
