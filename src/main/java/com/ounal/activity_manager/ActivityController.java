package com.ounal.activity_manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService;

    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    public Activity createActivity(@RequestBody Activity activity) {
        return activityService.createActivity(activity);
    }

    @PostMapping("/random")
    public Activity createRandomActivity() {
        return activityService.createRandomActivity();
    }

    @GetMapping
    public List<Activity> getAllActivities() {
        return activityService.getAllActivities();
    }

    @PutMapping("/{id}")
    public Activity updateActivity(@PathVariable Long id, @RequestBody Activity activity) {
        return activityService.updateActivity(id, activity);
    }

    @DeleteMapping("/{id}")
    public void deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
    }
}
