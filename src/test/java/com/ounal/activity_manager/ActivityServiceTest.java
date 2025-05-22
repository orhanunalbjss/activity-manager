package com.ounal.activity_manager;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static com.ounal.activity_manager.ActivityService.BORED_API_GET_RANDOM_ACTIVITY_URL;
import static com.ounal.activity_manager.ActivityTestHelper.generateTestActivities;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private ActivityService activityService;

    @Test
    public void whenCreateActivity_thenCallRepository() {
        var expectedActivity = generateTestActivities().get(0);

        activityService.createActivity(expectedActivity);

        BDDMockito.then(activityRepository)
                .should()
                .save(expectedActivity);
    }

    @Test
    public void whenCreateActivity_thenReturnActivity() {
        var expectedActivity = generateTestActivities().get(0);

        given(activityRepository.save(expectedActivity))
                .willReturn(expectedActivity);

        var actualActivity = activityService.createActivity(expectedActivity);

        BDDAssertions.then(actualActivity)
                .isEqualTo(expectedActivity);
    }

    @Test
    public void whenCreateRandomActivity_thenCallRepository() {
        var activityDto = ActivityDto.builder()
                .name("random name")
                .type("random type")
                .participants(24)
                .build();

        given(restTemplate.getForObject(BORED_API_GET_RANDOM_ACTIVITY_URL, ActivityDto.class))
                .willReturn(activityDto);

        activityService.createRandomActivity();

        var expectedActivity = Activity.builder()
                .name("random name")
                .type("random type")
                .participants(24)
                .build();

        BDDMockito.then(activityRepository)
                .should()
                .save(expectedActivity);
    }

    @Test
    public void givenThirdPartyApiReturnsNull_whenCreateRandomActivity_thenDoNotSaveActivity() {
        given(restTemplate.getForObject(BORED_API_GET_RANDOM_ACTIVITY_URL, ActivityDto.class))
                .willReturn(null);

        activityService.createRandomActivity();

        BDDMockito.then(activityRepository)
                .should(never())
                .save(any());
    }

    @Test
    public void whenCreateRandomActivity_thenReturnActivity() {
        var activityDto = ActivityDto.builder()
                .name("random name")
                .type("random type")
                .participants(24)
                .build();

        given(restTemplate.getForObject(BORED_API_GET_RANDOM_ACTIVITY_URL, ActivityDto.class))
                .willReturn(activityDto);

        var expectedActivity = Activity.builder()
                .name("random name")
                .type("random type")
                .participants(24)
                .build();

        given(activityRepository.save(expectedActivity))
                .willReturn(expectedActivity);

        var actualActivity = activityService.createRandomActivity();

        BDDAssertions.then(actualActivity)
                .isEqualTo(expectedActivity);
    }

    @Test
    public void givenThirdPartyApiReturnsNull_whenCreateRandomActivity_thenReturnNull() {
        given(restTemplate.getForObject(BORED_API_GET_RANDOM_ACTIVITY_URL, ActivityDto.class))
                .willReturn(null);

        var actualActivity = activityService.createRandomActivity();

        BDDAssertions.then(actualActivity)
                .isNull();
    }

    @Test
    public void whenGetAllActivities_thenCallRepository() {
        activityService.getAllActivities();

        BDDMockito.then(activityRepository)
                .should()
                .findAll();
    }

    @Test
    public void givenActivities_whenGetAllActivities_thenReturnAllActivities() {
        var expectedActivities = generateTestActivities();

        given(activityRepository.findAll())
                .willReturn(expectedActivities);

        var actualActivities = activityService.getAllActivities();

        BDDAssertions.then(actualActivities)
                .isEqualTo(expectedActivities);
    }

    @Test
    public void givenActivityExists_whenUpdateActivity_thenCallRepositoryInCorrectOrder() {
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
    public void givenActivityDoesNotExist_whenUpdateActivity_thenDoNotSaveActivity() {
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
    public void givenActivityExists_whenUpdateActivity_thenReturnActivity() {
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
    public void givenActivityDoesNotExist_whenUpdateActivity_thenReturnNull() {
        var expectedActivity = generateTestActivities().get(0);

        given(activityRepository.findById(1L))
                .willReturn(Optional.empty());

        var actualActivity = activityService.updateActivity(1L, expectedActivity);

        BDDAssertions.then(actualActivity)
                .isNull();
    }

    @Test
    public void whenDeleteActivity_thenCallRepository() {
        activityService.deleteActivity(1L);

        BDDMockito.then(activityRepository)
                .should()
                .deleteById(1L);
    }
}