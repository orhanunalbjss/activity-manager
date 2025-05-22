package com.ounal.activity_manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static com.ounal.activity_manager.ActivityService.BORED_API_GET_RANDOM_ACTIVITY_URL;
import static com.ounal.activity_manager.ActivityTestHelper.generateTestActivities;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ActivityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private RestTemplate restTemplate;

    private ObjectMapper mapper;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        activityRepository.deleteAll();

        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    /**
     * Save a new activity, update it, then delete it.
     * Use getAllActivities to verify contents.
     */
    @Test
    public void testBasicCrudOperations() throws Exception {
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

    /**
     * Ensure that we can create a random activity, using the third party API.
     * Use getAllActivities to verify contents.
     */
    @Test
    public void testCreateRandomActivity() throws Exception {
        var expectedCreatedActivityDto = ActivityDto.builder()
                .name("Activity name 2")
                .type("Activity type 2")
                .participants(5)
                .build();

        mockServer.expect(ExpectedCount.once(), requestTo(new URI(BORED_API_GET_RANDOM_ACTIVITY_URL)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(expectedCreatedActivityDto)));

        var expectedCreatedActivity = generateTestActivities().get(1);
        expectedCreatedActivity.setId(1L);

        var createMvcResult = mockMvc.perform(post("/activities/random")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        var actualCreatedActivity = mapper.readValue(createMvcResult.getResponse().getContentAsString(), Activity.class);

        BDDAssertions.then(actualCreatedActivity)
                .isEqualTo(expectedCreatedActivity);

        assertGetAllContainsExactlyExpectedActivity(expectedCreatedActivity);
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