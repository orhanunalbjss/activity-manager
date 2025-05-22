package com.ounal.activity_manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ActivityService {

    public static final String BORED_API_GET_RANDOM_ACTIVITY_URL = "https://bored-api.appbrewery.com/random";

    private final ActivityRepository activityRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public ActivityService(ActivityRepository activityRepository, RestTemplate restTemplate) {
        this.activityRepository = activityRepository;
        this.restTemplate = restTemplate;
    }

    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    public Activity createRandomActivity() {
        var activityDto = restTemplate.getForObject(BORED_API_GET_RANDOM_ACTIVITY_URL, ActivityDto.class);
        if (activityDto == null) {
            return null;
        }

        var activity = convertActivityDtoToEntity(activityDto);

        return activityRepository.save(activity);
    }

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public Activity updateActivity(Long id, Activity updatedActivity) {
        var activityOptional = activityRepository.findById(id);
        if (activityOptional.isEmpty()) {
            return null;
        }

        updatedActivity.setId(id);

        return activityRepository.save(updatedActivity);
    }

    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    private static Activity convertActivityDtoToEntity(ActivityDto activityDto) {
        return Activity.builder()
                .name(activityDto.getName())
                .type(activityDto.getType())
                .participants(activityDto.getParticipants())
                .build();
    }
}
