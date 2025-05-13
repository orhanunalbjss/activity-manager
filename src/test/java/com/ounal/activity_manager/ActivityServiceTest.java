package com.ounal.activity_manager;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;
    @InjectMocks
    private ActivityService activityService;

    @Test
    void whenCreateActivity_thenCallRepository() {
        var expectedActivity = generateTestActivities().get(0);

        activityService.createActivity(expectedActivity);

        BDDMockito.then(activityRepository)
                .should()
                .save(expectedActivity);
    }

    @Test
    void whenCreateActivity_thenReturnActivity() {
        var expectedActivity = generateTestActivities().get(0);

        given(activityRepository.save(expectedActivity))
                .willReturn(expectedActivity);

        var actualActivity = activityService.createActivity(expectedActivity);

        BDDAssertions.then(actualActivity)
                .isEqualTo(expectedActivity);
    }

    private static List<Activity> generateTestActivities() {
        var activity1 = Activity.builder()
                .id(1L)
                .name("Activity name 1")
                .type("Activity type 1")
                .participants(3)
                .build();

        var activity2 = Activity.builder()
                .id(2L)
                .name("Activity name 2")
                .type("Activity type 2")
                .participants(5)
                .build();

        return List.of(activity1, activity2);
    }
}