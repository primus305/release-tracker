package com.neon.releasetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neon.releasetracker.AbstractReleaseTrackerTest;
import com.neon.releasetracker.config.TestJacksonConfig;
import com.neon.releasetracker.data.ReleaseTestData;
import com.neon.releasetracker.model.ReleaseStatus;
import com.neon.releasetracker.request.CreateReleaseRequest;
import com.neon.releasetracker.request.UpdateReleaseRequest;
import com.neon.releasetracker.response.ReleaseResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestJacksonConfig.class)
@TestPropertySource(properties = {
        "spring.messages.basename=messages/messages",
        "release-tracker.security.enabled=false"
})
class ReleaseControllerIntegrationTest extends AbstractReleaseTrackerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenValidRequest_whenCreate_thenPersistAndReturn201() throws Exception {
        CreateReleaseRequest request = ReleaseTestData.createReleaseRequest().build();

        mockMvc.perform(post("/v1/releases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(request.name()))
                .andExpect(jsonPath("$.description").value(request.description()))
                .andExpect(jsonPath("$.status").value(ReleaseStatus.CREATED.name()));
    }

    @Test
    void givenInvalidRequest_whenCreate_thenReturn400() throws Exception {
        CreateReleaseRequest invalidRequest = CreateReleaseRequest.builder()
                .build();

        mockMvc.perform(post("/v1/releases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenExistingId_whenUpdate_thenReturnUpdated() throws Exception {
        ReleaseResponse releaseResponse = createRelease("Test Release");

        UpdateReleaseRequest updateRequest = ReleaseTestData.updateReleaseRequest().build();

        mockMvc.perform(put("/v1/releases/{id}", releaseResponse.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(releaseResponse.id()))
                .andExpect(jsonPath("$.name").value(updateRequest.name()))
                .andExpect(jsonPath("$.description").value(updateRequest.description()))
                .andExpect(jsonPath("$.status").value(updateRequest.status().name()));
    }

    @Test
    void givenNonExistingId_whenUpdate_thenReturn404() throws Exception {
        UpdateReleaseRequest request = ReleaseTestData.updateReleaseRequest().build();

        mockMvc.perform(put("/v1/releases/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void givenExistingId_whenGetById_thenReturn200() throws Exception {
        ReleaseResponse releaseResponse = createRelease("Test Release");

        mockMvc.perform(get("/v1/releases/{id}", releaseResponse.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(releaseResponse.id()))
                .andExpect(jsonPath("$.name").value(releaseResponse.name()))
                .andExpect(jsonPath("$.description").value(releaseResponse.description()))
                .andExpect(jsonPath("$.status").value(releaseResponse.status().name()));
    }

    @Test
    void givenNonExistingId_whenGetById_thenReturn404() throws Exception {
        mockMvc.perform(get("/v1/releases/{id}", 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void givenExistingId_whenDeleteById_thenReturn204() throws Exception {
        ReleaseResponse releaseResponse = createRelease("Test Release");

        mockMvc.perform(delete("/v1/releases/{id}", releaseResponse.id()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/releases/{id}", releaseResponse.id()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenNonExistingId_whenDeleteById_thenReturn404() throws Exception {
        mockMvc.perform(delete("/v1/releases/{id}", 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void givenNameFilter_whenSearch_thenReturnMatching() throws Exception {
        createRelease("Test Release");

        mockMvc.perform(get("/v1/releases")
                        .param("name", "test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content[0].name").value("Test Release"));
    }

    @Test
    void givenMultipleStatusFilter_whenSearch_thenReturnMatching() throws Exception {
        ReleaseResponse releaseResponse = createRelease("Test Release 1");
        updateRelease(releaseResponse.id());

        createRelease("Test Release 2");

        mockMvc.perform(get("/v1/releases")
                        .param("statuses", ReleaseStatus.CREATED.name(), ReleaseStatus.IN_DEVELOPMENT.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void givenDateRangeFilter_whenSearch_thenReturnMatching() throws Exception {
        createReleaseWithDate("Test Release 1", LocalDate.of(2026, 2, 25));
        createReleaseWithDate("Test Release 2", LocalDate.of(2026, 4, 25));

        mockMvc.perform(get("/v1/releases")
                        .param("releaseDateFrom", LocalDate.of(2026, 1, 25).toString())
                        .param("releaseDateTo", LocalDate.of(2026, 3, 25).toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content[0].name").value("Test Release 1"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void givenInvalidDateRange_whenSearch_thenReturn400() throws Exception {
        mockMvc.perform(get("/v1/releases")
                        .param("releaseDateFrom", LocalDate.of(2026, 12, 1).toString())
                        .param("releaseDateTo", LocalDate.of(2026, 1, 1).toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private ReleaseResponse createRelease(String name) throws Exception {
        CreateReleaseRequest createReleaseRequest = ReleaseTestData.createReleaseRequest()
                .name(name)
                .build();

        String json = mockMvc.perform(post("/v1/releases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReleaseRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(json, ReleaseResponse.class);
    }

    private void createReleaseWithDate(String name, LocalDate date) throws Exception {
        CreateReleaseRequest request = ReleaseTestData.createReleaseRequest()
                .name(name)
                .releaseDate(date)
                .build();

        mockMvc.perform(post("/v1/releases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    private void updateRelease(Long id) throws Exception {
        UpdateReleaseRequest updateRequest = ReleaseTestData.updateReleaseRequest()
                .build();

        mockMvc.perform(put("/v1/releases/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)));
    }
}