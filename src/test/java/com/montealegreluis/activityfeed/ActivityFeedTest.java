package com.montealegreluis.activityfeed;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.montealegreluis.assertions.IllegalArgumentException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.Marker;

final class ActivityFeedTest {
  @Test
  void it_adds_an_activity_to_all_its_recorders() {
    var recorderA = mock(ActivityRecorder.class);
    var recorderB = mock(ActivityRecorder.class);
    var feed = new ActivityFeed(List.of(recorderA, recorderB));
    var activity = Activity.info("save-customer-profile", "Customer profile was saved");

    feed.add(activity);

    verify(recorderA, times(1)).record(activity);
    verify(recorderB, times(1)).record(activity);
  }

  @Test
  void it_adds_an_activity_and_logs_it_by_default() {
    var logger = mock(Logger.class);
    when(logger.isInfoEnabled()).thenReturn(true);
    var feed = ActivityFeed.withLogging(logger);
    var activity = Activity.info("save-customer-profile", "Customer profile was saved");

    feed.add(activity);

    verify(logger, times(1)).info(any(Marker.class), eq("Customer profile was saved"));
  }

  @Test
  void it_cannot_be_created_without_recorders() {
    assertThrows(IllegalArgumentException.class, () -> new ActivityFeed(Collections.emptyList()));
  }
}
