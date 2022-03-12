package com.montealegreluis.activityfeed;

import static com.montealegreluis.activityfeed.ActivityFactory.anActivity;
import static com.montealegreluis.activityfeed.ExceptionContextFactory.contextFrom;
import static org.junit.jupiter.api.Assertions.*;

import com.montealegreluis.assertions.IllegalArgumentException;
import org.junit.jupiter.api.Test;

final class ActivityFactoryTest {
  @Test
  void it_fails_to_build_an_activity_without_a_level() {
    assertThrows(IllegalArgumentException.class, () -> anActivity().build());
  }

  @Test
  void it_fails_to_build_an_activity_without_identifier() {
    assertThrows(IllegalArgumentException.class, () -> anActivity().info().build());
  }

  @Test
  void it_fails_to_build_an_activity_without_message() {
    assertThrows(
        IllegalArgumentException.class,
        () -> anActivity().info().withIdentifier("application-error").build());
  }

  @Test
  void it_builds_an_error_activity_without_context() {
    var errorActivity = Activity.error("application-error", "Application Error");

    var activity =
        anActivity()
            .error()
            .withIdentifier("application-error")
            .withMessage("Application Error")
            .build();

    assertEquals(errorActivity, activity);
  }

  @Test
  void it_builds_a_debugging_activity_without_context() {
    var debugActivity = Activity.debug("saving-file", "Saving file");

    var activity =
        anActivity().debug().withIdentifier("saving-file").withMessage("Saving file").build();

    assertEquals(debugActivity, activity);
  }

  @Test
  void it_builds_a_warning_activity_without_context() {
    var warningActivity = Activity.warning("validation-errors", "Validation errors were found");

    var activity =
        anActivity()
            .warning()
            .withIdentifier("validation-errors")
            .withMessage("Validation errors were found")
            .build();

    assertEquals(warningActivity, activity);
  }

  @Test
  void it_builds_an_informational_activity_without_context() {
    var infoActivity = Activity.info("file-saved", "File saved successfully");

    var activity =
        anActivity()
            .info()
            .withIdentifier("file-saved")
            .withMessage("File saved successfully")
            .build();

    assertEquals(infoActivity, activity);
  }

  @Test
  void it_builds_an_error_activity_with_context() {
    var errorActivity =
        Activity.error(
            "application-error",
            "Application Error",
            (context) -> context.put("action", "search-concerts"));

    var activity =
        anActivity()
            .error()
            .withIdentifier("application-error")
            .withMessage("Application Error")
            .with("action", "search-concerts")
            .build();

    assertEquals(errorActivity, activity);
    assertEquals(errorActivity.context(), activity.context());
  }

  @Test
  void it_builds_a_warning_activity_with_context() {
    var warningActivity =
        Activity.warning(
            "validation-error",
            "Validation errors were found",
            (context) -> context.put("errorsCount", 1));

    var activity =
        anActivity()
            .warning()
            .withIdentifier("validation-error")
            .withMessage("Validation errors were found")
            .with("errorsCount", 1)
            .build();

    assertEquals(warningActivity, activity);
    assertEquals(warningActivity.context(), activity.context());
  }

  @Test
  void it_builds_a_debugging_activity_with_context() {
    var debuggingActivity =
        Activity.debug(
            "saving-file", "Saving file", (context) -> context.put("filename", "example.pdf"));

    var activity =
        anActivity()
            .debug()
            .withIdentifier("saving-file")
            .withMessage("Saving file")
            .with("filename", "example.pdf")
            .build();

    assertEquals(debuggingActivity, activity);
    assertEquals(debuggingActivity.context(), activity.context());
  }

  @Test
  void it_builds_an_informational_activity_with_context() {
    var infoActivity =
        Activity.info(
            "file-saved", "File saved", (context) -> context.put("filename", "example.pdf"));

    var activity =
        anActivity()
            .info()
            .withIdentifier("file-saved")
            .withMessage("File saved")
            .with("filename", "example.pdf")
            .build();

    assertEquals(infoActivity, activity);
    assertEquals(infoActivity.context(), activity.context());
  }

  @Test
  void it_prevents_adding_a_null_exception_to_its_context() {
    assertThrows(IllegalArgumentException.class, () -> anActivity().error().withException(null));
  }

  @Test
  void it_adds_an_exception_to_its_context() {
    var exception = new RuntimeException("An error occurred");
    var errorActivity =
        Activity.error(
            "application-error",
            "Application error",
            (context) -> context.put("exception", contextFrom(exception)));

    var activity =
        anActivity()
            .error()
            .withIdentifier("application-error")
            .withMessage("Application error")
            .withException(exception)
            .build();

    assertEquals(errorActivity, activity);
    assertEquals(errorActivity.context(), activity.context());
  }
}
