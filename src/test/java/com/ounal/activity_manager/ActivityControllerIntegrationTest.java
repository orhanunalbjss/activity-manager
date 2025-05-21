package com.ounal.activity_manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.ounal.activity_manager.ActivityTestHelper.generateTestActivities;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ActivityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ActivityRepository activityRepository;

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        activityRepository.deleteAll();

        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @AfterEach
    void tearDown() {
        mapper = null;
    }

    /**
     * Save a new activity, update it, then delete it.
     * Use getAllActivities to verify contents.
     */
    @Test
    public void testCrudOperations() throws Exception {
        assertGetAllReturnsEmptyList();

        var activityToSave = generateTestActivities().get(0);
        activityToSave.setId(null);

        var activityToSaveBytes = mapper.writeValueAsBytes(activityToSave);

        var createMvcResult = mockMvc.perform(post("/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activityToSaveBytes))
                .andExpect(status().isOk())
                .andReturn();

        var actualCreatedActivity = mapper.readValue(createMvcResult.getResponse().getContentAsString(), Activity.class);

        var expectedCreatedActivity = generateTestActivities().get(0);
        expectedCreatedActivity.setId(1L);

        BDDAssertions.then(actualCreatedActivity)
                .isEqualTo(expectedCreatedActivity);

        assertGetAllContainsExactlyExpectedActivity(expectedCreatedActivity);

        var activityToUpdate = generateTestActivities().get(0);
        activityToUpdate.setId(1L);
        activityToUpdate.setName("Updated name!");

        var activityToUpdateBytes = mapper.writeValueAsBytes(activityToUpdate);

        var updateMvcResult = mockMvc.perform(put("/activities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activityToUpdateBytes))
                .andExpect(status().isOk())
                .andReturn();

        var actualUpdatedActivity = mapper.readValue(updateMvcResult.getResponse().getContentAsString(), Activity.class);

        var expectedUpdatedActivity = generateTestActivities().get(0);
        expectedUpdatedActivity.setId(1L);
        expectedUpdatedActivity.setName("Updated name!");

        BDDAssertions.then(actualUpdatedActivity)
                .isEqualTo(expectedUpdatedActivity);

        assertGetAllContainsExactlyExpectedActivity(expectedUpdatedActivity);

        var deleteMvcResult = mockMvc.perform(delete("/activities/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BDDAssertions.then(deleteMvcResult.getResponse().getContentAsString())
                .isEmpty();

        assertGetAllReturnsEmptyList();
    }

    private void assertGetAllContainsExactlyExpectedActivity(Activity expectedActivity) throws Exception {
        var getAllMvcResult = mockMvc.perform(get("/activities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();

        var actualActivities = mapper.readValue(
                getAllMvcResult.getResponse().getContentAsString(), new TypeReference<List<Activity>>() {
                });

        BDDAssertions.then(actualActivities)
                .containsExactly(expectedActivity);
    }

    private void assertGetAllReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/activities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}