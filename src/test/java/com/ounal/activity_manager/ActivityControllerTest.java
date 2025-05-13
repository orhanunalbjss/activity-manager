package com.ounal.activity_manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ActivityController.class)
public class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActivityService activityService;

    @Test
    public void whenCreateActivity_thenCallService() throws Exception {
        var expectedActivity = generateTestActivities().get(0);

        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        var activityBytes = mapper.writeValueAsBytes(expectedActivity);

        mockMvc.perform(post("/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(activityBytes));

        then(activityService)
                .should()
                .createActivity(expectedActivity);
    }

    @Test
    public void whenCreateActivity_thenReturnActivity() throws Exception {
        var expectedActivity = generateTestActivities().get(0);

        given(activityService.createActivity(expectedActivity))
                .willReturn(expectedActivity);

        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        var activityBytes = mapper.writeValueAsBytes(expectedActivity);

        var mvcResult = mockMvc.perform(post("/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activityBytes))
                .andExpect(status().isOk())
                .andReturn();

        var actualActivity = mapper.readValue(mvcResult.getResponse().getContentAsString(), Activity.class);

        BDDAssertions.then(actualActivity)
                .isEqualTo(expectedActivity);
    }

    @Test
    public void whenGetAllActivities_thenCallService() throws Exception {
        mockMvc.perform(get("/activities"));

        then(activityService)
                .should()
                .getAllActivities();
    }

    @Test
    public void givenActivities_whenGetAllActivities_thenReturnAllActivities() throws Exception {
        var expectedActivities = generateTestActivities();

        given(activityService.getAllActivities())
                .willReturn(expectedActivities);

        var mvcResult = mockMvc.perform(get("/activities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andReturn();

        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        var actualActivities = mapper.readValue(
                mvcResult.getResponse().getContentAsString(), new TypeReference<List<Activity>>() {
                });

        assertThat(actualActivities, containsInAnyOrder(expectedActivities.toArray()));
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