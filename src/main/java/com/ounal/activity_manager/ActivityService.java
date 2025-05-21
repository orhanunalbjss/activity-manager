package com.ounal.activity_manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    @Autowired
    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public Activity createActivity(Activity activity) {
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
}
