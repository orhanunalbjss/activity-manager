package com.ounal.activity_manager;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.ounal.activity_manager.ActivityTestHelper.generateTestActivities;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;

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

    @Test
    void whenGetAllActivities_thenCallRepository() {
        activityService.getAllActivities();

        BDDMockito.then(activityRepository)
                .should()
                .findAll();
    }

    @Test
    void givenActivities_whenGetAllActivities_thenReturnAllActivities() {
        var expectedActivities = generateTestActivities();

        given(activityRepository.findAll())
                .willReturn(expectedActivities);

        var actualActivities = activityService.getAllActivities();

        BDDAssertions.then(actualActivities)
                .isEqualTo(expectedActivities);
    }

    @Test
    void givenActivityExists_whenUpdateActivity_thenCallRepositoryInCorrectOrder() {
        var expectedActivity = generateTestActivities().get(0);

        given(activityRepository.findById(1L))
                .willReturn(Optional.of(expectedActivity));

        activityService.updateActivity(1L, expectedActivity);

        var inOrder = Mockito.inOrder(activityRepository);

        BDDMockito.then(activityRepository)
                .should(inOrder)
                .findById(1L);

        BDDMockito.then(activityRepository)
                .should(inOrder)
                .save(expectedActivity);
    }

    @Test
    void givenActivityDoesNotExist_whenUpdateActivity_thenDoNotSaveActivity() {
        var expectedActivity = generateTestActivities().get(0);

        given(activityRepository.findById(1L))
                .willReturn(Optional.empty());

        activityService.updateActivity(1L, expectedActivity);

        var inOrder = Mockito.inOrder(activityRepository);

        BDDMockito.then(activityRepository)
                .should(inOrder)
                .findById(1L);

        BDDMockito.then(activityRepository)
                .should(inOrder, never())
                .save(expectedActivity);
    }

    @Test
    void givenActivityExists_whenUpdateActivity_thenReturnActivity() {
        var expectedActivity = generateTestActivities().get(0);

        given(activityRepository.findById(1L))
                .willReturn(Optional.of(expectedActivity));

        given(activityRepository.save(expectedActivity))
                .willReturn(expectedActivity);

        var actualActivity = activityService.updateActivity(1L, expectedActivity);

        BDDAssertions.then(actualActivity)
                .isEqualTo(expectedActivity);
    }

    @Test
    void givenActivityDoesNotExist_whenUpdateActivity_thenReturnNull() {
        var expectedActivity = generateTestActivities().get(0);

        given(activityRepository.findById(1L))
                .willReturn(Optional.empty());

        var actualActivity = activityService.updateActivity(1L, expectedActivity);

        BDDAssertions.then(actualActivity)
                .isNull();
    }

    @Test
    void whenDeleteActivity_thenCallRepository() {
        activityService.deleteActivity(1L);

        BDDMockito.then(activityRepository)
                .should()
                .deleteById(1L);
    }
}