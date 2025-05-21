package com.ounal.activity_manager;

import java.util.List;

public class ActivityTestHelper {

    public static List<Activity> generateTestActivities() {
        var activity1 = Activity.builder()
                .name("Activity name 1")
                .type("Activity type 1")
                .participants(3)
                .build();

        var activity2 = Activity.builder()
                .name("Activity name 2")
                .type("Activity type 2")
                .participants(5)
                .build();

        return List.of(activity1, activity2);
    }
}
